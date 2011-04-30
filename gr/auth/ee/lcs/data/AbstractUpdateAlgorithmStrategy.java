package gr.auth.ee.lcs.data;

import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ClassifierSet;

import java.io.Serializable;

/**
 * An interface for representing different update strategies, depending on the
 * LCS algorithm. The implementing objects are factories because they
 * instantiate update algorithm-specific objects for the classifiers. They also
 * represent a strategy because they provide different. methods to update each
 * classifier's fitness.
 * 
 * @stereotype Strategy
 * 
 * @author Miltos Allamanis
 */
public abstract class AbstractUpdateAlgorithmStrategy {

	/**
	 * The static strategy used throughout the algorithms. Works like a
	 * singleton.
	 */
	public static AbstractUpdateAlgorithmStrategy currentStrategy;

	/**
	 * Comparison mode used for LCS exploitation.
	 */
	public static final int COMPARISON_MODE_EXPLOITATION = 0;

	/**
	 * Comparison mode used for population deletion.
	 */
	public static final int COMPARISON_MODE_DELETION = 1;

	/**
	 * Comparison mode used for exploration.
	 */
	public static final int COMPARISON_MODE_EXPLORATION = 2;

	/**
	 * Bridge to selected strategy.
	 * 
	 * @return returns the data object as specified by the implementation
	 */
	public static Serializable createDefaultDataObject() {
		if (currentStrategy != null)
			return currentStrategy.createStateClassifierObject();
		else
			return null;
	}

	/**
	 * A bridge between update data and the actual implementation.
	 * 
	 * @param population
	 *            the current population
	 * @param matchSet
	 *            the match set
	 * @param setB
	 *            the correct set
	 * @param instanceIndex
	 *            the index of the instance that will be used for updating the
	 *            population
	 */
	public static void updateData(final ClassifierSet population,
			final ClassifierSet matchSet, final int instanceIndex) {
		if (currentStrategy != null) {
			currentStrategy.updateSet(population, matchSet, instanceIndex);

		}
	}

	/**
	 * Covers an instance adding it to the population.
	 * 
	 * @param population
	 *            the population
	 * @param instanceIndex
	 *            the instance index to cover
	 */
	public abstract void cover(final ClassifierSet population,
			final int instanceIndex);

	/**
	 * Creates a data object for a classifier.
	 * 
	 * @return the data object
	 */
	public abstract Serializable createStateClassifierObject();

	/**
	 * Returns the implementation specific attribute that represents the
	 * classifier's comparison to the other's.
	 * 
	 * @param aClassifier
	 *            the classifier's value to be returned
	 * @param mode
	 *            the mode for the comparison values
	 * @return the numeric value
	 */
	public abstract double getComparisonValue(Classifier aClassifier, int mode);

	/**
	 * Returns a string with the update specific data.
	 * 
	 * @param aClassifier
	 *            the classifier used to obtain the data
	 * @return a string representation of the classifier data
	 */
	public abstract String getData(Classifier aClassifier);

	/**
	 * Perform an update knowing only the correct set and match set.
	 * 
	 * @param matchSet
	 *            the match set
	 * @param correctSet
	 *            the correct set
	 */
	public abstract void performUpdate(final ClassifierSet matchSet,
			final ClassifierSet correctSet);

	/**
	 * Set an update specific comparison value.
	 * 
	 * @param aClassifier
	 *            the classifier to set
	 * @param mode
	 *            the mode of the comparison value
	 * @param comparisonValue
	 *            the actual comparison value
	 */
	public abstract void setComparisonValue(Classifier aClassifier, int mode,
			double comparisonValue);

	/**
	 * Updates classifiers of a setA taking into consideration setB.
	 * 
	 * @param population
	 *            The problem population
	 * @param matchSet
	 *            The first set to take into consideration during update
	 * @param instanceIndex
	 *            The instance to take into consideration when updating
	 */
	protected abstract void updateSet(ClassifierSet population,
			ClassifierSet matchSet, int instanceIndex);

}