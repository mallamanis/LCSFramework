package gr.auth.ee.lcs.geneticalgorithm.selectors;

import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.classifiers.Macroclassifier;
import gr.auth.ee.lcs.geneticalgorithm.INaturalSelector;

/**
 * A Natural Selection operator performing a weighted roulette wheel selection.
 * 
 * @author Miltos Allamanis
 * @deprecated
 */
@Deprecated
public class WeightedRouletteSelector implements INaturalSelector {

	/**
	 * The comparison mode
	 */
	private final int mode;

	/**
	 * Constructor.
	 * 
	 * @param comparisonMode
	 *            the comparison mode
	 */
	WeightedRouletteSelector(final int comparisonMode) {
		mode = comparisonMode;
	}

	@Override
	public int select(ClassifierSet fromPopulation) {
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
	public void select(final int howManyToSelect,
			final ClassifierSet fromPopulation, final ClassifierSet toPopulation) {
		// Find total sum
		double fitnessSum = 0;
		for (int i = 0; i < fromPopulation.getNumberOfMacroclassifiers(); i++) {
			fitnessSum += fromPopulation.getClassifierNumerosity(i)
					* fromPopulation.getClassifier(i).getComparisonValue(mode);
		}

		// Repeat roulette for howManyToSelect times
		for (int i = 0; i < howManyToSelect; i++) {
			// Roulette
			double rand = Math.random() * fitnessSum;
			double tempSum = 0;
			int selectedIndex = -1;
			do {
				selectedIndex++;
				tempSum += fromPopulation
						.getClassifierNumerosity(selectedIndex)
						* fromPopulation.getClassifier(selectedIndex)
								.getComparisonValue(mode);
			} while (tempSum < rand);
			// Add selectedIndex
			toPopulation.addClassifier(
					new Macroclassifier(fromPopulation
							.getClassifier(selectedIndex), 1), false);
		} // next roulette

	}

}