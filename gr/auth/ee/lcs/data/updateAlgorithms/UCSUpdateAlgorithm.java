/**
 * 
 */
package gr.auth.ee.lcs.data.updateAlgorithms;

import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy;

import java.io.Serializable;

/**
 * A UCS implementation with fitness sharing.
 * 
 * @author Miltos Allamanis
 */
public class UCSUpdateAlgorithm extends UpdateAlgorithmFactoryAndStrategy {

	/**
	 * Private variables: the UCS parameter sharing. accuracy0 is considered the
	 * subsumption fitness threshold
	 */
	private final double a, accuracy0, n, b;

	/**
	 * The experience threshold for subsumption.
	 */
	private final int subsumptionExperienceThreshold;

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
	 * 
	 * @param fitnessThreshold
	 *            the fitness threshold for subsumption
	 * @param experienceThreshold
	 *            the experience threshold for subsumption
	 */
	public UCSUpdateAlgorithm(final double alpha, final double nParameter,
			final double acc0, final double learningRate,
			final int experienceThreshold) {
		this.a = alpha;
		this.n = nParameter;
		this.accuracy0 = acc0;
		this.b = learningRate;
		subsumptionExperienceThreshold = experienceThreshold;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy#getComparisonValue
	 * (gr.auth.ee.lcs.classifiers.Classifier, int)
	 */
	@Override
	public double getComparisonValue(final Classifier aClassifier,
			final int mode) {
		UCSClassifierData data = (UCSClassifierData) aClassifier
				.getUpdateDataObject();

		switch (mode) {
		case COMPARISON_MODE_EXPLORATION:
			return data.fitness * (aClassifier.experience < 8 ? 0 : 1);
		case COMPARISON_MODE_DELETION:
			return data.fitness
					* ((aClassifier.experience < 20) ? 100. : Math.exp(-(Double
							.isNaN(data.ns) ? 1 : data.ns) + 1)) // TODO:
																	// Correct
																	// ns
					* (((aClassifier.getCoverage() == 0) && (aClassifier.experience == 1)) ? 0.
							: 1);
			// TODO: Something else?
		case COMPARISON_MODE_EXPLOITATION:
			return (((double) (data.tp)) / (double) (data.msa));
		}
		return 0;
	}

	public String getData(Classifier aClassifier) {
		UCSClassifierData data = ((UCSClassifierData) aClassifier
				.getUpdateDataObject());
		return "tp:" + data.tp;
	}

	@Override
	public void setComparisonValue(Classifier aClassifier, int mode,
			double comparisonValue) {
		UCSClassifierData data = ((UCSClassifierData) aClassifier
				.getUpdateDataObject());
		data.fitness = comparisonValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy#
	 * createStateClassifierObject()
	 */
	@Override
	protected Serializable createStateClassifierObject() {
		return new UCSClassifierData();
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
	protected final void updateSet(final ClassifierSet matchSet,
			final ClassifierSet correctSet) {
		double strengthSum = 0;
		for (int i = 0; i < matchSet.getNumberOfMacroclassifiers(); i++) {
			Classifier cl = matchSet.getClassifier(i);
			UCSClassifierData data = ((UCSClassifierData) cl
					.getUpdateDataObject());
			cl.experience++;
			data.msa += 1;
			if (correctSet.getClassifierNumerosity(cl) > 0) {
				data.tp += 1;
				double accuracy = ((double) data.tp) / ((double) data.msa);
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

		for (int i = 0; i < matchSet.getNumberOfMacroclassifiers(); i++) {
			Classifier cl = matchSet.getClassifier(i);
			UCSClassifierData data = ((UCSClassifierData) cl
					.getUpdateDataObject());
			data.fitness += b * (data.fitness0 / strengthSum - data.fitness);
		}

	}

	class UCSClassifierData implements Serializable {

		/**
		 * Serial code for serialization
		 */
		private static final long serialVersionUID = 3098073593334379507L;

		/**
		 *
		 */
		private double fitness = .5;

		/**
		 * niche set size estimation.
		 */
		private double ns = 1;

		/**
		 * Match Set Appearances.
		 */
		private int msa = 1;

		/**
		 * true positives.
		 */
		private int tp = 1;

		/**
		 * false positives.
		 */
		private int fp = 0;

		/**
		 * Strength.
		 */
		private double fitness0 = 0;

	}

}
