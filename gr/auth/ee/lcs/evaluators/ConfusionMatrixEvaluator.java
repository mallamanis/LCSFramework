/**
 * 
 */
package gr.auth.ee.lcs.evaluators;

import gr.auth.ee.lcs.AbstractLearningClassifierSystem;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.data.ClassifierTransformBridge;
import gr.auth.ee.lcs.data.IEvaluator;
import weka.classifiers.evaluation.ConfusionMatrix;

/**
 * @author Miltos Allamanis
 * 
 */
public final class ConfusionMatrixEvaluator implements IEvaluator {

	/**
	 * A Weka confusion matrix.
	 */
	private final ConfusionMatrix conf;

	/**
	 * The LCS instance being used.
	 */
	private final AbstractLearningClassifierSystem myLcs;

	/**
	 * The instances.
	 */
	private final double[][] instances;

	/**
	 * Constructor for creating a confusion matrix.
	 * 
	 * @param classNames
	 *            the class names
	 * @param set
	 *            the set of instances to be used for creating the confusion
	 *            matrix
	 * @param lcs
	 *            the LCS instance used
	 */
	public ConfusionMatrixEvaluator(final String[] classNames,
			final double[][] set, final AbstractLearningClassifierSystem lcs) {
		conf = new ConfusionMatrix(classNames);
		this.instances = set;
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
	public double evaluateSet(final ClassifierSet classifiers) {
		final ClassifierTransformBridge bridge = myLcs
				.getClassifierTransformBridge();

		for (int i = 0; i < instances.length; i++) {
			final int[] classes = bridge.classify(classifiers, instances[i]);
			if (classes == null)
				continue; // TODO: Use majority
			final int y = classes[0];
			final int x = bridge.getDataInstanceLabels(instances[i])[0];
			conf.setElement(x, y, conf.getElement(x, y) + 1);
		}

		System.out.println(conf);
		System.out.println("Error rate: " + conf.errorRate());

		return conf.errorRate();
	}

}
