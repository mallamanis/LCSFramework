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
import gr.auth.ee.lcs.utilities.InstancesUtility;

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
	 * The LCS instance being used.
	 */
	private final AbstractLearningClassifierSystem myLcs;

	/**
	 * Constructor using a double array.
	 * 
	 * @param instances
	 *            the double[][] of instances
	 * @param print
	 *            true to turn printing on
	 * @param lcs
	 *            the LCS instance used
	 */
	public ExactMatchEvalutor(final double[][] instances, final boolean print,
			final AbstractLearningClassifierSystem lcs) {
		printResults = print;
		this.instances = instances;
		myLcs = lcs;
	}

	/**
	 * The constructor.
	 * 
	 * @param instances
	 *            the set of instances to evaluate on
	 * @param print
	 *            true to turn printing on
	 * @param lcs
	 *            the LCS instance used
	 */
	public ExactMatchEvalutor(final Instances instances, final boolean print,
			final AbstractLearningClassifierSystem lcs) {
		this.instances = InstancesUtility.convertIntancesToDouble(instances);
		printResults = print;
		myLcs = lcs;
	}

	/**
	 * Construct an evaluator using an .arff test file.
	 * 
	 * @param arffFileName
	 *            the name of the test file
	 * @param print
	 *            true to turn printing on
	 * @param lcs
	 *            the LCS instance used
	 * @throws IOException
	 *             when file not found
	 */
	public ExactMatchEvalutor(final String arffFileName, final boolean print,
			final AbstractLearningClassifierSystem lcs) throws IOException {
		printResults = print;

		this.instances = InstancesUtility
				.convertIntancesToDouble(InstancesUtility
						.openInstance(arffFileName));
		myLcs = lcs;

	}

	@Override
	public final double evaluateLCS(final AbstractLearningClassifierSystem lcs) {
		final ClassifierTransformBridge bridge = myLcs
				.getClassifierTransformBridge();

		int tp = 0, fp = 0;
		for (int i = 0; i < instances.length; i++) {
			final int[] classes = myLcs.classifyInstance(instances[i]);
			final int[] classification = bridge
					.getDataInstanceLabels(instances[i]);
			Arrays.sort(classes);
			Arrays.sort(classification);

			if (Arrays.equals(classes, classification))
				tp++;
			else
				fp++;

		}

		final double correctRate = ((double) tp) / ((double) (fp + tp));

		if (printResults) {
			System.out.println("tp:" + tp + " fp:" + fp + " exactMatch:"
					+ correctRate + " total instances:" + instances.length);
		}
		return correctRate;
	}

}
