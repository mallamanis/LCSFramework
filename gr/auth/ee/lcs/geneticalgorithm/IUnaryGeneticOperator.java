package gr.auth.ee.lcs.geneticalgorithm;

import gr.auth.ee.lcs.classifiers.Classifier;

/**
 * The interface of a unary genetic operator
 * @author Miltos Allamanis
 *
 */
public interface IUnaryGeneticOperator {

	/**
	 * The operation of the operator
	 * @param aClassifier the classifier the operator will operate on
	 * @return the new genetic operator
	 */
	public Classifier operate(Classifier aClassifier);

}