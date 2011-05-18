/**
 * 
 */
package gr.auth.ee.lcs.utilities;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

/**
 * A label frequency calculator.
 * 
 * @author Miltiadis Allamanis
 * 
 */
public class LabelFrequencyCalculator {
	/**
	 * A static utility for getting an all-active label index array.
	 * 
	 * @param labels
	 *            the number of labels of the problem
	 * @return an array containing 0,1,2,...,labels-1
	 */
	public static int[] generateAllActiveIndexes(int labels) {
		int[] result = new int[labels];
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
			final int labelIndex = instance.length - labels + activeLabels[i];
			result += (int) instance[labelIndex];
		}

		return result;
	}

	/**
	 * 
	 * @param activeLabels
	 * @param labels
	 * @param instances
	 */
	public static TreeMap<String, Integer> createCombinationMap(
			int[] activeLabels, int labels, double[][] instances) {
		TreeMap<String, Integer> map = new TreeMap<String, Integer>();
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

	public static double ImbalanceRate(TreeMap<String, Integer> map) {
		int minCount = Integer.MAX_VALUE;
		int maxCount = Integer.MIN_VALUE;

		Iterator<String> keys = map.keySet().iterator();

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
