/**
 * 
 */
package gr.auth.ee.lcs.classifiers.populationcontrol;

import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.classifiers.IPopulationControlStrategy;

/**
 * @author miltiadis
 * 
 */
public class InadequeteClassifierDeletionStrategy implements
		IPopulationControlStrategy {

	final int instanceSize;

	public InadequeteClassifierDeletionStrategy(int instanceSize) {
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
	public void controlPopulation(ClassifierSet aSet) {

		final int setSize = aSet.getNumberOfMacroclassifiers();

		for (int i = setSize - 1; i >= 0; i--) {
			final Classifier aClassifier = aSet.getClassifier(i);
			final boolean zeroCoverage = aClassifier.getCheckedInstances() >= instanceSize
					&& aClassifier.getCoverage() == 0;
			if (zeroCoverage)
				aSet.deleteClassifier(i);
		}

	}

}
