package gr.auth.ee.lcs.data.updateAlgorithms;

import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.geneticalgorithm.IGeneticAlgorithmStrategy;

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
	 * 
	 * @param gaMatchSetRunProbability
	 *            the probability to run the GA on the matchset
	 * @param geneticAlgorithm
	 *            the GA to be used for exploration
	 */
	public SSLCSUpdateAlgorithm(final double reward,
			final double penaltyPercent, final double fitnessThreshold,
			final int experienceThreshold, double gaMatchSetRunProbability,
			IGeneticAlgorithmStrategy geneticAlgorithm) {
		super(fitnessThreshold, experienceThreshold, gaMatchSetRunProbability,
				geneticAlgorithm);
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
			return data.fitness * ((data.fitness > 0) ? 1 / data.ns : data.ns);
		case COMPARISON_MODE_EXPLOITATION:
			return ((double) data.tp) / ((double) data.msa);
			// return data.str;
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
		if (Double.isNaN(data.str) || Double.isInfinite(data.str))
			data.str = 0;
		if (correctSet.getClassifierNumerosity(aClassifier) > 0) {
			// aClassifier belongs to correctSet
			data.str += strengthReward / correctSet.getTotalNumerosity();
			data.ns = (data.ns * data.tp + correctSet.getTotalNumerosity())
					/ ((double) (data.tp + 1.));
			data.tp++;
		} else {
			data.fp++;
			final double punishment = penalty * strengthReward / (data.ns);
			data.str -= (Double.isNaN(punishment) || Double
					.isInfinite(punishment)) ? penalty * strengthReward
					: punishment;
		}

		data.fitness = (data.str / data.msa);
		if (Double.isNaN(data.fitness))
			data.fitness = Double.MIN_VALUE;
	}

}