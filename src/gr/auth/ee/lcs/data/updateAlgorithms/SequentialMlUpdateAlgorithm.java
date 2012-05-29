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
/**
 * 
 */
package gr.auth.ee.lcs.data.updateAlgorithms;

import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.classifiers.Macroclassifier;
import gr.auth.ee.lcs.data.AbstractUpdateStrategy;
import gr.auth.ee.lcs.geneticalgorithm.IGeneticAlgorithmStrategy;

import java.io.Serializable;
import java.util.Random;

/**
 * @author Miltos Allamanis
 * 
 */
public class SequentialMlUpdateAlgorithm extends AbstractUpdateStrategy {

	/**
	 * The update strategy used by the problem.
	 */
	private final AbstractUpdateStrategy strategy;

	/**
	 * The GA to be used at updating.
	 */
	private final IGeneticAlgorithmStrategy ga;

	/**
	 * Number of labels used.
	 */
	private final int numberOfLabels;

	/**
	 * Constructor.
	 * 
	 * @param updateMethod
	 *            the update method to use
	 * @param numberOfLabels
	 *            the number of labels
	 * @param geneticAlgorithm
	 *            the GA to be used
	 */
	public SequentialMlUpdateAlgorithm(
			final AbstractUpdateStrategy updateMethod,
			final IGeneticAlgorithmStrategy geneticAlgorithm, int numberOfLabels) {
		this.strategy = updateMethod;
		this.numberOfLabels = numberOfLabels;
		this.ga = geneticAlgorithm;
	}

	/**
	 * Calls covering operator.
	 * 
	 * @param population
	 *            the population where the new classifier will be added to.
	 * @param instanceIndex
	 *            the index of the current sample
	 */
	@Override
	public void cover(final ClassifierSet population, final int instanceIndex) {
		strategy.cover(population, instanceIndex);
	}

	@Override
	public Serializable createStateClassifierObject() {

		return strategy.createStateClassifierObject();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy#getComparisonValue
	 * (gr.auth.ee.lcs.classifiers.Classifier, int)
	 */
	@Override
	public final double getComparisonValue(final Classifier aClassifier,
			final int mode) {
		return strategy.getComparisonValue(aClassifier, mode);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy#getData(gr.auth
	 * .ee.lcs.classifiers.Classifier)
	 */
	@Override
	public final String getData(final Classifier aClassifier) {
		return strategy.getData(aClassifier);
	}

	@Override
	public void inheritParentParameters(Classifier parentA, Classifier parentB,
			Classifier child) {
		strategy.inheritParentParameters(parentA, parentB, child);

	}

	/**
	 * Perform an update to the set.
	 * 
	 * @param matchSet
	 * @param correctSet
	 */
	@Override
	public void performUpdate(final ClassifierSet matchSet,
			final ClassifierSet correctSet) {

		strategy.performUpdate(matchSet, correctSet);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy#setComparisonValue
	 * (gr.auth.ee.lcs.classifiers.Classifier, int, double)
	 */
	@Override
	public final void setComparisonValue(final Classifier aClassifier,
			final int mode, final double comparisonValue) {
		strategy.setComparisonValue(aClassifier, mode, comparisonValue);
	}

	@Override
	public final void updateSet(final ClassifierSet population,
			final ClassifierSet matchSet, final int instanceIndex,
			final boolean evolve) {
		// Generate random labels
		final int[] labelSequence = new int[numberOfLabels];
		for (int i = 0; i < numberOfLabels; i++) {
			labelSequence[i] = i;
		}

		// Shuffle
		final Random rgen = new Random();
		for (int i = 0; i < numberOfLabels; i++) {
			final int randomPosition = rgen.nextInt(numberOfLabels);
			final int temp = labelSequence[i];
			labelSequence[i] = labelSequence[randomPosition];
			labelSequence[randomPosition] = temp;
		}

		// for each label loop
		for (int label = 0; label < this.numberOfLabels; label++) {
			/*
			 * Generate label set
			 */
			final ClassifierSet labelSet = generateLabelMatchSet(matchSet,
					instanceIndex, label);

			/*
			 * Generate correct set
			 */
			final ClassifierSet correctSet = generateCorrectSet(labelSet,
					instanceIndex, label);

			/*
			 * Cover if necessary
			 */
			if (correctSet.getNumberOfMacroclassifiers() == 0) {
				if (evolve)
					cover(population, instanceIndex);
				continue;
			}

			/*
			 * Update
			 */
			performUpdate(labelSet, correctSet);

			/*
			 * Run GA
			 */
			if ((correctSet.getNumberOfMacroclassifiers() > 1) && evolve)
				ga.evolveSet(correctSet, population);
			else if ((correctSet.getNumberOfMacroclassifiers() > 0) && evolve)
				ga.evolveSet(labelSet, population);
			else if (evolve)
				ga.evolveSet(matchSet, population);
		}

		// Delete classifiers that are only #'s
		final int matchSetSize = matchSet.getNumberOfMacroclassifiers();
		for (int i = (matchSetSize - 1); i >= 0; i--) {
			final Classifier cl = matchSet.getClassifier(i);
			if (cl.experience == 0)
				population.deleteClassifier(cl);
		}

	}

	/**
	 * Generates the correct set.
	 * 
	 * @param matchSet
	 *            the match set
	 * @param instanceIndex
	 *            the global instance index
	 * @param labelIndex
	 *            the label index
	 * @return the correct set
	 */
	private ClassifierSet generateCorrectSet(final ClassifierSet matchSet,
			final int instanceIndex, final int labelIndex) {
		final ClassifierSet correctSet = new ClassifierSet(null);
		final int matchSetSize = matchSet.getNumberOfMacroclassifiers();
		for (int i = 0; i < matchSetSize; i++) {
			Macroclassifier cl = matchSet.getMacroclassifier(i);
			if (cl.myClassifier.classifyLabelCorrectly(instanceIndex,
					labelIndex) > 0)
				correctSet.addClassifier(cl, false);
		}
		return correctSet;
	}

	/**
	 * Generates the set of classifier that advocate clearly against or for the
	 * label at the given index.
	 * 
	 * @param matchSet
	 *            the matchSet
	 * @param instanceIndex
	 *            the instance we are trying to match
	 * @param labelIndex
	 *            the label index we are trying to match
	 * @return a classifier set containing the LabelMatchSet of the given
	 *         matchset
	 */
	private ClassifierSet generateLabelMatchSet(final ClassifierSet matchSet,
			final int instanceIndex, final int labelIndex) {
		final ClassifierSet labelMatchSet = new ClassifierSet(null);
		final int matchSetSize = matchSet.getNumberOfMacroclassifiers();
		for (int i = 0; i < matchSetSize; i++) {
			Macroclassifier cl = matchSet.getMacroclassifier(i);
			if (cl.myClassifier.classifyLabelCorrectly(instanceIndex,
					labelIndex) != 0)
				labelMatchSet.addClassifier(cl, false);
		}
		return labelMatchSet;
	}

}
