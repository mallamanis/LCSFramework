/**
 * 
 */
package gr.auth.ee.lcs.data.representations;

import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy;

import java.io.IOException;

import weka.core.Instances;

/**
 * A class for representing multilabel rules. For each rule each label can be
 * represented as 0,1,#.
 * 
 * @author Miltos Allamanis
 * 
 */
public final class GenericMultiLabelRepresentation extends
		ComplexRepresentation {

	/**
	 * The metric type used for calculating classifier's ability to classify an
	 * instance.
	 */
	private final int metricType;

	private final double labelGeneralizationRate;

	public static final int EXACT_MATCH = 0;
	public static final int RELATIVE_ACCURACY = 1;
	public static final int HAMMING_LOSS = 2;

	public GenericMultiLabelRepresentation(String inputArff, int precision,
			int labels, int type, final double generalizationRate)
			throws IOException {
		super(inputArff, precision, labels);
		metricType = type;
		labelGeneralizationRate = generalizationRate;
	}

	public GenericMultiLabelRepresentation(final Attribute[] attributes,
			final String[] ruleConsequentsNames, final int labels,
			final int type, final double generalizationRate) {
		super(attributes, ruleConsequentsNames, labels);
		metricType = type;
		labelGeneralizationRate = generalizationRate;
	}

	@Override
	protected void createClassRepresentation(final Instances instances) {
		for (int i = 0; i < numberOfLabels; i++) {

			final int labelIndex = attributeList.length - numberOfLabels + i;

			String attributeName = instances.attribute(labelIndex).name();

			attributeList[labelIndex] = new BooleanAttribute(chromosomeSize,
					attributeName, labelGeneralizationRate);
		}
	}

	@Override
	public float classifyAbilityAll(final Classifier aClassifier,
			final int instanceIndex) {
		switch (metricType) {
		case EXACT_MATCH:
			return classifyAbsolute(aClassifier, instanceIndex);
		case RELATIVE_ACCURACY:
			return 0; // TODO
		case HAMMING_LOSS:
			return classifyHamming(aClassifier, instanceIndex);
		}
		return 0;
	}

	private float classifyHamming(final Classifier aClassifier,
			final int instanceIndex) {
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
			final String value = attributeList[currentLabelIndex]
					.toString(aClassifier);
			if (value == "#")
				result -= .99;// 1;
			else
				overgeneral = false;
		}

		return overgeneral ? 0 : result / numberOfLabels;
	}

	/**
	 * Absolute Classification.
	 * 
	 * @param aClassifier
	 * @param instanceIndex
	 * @return
	 */
	private float classifyAbsolute(final Classifier aClassifier,
			final int instanceIndex) {
		for (int i = 0; i < numberOfLabels; i++) {
			final int currentLabelIndex = attributeList.length - numberOfLabels
					+ i;
			if (!attributeList[currentLabelIndex].isMatch(
					(float) instances[instanceIndex][currentLabelIndex],
					aClassifier))
				return 0;
		}

		// Check for overgeneral
		for (int i = 0; i < numberOfLabels; i++) {
			final int currentLabelIndex = attributeList.length - numberOfLabels
					+ i;
			final String value = attributeList[currentLabelIndex]
					.toString(aClassifier);
			if (value != "#")
				return 1;
		}

		return 0;
	}

	@Override
	public int[] getClassification(final Classifier aClassifier) {
		int[] labels = new int[numberOfLabels];
		int labelIndex = 0;
		for (int i = 0; i < numberOfLabels; i++) {
			final int currentLabelIndex = attributeList.length - numberOfLabels
					+ i;
			final String value = attributeList[currentLabelIndex]
					.toString(aClassifier);
			if (value == "1") {
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

	@Override
	public final int[] getDataInstanceLabels(final double[] dataInstance) {
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

	@Override
	public final void setClassification(final Classifier aClassifier,
			final int action) {
		final int labelIndex = attributeList.length - numberOfLabels + action;
		attributeList[labelIndex].randomCoveringValue(1, aClassifier);
	}

	/**
	 * A Voting Classification Strategy.
	 * 
	 * @author Miltos Allamanis
	 * 
	 */
	public final class VotingClassificationStrategy implements
			IClassificationStrategy {

		@Override
		public int[] classify(final ClassifierSet aSet,
				final double[] visionVector) {
			final double[] votingTable = new double[numberOfLabels];
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
					if (cons == "#")
						continue;
					if (cons == "1")
						votingTable[label] += classifierNumerosity * fitness;
					else
						votingTable[label] -= classifierNumerosity * fitness;

				}
			}

			int numberOfActiveLabels = 0;
			for (int i = 0; i < votingTable.length; i++)
				if (votingTable[i] > 0)
					numberOfActiveLabels++;

			final int[] result = new int[numberOfActiveLabels];

			int currentIndex = 0;
			for (int i = 0; i < votingTable.length; i++)
				if (votingTable[i] > 0) {
					result[currentIndex] = i;
					currentIndex++;
				}

			return result;
		}

	}

	@Override
	public float classifyAbilityLabel(final Classifier aClassifier,
			final int instanceIndex, final int label) {
		final int currentLabelIndex = attributeList.length - numberOfLabels
				+ label;
		if (attributeList[currentLabelIndex].isMatch(
				(float) instances[instanceIndex][currentLabelIndex],
				aClassifier)) {
			final String value = attributeList[currentLabelIndex]
					.toString(aClassifier);
			if (value != "#")
				return 1;
			return 0;
		}
		return -1;
	}

}
