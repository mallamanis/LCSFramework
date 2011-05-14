/**
 * 
 */
package gr.auth.ee.lcs.utilities;

import weka.core.Instances;

/**
 * A utility class for converting a Weka Instance to a double array
 * 
 * @author Miltiadis Allamanis
 * 
 */
public final class InstanceToDoubleConverter {

	/**
	 * Perform the conversion.
	 * 
	 * @param set
	 *            the set containing the instances
	 * @return a double[][] containing the instances and their respective
	 *         attributes
	 */
	public static double[][] convert(final Instances set) {
		final double[][] result = new double[set.numInstances()][set
				.numAttributes()];
		for (int i = 0; i < set.numInstances(); i++) {

			for (int j = 0; j < set.numAttributes(); j++) {
				result[i][j] = set.instance(i).value(j);
			}
		}

		return result;

	};

	/**
	 * Private Constructor to avoid instanciation.
	 */
	private InstanceToDoubleConverter() {
	}
}
