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
