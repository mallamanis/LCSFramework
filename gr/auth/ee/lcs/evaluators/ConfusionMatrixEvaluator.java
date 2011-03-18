/**
 * 
 */
package gr.auth.ee.lcs.evaluators;

import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.data.ClassifierTransformBridge;
import gr.auth.ee.lcs.data.IEvaluator;
import weka.classifiers.evaluation.ConfusionMatrix;

/**
 * @author Miltos Allamanis
 * 
 */
public class ConfusionMatrixEvaluator implements IEvaluator {

	/**
	 * A weka confusion matrix.
	 */
	private ConfusionMatrix conf;

	/**
	 * The instances.
	 */
	private double[][] instances;

	public ConfusionMatrixEvaluator(String[] classNames, double[][] set) {
		conf = new ConfusionMatrix(classNames);
		this.instances = set;
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

		for (int i = 0; i < instances.length; i++) {
			final int[] classes = bridge.classify(classifiers, instances[i]);
			if (classes == null)
				continue; // TODO: Use majority
			final int x = classes[0];
			final int y = bridge.getDataInstanceLabels(instances[i])[0];
			conf.setElement(x, y, conf.getElement(x, y) + 1);
		}

		System.out.println(conf);
		System.out.println("Error rate: " + conf.errorRate());

		return conf.errorRate();
	}

}
