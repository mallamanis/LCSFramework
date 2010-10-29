package gr.auth.ee.lcs.geneticalgorithm;

import gr.auth.ee.lcs.classifiers.Classifier;

/**
 * An interface for representing all binary operators on classifiers
 * @author Miltos Allamanis
 *
 */
public interface IBinaryGeneticOperator {

	/**
	 * The operation of the operator
	 * @param classifierA the first argument of the binary classifier
	 * @param classifierB the second argument of the binary classifier
	 * @return the result of the operator
	 */
	public Classifier operate(Classifier classifierA, Classifier classifierB);

}