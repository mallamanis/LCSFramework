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
		for (int i = 0; i < numOfLabels; i++) {
			if (Arrays.binarySearch(activeClasses, i) >= 0) {
				response.append("1" + delimiter);
			} else {
				response.append("0" + delimiter);
			}
		}

		return response.toString();
	}
}
