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
 * A UCS implementation with fitness sharing.
 * 
 * @author Miltos Allamanis
 */
public class UCSUpdateAlgorithm extends UpdateAlgorithmFactoryAndStrategy {

	/**
	 * A data object for the UCS update algorithm.
	 * 
	 * @author Miltos Allamanis
	 * 
	 */
	class UCSClassifierData implements Serializable {

		/**
		 * Serial code for serialization.
		 */
		private static final long serialVersionUID = 3098073593334379507L;

		/**
		 *
		 */
		private double fitness = 1 / 2;

		/**
		 * niche set size estimation.
		 */
		private double cs = 1;

		/**
		 * Match Set Appearances.
		 */
		private int msa = 0;

		/**
		 * true positives.
		 */
		private int tp = 0;

		/**
		 * false positives.
		 */
		private int fp = 0;

		/**
		 * Strength.
		 */
		private double fitness0 = 0;

	}

	/**
	 * Genetic Algorithm.
	 */
	private final IGeneticAlgorithmStrategy ga;

	/**
	 * The \theta_{DEL} parameter of UCS.
	 */
	private final int deleteAge;

	/**
	 * Private variables: the UCS parameter sharing. accuracy0 is considered the
	 * subsumption fitness threshold
	 */
	private final double a, accuracy0, n, b;

	/**
	 * A double indicating the probability that the GA will run on the matchSet
	 * (and not on the correct set).
	 */
	private final double matchSetRunProbability;

	/**
	 * The experience threshold for subsumption.
	 */
	private final int subsumptionExperienceThreshold;

	/**
	 * A threshold of the classification ability of a classifier in order to be
	 * classified as correct (and added to the correct set).
	 */
	private final double correctSetThreshold;

	/**
	 * Default constructor.
	 * 
	 * @param alpha
	 *            used in fitness sharing
	 * @param nParameter
	 *            used in fitness sharing
	 * @param acc0
	 *            used in fitness sharing: the minimum "good" accuracy
	 * @param learningRate
	 *            the beta of UCS
	 * @param experienceThreshold
	 *            the experience threshold for subsumption
	 * @param gaMatchSetRunProbability
	 *            the probability of running the GA at the matchset
	 * @param geneticAlgorithm
	 *            the genetic algorithm to be used for evolving
	 * @param thetaDel
	 *            the theta del UCS parameter (deletion age)
	 * @param correctSetTheshold
	 *            Threshold the threshold used to set a rule in the correct set
	 */
	public UCSUpdateAlgorithm(final double alpha, final double nParameter,
			final double acc0, final double learningRate,
			final int experienceThreshold,
			final double gaMatchSetRunProbability,
			final IGeneticAlgorithmStrategy geneticAlgorithm,
			final int thetaDel, final double correctSetTheshold) {
		this.a = alpha;
		this.n = nParameter;
		this.accuracy0 = acc0;
		this.b = learningRate;
		subsumptionExperienceThreshold = experienceThreshold;
		this.matchSetRunProbability = gaMatchSetRunProbability;
		this.ga = geneticAlgorithm;
		deleteAge = thetaDel;
		this.correctSetThreshold = correctSetTheshold;

	}

	/**
	 * Calls covering operator.
	 * 
	 * @param population
	 *            the population where the covering classifier will be added
	 * @param instanceIndex
	 *            the index of the current sample
	 */
	@Override
	public void cover(final ClassifierSet population, final int instanceIndex) {
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
	public final Serializable createStateClassifierObject() {
		return new UCSClassifierData();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy#getComparisonValue
	 * (gr.auth.ee.lcs.classifiers.Classifier, int)
	 */
	@Override
	public final double getComparisonValue(final Classifier aClassifier,
			final int mode) {
		UCSClassifierData data = (UCSClassifierData) aClassifier
				.getUpdateDataObject();

		switch (mode) {
		case COMPARISON_MODE_EXPLORATION:
			final double value = data.fitness
					* (aClassifier.experience < deleteAge ? 0 : 1);
			return Double.isNaN(value) ? 0 : value;
		case COMPARISON_MODE_DELETION:

			if (aClassifier.experience < deleteAge) {
				final double result = data.cs / data.fitness;
				return Double.isNaN(result) ? 1 : result;
			}

			return data.cs;

		case COMPARISON_MODE_EXPLOITATION:
			final double acc = (((double) (data.tp)) / (double) (data.msa));
			final double exploitValue = acc
					* (aClassifier.experience < deleteAge ? 0 : 1);
			return Double.isNaN(exploitValue) ? 0 : exploitValue;
		default:
			return 0;
		}

	}

	@Override
	public final String getData(final Classifier aClassifier) {
		UCSClassifierData data = ((UCSClassifierData) aClassifier
				.getUpdateDataObject());
		return "tp:" + data.tp;
	}

	/**
	 * Perform an update to the set.
	 * 
	 * @param matchSet
	 *            the match set used for the update
	 * @param correctSet
	 *            the correct set used for the update
	 */
	@Override
	public void performUpdate(final ClassifierSet matchSet,
			final ClassifierSet correctSet) {
		performUpdate(matchSet, correctSet, 1);
	}

	public void performUpdate(final ClassifierSet matchSet,
			final ClassifierSet correctSet, int lbl) {
		double strengthSum = 0;
		final int matchSetMacroclassifiers = matchSet
				.getNumberOfMacroclassifiers();
		final int correctSetSize = correctSet.getTotalNumerosity();
		for (int i = 0; i < matchSetMacroclassifiers; i++) {
			final Classifier cl = matchSet.getClassifier(i);
			UCSClassifierData data = ((UCSClassifierData) cl
					.getUpdateDataObject());
			cl.experience++;
			data.msa += 1;

			if (correctSet.getClassifierNumerosity(cl) > 0) {
				data.cs = data.cs + b * (correctSetSize - data.cs);
				data.tp += 1;
				final double accuracy = ((double) data.tp)
						/ ((double) data.msa);
				if (accuracy > accuracy0) {
					data.fitness0 = 1;

					// Check subsumption
					if (cl.experience >= this.subsumptionExperienceThreshold)
						cl.setSubsumptionAbility(true);

				} else {
					data.fitness0 = a * Math.pow(accuracy / accuracy0, n);
					cl.setSubsumptionAbility(false);
				}

				strengthSum += data.fitness0
						* matchSet.getClassifierNumerosity(i);
			} else {
				data.fp += 1;
				data.fitness0 = 0;
			}

		}

		// Fix for avoiding problems...
		if (strengthSum == 0)
			strengthSum = 1;

		strengthSum /= lbl;

		// double fitnessSum = 0;
		final int msSize = matchSet.getNumberOfMacroclassifiers();
		for (int i = 0; i < msSize; i++) {
			Classifier cl = matchSet.getClassifier(i);
			UCSClassifierData data = ((UCSClassifierData) cl
					.getUpdateDataObject());
			data.fitness += b * (data.fitness0 / strengthSum - data.fitness);// TODO:
																				// Something
																				// else?
			// fitnessSum += data.fitness * matchSet.getClassifierNumerosity(i);
		}

	}

	@Override
	public final void setComparisonValue(final Classifier aClassifier,
			final int mode, final double comparisonValue) {
		UCSClassifierData data = ((UCSClassifierData) aClassifier
				.getUpdateDataObject());
		data.fitness = comparisonValue;
	}

	/**
	 * Generates the correct set.
	 * 
	 * @param matchSet
	 *            the match set
	 * @param instanceIndex
	 *            the global instance index
	 * @return the correct set
	 */
	private ClassifierSet generateCorrectSet(final ClassifierSet matchSet,
			final int instanceIndex) {
		ClassifierSet correctSet = new ClassifierSet(null);
		final int matchSetSize = matchSet.getNumberOfMacroclassifiers();
		for (int i = 0; i < matchSetSize; i++) {
			Macroclassifier cl = matchSet.getMacroclassifier(i);
			if (cl.myClassifier.classifyCorrectly(instanceIndex) >= correctSetThreshold)
				correctSet.addClassifier(cl, false);
		}
		return correctSet;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy#updateSet(gr.auth
	 * .ee.lcs.classifiers.ClassifierSet,
	 * gr.auth.ee.lcs.classifiers.ClassifierSet) Updates the set setA is the
	 * match set setB is the correct set
	 */
	@Override
	protected final void updateSet(final ClassifierSet population,
			final ClassifierSet matchSet, final int instanceIndex) {
		/*
		 * Generate correct set
		 */
		ClassifierSet correctSet = generateCorrectSet(matchSet, instanceIndex);

		/*
		 * Cover if necessary
		 */
		if (correctSet.getNumberOfMacroclassifiers() == 0) {
			cover(population, instanceIndex);
			return;
		}

		/*
		 * Update
		 */
		performUpdate(
				matchSet,
				correctSet,
				ClassifierTransformBridge.getInstance().getDataInstanceLabels(
						ClassifierTransformBridge.instances[instanceIndex]).length);

		/*
		 * Run GA
		 */
		if (Math.random() < matchSetRunProbability) {
			ga.evolveSet(matchSet, population);
		} else {
			ga.evolveSet(correctSet, population);
		}

	}

}
