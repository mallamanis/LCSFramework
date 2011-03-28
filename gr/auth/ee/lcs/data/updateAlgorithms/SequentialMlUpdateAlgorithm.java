/**
 * 
 */
package gr.auth.ee.lcs.data.updateAlgorithms;

import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.classifiers.Macroclassifier;
import gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy;
import gr.auth.ee.lcs.geneticalgorithm.IGeneticAlgorithmStrategy;

import java.io.Serializable;
import java.util.Random;

/**
 * @author Miltos Allamanis
 * 
 */
public class SequentialMlUpdateAlgorithm extends
		UpdateAlgorithmFactoryAndStrategy {

	private final UpdateAlgorithmFactoryAndStrategy strategy;

	private final IGeneticAlgorithmStrategy ga;

	/**
	 * Number of labels used
	 */
	private final int numberOfLabels;

	/**
	 * Constructor
	 * 
	 * @param updateMethod
	 *            the update method to use
	 * @param numberOfLabels
	 *            the number of labels
	 * @param geneticAlgorithm
	 *            the GA to be used
	 */
	public SequentialMlUpdateAlgorithm(
			final UpdateAlgorithmFactoryAndStrategy updateMethod,
			final IGeneticAlgorithmStrategy geneticAlgorithm, int numberOfLabels) {
		this.strategy = updateMethod;
		this.numberOfLabels = numberOfLabels;
		this.ga = geneticAlgorithm;
	}

	/**
	 * Calls covering operator.
	 * 
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
		ClassifierSet correctSet = new ClassifierSet(null);
		final int matchSetSize = matchSet.getNumberOfMacroclassifiers();
		for (int i = 0; i < matchSetSize; i++) {
			Macroclassifier cl = matchSet.getMacroclassifier(i);
			if (cl.myClassifier.classifyCorrectly(instanceIndex) > 0)
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
	 * @return
	 */
	private ClassifierSet generateLabelMatchSet(final ClassifierSet matchSet,
			final int instanceIndex, final int labelIndex) {
		ClassifierSet labelMatchSet = new ClassifierSet(null);
		final int matchSetSize = matchSet.getNumberOfMacroclassifiers();
		for (int i = 0; i < matchSetSize; i++) {
			Macroclassifier cl = matchSet.getMacroclassifier(i);
			if (cl.myClassifier.classifyLabelCorrectly(instanceIndex,
					labelIndex) != 0)
				labelMatchSet.addClassifier(cl, false);
		}
		return labelMatchSet;
	}

	@Override
	protected final void updateSet(final ClassifierSet population,
			final ClassifierSet matchSet, final int instanceIndex) {
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
				cover(population, instanceIndex);
				continue;
			}

			/*
			 * Update
			 */
			performUpdate(labelSet, correctSet);
		}

		/*
		 * Run GA
		 */
		if (matchSet.getNumberOfMacroclassifiers() > 0)
			ga.evolveSet(matchSet, population);

	}

}
