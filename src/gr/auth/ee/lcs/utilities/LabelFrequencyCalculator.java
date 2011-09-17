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

import java.util.Iterator;
import java.util.TreeMap;

/**
 * A label frequency calculator.
 * 
 * @author Miltiadis Allamanis
 * 
 */
public class LabelFrequencyCalculator {
	/**
	 * Create a map containing combinations-frequencies of each label
	 * combination.
	 * 
	 * @param activeLabels
	 * @param labels
	 *            the number of labels
	 * @param instances
	 *            the instances list
	 * @retrun a map containing the frequency of each combination
	 */
	public static TreeMap<String, Integer> createCombinationMap(
			int[] activeLabels, int labels, double[][] instances) {
		final TreeMap<String, Integer> map = new TreeMap<String, Integer>();
		for (int i = 0; i < instances.length; i++) {
			final String combination = generateString(activeLabels, labels,
					instances[i]);
			if (map.containsKey(combination)) {
				final int count = map.get(combination) + 1;
				map.put(combination, new Integer(count));
			} else {
				map.put(combination, new Integer(1));
			}

		}
		return map;
	}

	/**
	 * A static utility for getting an all-active label index array.
	 * 
	 * @param labels
	 *            the number of labels of the problem
	 * @return an array containing 0,1,2,...,labels-1
	 */
	public static int[] generateAllActiveIndexes(int labels) {
		final int[] result = new int[labels];
		for (int i = 0; i < labels; i++) {
			result[i] = i;
		}
		return result;
	}

	/**
	 * Generate a unique identifier for a specific instance, given some label
	 * activations.
	 * 
	 * @param activeLabels
	 *            the array of active instances
	 * @param instance
	 *            the double instance (the last 'label' number of fields are
	 *            labels)
	 * @param labels
	 *            the number of labels at the given instance
	 * @return the unique identifier of the label combination.
	 */
	private static String generateString(int[] activeLabels, int labels,
			double[] instance) {
		String result = "";
		for (int i = 0; i < activeLabels.length; i++) {
			final int labelIndex = (instance.length - labels) + activeLabels[i];
			result += (int) instance[labelIndex];
		}

		return result;
	}

	/**
	 * Calculate the imbalance rate.
	 * 
	 * @param map
	 *            a map for all possible classes/labels and their respective
	 *            frequencies
	 * @return the imbalance rate
	 */
	public static double imbalanceRate(TreeMap<String, Integer> map) {
		int minCount = Integer.MAX_VALUE;
		int maxCount = Integer.MIN_VALUE;

		final Iterator<String> keys = map.keySet().iterator();

		while (keys.hasNext()) {
			String currentKey = keys.next();
			final int currentCount = map.get(currentKey);
			if (currentCount > maxCount)
				maxCount = currentCount;
			if (currentCount < minCount)
				minCount = currentCount;
		}

		return ((double) maxCount) / ((double) minCount);

	}

}
