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
