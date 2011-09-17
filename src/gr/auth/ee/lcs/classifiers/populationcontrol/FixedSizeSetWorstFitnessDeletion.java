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
package gr.auth.ee.lcs.classifiers.populationcontrol;

import gr.auth.ee.lcs.AbstractLearningClassifierSystem;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.classifiers.IPopulationControlStrategy;
import gr.auth.ee.lcs.geneticalgorithm.INaturalSelector;

/**
 * A fixed size control strategy. Classifiers are deleted based on the selector
 * tournaments
 * 
 * @stereotype ConcreteStrategy
 * 
 * @author Miltos Allamanis
 * 
 */
public class FixedSizeSetWorstFitnessDeletion implements
		IPopulationControlStrategy {

	/**
	 * The Natural Selector used to select the the classifier to be deleted.
	 */
	private final INaturalSelector mySelector;

	/**
	 * The fixed population size of the controlled set.
	 */
	private final int populationSize;

	/**
	 * Removes all zero coverage rules
	 */
	private InadequeteClassifierDeletionStrategy zeroCoverageRemoval;

	/**
	 * Constructor of deletion strategy.
	 * 
	 * @param maxPopulationSize
	 *            the size that the population will have
	 * @param selector
	 *            the selector used for deleting
	 */
	public FixedSizeSetWorstFitnessDeletion(
			final AbstractLearningClassifierSystem lcs,
			final int maxPopulationSize, final INaturalSelector selector) {
		this.populationSize = maxPopulationSize;
		mySelector = selector;

		zeroCoverageRemoval = new InadequeteClassifierDeletionStrategy(lcs);
	}

	/**
	 * @param aSet
	 *            the set to control
	 * @see gr.auth.ee.lcs.classifiers.IPopulationControlStrategy#controlPopulation(gr.auth.ee.lcs.classifiers.ClassifierSet)
	 */
	@Override
	public final void controlPopulation(final ClassifierSet aSet) {

		final ClassifierSet toBeDeleted = new ClassifierSet(null);
		if (aSet.getTotalNumerosity() > populationSize)
			zeroCoverageRemoval.controlPopulation(aSet);
		while (aSet.getTotalNumerosity() > populationSize) {
			mySelector.select(1, aSet, toBeDeleted);
			aSet.deleteClassifier(toBeDeleted.getClassifier(0));
			toBeDeleted.deleteClassifier(0);
		}
	}

}
