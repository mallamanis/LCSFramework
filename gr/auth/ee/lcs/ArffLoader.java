package gr.auth.ee.lcs;

import gr.auth.ee.lcs.data.ClassifierTransformBridge;
import gr.auth.ee.lcs.utilities.InstanceToDoubleConverter;

import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

import weka.core.Instances;

/**
 * A simple loader using an .arff file.
 * 
 * @author Miltos Allamanis
 * 
 */
public class ArffLoader {

	/**
	 * A test set.
	 */
	public Instances testSet;

	/**
	 * The current trainSet.
	 */
	public Instances trainSet;

	/**
	 * Load instances into the global train store and create test set.
	 * 
	 * @param filename
	 *            the .arff filename to be used
	 * @param generateTestSet
	 *            true if a test set is going to be generated
	 * @throws IOException
	 *             if the input file is not found
	 */
	public void loadInstances(final String filename,
			final boolean generateTestSet) throws IOException {
		// Open .arff
		final FileReader reader = new FileReader(filename);
		final Instances set = new Instances(reader);
		if (set.classIndex() < 0)
			set.setClassIndex(set.numAttributes() - 1);
		set.randomize(new Random());
		set.stratify(10);

		if (generateTestSet) {
			final int fold = (int) Math.floor(Math.random() * 10);
			trainSet = set.trainCV(10, fold);
			testSet = set.testCV(10, fold);
		} else {
			trainSet = set;
		}

		ClassifierTransformBridge.instances = InstanceToDoubleConverter
				.convert(trainSet);

	}
}
