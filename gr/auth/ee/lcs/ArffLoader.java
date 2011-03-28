package gr.auth.ee.lcs;

import gr.auth.ee.lcs.data.ClassifierTransformBridge;

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
	 * A test set
	 */
	public Instances testSet;

	public void loadInstances(String filename, boolean generateTestSet)
			throws IOException {
		// Open .arff
		FileReader reader = new FileReader(filename);
		Instances set = new Instances(reader);
		if (set.classIndex() < 0)
			set.setClassIndex(set.numAttributes() - 1);
		set.randomize(new Random());
		set.stratify(10);

		Instances trainSet;
		if (generateTestSet) {
			trainSet = set.trainCV(10, 3);
			testSet = set.testCV(10, 3);
		} else {
			trainSet = set;
		}

		ClassifierTransformBridge.instances = new double[trainSet
				.numInstances()][trainSet.numAttributes()];

		// Load instances
		for (int i = 0; i < trainSet.numInstances(); i++) {
			for (int j = 0; j < trainSet.numAttributes(); j++) {
				ClassifierTransformBridge.instances[i][j] = trainSet
						.instance(i).value(j);
			}
		}

	}
}
