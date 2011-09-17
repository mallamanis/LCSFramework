/**
 * 
 */
package gr.auth.ee.lcs.classifiers.statistics;

import gr.auth.ee.lcs.AbstractLearningClassifierSystem;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.data.ILCSMetric;

/**
 * Calculates the mean coverage statistic weighted by each classifier's fitness.
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

	/* (non-Javadoc)
	 * @see gr.auth.ee.lcs.data.ILCSMetric#getMetric(gr.auth.ee.lcs.AbstractLearningClassifierSystem)
	 */
	@Override
	public double getMetric(AbstractLearningClassifierSystem lcs) {
		final ClassifierSet set = lcs.getRulePopulation();
		double coverageSum = 0;
		double fitnessSum = 0;
		
		final int numOfMacroclassifiers = set.getNumberOfMacroclassifiers();
		for (int i = 0; i < numOfMacroclassifiers; i++) {
			final int classifierNumerosity = set.getClassifierNumerosity(i);
			final double classifierFitness = set.getClassifier(i).getComparisonValue(comparisonMode);
			final double totalFitness = classifierFitness * classifierNumerosity;
			coverageSum += totalFitness
					* set.getClassifier(i).getCoverage();
			fitnessSum += totalFitness;
			
		}

		return coverageSum / fitnessSum;
	}

	/* (non-Javadoc)
	 * @see gr.auth.ee.lcs.data.ILCSMetric#getMetricName()
	 */
	@Override
	public String getMetricName() {
		return "Weighted Mean Coverage";
	}

}
