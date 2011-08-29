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
 * A Binary Relevance Label Selector. We select one after another.
 * 
 * @author Miltiadis Allamanis
 * 
 */
public final class BinaryRelevanceSelector implements ILabelSelector {

	/**
	 * The number of labels used at the selector.
	 */
	private final int numberOfLabels;

	/**
	 * The current label that the iterator presents.
	 */
	private int currentLabel = 0;

	/**
	 * Constructor.
	 * 
	 * @param labels
	 *            the number of labels at the problem
	 */
	public BinaryRelevanceSelector(final int labels) {
		numberOfLabels = labels;
	}

	@Override
	public int[] activeIndexes() {
		final int[] result = new int[1];
		result[0] = currentLabel;
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gr.auth.ee.lcs.utilities.ILabelSelector#getStatus(int)
	 */
	@Override
	public boolean getStatus(final int labelIndex) {
		return labelIndex == currentLabel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gr.auth.ee.lcs.utilities.ILabelSelector#hasNext()
	 */
	@Override
	public boolean hasNext() {
		if (currentLabel < (numberOfLabels - 1))
			return true;
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gr.auth.ee.lcs.utilities.ILabelSelector#next()
	 */
	@Override
	public boolean next() {
		if (currentLabel < (numberOfLabels - 1)) {
			currentLabel++;
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gr.auth.ee.lcs.utilities.ILabelSelector#reset()
	 */
	@Override
	public void reset() {
		currentLabel = 0;
	}

}
