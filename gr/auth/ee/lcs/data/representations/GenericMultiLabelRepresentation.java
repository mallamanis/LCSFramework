/**
 * 
 */
package gr.auth.ee.lcs.data.representations;

import java.io.IOException;

import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy;
import gr.auth.ee.lcs.data.representations.ComplexRepresentation.Attribute;
import gr.auth.ee.lcs.data.representations.ComplexRepresentation.IClassificationStrategy;
import gr.auth.ee.lcs.data.representations.StrictMultiLabelRepresentation.Label;
import weka.core.Instances;

/**
 * A class for representing multilabel rules. For each rule each label can be represented as 0,1,#.
 * @author Miltos Allamanis
 *
 */
public class GenericMultiLabelRepresentation extends ComplexRepresentation {

	private final int metricType;
	
	public static final int ABSOLUTE_ACCURACY=0;
	public static final int RELATIVE_ACCURACY=1;
	public static final int HAMMING_LOSS=2;
	
	public GenericMultiLabelRepresentation(String inputArff, int precision,
			int labels, int type) throws IOException {
		super(inputArff, precision, labels);
		metricType = type;
	}
	
	public GenericMultiLabelRepresentation(Attribute[] attributes,
			String[] ruleConsequentsNames, int labels, int type) {
		super(attributes, ruleConsequentsNames, labels);
		metricType = type;
	}

	/* (non-Javadoc)
	 * @see gr.auth.ee.lcs.data.representations.ComplexRepresentation#createClassRepresentation(weka.core.Instances)
	 */
	@Override
	protected void createClassRepresentation(Instances instances) {
		for (int i = 0; i < numberOfLabels; i++) {

			final int labelIndex = attributeList.length - numberOfLabels + i;

			String attributeName = instances.attribute(labelIndex).name();

			attributeList[labelIndex] = new BooleanAttribute(chromosomeSize,
					attributeName, .33);
		}
	}

	/* (non-Javadoc)
	 * @see gr.auth.ee.lcs.data.ClassifierTransformBridge#classifyAbility(gr.auth.ee.lcs.classifiers.Classifier, int)
	 */
	@Override
	public float classifyAbility(Classifier aClassifier, int instanceIndex) {
		switch(metricType){
			case ABSOLUTE_ACCURACY:
				return classifyAbsolute(aClassifier,instanceIndex);
			case RELATIVE_ACCURACY:
				return 0; //TODO
			case HAMMING_LOSS:
				return classifyHamming(aClassifier,instanceIndex);
		}
		return 0;
	}
	
	private float classifyHamming(Classifier aClassifier, int instanceIndex) {
		float result = 0;
		for (int i = 0; i < numberOfLabels; i++) {
			final int currentLabelIndex = attributeList.length - numberOfLabels
					+ i;
			if (attributeList[currentLabelIndex].isMatch(
					(float) instances[instanceIndex][currentLabelIndex],
					aClassifier))
				result++;
		}
		
		boolean overgeneral = true;
		for (int i = 0; i < numberOfLabels; i++) {
			final int currentLabelIndex = attributeList.length - numberOfLabels
					+ i;
			final String value = attributeList[currentLabelIndex].toString(aClassifier);
			if (value=="#") 
				result +=.01;
			else 
				overgeneral = false;
		}
		
		return overgeneral?0:result/numberOfLabels;
	}
	
	/**
	 * Absolute Classification
	 * @param aClassifier
	 * @param instanceIndex
	 * @return
	 */
	private float classifyAbsolute(Classifier aClassifier, int instanceIndex) {
		for (int i = 0; i < numberOfLabels; i++) {
			final int currentLabelIndex = attributeList.length - numberOfLabels
					+ i;
			if (!attributeList[currentLabelIndex].isMatch(
					(float) instances[instanceIndex][currentLabelIndex],
					aClassifier))
				return 0;
		}
		
		//Check for overgeneral
		for (int i = 0; i < numberOfLabels; i++) {
			final int currentLabelIndex = attributeList.length - numberOfLabels
					+ i;
			final String value = attributeList[currentLabelIndex].toString(aClassifier);
			if (value!="#") 
				return 1;
		}
		
		return 0;		
	}

	/* (non-Javadoc)
	 * @see gr.auth.ee.lcs.data.ClassifierTransformBridge#getClassification(gr.auth.ee.lcs.classifiers.Classifier)
	 */
	@Override
	public int[] getClassification(Classifier aClassifier) {
		int[] labels = new int[numberOfLabels];
		int labelIndex = 0;
		for (int i = 0; i < numberOfLabels; i++) {
			final int currentLabelIndex = attributeList.length - numberOfLabels
					+ i;
			final String value = attributeList[currentLabelIndex].toString(aClassifier);
			if (value=="1") {
				labels[labelIndex] = i;
				labelIndex++;
			}
		}
		int[] result = new int[labelIndex];

		for (int i = 0; i < labelIndex; i++) {
			result[i] = labels[i];
		}

		return result;
	}

	/* (non-Javadoc)
	 * @see gr.auth.ee.lcs.data.ClassifierTransformBridge#getDataInstanceLabels(double[])
	 */
	@Override
	public int[] getDataInstanceLabels(double[] dataInstance) {
		int numOfLabels = 0;
		for (int i = 0; i < numberOfLabels; i++) {
			final int currentLabelIndex = attributeList.length - numberOfLabels
					+ i;
			if (dataInstance[currentLabelIndex] == 1)
				numOfLabels++;
		}
		int[] result = new int[numOfLabels];
		int resultIndex = 0;
		for (int i = 0; i < numberOfLabels; i++) {
			final int currentLabelIndex = attributeList.length - numberOfLabels
					+ i;
			if (dataInstance[currentLabelIndex] == 1) {
				result[resultIndex] = i;
				resultIndex++;
			}
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see gr.auth.ee.lcs.data.ClassifierTransformBridge#setClassification(gr.auth.ee.lcs.classifiers.Classifier, int)
	 */
	@Override
	public void setClassification(Classifier aClassifier, int action) {
		final int labelIndex = attributeList.length - numberOfLabels + action;
		attributeList[labelIndex].randomCoveringValue(1, aClassifier);
	}
	
	public class VotingClassificationStrategy implements
	IClassificationStrategy {

		@Override
		public int[] classify(ClassifierSet aSet, double[] visionVector) {
			double[] votingTable = new double[numberOfLabels];
			for (int i = 0; i < numberOfLabels; i++)
				votingTable[i] = 0;

			final ClassifierSet matchSet = aSet.generateMatchSet(visionVector);
			// Let each classifier vote
			final int setSize = matchSet.getNumberOfMacroclassifiers();
			for (int i = 0; i < setSize; i++) {
				// For each classifier
				for (int label = 0; label < numberOfLabels; label++) {
					final Classifier currentClassifier = matchSet
							.getClassifier(i);
					final int classifierNumerosity = matchSet
							.getClassifierNumerosity(i);
					final double fitness = currentClassifier
							.getComparisonValue(UpdateAlgorithmFactoryAndStrategy.COMPARISON_MODE_EXPLOITATION);
					final String cons = (attributeList[attributeList.length
							- numberOfLabels + label])
							.toString(currentClassifier);
					if (cons=="#") continue;
					if (cons=="1")
						votingTable[label] += classifierNumerosity * fitness;
					else
						votingTable[label] -= classifierNumerosity * fitness;

				}
			}

			int numberOfActiveLabels = 0;
			for (int i = 0; i < votingTable.length; i++)
				if (votingTable[i] > 0)
					numberOfActiveLabels++;

			int[] result = new int[numberOfActiveLabels];

			int currentIndex = 0;
			for (int i = 0; i < votingTable.length; i++)
				if (votingTable[i] > 0) {
					result[currentIndex] = i;
					currentIndex++;
				}

			return result;
		}
		
	}

}
