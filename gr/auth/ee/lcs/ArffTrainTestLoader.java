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
package gr.auth.ee.lcs;

import gr.auth.ee.lcs.utilities.InstanceToDoubleConverter;
import gr.auth.ee.lcs.utilities.SettingsLoader;

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
public class ArffTrainTestLoader {

	/**
	 * A test set.
	 */
	public Instances testSet;

	/**
	 * The current trainSet.
	 */
	public Instances trainSet;

	/**
	 * The LCS instance.
	 */
	private final AbstractLearningClassifierSystem myLcs;

	/**
	 * Constructor. Creates a loader for the given LCS.
	 * 
	 * @param lcs
	 *            the lcs where instances will be loaded on
	 */
	public ArffTrainTestLoader(final AbstractLearningClassifierSystem lcs) {
		myLcs = lcs;
	}
	
	public void evaluate() {
		myLcs.train();
		final double[] evals = myLcs.getEvaluations(testSet);
		final String[] names = myLcs.getEvaluationNames();

		for (int i = 0; i < evals.length; i++) {
			System.out.println(names[i] + ": " + evals[i]);
		}
	}

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
	public final void loadInstances(final String filename,
			final boolean generateTestSet) throws IOException {
		// Open .arff
		final FileReader reader = new FileReader(filename);
		final Instances set = new Instances(reader);
		if (set.classIndex() < 0) {
			set.setClassIndex(set.numAttributes() - 1);
		}
		set.randomize(new Random());
		// set.stratify(10);

		if (generateTestSet) {
			final int numOfFolds = (int) SettingsLoader.getNumericSetting(
					"NumberOfFolds", 10);
			final int fold = (int) Math.floor(Math.random() * numOfFolds);
			trainSet = set.trainCV(numOfFolds, fold);
			testSet = set.testCV(numOfFolds, fold);
		} else {
			trainSet = set;
		}

		myLcs.instances = InstanceToDoubleConverter.convert(trainSet);

	}

	/**
	 * Load instances into the global train store and create test set.
	 * 
	 * @param filename
	 *            the .arff filename to be used
	 * @param testFile
	 *            the test file to be loaded
	 * @throws IOException
	 *             if the input file is not found
	 */
	public final void loadInstancesWithTest(final String filename,
			final String testFile) throws IOException {
		// Open .arff
		final FileReader reader = new FileReader(filename);
		final Instances set = new Instances(reader);
		if (set.classIndex() < 0)
			set.setClassIndex(set.numAttributes() - 1);
		set.randomize(new Random());
		// set.stratify(10);
		trainSet = set;

		myLcs.instances = InstanceToDoubleConverter.convert(trainSet);

		final FileReader testReader = new FileReader(testFile);
		testSet = new Instances(testReader);

	}
}
