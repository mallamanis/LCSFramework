/**
 * 
 */
package gr.auth.ee.lcs.evaluators;

import gr.auth.ee.lcs.AbstractLearningClassifierSystem;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.data.ClassifierTransformBridge;
import gr.auth.ee.lcs.data.IEvaluator;
import gr.auth.ee.lcs.utilities.InstanceToDoubleConverter;

import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import weka.core.Instances;

/**
 * Evaluates a ruleset on an instance set using the hamming loss metric.
 * 
 * @author Miltos Allamanis
 * 
 */
public class HammingLossEvaluator implements IEvaluator {

	/**
	 * The set of instances to evaluate on.
	 */
	private final double instances[][];

	/**
	 * A boolean indicating if the evaluator is going to print the results.
	 */
	private final boolean printResults;

	/**
	 * The number of labels used.
	 */
	private final int numberOfLabels;

	/**
	 * The LCS instance being used.
	 */
	private final AbstractLearningClassifierSystem myLcs;

	/**
	 * Constructor of the hamming loss evaluator.
	 * 
	 * @param instances
	 *            the set of Weka instances
	 * @param print
	 *            true to print results to stdout during evaluation
	 * @param lcs
	 *            the LCS instance used
	 * @param numOfLabels
	 *            the number of labels
	 */
	public HammingLossEvaluator(final Instances instances, final boolean print,
			final int numOfLabels, final AbstractLearningClassifierSystem lcs) {
		this.instances = InstanceToDoubleConverter.convert(instances);
		printResults = print;
		numberOfLabels = numOfLabels;
		myLcs = lcs;
	}

	/**
	 * Constructor of evaluator.
	 * 
	 * @param arffFileName
	 *            the data file input
	 * @param print
	 *            true to print results to stdout
	 * @param numOfLabels
	 *            the number of labels for the problem
	 * @param lcs
	 *            the LCS instance used
	 * @throws IOException
	 *             if file is not found
	 */
	public HammingLossEvaluator(final String arffFileName, final boolean print,
			final int numOfLabels, AbstractLearningClassifierSystem lcs)
			throws IOException {
		printResults = print;
		FileReader reader = new FileReader(arffFileName);
		this.instances = InstanceToDoubleConverter
				.convert(new Instances(reader));
		numberOfLabels = numOfLabels;
		myLcs = lcs;
	}

	/**
	 * Constructor using double[][] of instances.
	 * 
	 * @param instances
	 *            the instances
	 * @param print
	 *            true to turn printing on
	 * @param numOfLabels
	 *            the number of labels
	 * @param lcs
	 *            the LCS instance used
	 */
	public HammingLossEvaluator(final double[][] instances,
			final boolean print, final int numOfLabels,
			final AbstractLearningClassifierSystem lcs) {
		this.instances = instances;
		printResults = print;
		numberOfLabels = numOfLabels;
		myLcs = lcs;
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
		final ClassifierTransformBridge bridge = myLcs
				.getClassifierTransformBridge();
		int numberOfSymmetricDifferences = 0;
		for (int i = 0; i < instances.length; i++) {

			final int[] classes = bridge.classify(classifiers, instances[i]);
			final int[] classification = bridge
					.getDataInstanceLabels(instances[i]);

			// Find symmetric differences
			Arrays.sort(classes);
			Arrays.sort(classification);
			for (int j = 0; j < classes.length; j++) {
				if (Arrays.binarySearch(classification, classes[j]) < 0)
					numberOfSymmetricDifferences++;
			}
			for (int j = 0; j < classification.length; j++) {
				if (Arrays.binarySearch(classes, classification[j]) < 0)
					numberOfSymmetricDifferences++;
			}
		}
		final double hammingLoss = ((double) numberOfSymmetricDifferences)
				/ ((double) (instances.length * numberOfLabels));
		if (printResults)
			System.out.println("Hamming Loss: " + hammingLoss);
		return hammingLoss;
	}

}
