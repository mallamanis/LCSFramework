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
package gr.auth.ee.lcs.data;

import gr.auth.ee.lcs.AbstractLearningClassifierSystem;

/**
 * An metrics class interface. This interface will be used for calculating
 * metrics of LCSs
 * 
 * @author Miltos Allamanis
 * 
 */
public interface ILCSMetric {
	/**
	 * Evaluate a set of classifiers.
	 * 
	 * @param lcs
	 *            the LCS that we are going to use for evaluation
	 * @return a numeric value indicating ClassifierSet's quality
	 */
	public double getMetric(AbstractLearningClassifierSystem lcs);

	/**
	 * Return the metric's name.
	 * 
	 * @return a string containing the name
	 */
	public String getMetricName();

}
