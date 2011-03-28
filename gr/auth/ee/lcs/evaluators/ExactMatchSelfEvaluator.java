/**
 * 
 */
package gr.auth.ee.lcs.evaluators;

import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.data.ClassifierTransformBridge;
import gr.auth.ee.lcs.data.IEvaluator;

import java.util.Arrays;

/**
 * An Accuracy Evaluator. Accuracy is considered a binary value (0/1)
 * 
 * @author Miltos Allamanis
 * 
 */
public class ExactMatchSelfEvaluator implements IEvaluator {

	/**
	 * A boolean indicating if the evaluator is going to print the results.
	 */
	private final boolean printResults;

	/**
	 * A boolean indicating if the evaluator is going to print the confusion
	 * matrix.
	 */
	private final boolean printConfusionMatrix;

	/**
	 * Constructor for setting evaluator parameters.
	 * 
	 * @param print
	 *            true if evaluator will print data
	 * @param confusionMatrix
	 *            true if evaluator is going to print confusion matrix
	 */
	public ExactMatchSelfEvaluator(final boolean print,
			final boolean confusionMatrix) {
		printResults = print;
		printConfusionMatrix = confusionMatrix;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.data.IEvaluator#evaluateSet(gr.auth.ee.lcs.classifiers
	 * .ClassifierSet)
	 */
	@Override
	public double evaluateSet(ClassifierSet population) {
		ClassifierTransformBridge bridge = ClassifierTransformBridge
				.getInstance();

		int tp = 0, fp = 0;
		for (int i = 0; i < ClassifierTransformBridge.instances.length; i++) { // for
																				// each
																				// instance
			final int[] classes = bridge.classify(population,
					ClassifierTransformBridge.instances[i]);
			final int[] classification = bridge
					.getDataInstanceLabels(ClassifierTransformBridge.instances[i]);
			if (Arrays.equals(classes, classification))
				tp++;
			else
				fp++;
		}

		double errorRate = ((double) fp) / ((double) (fp + tp));

		if (printResults) {
			System.out.println("Exact Match: tp:" + tp + " fp:" + fp
					+ " errorRate:" + errorRate + " total instances:"
					+ ClassifierTransformBridge.instances.length);
		}
		return errorRate;
	}

}
