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
 * Calculates a fitness-weighted mean attribute specificity.
 * 
 * @author Miltos Allamanis
 * 
 */
public class WeightedMeanAttributeSpecificityStatistic implements ILCSMetric {

	/**
	 * The comparison mode used for getting the fitness values.
	 */
	private final int comparisonMode;

	public WeightedMeanAttributeSpecificityStatistic(int fitnessMode) {
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
		final ClassifierTransformBridge bridge = lcs
				.getClassifierTransformBridge();
		final int numberOfAttributes = bridge.getNumberOfAttributes();
		final ClassifierSet set = lcs.getRulePopulation();
		final int numberOfMacroclassifiers = set.getNumberOfMacroclassifiers();

		double specificAttibutes = 0;
		double fitnessSum = 0;
		for (int i = 0; i < numberOfMacroclassifiers; i++) {
			final Classifier cl = set.getClassifier(i);
			final int numerosity = set.getClassifierNumerosity(i);
			final double classifierFitness = set.getClassifier(i)
					.getComparisonValue(comparisonMode);
			final double weight = numerosity * classifierFitness;

			for (int j = 0; j < numberOfAttributes; j++) {

				if (bridge.isAttributeSpecific(cl, j)) {
					specificAttibutes += weight;
				}

				fitnessSum += weight;
			}
		}

		return specificAttibutes / fitnessSum;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gr.auth.ee.lcs.data.ILCSMetric#getMetricName()
	 */
	@Override
	public String getMetricName() {
		return "Weighted Mean Attribute Specificity";
	}
}
