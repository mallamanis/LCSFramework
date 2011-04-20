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
public class InstanceToDoubleConverter {

	/**
	 * Perform the conversion.
	 * 
	 * @param set
	 *            the set containing the instances
	 * @return a double[][] containing the instances and their respective
	 *         attributes
	 */
	public static double[][] convert(Instances set) {
		double[][] result = new double[set.numInstances()][];
		for (int i = 0; i < set.numInstances(); i++) {
			result[i] = new double[set.numAttributes()];
			for (int j = 0; j < set.numAttributes(); j++) {
				result[i][j] = set.instance(i).value(j);
			}
		}

		return result;

	}
}
