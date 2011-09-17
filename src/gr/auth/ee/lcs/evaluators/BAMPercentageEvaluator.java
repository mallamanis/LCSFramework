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
import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.data.ILCSMetric;

import java.util.Arrays;
import java.util.Vector;

/**
 * Evaluates a ClassifierSet for the exact percentage of the BAM that it
 * contains.
 * 
 * @author Miltiadis Allamanis
 * 
 */
public class BAMPercentageEvaluator implements ILCSMetric {

	/**
	 * The best action map rules.
	 */
	private final Vector<Classifier> bestActionMap;

	/**
	 * The constructor.
	 * 
	 * @param bamChromosomes
	 *            a vector containing the chromosomes of the BAM
	 */
	public BAMPercentageEvaluator(final Vector<Classifier> bamChromosomes) {
		bestActionMap = bamChromosomes;
	}

	@Override
	public double getMetric(final AbstractLearningClassifierSystem lcs) {
		final int bamSize = bestActionMap.size();
		final boolean[] covered = new boolean[bamSize];
		Arrays.fill(covered, false);
		final ClassifierSet classifiers = lcs.getRulePopulation();
		final int setSize = classifiers.getNumberOfMacroclassifiers();
		for (int i = 0; i < setSize; i++) {
			final Classifier actual = classifiers.getClassifier(i);

			for (int j = 0; j < bamSize; j++) {
				if (covered[j])
					continue;
				Classifier cl = bestActionMap.elementAt(j);
				covered[j] = cl.equals(actual);
			}
		}

		// Count covered instances
		int coveredInstances = 0;
		for (int i = 0; i < bamSize; i++) {
			if (covered[i])
				coveredInstances++;
		}

		final double bamPercentage = ((double) coveredInstances)
				/ ((double) bamSize);
		return bamPercentage;
	}

	@Override
	public String getMetricName() {
		return "BAM Percentage";
	}

}
