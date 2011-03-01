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
 * @author Miltos Allamanis
 */
public abstract class UpdateAlgorithmFactoryAndStrategy {

	/**
	 * The static strategy used throughout the algorithms. Works like a
	 * singleton.
	 */
	public static UpdateAlgorithmFactoryAndStrategy currentStrategy;

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
	 * @param setA
	 *            the match set
	 * @param setB
	 *            the correct set
	 */
	public static void updateData(final ClassifierSet setA,
			final ClassifierSet setB) {
		if (currentStrategy != null) {
			currentStrategy.updateSet(setA, setB);

		}
	}

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
	 * Creates a data object for a classifier.
	 * 
	 * @return the data object
	 */
	protected abstract Serializable createStateClassifierObject();

	/**
	 * Updates classifiers of a setA taking into consideration setB.
	 * 
	 * @param setA
	 *            The first set to take into consideration during update
	 * @param setB
	 *            The second set to take into consideration during update
	 */
	protected abstract void updateSet(ClassifierSet setA, ClassifierSet setB);

}