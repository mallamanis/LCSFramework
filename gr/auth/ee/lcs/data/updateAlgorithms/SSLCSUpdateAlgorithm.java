package gr.auth.ee.lcs.data.updateAlgorithms;

import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ClassifierSet;

/**
 * SS-LCS Update Algorithm.
 * 
 * @author Miltos Allamanis
 */
public class SSLCSUpdateAlgorithm extends AbstractSLCSUpdateAlgorithm {

	/**
	 * Reward and penalty percentage parameters.
	 */
	private final double strengthReward, penalty;

	/**
	 * Constructor of update algorithm.
	 * 
	 * @param reward
	 *            the reward a correct classifier will be given on correct
	 *            classification
	 * @param penaltyPercent
	 *            the percentage of the reward that the classifier's penalty
	 *            will be when failing to classify
	 * 
	 * @param fitnessThreshold
	 *            the fitness threshold for subsumption
	 * @param experienceThreshold
	 *            the experience threshold for subsumption
	 */
	public SSLCSUpdateAlgorithm(final double reward,
			final double penaltyPercent, final double fitnessThreshold,
			final int experienceThreshold) {
		super(fitnessThreshold, experienceThreshold);
		strengthReward = reward;
		penalty = penaltyPercent;
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
		SLCSClassifierData data = (SLCSClassifierData) aClassifier
				.getUpdateDataObject();

		switch (mode) {
		case COMPARISON_MODE_DELETION:
			// TODO: Something else?
			return data.fitness / data.ns;
		case COMPARISON_MODE_EXPLOITATION:
			return data.str;
		case COMPARISON_MODE_EXPLORATION:
			return data.fitness;
		default:
			return 0;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gr.auth.ee.lcs.data.updateAlgorithms.AbstractSLCSUpdateAlgorithm#
	 * updateFitness(gr.auth.ee.lcs.classifiers.Classifier, int,
	 * gr.auth.ee.lcs.classifiers.ClassifierSet)
	 */
	@Override
	public final void updateFitness(final Classifier aClassifier,
			final int numerosity, final ClassifierSet correctSet) {
		SLCSClassifierData data = ((SLCSClassifierData) aClassifier
				.getUpdateDataObject());

		if (correctSet.getClassifierNumerosity(aClassifier) > 0) {
			// aClassifier belongs to correctSet
			data.tp++;
			data.str += strengthReward / correctSet.getTotalNumerosity();
		} else {
			data.fp++;
			data.str -= penalty * strengthReward / data.ns;
		}

		data.fitness = (data.str / data.msa);

	}

}