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
package gr.auth.ee.lcs.utilities;

/**
 * A Pairwise label selector for PW problem transform.
 * 
 * @author Miltiadis Allamanis
 * 
 */
public class PairwiseLabelSelector implements ILabelSelector {

	/**
	 * The number of labels used at the problem.
	 */
	private final int numberOfLabels;

	/**
	 * Internal state parameters.
	 */
	private int i = 0, j = 1;

	/**
	 * Constructor.
	 * 
	 * @param labels
	 *            the problem's number of labels
	 */
	public PairwiseLabelSelector(int labels) {
		numberOfLabels = labels;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gr.auth.ee.lcs.utilities.ILabelSelector#getStatus(int)
	 */
	@Override
	public boolean getStatus(int labelIndex) {
		return (labelIndex == i || labelIndex == j);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gr.auth.ee.lcs.utilities.ILabelSelector#hasNext()
	 */
	@Override
	public boolean hasNext() {
		if (i == numberOfLabels - 2 && j == numberOfLabels - 1)
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gr.auth.ee.lcs.utilities.ILabelSelector#next()
	 */
	@Override
	public boolean next() {
		if (!hasNext())
			return false;
		j++;
		if (j >= numberOfLabels) {
			i++;
			j = i + 1;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gr.auth.ee.lcs.utilities.ILabelSelector#reset()
	 */
	@Override
	public void reset() {
		i = 0;
		j = 1;
	}

	@Override
	public int[] activeIndexes() {
		int[] result = new int[2];
		result[0] = i;
		result[1] = j;
		return result;
	}

}
