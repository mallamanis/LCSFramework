package gr.auth.ee.lcs.geneticalgorithm;

import gr.auth.ee.lcs.classifiers.ClassifierSet;

/**
 * A generic interface for a natural selection strategy.
 * 
 * @author Miltos Allamanis
 * 
 */
public interface INaturalSelector {

	/**
	 * The selection strategy.
	 * 
	 * @param howManyToSelect
	 *            the number of classifiers to select
	 * @param fromPopulation
	 *            the ClassifierSet to select classifiers from
	 * @param toPopulation
	 *            the ClassifierSet to copy the selected classifiers
	 */
	void select(int howManyToSelect, ClassifierSet fromPopulation,
			ClassifierSet toPopulation);

}