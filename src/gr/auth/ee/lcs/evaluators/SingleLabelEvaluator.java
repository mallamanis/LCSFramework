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
	 * The LCS instance being used.
	 */
	private final AbstractLearningClassifierSystem myLcs;

	/**
	 * Constructor.
	 * 
	 * @param labelIndex
	 *            the label's index that is under evaluation
	 * @param evaluateSet
	 *            the set to evaluate the label on
	 * @param lcs
	 *            the LCS instance used
	 */
	public SingleLabelEvaluator(final int labelIndex,
			final Instances evaluateSet, AbstractLearningClassifierSystem lcs) {
		label = labelIndex;
		instanceSet = evaluateSet;
		myLcs = lcs;
	}

	@Override
	public final double evaluateLCS(final AbstractLearningClassifierSystem lcs) {
		final ClassifierTransformBridge bridge = myLcs
				.getClassifierTransformBridge();
		int tp = 0;
		final double[][] instances = InstanceToDoubleConverter
				.convert(instanceSet);

		for (int i = 0; i < instances.length; i++) {

			final int[] classes = lcs.classifyInstance(instances[i]);
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
