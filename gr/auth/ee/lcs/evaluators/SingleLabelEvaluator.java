/**
 * 
 */
package gr.auth.ee.lcs.evaluators;

import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.data.ClassifierTransformBridge;
import gr.auth.ee.lcs.data.IEvaluator;
import gr.auth.ee.lcs.utilities.InstanceToDoubleConverter;

import java.util.Arrays;

import weka.core.Instances;

/**
 * A single label evaluator.
 * 
 * @author Miltiadis Allamanis
 * 
 */
public class SingleLabelEvaluator implements IEvaluator {
	/**
	 * The set of instances to evaluate on.
	 */
	private final Instances instanceSet;

	/**
	 * The label index of the label under evaluation.
	 */
	private final int label;

	/**
	 * Constructor.
	 * 
	 * @param labelIndex
	 *            the label's index that is under evaluation
	 * @param evaluateSet
	 *            the set to evaluate the label on
	 */
	public SingleLabelEvaluator(final int labelIndex,
			final Instances evaluateSet) {
		label = labelIndex;
		instanceSet = evaluateSet;
	}

	@Override
	public final double evaluateSet(final ClassifierSet classifiers) {
		final ClassifierTransformBridge bridge = ClassifierTransformBridge
				.getInstance();
		int tp = 0;
		double[][] instances = InstanceToDoubleConverter.convert(instanceSet);

		for (int i = 0; i < instances.length; i++) {

			final int[] classes = bridge.classify(classifiers, instances[i]);
			Arrays.sort(classes);

			final int[] classification = bridge
					.getDataInstanceLabels(instances[i]);
			Arrays.sort(classification);

			final boolean classifiedToLabel = Arrays.binarySearch(classes,
					label) >= 0;
			final boolean belongsToLabel = Arrays.binarySearch(classification,
					label) >= 0;
			if (!(classifiedToLabel ^ belongsToLabel))
				tp++;

		}
		return ((double) tp) / ((double) instances.length);
	}

}
