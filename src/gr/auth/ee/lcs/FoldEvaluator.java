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
package gr.auth.ee.lcs;

import gr.auth.ee.lcs.utilities.InstanceToDoubleConverter;
import gr.auth.ee.lcs.utilities.SettingsLoader;

import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

import weka.core.Instances;

/**
 * n-fold evaluator
 * 
 * @author Miltiadis Allamanis
 * 
 */
public class FoldEvaluator {

	private final int numOfFolds;

	private final AbstractLearningClassifierSystem prototype;

	private final Instances instances;
	private Instances trainSet;
	private Instances testSet;

	private double[][] evals;

	final int runs;

	/**
	 * Constructor.
	 * 
	 * @param folds
	 *            the number of folds
	 * @param myLcs
	 *            the LCS instance to be evaluated
	 * @param filename
	 *            the filename containing the instances
	 * @throws IOException
	 *             if the file is not found.
	 */
	public FoldEvaluator(int folds, AbstractLearningClassifierSystem myLcs,
			final String filename) throws IOException {
		numOfFolds = folds;
		prototype = myLcs;
		final FileReader reader = new FileReader(filename);
		instances = new Instances(reader);
		runs = (int) SettingsLoader.getNumericSetting("foldsToRun", numOfFolds);
		instances.randomize(new Random());

	}

	public void evaluate() {

		for (int i = 0; i < runs; i++) {
			AbstractLearningClassifierSystem foldLCS = prototype.createNew();
			System.out.println("Training Fold " + i);
			loadFold(i, foldLCS);
			foldLCS.train();
			// Gather results...
			double[] results = foldLCS.getEvaluations(testSet);
			gatherResults(results, i);
		}

		double[] means = calcMean(this.evals);
		// print results
		printEvals(means);
	}

	private double[] calcMean(double[][] results) {
		double[] means = new double[results[0].length];
		for (int i = 0; i < means.length; i++) {
			double sum = 0;
			for (int j = 0; j < results.length; j++) {
				sum += results[j][i];
			}
			means[i] = (sum) / (results.length);
		}
		return means;
	}

	private void gatherResults(double[] results, int fold) {
		if (evals == null) {
			evals = new double[runs][results.length];
		}

		evals[fold] = results;

	}

	private void loadFold(int foldNumber, AbstractLearningClassifierSystem lcs) {

		trainSet = instances.trainCV(numOfFolds, foldNumber);
		lcs.instances = InstanceToDoubleConverter.convert(trainSet);
		testSet = instances.testCV(numOfFolds, foldNumber);
	}

	private void printEvals(double[] means) {
		final String[] names = prototype.getEvaluationNames();

		for (int i = 0; i < means.length; i++) {
			System.out.println(names[i] + ": " + means[i]);
		}
	}
}
