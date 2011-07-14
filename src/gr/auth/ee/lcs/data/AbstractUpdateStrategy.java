/*
 *	Copyright (C) 2011 by Allamanis Miltiadis
 *
 *	Permission is hereby granted, free of charge, to any person obtaining a copy
 *	of this software and associated documentation files (the "Software"), to deal
 *	in the Software without restriction, including without limitation the rights
 *	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *	copies of the Software, and to permit persons to whom the Software is
 *	furnished to do so, subject to the following conditions:
 *
 *	The above copyright notice and this permission notice shall be included in
 *	all copies or substantial portions of the Software.
 *
 *	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *	THE SOFTWARE.
 */
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
public abstract class AbstractUpdateStrategy {

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
	 * @param evolve
	 *            true to update the data and evolve the set
	 */
	public abstract void updateSet(ClassifierSet population,
			ClassifierSet matchSet, int instanceIndex, boolean evolve);

}