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
	 * The abstract function used to calculate the fitness of a classifier.
	 * 
	 * @param aClassifier
	 * @param numerosity
	 * @param correctSet
	 */
	public abstract void updateFitness(Classifier aClassifier, int numerosity,
			ClassifierSet correctSet);

	@Override
	public Serializable createStateClassifierObject() {
		// TODO: Initial parameters
		return new GenericSLCSClassifierData();
	}

	/**
	 * Updates the set. setA is the match set setB is the correct set
	 */
	@Override
	public final void updateSet(ClassifierSet setA, ClassifierSet setB) {
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