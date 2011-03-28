/**
 * 
 */
package gr.auth.ee.lcs.data.representations;

import java.io.IOException;

import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.classifiers.ExtendedBitSet;
import gr.auth.ee.lcs.data.ClassifierTransformBridge;

/**
 * A multi-label representation using only one label. 
 * @author Miltos Allamanis
 *
 */
public class UniLabelRepresentation extends ClassifierTransformBridge {

	private SingleClassRepresentation rep;
	
	private final int numberOfLabels;
	
	public UniLabelRepresentation(final String inputArff, final int precision, final int labels) throws IOException {
		rep = new SingleClassRepresentation(inputArff,precision,labels);
		numberOfLabels = labels;
	}
	
	/* (non-Javadoc)
	 * @see gr.auth.ee.lcs.data.ClassifierTransformBridge#getLabelNames()
	 */
	@Override
	public String[] getLabelNames() {
		return rep.getLabelNames();
	}

	/* (non-Javadoc)
	 * @see gr.auth.ee.lcs.data.ClassifierTransformBridge#areEqual(gr.auth.ee.lcs.classifiers.Classifier, gr.auth.ee.lcs.classifiers.Classifier)
	 */
	@Override
	public boolean areEqual(Classifier cl1, Classifier cl2) {
		return rep.areEqual(cl1, cl2);
	}

	/* (non-Javadoc)
	 * @see gr.auth.ee.lcs.data.ClassifierTransformBridge#buildRepresentationModel()
	 */
	@Override
	public void buildRepresentationModel() {
		rep.buildRepresentationModel();
	}

	/* (non-Javadoc)
	 * @see gr.auth.ee.lcs.data.ClassifierTransformBridge#createRandomCoveringClassifier(double[])
	 */
	@Override
	public Classifier createRandomCoveringClassifier(double[] visionVector) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see gr.auth.ee.lcs.data.ClassifierTransformBridge#fixChromosome(gr.auth.ee.lcs.classifiers.ExtendedBitSet)
	 */
	@Override
	public void fixChromosome(ExtendedBitSet aChromosome) {
		rep.fixChromosome(aChromosome);

	}

	/* (non-Javadoc)
	 * @see gr.auth.ee.lcs.data.ClassifierTransformBridge#getChromosomeSize()
	 */
	@Override
	public int getChromosomeSize() {
		return rep.getChromosomeSize();
	}

	/* (non-Javadoc)
	 * @see gr.auth.ee.lcs.data.ClassifierTransformBridge#classifyAbilityAll(gr.auth.ee.lcs.classifiers.Classifier, int)
	 */
	@Override
	public float classifyAbilityAll(Classifier aClassifier, int instanceIndex) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see gr.auth.ee.lcs.data.ClassifierTransformBridge#classifyAbilityLabel(gr.auth.ee.lcs.classifiers.Classifier, int, int)
	 */
	@Override
	public float classifyAbilityLabel(Classifier aClassifier,
			int instanceIndex, int label) {
		if (rep.getClassification(aClassifier)[0]==label)
			return 1;
		else
			return 0;
	}

	/* (non-Javadoc)
	 * @see gr.auth.ee.lcs.data.ClassifierTransformBridge#getClassification(gr.auth.ee.lcs.classifiers.Classifier)
	 */
	@Override
	public int[] getClassification(Classifier aClassifier) {
		return rep.getClassification(aClassifier);
	}

	/* (non-Javadoc)
	 * @see gr.auth.ee.lcs.data.ClassifierTransformBridge#classify(gr.auth.ee.lcs.classifiers.ClassifierSet, double[])
	 */
	@Override
	public int[] classify(ClassifierSet aSet, double[] visionVector) {
		return rep.classify(aSet, visionVector);
	}

	/* (non-Javadoc)
	 * @see gr.auth.ee.lcs.data.ClassifierTransformBridge#isMatch(double[], gr.auth.ee.lcs.classifiers.ExtendedBitSet)
	 */
	@Override
	public boolean isMatch(double[] visionVector, ExtendedBitSet chromosome) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see gr.auth.ee.lcs.data.ClassifierTransformBridge#getDataInstanceLabels(double[])
	 */
	@Override
	public int[] getDataInstanceLabels(double[] dataInstance) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see gr.auth.ee.lcs.data.ClassifierTransformBridge#isMoreGeneral(gr.auth.ee.lcs.classifiers.Classifier, gr.auth.ee.lcs.classifiers.Classifier)
	 */
	@Override
	public boolean isMoreGeneral(Classifier baseClassifier,
			Classifier testClassifier) {
		return rep.isMoreGeneral(baseClassifier, testClassifier);
	}

	/* (non-Javadoc)
	 * @see gr.auth.ee.lcs.data.ClassifierTransformBridge#setClassification(gr.auth.ee.lcs.classifiers.Classifier, int)
	 */
	@Override
	public void setClassification(Classifier aClassifier, int action) {
		rep.setClassification(aClassifier, action);
	}

	/* (non-Javadoc)
	 * @see gr.auth.ee.lcs.data.ClassifierTransformBridge#setRepresentationSpecificClassifierData(gr.auth.ee.lcs.classifiers.Classifier)
	 */
	@Override
	public void setRepresentationSpecificClassifierData(Classifier aClassifier) {
		rep.setRepresentationSpecificClassifierData(aClassifier);
	}

	/* (non-Javadoc)
	 * @see gr.auth.ee.lcs.data.ClassifierTransformBridge#toBitSetString(gr.auth.ee.lcs.classifiers.Classifier)
	 */
	@Override
	public String toBitSetString(Classifier classifier) {
		return rep.toBitSetString(classifier);
	}

	/* (non-Javadoc)
	 * @see gr.auth.ee.lcs.data.ClassifierTransformBridge#toNaturalLanguageString(gr.auth.ee.lcs.classifiers.Classifier)
	 */
	@Override
	public String toNaturalLanguageString(Classifier aClassifier) {
		return rep.toBitSetString(aClassifier);
	}

}
