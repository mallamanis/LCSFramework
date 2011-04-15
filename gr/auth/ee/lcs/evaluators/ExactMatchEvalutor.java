/**
 * 
 */
package gr.auth.ee.lcs.evaluators;

import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.data.ClassifierTransformBridge;
import gr.auth.ee.lcs.data.IEvaluator;

import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import weka.core.Instances;

/**
 * An evaluator using an Weka Instance.
 * 
 * @author Miltos Allamanis
 * 
 */
public class ExactMatchEvalutor implements IEvaluator {

	/**
	 * The set of instances to evaluate on.
	 */
	private final Instances instanceSet;

	/**
	 * A boolean indicating if the evaluator is going to print the results.
	 */
	private final boolean printResults;

	/**
	 * The constructor.
	 * 
	 * @param instances
	 *            the set of instances to evaluate on
	 * @param print
	 *            true to turn printing on
	 */
	public ExactMatchEvalutor(final Instances instances, final boolean print) {
		instanceSet = instances;
		printResults = print;
	}

	/**
	 * Construct an evaluator using an .arff test file.
	 * 
	 * @param arffFileName
	 *            the name of the test file
	 * @param print
	 *            true to turn printing on
	 * @throws IOException
	 *             when file not found
	 */
	public ExactMatchEvalutor(final String arffFileName, final boolean print)
			throws IOException {
		printResults = print;
		FileReader reader = new FileReader(arffFileName);
		instanceSet = new Instances(reader);
	}

	@Override
	public final double evaluateSet(final ClassifierSet classifiers) {
		final ClassifierTransformBridge bridge = ClassifierTransformBridge
				.getInstance();

		int tp = 0, fp = 0;
		for (int i = 0; i < instanceSet.numInstances(); i++) {
			final double[] instance = new double[instanceSet.numAttributes()];
			for (int j = 0; j < instanceSet.numAttributes(); j++) {
				instance[j] = instanceSet.instance(i).value(j);
			}
			final int[] classes = bridge.classify(classifiers, instance);
			final int[] classification = bridge.getDataInstanceLabels(instance);
			if (Arrays.equals(classes, classification))
				tp++;
			else
				fp++;

		}

		final double errorRate = ((double) fp) / ((double) (fp + tp));

		if (printResults) {
			System.out.println("tp:" + tp + " fp:" + fp + " errorRate:"
					+ errorRate + " total instances:"
					+ instanceSet.numInstances());
		}
		return errorRate;
	}

}
