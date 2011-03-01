/**
 * 
 */
package gr.auth.ee.lcs.data.updateAlgorithms;

import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.data.updateAlgorithms.data.GenericSLCSClassifierData;

/**
 * @author Miltos Allamanis
 * @deprecated it's creation...
 * 
 */
@Deprecated
public class XSLCSUpdateAlgorithm extends AbstractSLCSUpdateAlgorithm {

	private final double beta = 0.1;

	public XSLCSUpdateAlgorithm(double subsumptionFitness,
			int subsumptionExperience) {
		super(subsumptionFitness, subsumptionExperience);

	}

	@Override
	public double getComparisonValue(Classifier aClassifier, int mode) {
		GenericSLCSClassifierData data = (GenericSLCSClassifierData) aClassifier.updateData;
		;
		switch (mode) {
		case COMPARISON_MODE_DELETION:
			return data.fitness / data.ns; // TODO: Something else?
		case COMPARISON_MODE_EXPLOITATION:
			return (((double) (data.tp)) / (double) (data.msa));
		case COMPARISON_MODE_EXPLORATION:
			return data.fitness;
		}
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.data.AbstractSLCSUpdateAlgorithm#updateFitness(gr.auth
	 * .ee.lcs.classifiers.Classifier, int,
	 * gr.auth.ee.lcs.classifiers.ClassifierSet)
	 */
	@Override
	public void updateFitness(Classifier aClassifier, int numerosity,
			ClassifierSet correctSet) {
		GenericSLCSClassifierData data = ((GenericSLCSClassifierData) aClassifier.updateData);
		if (correctSet.getClassifierNumerosity(aClassifier) > 0) { // aClassifier
																	// belongs
																	// to
																	// correctSet
			data.tp++;

			float acc = ((float) (data.tp)) / ((float) data.msa);

			data.str += beta * (acc - data.str);

			data.fp += (beta * (Math.abs(acc - data.fp) - data.fp)) * 100;

			data.fitness = 1 / data.ns / (data.fp + .01);

		}
	}

}
