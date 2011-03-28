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
 * An evaluator to evaluate on the accuracy of the classifiers
 * @author Miltos Allamanis
 *
 */
public class AccuracyEvaluator implements IEvaluator {

	/**
	 * The set of instances to evaluate on.
	 */
	private final Instances instanceSet;

	/**
	 * A boolean indicating if the evaluator is going to print the results.
	 */
	private final boolean printResults;
	
	public AccuracyEvaluator(final Instances instances, final boolean print) {
		instanceSet = instances;
		printResults = print;
	}

	public AccuracyEvaluator(final String arffFileName, final boolean print)
			throws IOException {
		printResults = print;
		FileReader reader = new FileReader(arffFileName);
		instanceSet = new Instances(reader);
	}
	
	/* (non-Javadoc)
	 * @see gr.auth.ee.lcs.data.IEvaluator#evaluateSet(gr.auth.ee.lcs.classifiers.ClassifierSet)
	 */
	@Override
	public double evaluateSet(ClassifierSet classifiers) {
		final ClassifierTransformBridge bridge = ClassifierTransformBridge
		.getInstance();
		double sumOfAccuracies = 0;
		double sumOfRecall = 0;
		for (int i = 0; i < instanceSet.numInstances(); i++) {
			int unionOfLabels = 0;
			int intersectionOfLabels = 0;
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
				if (Arrays.binarySearch(classification, classes[j]) < 0) {
					unionOfLabels++;
				} else {
					intersectionOfLabels ++;
					unionOfLabels ++;
				}
			}
			for (int j = 0; j < classification.length; j++) {
				if (Arrays.binarySearch(classes, classification[j]) < 0)
					unionOfLabels++;
			}
			sumOfAccuracies += ((double)intersectionOfLabels) / ((double)unionOfLabels);
			sumOfRecall += ((double)intersectionOfLabels) / ((double) classification.length);
		}
		final double accuracy = sumOfAccuracies / ((double)instanceSet.numInstances());
		final double recall =  sumOfRecall / ((double)instanceSet.numInstances());
		if (printResults){
			System.out.println("Accuracy: " + accuracy);
			System.out.println("Recall: " + recall);
		}
		return accuracy;
	}

}
