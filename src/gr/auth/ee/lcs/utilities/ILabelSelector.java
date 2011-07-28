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
 * A label subset selector, used for problem transformations.
 * 
 * @author Miltos Allamanis
 * 
 */
public interface ILabelSelector {
	/**
	 * The active indexes for the current selection.
	 * 
	 * @return an array containing the active indices
	 */
	int[] activeIndexes();

	/**
	 * Returns the 0/1 status of a label at a given index, for the current
	 * state.
	 * 
	 * @param labelIndex
	 *            the label index
	 * @return true if the label should be active, otherwise false
	 */
	boolean getStatus(int labelIndex);

	/**
	 * Returns if there is another combination.
	 * 
	 * @return true if there is another combination
	 */
	boolean hasNext();

	/**
	 * Change internal status to the next combination.
	 * 
	 * @return true if the is a next (and we have sucessfully transitioned)
	 */
	boolean next();

	/**
	 * Reset combination counting.
	 */
	void reset();

}
