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
package gr.auth.ee.lcs.classifiers.statistics;

import gr.auth.ee.lcs.AbstractLearningClassifierSystem;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.data.ILCSMetric;

/**
 * Calculates the mean coverage statistic weighted by each classifier's fitness.
 * 
 * @author Miltos Allamanis
 * 
 */
public class WeightedMeanCoverageStatistic implements ILCSMetric {

	/**
	 * The comparison mode used for getting the fitness values.
	 */
	private final int comparisonMode;

	public WeightedMeanCoverageStatistic(int fitnessMode) {
		comparisonMode = fitnessMode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gr.auth.ee.lcs.data.ILCSMetric#getMetric(gr.auth.ee.lcs.
	 * AbstractLearningClassifierSystem)
	 */
	@Override
	public double getMetric(AbstractLearningClassifierSystem lcs) {
		final ClassifierSet set = lcs.getRulePopulation();
		double coverageSum = 0;
		double fitnessSum = 0;

		final int numOfMacroclassifiers = set.getNumberOfMacroclassifiers();
		for (int i = 0; i < numOfMacroclassifiers; i++) {
			final int classifierNumerosity = set.getClassifierNumerosity(i);
			final double classifierFitness = set.getClassifier(i)
					.getComparisonValue(comparisonMode);
			final double totalFitness = classifierFitness
					* classifierNumerosity;
			coverageSum += totalFitness * set.getClassifier(i).getCoverage();
			fitnessSum += totalFitness;

		}

		return coverageSum / fitnessSum;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gr.auth.ee.lcs.data.ILCSMetric#getMetricName()
	 */
	@Override
	public String getMetricName() {
		return "Weighted Mean Coverage";
	}

}
