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

import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.geneticalgorithm.IUnaryGeneticOperator;

/**
 * Implements a mutation operator. Bits of a chromosome are mutated by following
 * a uniform distribution for each one.
 * 
 * @author Miltos Allamanis
 * 
 */
public class UniformBitMutation implements IUnaryGeneticOperator {

	/**
	 * The rate at which the mutation happens.
	 */
	final private double mutationRate;

	/**
	 * The default constructor.
	 * 
	 * @param rate
	 *            the probability that a bit will be flipped Initializes the
	 *            operator's attributes.
	 */
	public UniformBitMutation(final double rate) {
		this.mutationRate = rate;
	}

	/**
	 * operates on the given classifier by mutating its bits.
	 * 
	 * @param aClassifier
	 *            the classifier to operate on
	 * @return the mutated classifier
	 */
	@Override
	public final Classifier operate(final Classifier aClassifier) {
		final int chromosomeSize = aClassifier.size();

		for (int i = 0; i < chromosomeSize; i++) {
			if (Math.random() < mutationRate)
				aClassifier.invert(i);
		}
		return aClassifier;
	}

}