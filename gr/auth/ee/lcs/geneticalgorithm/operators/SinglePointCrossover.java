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
package gr.auth.ee.lcs.geneticalgorithm.operators;

import gr.auth.ee.lcs.AbstractLearningClassifierSystem;
import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.data.AbstractUpdateStrategy;
import gr.auth.ee.lcs.geneticalgorithm.IBinaryGeneticOperator;
import gr.auth.ee.lcs.utilities.ExtendedBitSet;

/**
 * A binary genetic operator that performs gene crossover at a single point.
 * 
 * @author Miltos Allamanis
 */
public class SinglePointCrossover implements IBinaryGeneticOperator {

	/**
	 * The LCS instance being used.
	 */
	final AbstractLearningClassifierSystem myLcs;

	/**
	 * Constructor.
	 * 
	 * @param lcs
	 *            the lcs to be used for creating new classifiers
	 */
	public SinglePointCrossover(AbstractLearningClassifierSystem lcs) {
		myLcs = lcs;
	}

	/**
	 * The implementation of the abstract method.
	 * 
	 * @see gr.auth.ee.lcs.geneticalgorithm.IBinaryGeneticOperator
	 */
	@Override
	public final Classifier operate(final Classifier classifierA,
			final Classifier classifierB) {
		final int chromosomeSize = classifierB.size();
		final Classifier child;
		/*
		 * The point at which the crossover will occur
		 */
		final int mutationPoint = (int) Math.round(Math.random()
				* chromosomeSize - 1);
		child = myLcs.getNewClassifier(performCrossover(classifierA,
				classifierB, mutationPoint));
		double newFitness = classifierA
				.getComparisonValue(AbstractUpdateStrategy.COMPARISON_MODE_EXPLOITATION)
				+ classifierB
						.getComparisonValue(AbstractUpdateStrategy.COMPARISON_MODE_EXPLOITATION);
		newFitness /= 2;
		child.setComparisonValue(
				AbstractUpdateStrategy.COMPARISON_MODE_EXPLOITATION, newFitness);
		// TODO: Set specific update data
		return child;
	}

	/**
	 * A protected function that performs a single point crossover.
	 * 
	 * @param chromosomeA
	 *            the first chromosome to crossover
	 * @param chromosomeB
	 *            the second chromosome to crossover
	 * @param position
	 *            the position (bit) to perform the crossover
	 * @return the new cross-overed (child) chromosome
	 */
	protected final ExtendedBitSet performCrossover(
			final ExtendedBitSet chromosomeA, final ExtendedBitSet chromosomeB,
			final int position) {
		final ExtendedBitSet child = (ExtendedBitSet) chromosomeA.clone();
		child.setSubSet(position,
				chromosomeB.getSubSet(position, chromosomeB.size() - position));

		return child;
	}

}