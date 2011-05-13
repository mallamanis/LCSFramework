/**
 * 
 */
package gr.auth.ee.lcs.tests.mocks;

import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.data.ClassifierTransformBridge;
import gr.auth.ee.lcs.utilities.ExtendedBitSet;

/**
 * A mock transform bridge.
 * 
 * @author Miltos Allamanis
 * 
 */
public class MockTransformBridge extends ClassifierTransformBridge {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.data.ClassifierTransformBridge#areEqual(gr.auth.ee.lcs
	 * .classifiers.Classifier, gr.auth.ee.lcs.classifiers.Classifier)
	 */
	@Override
	public boolean areEqual(Classifier cl1, Classifier cl2) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.data.ClassifierTransformBridge#buildRepresentationModel()
	 */
	@Override
	public void buildRepresentationModel() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.data.ClassifierTransformBridge#classify(gr.auth.ee.lcs
	 * .classifiers.ClassifierSet, double[])
	 */
	@Override
	public int[] classify(ClassifierSet aSet, double[] visionVector) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.data.ClassifierTransformBridge#classifyAbilityAll(gr.auth
	 * .ee.lcs.classifiers.Classifier, int)
	 */
	@Override
	public float classifyAbilityAll(Classifier aClassifier, int instanceIndex) {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.data.ClassifierTransformBridge#classifyAbilityLabel(gr
	 * .auth.ee.lcs.classifiers.Classifier, int, int)
	 */
	@Override
	public float classifyAbilityLabel(Classifier aClassifier,
			int instanceIndex, int label) {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.data.ClassifierTransformBridge#createRandomCoveringClassifier
	 * (double[])
	 */
	@Override
	public Classifier createRandomCoveringClassifier(double[] visionVector) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.data.ClassifierTransformBridge#fixChromosome(gr.auth.ee
	 * .lcs.utilities.ExtendedBitSet)
	 */
	@Override
	public void fixChromosome(ExtendedBitSet aChromosome) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gr.auth.ee.lcs.data.ClassifierTransformBridge#getChromosomeSize()
	 */
	@Override
	public int getChromosomeSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.data.ClassifierTransformBridge#getClassification(gr.auth
	 * .ee.lcs.classifiers.Classifier)
	 */
	@Override
	public int[] getClassification(Classifier aClassifier) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.data.ClassifierTransformBridge#getDataInstanceLabels(double
	 * [])
	 */
	@Override
	public int[] getDataInstanceLabels(double[] dataInstance) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gr.auth.ee.lcs.data.ClassifierTransformBridge#getLabelNames()
	 */
	@Override
	public String[] getLabelNames() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gr.auth.ee.lcs.data.ClassifierTransformBridge#isMatch(double[],
	 * gr.auth.ee.lcs.utilities.ExtendedBitSet)
	 */
	@Override
	public boolean isMatch(double[] visionVector, ExtendedBitSet chromosome) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.data.ClassifierTransformBridge#isMoreGeneral(gr.auth.ee
	 * .lcs.classifiers.Classifier, gr.auth.ee.lcs.classifiers.Classifier)
	 */
	@Override
	public boolean isMoreGeneral(Classifier baseClassifier,
			Classifier testClassifier) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.data.ClassifierTransformBridge#setClassification(gr.auth
	 * .ee.lcs.classifiers.Classifier, int)
	 */
	@Override
	public void setClassification(Classifier aClassifier, int action) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gr.auth.ee.lcs.data.ClassifierTransformBridge#
	 * setRepresentationSpecificClassifierData
	 * (gr.auth.ee.lcs.classifiers.Classifier)
	 */
	@Override
	public void setRepresentationSpecificClassifierData(Classifier aClassifier) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.data.ClassifierTransformBridge#toBitSetString(gr.auth.
	 * ee.lcs.classifiers.Classifier)
	 */
	@Override
	public String toBitSetString(Classifier classifier) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.data.ClassifierTransformBridge#toNaturalLanguageString
	 * (gr.auth.ee.lcs.classifiers.Classifier)
	 */
	@Override
	public String toNaturalLanguageString(Classifier aClassifier) {
		// TODO Auto-generated method stub
		return null;
	}

}
