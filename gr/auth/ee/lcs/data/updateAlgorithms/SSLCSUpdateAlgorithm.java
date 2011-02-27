package gr.auth.ee.lcs.data.updateAlgorithms;

import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.data.updateAlgorithms.data.GenericSLCSClassifierData;

/**
 * SS-LCS Update Algorithm.
 * 
 * @author Miltos Allamanis
 */
public class SSLCSUpdateAlgorithm extends AbstractSLCSUpdateAlgorithm {

	/**
	 * Reward and penalty percentage parameters.
	 */
	private double strengthReward, penalty;

	/**
	 * Constructor of update algorithm
	 * 
	 * @param reward
	 *            the reward a correct classifier will be given on correct
	 *            classification
	 * @param penaltyPercent
	 *            the percentage of the reward that the classifier's penalty
	 *            will be when failing to classify
	 */
	public SSLCSUpdateAlgorithm(double reward, double penaltyPercent) {
		strengthReward = reward;
		penalty = penaltyPercent;
	}

	/**
	 * Implementation of abstract method defined in @see
	 * gr.auth.ee.lcs.data.AbstractSLCSUpdateAlgorithm .
	 */
	public void updateFitness(Classifier aClassifier, int numerosity,
			ClassifierSet correctSet) {
		GenericSLCSClassifierData data = ((GenericSLCSClassifierData) aClassifier.updateData);

		if (correctSet.getClassifierNumerosity(aClassifier) > 0) {
			// aClassifier belongs to correctSet
			data.tp++;
			data.str += strengthReward / correctSet.getTotalNumerosity();
		} else {
			data.fp++;
			data.str -= penalty * strengthReward / data.ns;
		}

		aClassifier.fitness = (data.str / data.msa);

	}

	@Override
	public double getComparisonValue(Classifier aClassifier, int mode) {
		GenericSLCSClassifierData data;
		switch (mode) {
		case COMPARISON_MODE_DELETION:
			data = (GenericSLCSClassifierData) aClassifier.updateData;
			// TODO: Something else?
			return aClassifier.fitness / data.ns;
		case COMPARISON_MODE_EXPLOITATION:
			data = (GenericSLCSClassifierData) aClassifier.updateData;
			return data.str;
		case COMPARISON_MODE_EXPLORATION:
			return aClassifier.fitness;
		default:
			return 0;
		}

	}

}