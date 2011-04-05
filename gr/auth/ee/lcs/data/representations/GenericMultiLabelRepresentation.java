/**
 * 
 */
package gr.auth.ee.lcs.data.representations;

import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.classifiers.ExtendedBitSet;
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

	public static final double ACCURACY_DONT_CARE_VALUE = 0.5;

	public static final double HAMMING_DONT_CARE_VALUE = 1;

	/**
	 * A boolean label representation with dont'cares. The only difference
	 * between this class an the BooleanAttribute is that generalization is
	 * reversed
	 * 
	 * @author Miltos Allamanis
	 * 
	 */
	public class GenericLabel extends Attribute {

		/**
		 * The constructor.
		 * 
		 * @param startPosition
		 *            the position attribute gene starts in chromosome
		 * @param attributeName
		 *            the name of the attribute
		 * @param generalizationRate
		 *            the generalization rate, used at covering
		 */
		public GenericLabel(final int startPosition,
				final String attributeName, final double generalizationRate) {
			super(startPosition, attributeName, generalizationRate);
			lengthInBits = 2;
			chromosomeSize += lengthInBits;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * gr.auth.ee.lcs.data.representations.ComplexRepresentation.Attribute
		 * #fixAttributeRepresentation
		 * (gr.auth.ee.lcs.classifiers.ExtendedBitSet)
		 */
		@Override
		public final void fixAttributeRepresentation(
				final ExtendedBitSet generatedClassifier) {
			return;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * gr.auth.ee.lcs.data.representations.ComplexRepresentation.Attribute
		 * #isEqual(gr.auth.ee.lcs.classifiers.ExtendedBitSet,
		 * gr.auth.ee.lcs.classifiers.ExtendedBitSet)
		 */
		@Override
		public final boolean isEqual(final ExtendedBitSet baseChromosome,
				final ExtendedBitSet testChromosome) {

			// if the base classifier is specific and the test is # return false
			if (baseChromosome.get(positionInChromosome) != testChromosome
					.get(positionInChromosome))
				return false;
			else if ((baseChromosome.get(positionInChromosome + 1) != testChromosome
					.get(positionInChromosome + 1))
					&& baseChromosome.get(positionInChromosome))
				return false;

			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * gr.auth.ee.lcs.data.representations.ComplexRepresentation.Attribute
		 * #isMatch(float, gr.auth.ee.lcs.classifiers.ExtendedBitSet)
		 */
		@Override
		public final boolean isMatch(final float attributeVision,
				final ExtendedBitSet testedChromosome) {

			if (testedChromosome.get(this.positionInChromosome)) {
				if ((attributeVision == 0 ? false : true) == testedChromosome
						.get(this.positionInChromosome + 1))
					return true;
				else
					return false;
			} else {
				return true;
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * gr.auth.ee.lcs.data.representations.ComplexRepresentation.Attribute
		 * #isMoreGeneral(gr.auth.ee.lcs.classifiers.ExtendedBitSet,
		 * gr.auth.ee.lcs.classifiers.ExtendedBitSet)
		 */
		@Override
		public final boolean isMoreGeneral(final ExtendedBitSet testChromosome,
				final ExtendedBitSet baseChromosome) {
			// if the base classifier is specific and the test is # return false
			if (baseChromosome.get(positionInChromosome)
					&& !testChromosome.get(positionInChromosome))
				return false;
			if ((baseChromosome.get(positionInChromosome + 1) != testChromosome
					.get(positionInChromosome + 1))
					&& baseChromosome.get(positionInChromosome))
				return false;

			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * gr.auth.ee.lcs.data.representations.ComplexRepresentation.Attribute
		 * #randomCoveringValue(float, gr.auth.ee.lcs.classifiers.Classifier)
		 */
		@Override
		public final void randomCoveringValue(final float attributeValue,
				final Classifier generatedClassifier) {
			if (attributeValue == 0)
				generatedClassifier.clear(positionInChromosome + 1);
			else
				generatedClassifier.set(positionInChromosome + 1);

			if (Math.random() < generalizationRate) // TODO: Configurable
													// generalization rate
				generatedClassifier.clear(positionInChromosome);
			else
				generatedClassifier.set(positionInChromosome);

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * gr.auth.ee.lcs.data.representations.ComplexRepresentation.Attribute
		 * #toString(gr.auth.ee.lcs.classifiers.ExtendedBitSet)
		 */
		@Override
		public final String toString(final ExtendedBitSet convertingClassifier) {
			if (convertingClassifier.get(this.positionInChromosome)) {
				return convertingClassifier.get(this.positionInChromosome + 1) ? "1"
						: "0";
			} else {
				return "#";
			}

		}

	}

	/**
	 * A classification strategy selecting the labels according to the highest
	 * fitness of a classifier. If a classifier has an # then the next
	 * classifier is selected.
	 * 
	 * @author Miltos Allamanis
	 * 
	 */
	public final class BestFitnessClassificationStrategy implements
			IClassificationStrategy {

		@Override
		public int[] classify(ClassifierSet aSet, double[] visionVector) {
			final double[] decisionTable = new double[numberOfLabels];
			final double[] confidenceTable = new double[numberOfLabels];
			for (int i = 0; i < numberOfLabels; i++) {
				decisionTable[i] = 0;
				confidenceTable[i] = 0;
			}

			final ClassifierSet matchSet = aSet.generateMatchSet(visionVector);
			final int setSize = matchSet.getNumberOfMacroclassifiers();
			for (int i = 0; i < setSize; i++) {
				// For each classifier
				for (int label = 0; label < numberOfLabels; label++) {
					final Classifier currentClassifier = matchSet
							.getClassifier(i);
					final double fitness = currentClassifier
							.getComparisonValue(UpdateAlgorithmFactoryAndStrategy.COMPARISON_MODE_EXPLOITATION);
					if (fitness > confidenceTable[label]) {
						final String cons = (attributeList[attributeList.length
								- numberOfLabels + label])
								.toString(currentClassifier);
						if (cons == "#")
							continue;
						confidenceTable[label] = fitness;
						if (cons == "1")
							decisionTable[label] = 1;
						else
							decisionTable[label] = 0;

					}
				}
			}

			int numberOfActiveLabels = 0;
			for (int i = 0; i < decisionTable.length; i++)
				if (decisionTable[i] == 1)
					numberOfActiveLabels++;

			final int[] result = new int[numberOfActiveLabels];
			int currentIndex = 0;
			for (int i = 0; i < decisionTable.length; i++)
				if (decisionTable[i] == 1) {
					result[currentIndex] = i;
					currentIndex++;
				}

			return result;
		}

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
			final double voteThreshold = 0;
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
				if (votingTable[i] > voteThreshold)
					numberOfActiveLabels++;

			final int[] result = new int[numberOfActiveLabels];

			int currentIndex = 0;
			for (int i = 0; i < votingTable.length; i++)
				if (votingTable[i] > voteThreshold) {
					result[currentIndex] = i;
					currentIndex++;
				}

			return result;
		}

	}

	/**
	 * The metric type used for calculating classifier's ability to classify an
	 * instance.
	 */
	private final int metricType;
	/**
	 * The label generalization rate.
	 */
	private final double labelGeneralizationRate;
	public static final int EXACT_MATCH = 0;

	public static final int RELATIVE_ACCURACY = 1;

	public static final int HAMMING_LOSS = 2;
	

	public GenericMultiLabelRepresentation(final Attribute[] attributes,
			final String[] ruleConsequentsNames, final int labels,
			final int type, final double generalizationRate) {
		super(attributes, ruleConsequentsNames, labels);
		metricType = type;
		labelGeneralizationRate = generalizationRate;
	}

	public GenericMultiLabelRepresentation(String inputArff, int precision,
			int labels, int type, final double generalizationRate)
			throws IOException {
		super(inputArff, precision, labels);
		metricType = type;
		labelGeneralizationRate = generalizationRate;
	}

	@Override
	public float classifyAbilityAll(final Classifier aClassifier,
			final int instanceIndex) {
		switch (metricType) {
		case EXACT_MATCH:
			return classifyAbsolute(aClassifier, instanceIndex);
		case RELATIVE_ACCURACY:
			return classifyAccuracy(aClassifier, instanceIndex);
		case HAMMING_LOSS:
			return classifyHamming(aClassifier, instanceIndex);
		}
		return 0;
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

	/**
	 * Absolute Classification.
	 * 
	 * @param aClassifier
	 * @param instanceIndex
	 * @return
	 */
	public float classifyAbsolute(final Classifier aClassifier,
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

	public float classifyAccuracy(final Classifier aClassifier,
			final int instanceIndex) {
		float correct = 0;
		float wrong = 0;
		for (int i = 0; i < numberOfLabels; i++) {
			final int currentLabelIndex = attributeList.length - numberOfLabels
					+ i;
			final String actualLabel = instances[instanceIndex][currentLabelIndex] == 1 ? "1"
					: "0";
			final String classifiedLabel = attributeList[currentLabelIndex]
					.toString(aClassifier);
			if (classifiedLabel == "#") {
				if (actualLabel == "1")
					correct += ACCURACY_DONT_CARE_VALUE;
			} else if (classifiedLabel == actualLabel) {
				if (actualLabel == "1")
					correct++;
			} else {
				wrong++;
			}
		}
		if (wrong + correct > 0)
			return ((float) correct) / ((float) (wrong + correct));
		else
			return 0;
	}

	/**
	 * Evaluate classify ability of an instance through Hamming distance.
	 * 
	 * @param aClassifier
	 *            the classifier to evaluatre
	 * @param instanceIndex
	 *            the index of the instance
	 * @return the hamming distance of the classifier and the instance.
	 */
	public float classifyHamming(final Classifier aClassifier,
			final int instanceIndex) {
		float result = 0;
		float totalClassifications = 0;
		for (int i = 0; i < numberOfLabels; i++) {
			final int currentLabelIndex = attributeList.length - numberOfLabels
					+ i;
			final String value = attributeList[currentLabelIndex]
			               					.toString(aClassifier);
			if (value == "#")
				continue;
			totalClassifications++;
			if (attributeList[currentLabelIndex].isMatch(
					(float) instances[instanceIndex][currentLabelIndex],
					aClassifier))
				result++;
		}

		final float hammingWin = ((float) result ) /((float) totalClassifications);

		return Double.isNaN(hammingWin) ? 0 : hammingWin;
	}

	@Override
	protected void createClassRepresentation(final Instances instances) {
		for (int i = 0; i < numberOfLabels; i++) {

			final int labelIndex = attributeList.length - numberOfLabels + i;

			String attributeName = instances.attribute(labelIndex).name();

			attributeList[labelIndex] = new GenericLabel(chromosomeSize,
					attributeName, labelGeneralizationRate);
		}
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

}
