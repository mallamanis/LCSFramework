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
package gr.auth.ee.lcs.classifiers.statistics.bundles;

import gr.auth.ee.lcs.AbstractLearningClassifierSystem;
import gr.auth.ee.lcs.classifiers.statistics.MeanAttributeSpecificityStatistic;
import gr.auth.ee.lcs.classifiers.statistics.MeanCoverageStatistic;
import gr.auth.ee.lcs.classifiers.statistics.MeanFitnessStatistic;
import gr.auth.ee.lcs.classifiers.statistics.MeanLabelSpecificity;
import gr.auth.ee.lcs.classifiers.statistics.WeightedMeanAttributeSpecificityStatistic;
import gr.auth.ee.lcs.classifiers.statistics.WeightedMeanCoverageStatistic;
import gr.auth.ee.lcs.classifiers.statistics.WeightedMeanLabelSpecificity;
import gr.auth.ee.lcs.data.AbstractUpdateStrategy;
import gr.auth.ee.lcs.data.ILCSMetric;
import gr.auth.ee.lcs.data.LCSMetricBundle;

import java.util.Vector;

/**
 * A bundle containing major classifier statistics.
 * 
 * @author Miltiadis Allamanis
 * 
 */
public class SetStatisticsBundle extends LCSMetricBundle {

	public SetStatisticsBundle(AbstractLearningClassifierSystem lcs,
			final int numberOfLabels) {
		super(lcs, new Vector<ILCSMetric>());

		addMetric(new MeanFitnessStatistic(
				AbstractUpdateStrategy.COMPARISON_MODE_EXPLOITATION));
		addMetric(new MeanCoverageStatistic());
		addMetric(new WeightedMeanCoverageStatistic(
				AbstractUpdateStrategy.COMPARISON_MODE_EXPLOITATION));
		addMetric(new MeanAttributeSpecificityStatistic());
		addMetric(new WeightedMeanAttributeSpecificityStatistic(
				AbstractUpdateStrategy.COMPARISON_MODE_EXPLOITATION));

		addMetric(new MeanLabelSpecificity(numberOfLabels));
		addMetric(new WeightedMeanLabelSpecificity(numberOfLabels,
				AbstractUpdateStrategy.COMPARISON_MODE_EXPLOITATION));
	}

}
