/**
 * 
 */
package gr.auth.ee.lcs.classifiers.populationcontrol;

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

	/**
	 * The minimum number of instances that should have been checked before a
	 * classifier can be considered inadequate.
	 */
	private final int instanceSize;

	/**
	 * The strategy constructor.
	 * 
	 * @param instanceSize
	 *            the minimum number of instances to be checked before deleting
	 *            a zero-coverage classifier.
	 */
	public InadequeteClassifierDeletionStrategy(final int instanceSize) {
		this.instanceSize = instanceSize;
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
			final boolean zeroCoverage = (aClassifier.getCheckedInstances() >= instanceSize)
					&& (aClassifier.getCoverage() == 0);
			if (zeroCoverage)
				aSet.deleteClassifier(i);
		}

	}

}
