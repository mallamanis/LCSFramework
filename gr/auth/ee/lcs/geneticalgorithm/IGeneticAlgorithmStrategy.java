package gr.auth.ee.lcs.geneticalgorithm;

import gr.auth.ee.lcs.classifiers.ClassifierSet;

/**
 * An interface for evolving a set.
 * 
 * @author Miltos Allamanis
 */
public interface IGeneticAlgorithmStrategy {

	/**
	 * An interface for the different strategies for genetically evolving a
	 * population.
	 * 
	 * @param evolveSet
	 *            The set to evolve
	 * @param population
	 *            The population to add new classifiers
	 */
	void evolveSet(ClassifierSet evolveSet, ClassifierSet population);

}