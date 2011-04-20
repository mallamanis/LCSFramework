/**
 * 
 */
package gr.auth.ee.lcs.evaluators;

import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.data.IEvaluator;
import weka.core.Instances;

/**
 * All label single evaluator. The class acts as a wrapper against single label
 * evaluator.
 * 
 * @author Miltos Allamanis
 * 
 */
public class AllSingleLabelEvaluator implements IEvaluator {

	/**
	 * Single label evaluators.
	 */
	private final SingleLabelEvaluator[] evaluators;

	/**
	 * Boolean indicating if we are going to print results
	 */
	private final boolean print;

	/**
	 * 
	 * @param evaluateSet
	 * @param numberOfLabels
	 * @param printResults
	 */
	public AllSingleLabelEvaluator(Instances evaluateSet, int numberOfLabels,
			boolean printResults) {		
		print = printResults;
		evaluators = new SingleLabelEvaluator[numberOfLabels];
		for (int i = 0; i < numberOfLabels; i++)
			evaluators[i] = new SingleLabelEvaluator(i, evaluateSet);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.data.IEvaluator#evaluateSet(gr.auth.ee.lcs.classifiers
	 * .ClassifierSet)
	 */
	@Override
	public double evaluateSet(ClassifierSet classifiers) {
		double sum = 0;
		for (int i = 0; i < evaluators.length; i++) {
			final double result = evaluators[i].evaluateSet(classifiers);
			if (print) {
				System.out.println("Label " + i + " exact match:" + result);
			}
			sum += result;
		}
		return sum / ((double) evaluators.length);
	}

}
