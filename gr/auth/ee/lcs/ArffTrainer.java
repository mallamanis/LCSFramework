package gr.auth.ee.lcs;

import gr.auth.ee.lcs.data.ClassifierTransformBridge;
import gr.auth.ee.lcs.evaluators.BinaryAccuracyEvalutor;

import java.io.FileReader;
import java.io.IOException;

import weka.core.Instances;

/**
 * @author Miltos Allamanis
 * 
 */
public class ArffTrainer {

	public Instances testSet;

	public void loadInstances(String filename) throws IOException {
		// Open .arff
		FileReader reader = new FileReader(filename);
		Instances set = new Instances(reader);
		if (set.classIndex() < 0)
			set.setClassIndex(set.numAttributes() - 1);

		set.stratify(10);

		Instances trainSet = set.trainCV(10, 3);
		testSet = set.testCV(10, 3);

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
