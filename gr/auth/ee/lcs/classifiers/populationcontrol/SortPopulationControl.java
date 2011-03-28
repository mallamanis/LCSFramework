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
 * @author Miltos Allamanis
 * 
 */
public class SortPopulationControl implements IPopulationControlStrategy {

	final int comparisonMode;

	public SortPopulationControl(final int sortFitnessMode) {
		comparisonMode = sortFitnessMode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.classifiers.IPopulationControlStrategy#controlPopulation
	 * (gr.auth.ee.lcs.classifiers.ClassifierSet)
	 */
	@Override
	public void controlPopulation(ClassifierSet aSet) {
		// Create an array to place Classifiers
		Macroclassifier[] list = new Macroclassifier[aSet
				.getNumberOfMacroclassifiers()];

		// Copy Macroclassifiers to list
		for (int i = 0; i < list.length; i++)
			list[i] = aSet.getMacroclassifier(i);

		/*
		 * Create Comparator.
		 */
		Comparator<Macroclassifier> comp = new Comparator<Macroclassifier>() {

			@Override
			public final int compare(final Macroclassifier MacroA,
					final Macroclassifier MacroB) {

				final double fitnessA = MacroA.myClassifier
						.getComparisonValue(comparisonMode);
				final double fitnessB = MacroB.myClassifier
						.getComparisonValue(comparisonMode);

				final int experienceA = MacroA.myClassifier.experience;
				final int experienceB = MacroB.myClassifier.experience;

				final double coverageA = MacroA.myClassifier.getCoverage();
				final double coverageB = MacroB.myClassifier.getCoverage();

				final double expFitA = experienceA < 10 ? 0 : fitnessA;
				final double expFitB = experienceB < 10 ? 0 : fitnessB;

				if (expFitA > expFitB)
					return -1;
				else if (expFitA < expFitB)
					return 1;

				if (coverageA > coverageB)
					return -1;
				else if (coverageA < coverageB)
					return 1;

				if (experienceA > experienceB)
					return -1;
				else if (experienceA < experienceB)
					return 1;

				return 0;
			}

		};

		Arrays.sort(list, comp);

		aSet.removeAllMacroclassifiers();

		for (int i = 0; i < list.length; i++)
			aSet.addClassifier(list[i], false);
	}

}
