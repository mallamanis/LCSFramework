/**
 * 
 */
package gr.auth.ee.lcs.data;

import gr.auth.ee.lcs.classifiers.ClassifierSet;

/**
 * An evaluator class interface. This interface will be used for evaluation of
 * all LCSs
 * 
 * @author Miltos Allamanis
 * 
 */
public interface IEvaluator {
	/**
	 * Evaluate a set of classifiers.
	 * 
	 * @param classifiers
	 *            the ClassifierSet that we are going to use for evaluation
	 * @return a numeric value indicating ClassifierSet's quality
	 */
	double evaluateSet(ClassifierSet classifiers);
}
