package gr.auth.ee.lcs;

import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.data.ClassifierTransformBridge;

import java.io.FileReader;
import java.io.IOException;

import weka.core.Instances;

/**
 * @author Miltos Allamanis
 * 
 */
public class ArffTrainer {

	Instances testSet;

	/*
	 * Evaluate on testset.
	 * 
	 * @param population
	 *            the population on with the rules will be evaluated TODO: Move
	 *            to evaluator & correct public void
	 *            evaluateOnTest(ClassifierSet population) { LCSExploitTemplate
	 *            eval = new LCSExploitTemplate(); int tp = 0, fp = 0;
	 * 
	 *            for (int i = 0; i < testSet.numInstances(); i++) { double[]
	 *            instance = new double[testSet.numAttributes() - 1];
	 * 
	 *            for (int j = 0; j < testSet.numAttributes(); j++) {
	 *            instance[j] = testSet.instance(i).value(j); } if
	 *            (eval.classifyCorrectly(instance, population)) { tp++; } else
	 *            if (eval.classify(instance, population) != -1) { fp++; }
	 * 
	 *            }
	 * 
	 *            double errorRate = ((double) fp) / ((double) (fp + tp));
	 *            System.out.println("tp:" + tp + " fp:" + fp + " errorRate:" +
	 *            errorRate + " total instances:" + testSet.numInstances());
	 * 
	 *            }
	 */

	public void loadInstances(String filename) throws IOException {
		// Open .arff
		FileReader reader = new FileReader(filename);
		Instances set = new Instances(reader);
		if (set.classIndex() < 0)
			set.setClassIndex(set.numAttributes() - 1);

		set.stratify(10);

		Instances trainSet = set;// set.trainCV(10, 9);
		// testSet = set.testCV(10, 9);

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
