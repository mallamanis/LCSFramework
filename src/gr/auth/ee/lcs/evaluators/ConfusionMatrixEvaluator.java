/*
 *	Copyright (C) 2011 by Allamanis Miltiadis
 *
 *	Permission is hereby granted, free of charge, to any person obtaining a copy
 *	of this software and associated documentation files (the "Software"), to deal
 *	in the Software without restriction, including without limitation the rights
 *	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *	copies of the Software, and to permit persons to whom the Software is
 *	furnished to do so, subject to the following conditions:
 *
 *	The above copyright notice and this permission notice shall be included in
 *	all copies or substantial portions of the Software.
 *
 *	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *	THE SOFTWARE.
 */
/**
 * 
 */
package gr.auth.ee.lcs.evaluators;

import gr.auth.ee.lcs.AbstractLearningClassifierSystem;
import gr.auth.ee.lcs.data.ClassifierTransformBridge;
import gr.auth.ee.lcs.data.ILCSMetric;
import weka.classifiers.evaluation.ConfusionMatrix;

/**
 * @author Miltos Allamanis
 * 
 */
public final class ConfusionMatrixEvaluator implements ILCSMetric {

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
	public double getMetric(final AbstractLearningClassifierSystem theLcs) {
		final ClassifierTransformBridge bridge = myLcs
				.getClassifierTransformBridge();

		for (int i = 0; i < instances.length; i++) {
			final int[] classes = bridge.classify(theLcs.getRulePopulation(),
					instances[i]);
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

	public String getMetricName() {
		return "Confusion Matrix";
	}

}
