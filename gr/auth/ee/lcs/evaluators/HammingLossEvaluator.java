/**
 * 
 */
package gr.auth.ee.lcs.evaluators;

import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import weka.core.Instances;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.data.ClassifierTransformBridge;
import gr.auth.ee.lcs.data.IEvaluator;

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
	private final Instances instanceSet;

	/**
	 * A boolean indicating if the evaluator is going to print the results.
	 */
	private final boolean printResults;
	
	/**
	 * The number of labels used.
	 */
	private final int numberOfLabels;

	public HammingLossEvaluator(final Instances instances, final boolean print, final int numOfLabels) {
		instanceSet = instances;
		printResults = print;
		numberOfLabels = numOfLabels;
	}

	public HammingLossEvaluator(final String arffFileName, final boolean print, final int numOfLabels)
			throws IOException {
		printResults = print;
		FileReader reader = new FileReader(arffFileName);
		instanceSet = new Instances(reader);
		numberOfLabels = numOfLabels;
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
		final ClassifierTransformBridge bridge = ClassifierTransformBridge
				.getInstance();
		int numberOfSymmetricDifferences = 0;
		for (int i = 0; i < instanceSet.numInstances(); i++) {
			final double[] instance = new double[instanceSet.numAttributes()];
			for (int j = 0; j < instanceSet.numAttributes(); j++) {
				instance[j] = instanceSet.instance(i).value(j);
			}
			final int[] classes = bridge.classify(classifiers, instance);
			final int[] classification = bridge.getDataInstanceLabels(instance);

			// Find symetric differences
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
				/ ((double) (instanceSet.numInstances() * numberOfLabels));
		if (printResults)
			System.out.println("Hamming Loss: " + hammingLoss);
		return hammingLoss;
	}

}
