package gr.auth.ee.lcs.geneticalgorithm.selectors;

import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.classifiers.Macroclassifier;
import gr.auth.ee.lcs.geneticalgorithm.INaturalSelector;

/**
 * A Natural Selection operator performing a weighted roulette wheel selection.
 * This implementation contracts that all classifier have positive values of
 * fitness. TODO: Throw exception otherwise?
 * 
 * @author Miltos Allamanis
 * 
 */

public class WeightedRouletteSelector implements INaturalSelector {

	/**
	 * The comparison mode used for fitness selecting.
	 */
	private final int mode;

	/**
	 * Private variable for selecting maximum or minimum selection.
	 */
	private final boolean max;

	/**
	 * Constructor.
	 * 
	 * @param comparisonMode
	 *            the comparison mode
	 * @param max
	 *            whether the selector selects min or max fitness (when max,
	 *            max=true)
	 */
	public WeightedRouletteSelector(final int comparisonMode, final boolean max) {
		mode = comparisonMode;
		this.max = max;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.geneticalgorithm.INaturalSelector#select(gr.auth.ee.lcs
	 * .classifiers.ClassifierSet)
	 */
	@Override
	public final int select(final ClassifierSet fromPopulation) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Roulette Wheel selection strategy.
	 * 
	 * @param howManyToSelect
	 *            the number of draws.
	 * @param fromPopulation
	 *            the ClassifierSet from which the selection will take place
	 * @param toPopulation
	 *            the ClassifierSet to which the selected Classifers will be
	 *            added
	 */
	@Override
	public final void select(final int howManyToSelect,
			final ClassifierSet fromPopulation, final ClassifierSet toPopulation) {
		// Find total sum
		double fitnessSum = 0;
		for (int i = 0; i < fromPopulation.getNumberOfMacroclassifiers(); i++) {
			final double fitnessValue = fromPopulation
					.getClassifierNumerosity(i)
					* fromPopulation.getClassifier(i).getComparisonValue(mode);
			fitnessSum += max ? fitnessValue : 1 / (fitnessValue);
		}

		// Repeat roulette for howManyToSelect times
		for (int i = 0; i < howManyToSelect; i++) {
			// Roulette
			final double rand = Math.random() * fitnessSum;
			double tempSum = 0;
			int selectedIndex = -1;
			do {
				selectedIndex++;
				final double tempValue = fromPopulation
						.getClassifierNumerosity(selectedIndex)
						* fromPopulation.getClassifier(selectedIndex)
								.getComparisonValue(mode);
				tempSum += max ? tempValue : 1 / (tempValue);
			} while (tempSum < rand);
			// Add selectedIndex
			toPopulation.addClassifier(
					new Macroclassifier(fromPopulation
							.getClassifier(selectedIndex), 1), false);
		} // next roulette

	}
}