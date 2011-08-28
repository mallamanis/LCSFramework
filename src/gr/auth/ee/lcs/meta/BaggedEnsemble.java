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
package gr.auth.ee.lcs.meta;

import gr.auth.ee.lcs.AbstractLearningClassifierSystem;

import java.util.Arrays;

import weka.core.Instances;

/**
 * A wrapper for bagged LCSs.
 * 
 * @author Miltiadis Allamanis
 * 
 */
public abstract class BaggedEnsemble extends AbstractLearningClassifierSystem {

	/**
	 * The number of labels used.
	 */
	protected final int numberOfLabels;

	/**
	 * The ensemble of LCSs.
	 * 
	 */
	protected AbstractLearningClassifierSystem ensemble[];

	public BaggedEnsemble(int numOfLabels,
			AbstractLearningClassifierSystem lcss[]) {
		numberOfLabels = numOfLabels;
		ensemble = lcss;
	}

	@Override
	public int[] classifyInstance(double[] instance) {
		final int[] classifications = new int[numberOfLabels];
		Arrays.fill(classifications, 0);

		for (int i = 0; i < ensemble.length; i++) {
			int[] classification = ensemble[i].classifyInstance(instance);
			for (int j = 0; j < classifications.length; j++)
				classifications[j] -= 1;

			for (int j = 0; j < classification.length; j++)
				classifications[classification[j]] += 2;
		}

		int activeLabels = 0;
		for (int i = 0; i < classifications.length; i++) {
			if (classifications[i] > 0) // what about 0?
				activeLabels++;
		}

		final int[] result = new int[activeLabels];
		int currentPosition = 0;
		for (int i = 0; i < classifications.length; i++) {
			if (classifications[i] > 0) { // what about 0?
				result[currentPosition] = i;
				currentPosition++;
			}
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gr.auth.ee.lcs.AbstractLearningClassifierSystem#createNew()
	 */
	@Override
	public abstract AbstractLearningClassifierSystem createNew();

	/*
	 * (non-Javadoc)
	 * 
	 * @see gr.auth.ee.lcs.AbstractLearningClassifierSystem#getEvaluationNames()
	 */
	@Override
	public abstract String[] getEvaluationNames();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.AbstractLearningClassifierSystem#getEvaluations(weka.core
	 * .Instances)
	 */
	@Override
	public abstract double[] getEvaluations(Instances testSet);

	/*
	 * (non-Javadoc)
	 * 
	 * @see gr.auth.ee.lcs.AbstractLearningClassifierSystem#train()
	 */
	@Override
	public void train() {
		for (int i = 0; i < ensemble.length; i++) {
			ensemble[i].instances = sampleTrainInstances();
			ensemble[i].train();
		}

	}

	private double[][] sampleTrainInstances() {
		double[][] sample = new double[this.instances.length][];
		for (int i = 0; i < sample.length; i++) {
			int pos = (int) Math.floor(Math.random() * this.instances.length);

			sample[i] = this.instances[pos];
		}
		return sample;
	}

}
