package gr.auth.ee.lcs.geneticalgorithm.selectors;

import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.classifiers.Macroclassifier;
import gr.auth.ee.lcs.geneticalgorithm.INaturalSelector;

/**
 * Selects and adds the best classifier (based on fitness) from the inital
 * ClassifierSet to the target set. It adds the best classifier with
 * howManyToSelect numerosity.
 * 
 * @author Miltos Allamanis
 * 
 */
public class BestClassifierSelector implements INaturalSelector {

	/**
	 * Boolean indicating if the selector selects the best or worst classifier.
	 */
	private boolean max = true;

	/**
	 * The mode used for comparing classifiers.
	 */
	private int mode;

	/**
	 * Default constructor.
	 * 
	 * @param maximum
	 *            if by best we mean the max fitness then true, else false
	 * @param comparisonMode
	 *            the mode of the values taken
	 */
	public BestClassifierSelector(final boolean maximum,
			final int comparisonMode) {
		this.max = maximum;
		this.mode = comparisonMode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gr.auth.ee.lcs.geneticalgorithm.INaturalSelector#select(int,
	 * gr.auth.ee.lcs.classifiers.ClassifierSet,
	 * gr.auth.ee.lcs.classifiers.ClassifierSet)
	 */
	@Override
	public final void select(final int howManyToSelect,
			final ClassifierSet fromPopulation, final ClassifierSet toPopulation) {
		// Add it toPopulation
		int bestIndex = select(fromPopulation);
		if (bestIndex == -1)
			return;
		toPopulation.addClassifier(
				new Macroclassifier(fromPopulation.getClassifier(bestIndex),
						howManyToSelect), true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.geneticalgorithm.INaturalSelector#select(gr.auth.ee.lcs
	 * .classifiers.ClassifierSet)
	 */
	@Override
	public int select(final ClassifierSet fromPopulation) {
		// Search for the best classifier
		double bestFitness = max ? Double.NEGATIVE_INFINITY
				: Double.POSITIVE_INFINITY;
		int bestExp = 0;
		int bestIndex = -1;
		final int popSize = fromPopulation.getNumberOfMacroclassifiers();
		for (int i = 0; i < popSize; i++) {
			double temp = fromPopulation.getClassifier(i).getComparisonValue(
					mode);
			if ((max ? 1. : -1.) * (temp - bestFitness) > 0) {
				bestFitness = temp;
				bestIndex = i;
				bestExp = fromPopulation.getClassifier(i).experience;
			} else if (temp == bestFitness
					&& fromPopulation.getClassifier(i).experience > bestExp) {
				bestFitness = temp;
				bestIndex = i;
				bestExp = fromPopulation.getClassifier(i).experience;
			}
		}

		return bestIndex;
	}

}