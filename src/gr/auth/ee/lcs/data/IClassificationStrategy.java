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
package gr.auth.ee.lcs.data;

import gr.auth.ee.lcs.classifiers.ClassifierSet;

/**
 * A classification strategy interface.
 * 
 * @author Miltos Allamanis
 * 
 */
public interface IClassificationStrategy {
	/**
	 * Classify a given vision vector with a given set of classifiers.
	 * 
	 * @param aSet
	 *            the set of classifiers used at the classification
	 * @param visionVector
	 *            the vision vector of the instance to be classified
	 * @return an integer array containing the labels/ classes that the instance
	 *         has been classified in
	 */
	int[] classify(ClassifierSet aSet, double[] visionVector);

	/**
	 * Set the classification threshold, if applicable.
	 * 
	 * @param threshold
	 *            the threshold being set
	 */
	void setThreshold(double threshold);
}