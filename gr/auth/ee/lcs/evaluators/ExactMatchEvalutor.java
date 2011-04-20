/**
 * 
 */
package gr.auth.ee.lcs.evaluators;

import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.data.ClassifierTransformBridge;
import gr.auth.ee.lcs.data.IEvaluator;
import gr.auth.ee.lcs.utilities.InstanceToDoubleConverter;

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
	private final double[][] instances;

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
		this.instances = InstanceToDoubleConverter.convert(instances);
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
		this.instances = InstanceToDoubleConverter.convert(new Instances(reader));
	}
	
	/**
	 * Constructor using a double array
	 * @param instances the double[][] of instances
	 * @param print true to turn printing on
	 */
	public ExactMatchEvalutor(final double[][] instances, final boolean print) {
		printResults = print;
		this.instances = instances;
	}

	@Override
	public final double evaluateSet(final ClassifierSet classifiers) {
		final ClassifierTransformBridge bridge = ClassifierTransformBridge
				.getInstance();

		int tp = 0, fp = 0;
		for (int i = 0; i < instances.length; i++) {			
			final int[] classes = bridge.classify(classifiers, instances[i]);
			final int[] classification = bridge.getDataInstanceLabels(instances[i]);
			if (Arrays.equals(classes, classification))
				tp++;
			else
				fp++;

		}

		final double errorRate = ((double) fp) / ((double) (fp + tp));

		if (printResults) {
			System.out.println("tp:" + tp + " fp:" + fp + " errorRate:"
					+ errorRate + " total instances:"
					+ instances.length);
		}
		return errorRate;
	}

}
