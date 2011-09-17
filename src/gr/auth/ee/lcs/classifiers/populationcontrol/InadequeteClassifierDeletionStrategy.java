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
package gr.auth.ee.lcs.classifiers.populationcontrol;

import gr.auth.ee.lcs.AbstractLearningClassifierSystem;
import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.classifiers.IPopulationControlStrategy;

/**
 * Deletes all classifiers with zero coverage.
 * 
 * @stereotype ConcreteStrategy
 * 
 * @author Miltos Allamanis
 * 
 */
public class InadequeteClassifierDeletionStrategy implements
		IPopulationControlStrategy {

	final AbstractLearningClassifierSystem myLcs;

	/**
	 * The strategy constructor.
	 * 
	 * @param instanceSize
	 *            the minimum number of instances to be checked before deleting
	 *            a zero-coverage classifier.
	 */
	public InadequeteClassifierDeletionStrategy(
			final AbstractLearningClassifierSystem lcs) {
		this.myLcs = lcs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.classifiers.IPopulationControlStrategy#controlPopulation
	 * (gr.auth.ee.lcs.classifiers.ClassifierSet)
	 */
	@Override
	public final void controlPopulation(final ClassifierSet aSet) {

		final int setSize = aSet.getNumberOfMacroclassifiers();

		for (int i = setSize - 1; i >= 0; i--) {
			final Classifier aClassifier = aSet.getClassifier(i);
			final boolean zeroCoverage = (aClassifier.getCheckedInstances() >= myLcs.instances.length)
					&& (aClassifier.getCoverage() == 0);
			if (zeroCoverage)
				aSet.deleteClassifier(i);
		}

	}

}
