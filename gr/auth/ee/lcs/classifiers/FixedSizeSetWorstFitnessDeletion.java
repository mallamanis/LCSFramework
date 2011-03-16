package gr.auth.ee.lcs.classifiers;

import gr.auth.ee.lcs.geneticalgorithm.INaturalSelector;

/**
 * A fixed size control strategy. Classifiers are deleted based on the selector
 * tournaments
 * 
 * @author Miltos Allamanis
 * 
 */
public class FixedSizeSetWorstFitnessDeletion implements
		IPopulationControlStrategy {

	/**
	 * The Natural Selector used to select the the classifier to be deleted.
	 */
	private INaturalSelector mySelector;

	/**
	 * The fixed population size of the controlled set.
	 */
	private int populationSize;

	/**
	 * Constructor of deletion strategy.
	 * 
	 * @param maxPopulationSize
	 *            the size that the population will have
	 * @param selector
	 *            the selector used for deleting
	 */
	public FixedSizeSetWorstFitnessDeletion(final int maxPopulationSize,
			final INaturalSelector selector) {
		this.populationSize = maxPopulationSize;
		mySelector = selector;
	}

	/**
	 * @param aSet
	 *            the set to control
	 * @see gr.auth.ee.lcs.classifiers.IPopulationControlStrategy#controlPopulation(gr.auth.ee.lcs.classifiers.ClassifierSet)
	 */
	@Override
	public final void controlPopulation(final ClassifierSet aSet) {
		ClassifierSet toBeDeleted = new ClassifierSet(null);
		while (aSet.getTotalNumerosity() > populationSize) {
			mySelector.select(1, aSet, toBeDeleted);
			aSet.deleteClassifier(toBeDeleted.getClassifier(0));
			toBeDeleted.deleteClassifier(0);
		}

	}

}
