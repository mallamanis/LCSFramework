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
import gr.auth.ee.lcs.data.ILCSMetric;
import weka.core.Instances;

/**
 * All label single evaluator. The class acts as a wrapper against single label
 * evaluator.
 * 
 * @author Miltos Allamanis
 * 
 */
public final class AllSingleLabelEvaluator implements ILCSMetric {

	/**
	 * Single label evaluators.
	 */
	private final SingleLabelEvaluator[] evaluators;

	/**
	 * Boolean indicating if we are going to print results.
	 */
	private final boolean print;

	/**
	 * Constructor.
	 * 
	 * @param evaluateSet
	 *            the Weka instances to perform evaluation on
	 * @param numberOfLabels
	 *            the number of labels at the problem
	 * @param printResults
	 *            print results to stout
	 * @param lcs
	 *            the LCS instance used
	 */
	public AllSingleLabelEvaluator(final Instances evaluateSet,
			final int numberOfLabels, final boolean printResults,
			final AbstractLearningClassifierSystem lcs) {
		print = printResults;
		evaluators = new SingleLabelEvaluator[numberOfLabels];
		for (int i = 0; i < numberOfLabels; i++)
			evaluators[i] = new SingleLabelEvaluator(i, evaluateSet, lcs);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.data.IEvaluator#evaluateSet(gr.auth.ee.lcs.classifiers
	 * .ClassifierSet)
	 */
	@Override
	public double getMetric(final AbstractLearningClassifierSystem lcs) {
		double sum = 0;
		for (int i = 0; i < evaluators.length; i++) {
			final double result = evaluators[i].getMetric(lcs);
			if (print) {
				System.out.println("Label " + i + " exact match:" + result);
			}
			sum += result;
		}
		return sum / (evaluators.length);
	}

	public String getMetricName() {
		return "Single Label Evaluator";
	}

}
