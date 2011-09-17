/**
 * 
 */
package gr.auth.ee.lcs.classifiers.statistics;

import gr.auth.ee.lcs.AbstractLearningClassifierSystem;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.data.ILCSMetric;

/**
 * Retrieve the mean coverage statistic.
 * @author Miltos Allamanis
 *
 */
public class MeanCoverageStatistic implements ILCSMetric {

	/* (non-Javadoc)
	 * @see gr.auth.ee.lcs.data.ILCSMetric#getMetric(gr.auth.ee.lcs.AbstractLearningClassifierSystem)
	 */
	@Override
	public double getMetric(AbstractLearningClassifierSystem lcs) {
		final ClassifierSet set = lcs.getRulePopulation();
		double coverageSum = 0;

		final int numOfMacroclassifiers = set.getNumberOfMacroclassifiers();
		for (int i = 0; i < numOfMacroclassifiers; i++)
			coverageSum += set.getClassifierNumerosity(i)
					* set.getClassifier(i).getCoverage();

		return coverageSum / ((double) set.getTotalNumerosity());
	}

	/* (non-Javadoc)
	 * @see gr.auth.ee.lcs.data.ILCSMetric#getMetricName()
	 */
	@Override
	public String getMetricName() {
		return "Mean Coverage";
	}

}
