/**
 * 
 */
package gr.auth.ee.lcs.classifiers.populationcontrol;

import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.classifiers.IPopulationControlStrategy;
import gr.auth.ee.lcs.classifiers.Macroclassifier;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Sort the population.
 * 
 * @stereotype ConcreteStrategy
 * 
 * @author Miltos Allamanis
 * 
 */
public class SortPopulationControl implements IPopulationControlStrategy {

	/**
	 * The comparison mode to be used for sorting.
	 */
	private final int comparisonMode;

	/**
	 * Constructor.
	 * 
	 * @param sortFitnessMode
	 *            the comparison mode to be used for sorting
	 */
	public SortPopulationControl(final int sortFitnessMode) {
		comparisonMode = sortFitnessMode;
	}

	@Override
	public final void controlPopulation(final ClassifierSet aSet) {
		// Create an array to place Classifiers
		final Macroclassifier[] list = new Macroclassifier[aSet
				.getNumberOfMacroclassifiers()];

		// Copy Macroclassifiers to list
		for (int i = 0; i < list.length; i++) {
			list[i] = aSet.getMacroclassifier(i);
		}

		/*
		 * Create Comparator.
		 */
		final Comparator<Macroclassifier> comp = new Comparator<Macroclassifier>() {

			@Override
			public final int compare(final Macroclassifier macroA,
					final Macroclassifier macroB) {

				final double fitnessA = macroA.myClassifier
						.getComparisonValue(comparisonMode) * macroA.numerosity;
				final double fitnessB = macroB.myClassifier
						.getComparisonValue(comparisonMode) * macroB.numerosity;

				final int experienceA = macroA.myClassifier.experience;
				final int experienceB = macroB.myClassifier.experience;

				final double coverageA = macroA.myClassifier.getCoverage();
				final double coverageB = macroB.myClassifier.getCoverage();

				final int experienceThreshold = 10;

				final double expFitA = (experienceA < experienceThreshold) ? 0
						: fitnessA;
				final double expFitB = (experienceB < experienceThreshold) ? 0
						: fitnessB;

				if (expFitA > expFitB) {
					return -1;
				} else if (expFitA < expFitB) {
					return 1;
				}

				if (coverageA > coverageB) {
					return -1;
				} else if (coverageA < coverageB) {
					return 1;
				}

				if (experienceA > experienceB) {
					return -1;
				} else if (experienceA < experienceB) {
					return 1;
				}

				return 0;
			}

		};

		Arrays.sort(list, comp);

		aSet.removeAllMacroclassifiers();

		for (int i = 0; i < list.length; i++) {
			aSet.addClassifier(list[i], false);
		}
	}

}
