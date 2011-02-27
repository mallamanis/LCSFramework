/**
 * 
 */
package gr.auth.ee.lcs.data.updateAlgorithms;

import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy;
import gr.auth.ee.lcs.data.updateAlgorithms.data.GenericSLCSClassifierData;

import java.io.Serializable;

/**
 * A UCS implementation with fitness sharing.
 * 
 * @author Miltos Allamanis
 */
public class UCSUpdateAlgorithm extends UpdateAlgorithmFactoryAndStrategy {

	/**
	 * Private variables: the UCS parameter sharing.
	 */
	private double a, acc0, n, b;

	/**
	 * Default constructor.
	 * @param alpha used in fitness sharing
	 * @param n used in fitness sharing
	 * @param acc0 used in fitness sharing: the minimum "good" accuracy
	 * @param learningRate the beta of UCS 
	 */
	public UCSUpdateAlgorithm(final double alpha, final double n, final double acc0,
			final double learningRate) {
		this.a = alpha;
		this.n = n;
		this.acc0 = acc0;
		this.b = learningRate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy#
	 * createStateClassifierObject()
	 */
	@Override
	protected Serializable createStateClassifierObject() {
		return new GenericSLCSClassifierData();
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
	protected void updateSet(final ClassifierSet matchSet, final ClassifierSet correctSet) {
		double strengthSum = 0;
		for (int i = 0; i < matchSet.getNumberOfMacroclassifiers(); i++) {
			Classifier cl = matchSet.getClassifier(i);
			GenericSLCSClassifierData data = ((GenericSLCSClassifierData) cl.updateData);

			data.msa += 1;
			if (correctSet.getClassifierNumerosity(cl) > 0) {
				data.tp += 1;
				double accuracy = ((double) data.tp) / ((double) data.msa);
				if (accuracy > acc0) {
					data.str = 1;
				} else {
					data.str = a * Math.pow(accuracy / acc0, n);
				}

				strengthSum += data.str * matchSet.getClassifierNumerosity(i);
			} else {
				data.fp += 1;
				data.str = 0;
			}

			this.updateSubsumption(cl);
			cl.experience++;
		}

		// Fix for avoiding problems...
		if (strengthSum == 0)
			strengthSum = 1;

		for (int i = 0; i < matchSet.getNumberOfMacroclassifiers(); i++) {
			Classifier cl = matchSet.getClassifier(i);
			GenericSLCSClassifierData data = ((GenericSLCSClassifierData) cl.updateData);
			cl.fitness += b * (data.str / strengthSum - cl.fitness);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy#getComparisonValue
	 * (gr.auth.ee.lcs.classifiers.Classifier, int)
	 */
	@Override
	public double getComparisonValue(final Classifier aClassifier, final int mode) {
		GenericSLCSClassifierData data;
		switch (mode) {
		case COMPARISON_MODE_EXPLORATION:
			return aClassifier.fitness * (aClassifier.experience < 8 ? 0 : 1);
		case COMPARISON_MODE_DELETION:
			data = (GenericSLCSClassifierData) aClassifier.updateData;
			return aClassifier.fitness
					* ((aClassifier.experience < 20) ? 100. : Math.exp(-(Double
							.isNaN(data.ns) ? 1 : data.ns) + 1))
					* (((aClassifier.getCoverage() == 0) && aClassifier.experience == 1) ? 0.
							: 1);
			// TODO: Something else?
		case COMPARISON_MODE_EXPLOITATION:
			data = (GenericSLCSClassifierData) aClassifier.updateData;
			return (((double) (data.tp)) / (double) (data.msa));
		}
		return 0;
	}

}
