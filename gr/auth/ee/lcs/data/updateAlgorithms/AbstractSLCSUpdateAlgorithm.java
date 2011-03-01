package gr.auth.ee.lcs.data.updateAlgorithms;

import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy;
import gr.auth.ee.lcs.data.updateAlgorithms.data.GenericSLCSClassifierData;

import java.io.Serializable;

/**
 * An abstract *S-LCS update algorithm as described in Tzima-Mitkas paper.
 */
public abstract class AbstractSLCSUpdateAlgorithm extends
		UpdateAlgorithmFactoryAndStrategy {

	/**
	 * @param subsumptionFitness
	 *            the fitness threshold for subsumption
	 * @param subsumptionExperience
	 *            the experience threshold for subsumption
	 */
	public AbstractSLCSUpdateAlgorithm(final double subsumptionFitness,
			final int subsumptionExperience) {
		super(subsumptionFitness, subsumptionExperience);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy#
	 * createStateClassifierObject()
	 */
	@Override
	public Serializable createStateClassifierObject() {
		// TODO: Initial parameters
		return new GenericSLCSClassifierData();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy#setComparisonValue
	 * (gr.auth.ee.lcs.classifiers.Classifier, int, double)
	 */
	@Override
	public final void setComparisonValue(Classifier aClassifier, int mode,
			double comparisonValue) {
		GenericSLCSClassifierData data = ((GenericSLCSClassifierData) aClassifier.updateData);
		data.fitness = comparisonValue; // TODO: More generic

	}

	/**
	 * The abstract function used to calculate the fitness of a classifier.
	 * 
	 * @param aClassifier
	 *            the classifier to calculate the fitness
	 * @param numerosity
	 *            the numerosity of the given classifier
	 * @param correctSet
	 *            the correct set, used at updating the fitness
	 */
	public abstract void updateFitness(Classifier aClassifier, int numerosity,
			ClassifierSet correctSet);

	/**
	 * Updates the set. setA is the match set setB is the correct set
	 * 
	 * @see gr.auth.ee.lcs.data.UpdateAlgorithmAndStrategy.updateSet
	 * @param setA
	 *            match set
	 * @param setB
	 *            correct set
	 */
	@Override
	public final void updateSet(final ClassifierSet setA,
			final ClassifierSet setB) {
		for (int i = 0; i < setA.getNumberOfMacroclassifiers(); i++) {
			Classifier cl = setA.getClassifier(i);
			GenericSLCSClassifierData data = ((GenericSLCSClassifierData) cl.updateData);
			data.ns = (data.ns * data.msa + setB.getTotalNumerosity())
					/ (data.msa + 1);

			data.msa++;
			updateFitness(cl, setA.getClassifierNumerosity(i), setB);
			this.updateSubsumption(cl);
			cl.experience++;
		}

	}

}