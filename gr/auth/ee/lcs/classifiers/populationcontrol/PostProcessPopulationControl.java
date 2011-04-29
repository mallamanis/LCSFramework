/**
 * 
 */
package gr.auth.ee.lcs.classifiers.populationcontrol;

import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.classifiers.IPopulationControlStrategy;

/**
 * A population control strategy post processing all classifiers. This strategy
 * removes rules with low experience and
 * 
 * @stereotype ConcreteStrategy
 * 
 * @author Miltos Allamanis
 * 
 */
public class PostProcessPopulationControl implements IPopulationControlStrategy {

	/**
	 * The minimum experience for a rule.
	 */
	private final int minimumExperience;

	/**
	 * The minimum coverage for a rule.
	 */
	private final float coverageThreshold;

	/**
	 * The minimum fitness for postprocessing.
	 */
	private final double minimumFitness;

	/**
	 * The comparison mode used for getting classifiers' fitness.
	 */
	private final int comparisonMode;

	/**
	 * The constuctor.
	 * 
	 * @param exprienceThreshold
	 *            the experience threshold
	 * @param minCoverage
	 *            the coverage threshold (minimum acceptable)
	 * @param minFitness
	 *            the fitness threshold (minimum acceptable)
	 * @param fitnessComparisonMode
	 *            the fitness comparison mode
	 */
	public PostProcessPopulationControl(final int exprienceThreshold,
			final float minCoverage, final double minFitness,
			final int fitnessComparisonMode) {
		minimumExperience = exprienceThreshold;
		coverageThreshold = minCoverage;
		minimumFitness = minFitness;
		comparisonMode = fitnessComparisonMode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.classifiers.IPopulationControlStrategy#controlPopulation
	 * (gr.auth.ee.lcs.classifiers.ClassifierSet)
	 */
	@Override
	public void controlPopulation(final ClassifierSet aSet) {
		final int populationSize = aSet.getNumberOfMacroclassifiers();

		for (int i = populationSize - 1; i >= 0; i--) {
			final Classifier currentClassifier = aSet.getClassifier(i);

			final boolean notExperienced = (currentClassifier.experience < minimumExperience);
			final boolean lowCoverage = (currentClassifier.getCoverage() <= coverageThreshold);
			final boolean lowFitness = (currentClassifier
					.getComparisonValue(comparisonMode) < minimumFitness);

			if (notExperienced || lowCoverage || lowFitness) {
				while (aSet.getClassifierNumerosity(currentClassifier) > 0)
					aSet.deleteClassifier(currentClassifier);
			}
		}

		aSet.selfSubsume();

	}

}
