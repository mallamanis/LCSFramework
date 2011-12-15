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

import java.util.Arrays;

/**
 * Convert around various label representation methods.
 * 
 * @author Miltiadis Allamanis
 * 
 */
public class LabelRepresentationConverter {
	/**
	 * Gets an array of active classes and returns a string representation.
	 * 
	 * @param activeClasses
	 *            the array of active classes
	 * @param numOfLabels
	 *            the number of labels
	 * @param delimiter
	 *            the delimiter to be used
	 * @return a String representation of the labels
	 */
	public static String activeLabelsToString(int[] activeClasses,
			final int numOfLabels, final String delimiter) {
		final StringBuffer response = new StringBuffer();

		Arrays.sort(activeClasses);
		for (int i = 0; i < (numOfLabels - 1); i++) {
			if (Arrays.binarySearch(activeClasses, i) >= 0) {
				response.append("1" + delimiter);
			} else {
				response.append("0" + delimiter);
			}
		}

		if (Arrays.binarySearch(activeClasses, numOfLabels - 1) >= 0) {
			response.append("1");
		} else {
			response.append("0");
		}

		return response.toString();
	}
}
