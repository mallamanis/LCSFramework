/**
 * 
 */
package gr.auth.ee.lcs.classifiers.statistics;

import gr.auth.ee.lcs.AbstractLearningClassifierSystem;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.data.ILCSMetric;

/**
 * Calculate the mean fitness statistic of a population.
 * 
 * @author Miltos Allamanis
 * 
 */
public class MeanFitnessStatistic implements ILCSMetric {

	/**
	 * The comparison mode used for getting the fitness values.
	 */
	private final int comparisonMode;

	public MeanFitnessStatistic(int fitnessMode) {
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
		double fitnessSum = 0;

		final int numOfMacroclassifiers = set.getNumberOfMacroclassifiers();
		for (int i = 0; i < numOfMacroclassifiers; i++)
			fitnessSum += set.getClassifierNumerosity(i)
					* set.getClassifier(i).getComparisonValue(comparisonMode);

		return fitnessSum / ((double) set.getTotalNumerosity());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gr.auth.ee.lcs.data.ILCSMetric#getMetricName()
	 */
	@Override
	public String getMetricName() {
		return "Mean Fitness";
	}

}
