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
import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.data.ClassifierTransformBridge;
import gr.auth.ee.lcs.data.ILCSMetric;

/**
 * @author miltiadis
 * 
 */
public class WeightedMeanLabelSpecificity implements ILCSMetric {

	/**
	 * The number of labels in the problem.
	 */
	private final int numOfLabels;

	/**
	 * The comparison mode used for getting the fitness values.
	 */
	private final int comparisonMode;

	public WeightedMeanLabelSpecificity(final int numberOfLabels,
			final int fitnessMode) {
		numOfLabels = numberOfLabels;
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
		final ClassifierTransformBridge bridge = lcs
				.getClassifierTransformBridge();

		final int numberOfAttributes = bridge.getNumberOfAttributes();

		final int numberOfMacroclassifiers = set.getNumberOfMacroclassifiers();

		double specificLabels = 0;
		double fitnessSum = 0;

		for (int i = 0; i < numberOfMacroclassifiers; i++) {
			final Classifier cl = set.getClassifier(i);
			final int numerosity = set.getClassifierNumerosity(i);
			final double classifierFitness = set.getClassifier(i)
					.getComparisonValue(comparisonMode);
			final double totalFitness = classifierFitness * numerosity;
			for (int l = numberOfAttributes; l < numberOfAttributes
					+ numOfLabels; l++) {
				if (bridge.isAttributeSpecific(cl, l)) {
					specificLabels += totalFitness;
				}
			}
			fitnessSum += totalFitness * numOfLabels;
		}

		return specificLabels / fitnessSum;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gr.auth.ee.lcs.data.ILCSMetric#getMetricName()
	 */
	@Override
	public String getMetricName() {
		return "Weighted Mean Label Specificity";
	}

}
