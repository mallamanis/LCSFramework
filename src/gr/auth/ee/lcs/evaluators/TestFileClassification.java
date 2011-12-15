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
package gr.auth.ee.lcs.evaluators;

import gr.auth.ee.lcs.AbstractLearningClassifierSystem;
import gr.auth.ee.lcs.utilities.InstancesUtility;
import gr.auth.ee.lcs.utilities.LabelRepresentationConverter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

/**
 * TestFileEvaluator classifies the data in a test files and output the
 * classification.
 * 
 * @author Miltos Allamanis
 * 
 */
public class TestFileClassification {

	/**
	 * The set of instances to classify.
	 */
	private final double[][] mInstances;

	/**
	 * The LCS instance being used.
	 */
	private final AbstractLearningClassifierSystem mLcs;

	/**
	 * The filename to output results.
	 */
	private final String mOutputFilename;

	private final int mNumOfLabels;

	/**
	 * Default constructor.
	 * 
	 * @param instances
	 *            the instances to perform classification on
	 * @param outputFilename
	 *            the name of the file where classifications will be outputed
	 * @param lcs
	 *            the LCS that will classify the instances
	 * @param numOfLabels
	 *            the number of labels in the problem
	 */
	public TestFileClassification(final double[][] instances,
			final String outputFilename,
			final AbstractLearningClassifierSystem lcs, final int numOfLabels) {
		mLcs = lcs;
		mInstances = instances;
		mOutputFilename = outputFilename;
		mNumOfLabels = numOfLabels;
	}

	/**
	 * Constructor from a file.
	 * 
	 * @param arffFileName
	 *            the filename of the .arff file containing the instances to
	 *            classify
	 * @param lcs
	 *            the LCS to classify the instances
	 * @param numOfLabels
	 *            the number of labels contained in the problem
	 * @throws IOException
	 *             if file is not found
	 */
	public TestFileClassification(final String arffFileName,
			final String outputFilename,
			final AbstractLearningClassifierSystem lcs, final int numOfLabels)
			throws IOException {
		mLcs = lcs;
		mOutputFilename = outputFilename;
		mInstances = InstancesUtility.convertIntancesToDouble(InstancesUtility
				.openInstance(arffFileName));
		mNumOfLabels = numOfLabels;

	}

	/**
	 * Produce the classification results file.
	 * 
	 * @throws IOException
	 *             when the output file cannot be written
	 */
	public void produceClassification() throws IOException {

		final StringBuffer response = new StringBuffer();

		for (int i = 0; i < mInstances.length; i++) {
			final int[] classes = mLcs.classifyInstance(mInstances[i]);
			Arrays.sort(classes);
			response.append(LabelRepresentationConverter.activeLabelsToString(
					classes, mNumOfLabels, ",")
					+ System.getProperty("line.separator"));
		}
		BufferedWriter out = new BufferedWriter(new FileWriter(mOutputFilename));
		out.write(response.toString());
		out.close();

	}
}
