/**
 * 
 */
package gr.auth.ee.lcs.data.updateAlgorithms;

import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.classifiers.Macroclassifier;
import gr.auth.ee.lcs.data.ClassifierTransformBridge;
import gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy;
import gr.auth.ee.lcs.geneticalgorithm.IGeneticAlgorithmStrategy;

import java.io.Serializable;

/**
 * A strength based multi-label update algorithm
 * 
 * @author Miltos Allamanis
 * 
 */
public class MlSSLCSUpdateAlgorithm extends UpdateAlgorithmFactoryAndStrategy {

	/**
	 * The number of labels used.
	 */
	private final int numberOfLabels;

	private final int subsumptionExperienceThreshold;

	private final double subsumptionAccuracyThreshold;

	/**
	 * Genetic Algorithm.
	 */
	private final IGeneticAlgorithmStrategy ga;

	/**
	 * Reward and penalty percentage parameters.
	 */
	private final double strengthReward, penalty;

	public MlSSLCSUpdateAlgorithm(final double reward,
			final double penaltyPercent, final int labels,
			final IGeneticAlgorithmStrategy geneticAlgorithm,
			final int subsumptionExperience, final double subsumptionAccuracy) {
		numberOfLabels = labels;
		strengthReward = reward;
		ga = geneticAlgorithm;
		penalty = penaltyPercent;
		subsumptionExperienceThreshold = subsumptionExperience;
		subsumptionAccuracyThreshold = subsumptionAccuracy;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy#cover(gr.auth.ee
	 * .lcs.classifiers.ClassifierSet, int)
	 */
	@Override
	public void cover(ClassifierSet population, int instanceIndex) {
		Classifier coveringClassifier = ClassifierTransformBridge.getInstance()
				.createRandomCoveringClassifier(
						ClassifierTransformBridge.instances[instanceIndex]);
		population.addClassifier(new Macroclassifier(coveringClassifier, 1),
				false);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy#
	 * createStateClassifierObject()
	 */
	@Override
	public Serializable createStateClassifierObject() {
		return new MlSSLCSClassifierData();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy#getComparisonValue
	 * (gr.auth.ee.lcs.classifiers.Classifier, int)
	 */
	@Override
	public double getComparisonValue(Classifier aClassifier, int mode) {
		MlSSLCSClassifierData data = (MlSSLCSClassifierData) aClassifier
				.getUpdateDataObject();

		switch (mode) {
		case COMPARISON_MODE_DELETION:
			// TODO: Something else?
			return aClassifier.experience < 0 ? 1 : 1 / (data.fitness
					* data.activeLabels / numberOfLabels);
		case COMPARISON_MODE_EXPLOITATION:
			return data.str; // TODO: Or maybe tp/(tp+fp)?
		case COMPARISON_MODE_EXPLORATION:
			return aClassifier.experience < 10 ? 0 : (data.fitness);
		default:
			return 0;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy#getData(gr.auth
	 * .ee.lcs.classifiers.Classifier)
	 */
	@Override
	public String getData(Classifier aClassifier) {
		MlSSLCSClassifierData data = ((MlSSLCSClassifierData) aClassifier
				.getUpdateDataObject());
		return "tp:" + data.tp + "msa:" + data.msa + " str:" + data.str;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy#performUpdate(gr
	 * .auth.ee.lcs.classifiers.ClassifierSet,
	 * gr.auth.ee.lcs.classifiers.ClassifierSet)
	 */
	@Override
	public void performUpdate(ClassifierSet matchSet, ClassifierSet correctSet) {
		return;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy#setComparisonValue
	 * (gr.auth.ee.lcs.classifiers.Classifier, int, double)
	 */
	@Override
	public void setComparisonValue(Classifier aClassifier, int mode,
			double comparisonValue) {
		MlSSLCSClassifierData data = ((MlSSLCSClassifierData) aClassifier
				.getUpdateDataObject());
		data.fitness = comparisonValue;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy#updateSet(gr.auth
	 * .ee.lcs.classifiers.ClassifierSet,
	 * gr.auth.ee.lcs.classifiers.ClassifierSet, int)
	 */
	@Override
	protected void updateSet(ClassifierSet population, ClassifierSet matchSet,
			int instanceIndex) {
		final int matchSetSize = matchSet.getNumberOfMacroclassifiers();

		for (int lbl = 0; lbl < numberOfLabels; lbl++) {
			int totalCorrectRules = 0;

			for (int i = 0; i < matchSetSize; i++) {
				if (matchSet.getClassifier(i).classifyLabelCorrectly(
						instanceIndex, lbl) == 1)
					totalCorrectRules += matchSet.getClassifierNumerosity(i);
			}

			for (int i = 0; i < matchSetSize; i++) {
				final Classifier currentClassifier = matchSet.getClassifier(i);
				final MlSSLCSClassifierData data = ((MlSSLCSClassifierData) currentClassifier
						.getUpdateDataObject());
				final float classificationAbility = currentClassifier
						.classifyLabelCorrectly(instanceIndex, lbl);

				data.ns[lbl] = (data.ns[lbl] + totalCorrectRules)
						/ (data.msa + 1);
				if (classificationAbility >= 0) {
					data.str += ((double) strengthReward)
							/ ((double) totalCorrectRules);
					if (Double.isInfinite(data.str))
						data.str = 10;
					data.tp += 1;
				} else if (classificationAbility == -1) {
					data.str -= penalty * ((double) strengthReward)
							/ ((double) data.ns[lbl]);
					data.fp += 1;
				}

			}

			if (totalCorrectRules == 0) {
				this.cover(population, instanceIndex);
			}
		}

		for (int i = 0; i < matchSetSize; i++) {
			final Classifier currentClassifier = matchSet.getClassifier(i);
			final MlSSLCSClassifierData data = ((MlSSLCSClassifierData) currentClassifier
					.getUpdateDataObject());
			data.activeLabels = 0;
			for (int lbl = 0; lbl < numberOfLabels; lbl++) {
				final float classificationAbility = currentClassifier
						.classifyLabelCorrectly(instanceIndex, lbl);
				if (classificationAbility != 0)
					data.activeLabels += 1;
			}

			data.msa += 1;
			currentClassifier.experience++;
			data.fitness = data.str < 0 ? 0 : (data.str / data.msa);
			if (((double) data.tp) / ((double) (data.tp + data.fp)) > subsumptionAccuracyThreshold
					&& currentClassifier.experience > subsumptionExperienceThreshold)
				currentClassifier.setSubsumptionAbility(true);
			else
				currentClassifier.setSubsumptionAbility(false);
		}

		if (matchSetSize > 0)
			ga.evolveSet(matchSet, population);

	}

	class MlSSLCSClassifierData implements Serializable {

		public MlSSLCSClassifierData() {
			ns = new double[numberOfLabels];
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 2060323742317299358L;

		/**
		 *
		 */
		public double fitness = Double.NEGATIVE_INFINITY;

		/**
		 * niche set size estimation.
		 */
		public double[] ns;

		/**
		 * Match Set Appearances.
		 */
		public int msa = 1;

		/**
		 * true positives.
		 */
		public int tp = 1;

		/**
		 * false positives.
		 */
		public int fp = 0;

		/**
		 * Strength.
		 */
		public double str = 0;

		/**
		 * Number of active labels
		 */
		public int activeLabels = -1;

	}

}
