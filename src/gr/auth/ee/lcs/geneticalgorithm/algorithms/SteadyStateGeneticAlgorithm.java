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
package gr.auth.ee.lcs.geneticalgorithm.algorithms;

import gr.auth.ee.lcs.AbstractLearningClassifierSystem;
import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.classifiers.Macroclassifier;
import gr.auth.ee.lcs.data.AbstractUpdateStrategy;
import gr.auth.ee.lcs.geneticalgorithm.IBinaryGeneticOperator;
import gr.auth.ee.lcs.geneticalgorithm.IGeneticAlgorithmStrategy;
import gr.auth.ee.lcs.geneticalgorithm.INaturalSelector;
import gr.auth.ee.lcs.geneticalgorithm.IUnaryGeneticOperator;

/**
 * A steady-stage GA that selects two individuals from a set (with probability
 * proportional to their total fitness) and performs a crossover and mutation
 * corrects the classifier (if needed) and adds it to the set.
 * 
 * @author Miltos Allamanis
 * 
 */
public class SteadyStateGeneticAlgorithm implements IGeneticAlgorithmStrategy {

	/**
	 * The selector used for the next generation selection.
	 */
	final private INaturalSelector gaSelector;

	/**
	 * The crossover operator that will be used by the GA.
	 */
	final private IBinaryGeneticOperator crossoverOp;

	/**
	 * The mutation operator used by the GA.
	 */
	final private IUnaryGeneticOperator mutationOp;

	/**
	 * The GA activation age. The population must have an average age, greater
	 * that this in order for the GA to run.
	 */
	private int gaActivationAge;

	/**
	 * The current timestamp. Used by the GA to count generations.
	 */
	private int timestamp = 0;

	/**
	 * The rate that the crossover is performed.
	 */
	private final float crossoverRate;

	/**
	 * The number of children per generation.
	 */
	private static final int CHILDREN_PER_GENERATION = 2;

	/**
	 * The LCS instance being used.
	 */
	private final AbstractLearningClassifierSystem myLcs;

	/**
	 * Default constructor.
	 * 
	 * @param gaSelector
	 *            the INautralSelector that selects parents for next generation
	 * @param crossoverOperator
	 *            the crossover operator that will be used
	 * @param mutationOperator
	 *            the mutation operator that will be used
	 * @param gaActivationAge
	 *            the age of the population that activates the G.A.
	 * @param crossoverRate
	 *            the rate at which the crossover operator will be called
	 * @param lcs
	 *            the LCS instance used
	 * 
	 */
	public SteadyStateGeneticAlgorithm(final INaturalSelector gaSelector,
			final IBinaryGeneticOperator crossoverOperator,
			final float crossoverRate,
			final IUnaryGeneticOperator mutationOperator,
			final int gaActivationAge,
			final AbstractLearningClassifierSystem lcs) {
		this.gaSelector = gaSelector;
		this.crossoverOp = crossoverOperator;
		this.mutationOp = mutationOperator;
		this.gaActivationAge = gaActivationAge;
		this.crossoverRate = crossoverRate;
		this.myLcs = lcs;
	}

	/**
	 * GA Setter.
	 * 
	 * @param age
	 *            the theta_GA
	 */
	public void setThetaGA(int age) {
		this.gaActivationAge = age;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.geneticalgorithm.IGeneticAlgorithmStrategy#evolveSet(gr
	 * .auth.ee.lcs.classifiers.ClassifierSet,
	 * gr.auth.ee.lcs.classifiers.ClassifierSet)
	 */
	@Override
	public final void evolveSet(final ClassifierSet evolveSet,
			final ClassifierSet population) {

		timestamp++;

		final int meanAge = getMeanAge(evolveSet);
		if (timestamp - meanAge < this.gaActivationAge)
			return;

		final int evolveSetSize = evolveSet.getNumberOfMacroclassifiers();
		for (int i = 0; i < evolveSetSize; i++) {
			evolveSet.getClassifier(i).timestamp = timestamp;
		}

		final ClassifierSet parents = new ClassifierSet(null);

		// Select parents
		gaSelector.select(1, evolveSet, parents);
		final Classifier parentA = parents.getClassifier(0);
		parents.deleteClassifier(0);
		gaSelector.select(1, evolveSet, parents);
		final Classifier parentB = parents.getClassifier(0);
		parents.deleteClassifier(0);

		// Reproduce
		for (int i = 0; i < CHILDREN_PER_GENERATION; i++) {
			Classifier child;
			// produce a child
			if (Math.random() < crossoverRate && parentA != parentB) {
				child = crossoverOp.operate((i == 0) ? parentB : parentA,
						(i == 0) ? parentA : parentB);
			} else {
				child = (Classifier) ((i == 0) ? parentA : parentB).clone();
				child.setComparisonValue(
						AbstractUpdateStrategy.COMPARISON_MODE_EXPLORATION,
						((i == 0) ? parentA : parentB)
								.getComparisonValue(AbstractUpdateStrategy.COMPARISON_MODE_EXPLORATION));
			}

			child = mutationOp.operate(child);
			myLcs.getClassifierTransformBridge().fixChromosome(child);
			population.addClassifier(new Macroclassifier(child, 1), true);

		}

	}

	/**
	 * Get the population mean age.
	 * 
	 * @param set
	 *            the set of classifiers to find the mean age
	 * @return an int representing the set's mean age (rounded)
	 */
	private int getMeanAge(final ClassifierSet set) {
		int meanAge = 0;
		// Cache value for optimization
		final int evolveSetSize = set.getNumberOfMacroclassifiers();

		for (int i = 0; i < evolveSetSize; i++) {
			meanAge += set.getClassifierNumerosity(i)
					* set.getClassifier(i).timestamp;
		}
		meanAge /= ((double) set.getTotalNumerosity());

		return meanAge;
	}

}