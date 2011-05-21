package gr.auth.ee.lcs.data;

import gr.auth.ee.lcs.classifiers.ClassifierSet;

/**
 * A classification strategy interface.
 * 
 * @author Miltos Allamanis
 * 
 */
public interface IClassificationStrategy {
	/**
	 * Classify a given vision vector with a given set of classifiers.
	 * 
	 * @param aSet
	 *            the set of classifiers used at the classification
	 * @param visionVector
	 *            the vision vector of the instance to be classified
	 * @return an integer array containing the labels/ classes that the instance
	 *         has been classified in
	 */
	int[] classify(ClassifierSet aSet, double[] visionVector);

	void setThreshold(double threshold);
}