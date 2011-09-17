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
