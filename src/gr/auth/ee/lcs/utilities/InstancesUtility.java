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

import java.io.FileReader;
import java.io.IOException;

import weka.core.Instances;

/**
 * A utility class for converting a Weka Instance to a double array
 * 
 * @author Miltiadis Allamanis
 * 
 */
public final class InstancesUtility {

	/**
	 * Perform the conversion.
	 * 
	 * @param set
	 *            the set containing the instances
	 * @return a double[][] containing the instances and their respective
	 *         attributes
	 */
	public static double[][] convertIntancesToDouble(final Instances set) {
		final double[][] result = new double[set.numInstances()][set
				.numAttributes()];
		for (int i = 0; i < set.numInstances(); i++) {

			for (int j = 0; j < set.numAttributes(); j++) {
				result[i][j] = set.instance(i).value(j);
			}
		}

		return result;

	}

	/**
	 * Opens an file and creates an instance
	 * 
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	public static Instances openInstance(final String filename)
			throws IOException {
		final FileReader reader = new FileReader(filename);
		return new Instances(reader);
	};

	/**
	 * Private Constructor to avoid instantiation.
	 */
	private InstancesUtility() {
	}
}
