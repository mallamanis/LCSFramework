/**
 * 
 */
package gr.auth.ee.lcs.data.representations;

import gr.auth.ee.lcs.AbstractLearningClassifierSystem;
import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.data.AbstractUpdateStrategy;
import gr.auth.ee.lcs.utilities.ExtendedBitSet;

import java.io.IOException;
import java.util.Arrays;

import weka.core.Instances;

/**
 * A strict multi-label representation. Each labels uses one bit that it may be
 * 0/1 to either classify or not a sample to a label
 * 
 * @author Miltos Allamanis
 * 
 */
public class StrictMultiLabelRepresentation extends ComplexRepresentation {

	/**
	 * The type of metric used to calculate a rule's (classifier) ability to
	 * classify a specific instance.
	 */
	private final int metricType;

	/**
	 * Exact Match Metric type.
	 */
	public static final int EXACT_MATCH = 0;

	/**
	 * Accuracy metric type.
	 */
	public static final int ACCURACY = 1;

	/**
	 * F-Measure Metric type.
	 */
	public static final int F_MEASURE = 2;

	/**
	 * Hamming Loss Metric Type.
	 */
	public static final int HAMMING_LOSS = 3;

	/**
	 * Constructor for directly creating object.
	 * 
	 * @param attributes
	 *            the attributes of the representation
	 * @param ruleConsequentsNames
	 *            the names of the rule consequents (labels)
	 * @param labels
	 *            the number of labels
	 * @param type
	 *            the type of the metric used
	 */
	public StrictMultiLabelRepresentation(final AbstractAttribute[] attributes,
			final String[] ruleConsequentsNames, final int labels,
			final int type, final double generalizationRate,
			final AbstractLearningClassifierSystem lcs) {
		super(attributes, ruleConsequentsNames, labels, generalizationRate, lcs);
		metricType = type;
	}

	/**
	 * Constructor for creating object through file input.
	 * 
	 * @param inputArff
	 *            the .arff input filename
	 * @param precision
	 *            the number of bits to be used for representing continuous
	 *            variables.
	 * @param labels
	 *            the number of labels of the probel
	 * @param type
	 *            the type of metric used for evaluating whole classifiers
	 * @throws IOException
	 *             if file is not found
	 */
	public StrictMultiLabelRepresentation(final String inputArff,
			final int precision, final int labels, final int type,
			final double generalizationRate,
			final AbstractLearningClassifierSystem lcs) throws IOException {
		super(inputArff, precision, labels, generalizationRate, lcs);
		metricType = type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gr.auth.ee.lcs.data.representations.ComplexRepresentation#
	 * createClassRepresentation(weka.core.Instances)
	 */
	@Override
	protected final void createClassRepresentation(final Instances instances) {
		for (int i = 0; i < numberOfLabels; i++) {

			final int labelIndex = attributeList.length - numberOfLabels + i;

			final String attributeName = instances.attribute(labelIndex).name();

			attributeList[labelIndex] = new Label(chromosomeSize, attributeName);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.data.ClassifierTransformBridge#classifyAbility(gr.auth
	 * .ee.lcs.classifiers.Classifier, int)
	 */
	@Override
	public final float classifyAbilityAll(final Classifier aClassifier,
			final int instanceIndex) {
		switch (metricType) {
		case EXACT_MATCH:
			return classifyExact(aClassifier, instanceIndex);
		case ACCURACY:
			return classifyAccuracy(aClassifier, instanceIndex);
		case HAMMING_LOSS:
			return classifyHamming(aClassifier, instanceIndex);
		default:
			return 0;
		}

	}

	/**
	 * Classify an instance using a classifier and the accuracy metric.
	 * 
	 * @param aClassifier
	 *            the classifier to be used
	 * @param instanceIndex
	 *            the instance index.
	 * @return a float representing the ml-accuracy of the classification
	 */
	public float classifyAccuracy(final Classifier aClassifier,
			final int instanceIndex) {
		float correct = 0;
		float wrong = 0;
		for (int i = 0; i < numberOfLabels; i++) {
			final int currentLabelIndex = attributeList.length - numberOfLabels
					+ i;
			final String actualLabel = myLcs.instances[instanceIndex][currentLabelIndex] == 1 ? "1"
					: "0";
			final String classifiedLabel = attributeList[currentLabelIndex]
					.toString(aClassifier);
			if (actualLabel != classifiedLabel)
				wrong++;
			else if (actualLabel == "1")
				correct++;
		}

		if (wrong + correct > 0)
			return ((float) correct) / ((float) (wrong + correct));
		else
			return 0;
	}

	/**
	 * Finds the (1 - HammingDistance) of the classifier and the instance at the
	 * given index.
	 * 
	 * @param aClassifier
	 *            the classifier used to classify the instance
	 * @param instanceIndex
	 *            the index of the train instance
	 * @return a float representing the (1 - HammingDistance) of the
	 *         classification
	 */
	public float classifyHamming(final Classifier aClassifier,
			final int instanceIndex) {
		float result = 0;
		for (int i = 0; i < numberOfLabels; i++) {
			final int currentLabelIndex = attributeList.length - numberOfLabels
					+ i;
			if (attributeList[currentLabelIndex].isMatch(
					(float) myLcs.instances[instanceIndex][currentLabelIndex],
					aClassifier))
				result++;
		}
		return result / numberOfLabels;
	}

	/**
	 * Classify with exact match as 0/1.
	 * 
	 * @param aClassifier
	 *            the classifier used to classify
	 * @param instanceIndex
	 *            the instance to classify
	 * @return 0 if classifier does not classify the instance correctly, 1
	 *         otherwise
	 */
	public float classifyExact(final Classifier aClassifier,
			final int instanceIndex) {
		for (int i = 0; i < numberOfLabels; i++) {
			final int currentLabelIndex = attributeList.length - numberOfLabels
					+ i;
			if (!attributeList[currentLabelIndex].isMatch(
					(float) myLcs.instances[instanceIndex][currentLabelIndex],
					aClassifier))
				return 0;
		}
		return 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.data.ClassifierTransformBridge#getClassification(gr.auth
	 * .ee.lcs.classifiers.Classifier)
	 */
	@Override
	public final int[] getClassification(final Classifier aClassifier) {
		int[] labels = new int[numberOfLabels];
		int labelIndex = 0;
		for (int i = 0; i < numberOfLabels; i++) {
			final int currentLabelIndex = attributeList.length - numberOfLabels
					+ i;
			if (attributeList[currentLabelIndex].isMatch(1, aClassifier)) {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.data.ClassifierTransformBridge#getDataInstanceLabels(double
	 * [])
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.data.ClassifierTransformBridge#setClassification(gr.auth
	 * .ee.lcs.classifiers.Classifier, int)
	 */
	@Override
	public final void setClassification(final Classifier aClassifier,
			final int action) {
		final int labelIndex = attributeList.length - numberOfLabels + action;
		attributeList[labelIndex].randomCoveringValue(1, aClassifier);

	}

	/**
	 * A simple 0/1 label.
	 * 
	 * @author Miltos Allamanis
	 * 
	 */
	public final class Label extends AbstractAttribute {

		/**
		 * The Label Attribute Constructor.
		 * 
		 * @param startPosition
		 *            the starting position of the attribute in the chrosmosome.
		 * @param attributeName
		 *            the name of the attribute.
		 */
		public Label(final int startPosition, final String attributeName) {
			super(startPosition, attributeName, 0);
			lengthInBits = 1;
			chromosomeSize += lengthInBits;
		}

		@Override
		public void fixAttributeRepresentation(
				final ExtendedBitSet generatedClassifier) {
			return;
		}

		@Override
		public boolean isEqual(final ExtendedBitSet baseChromosome,
				final ExtendedBitSet testChromosome) {
			return (baseChromosome.get(positionInChromosome) == testChromosome
					.get(positionInChromosome));
		}

		/**
		 * Get the value of the label of the given chromosome.
		 * 
		 * @param chromosome
		 *            the chromosome
		 * @return the value of the label (0/1)
		 */
		public boolean getValue(final ExtendedBitSet chromosome) {
			return chromosome.get(positionInChromosome);
		}

		@Override
		public boolean isMatch(final float attributeVision,
				final ExtendedBitSet testedChromosome) {
			return (testedChromosome.get(positionInChromosome) == (attributeVision == 1. ? true
					: false));

		}

		@Override
		public boolean isMoreGeneral(final ExtendedBitSet baseChromosome,
				final ExtendedBitSet testChromosome) {
			return baseChromosome.get(positionInChromosome) == testChromosome
					.get(positionInChromosome);
		}

		@Override
		public void randomCoveringValue(final float attributeValue,
				final Classifier generatedClassifier) {
			if (attributeValue == 1)
				generatedClassifier.set(positionInChromosome);
			else
				generatedClassifier.clear(positionInChromosome);

		}

		@Override
		public String toString(final ExtendedBitSet convertingClassifier) {
			return convertingClassifier.get(positionInChromosome) ? "1" : "0";
		}

	}

	/**
	 * Voting Classification Strategy for the Strict Representation.
	 * 
	 * @author Miltos Allamanis
	 * 
	 */
	public final class BestFitnessClassificationStrategy implements
			IClassificationStrategy {

		@Override
		public int[] classify(ClassifierSet aSet, double[] visionVector) {
			double bestFitness = Double.MIN_VALUE;
			int bestClassifierIndex = -1;
			final ClassifierSet matchSet = aSet.generateMatchSet(visionVector);
			final int setSize = matchSet.getNumberOfMacroclassifiers();
			for (int i = 0; i < setSize; i++) {
				// For each classifier
				final Classifier currentClassifier = matchSet.getClassifier(i);
				final int numerosity = matchSet.getClassifierNumerosity(i);
				final double fitness = numerosity
						* currentClassifier
								.getComparisonValue(AbstractUpdateStrategy.COMPARISON_MODE_EXPLOITATION);
				if (fitness > bestFitness) {
					bestFitness = fitness;
					bestClassifierIndex = i;
				}
			}
			if (bestClassifierIndex != -1) {
				final Classifier bestClassifier = matchSet
						.getClassifier(bestClassifierIndex);
				return bestClassifier.getActionAdvocated();
			}
			return new int[0];
		}

	}

	/**
	 * A voting strategy using voting. Each classifier can vote for or against a
	 * label. The votes are proportional to each classifier's numerosity and
	 * fitness. When a label has positive votes the input is classified with
	 * this label.
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
			Arrays.fill(votingTable, 0);

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
							.getComparisonValue(AbstractUpdateStrategy.COMPARISON_MODE_EXPLOITATION);
					final boolean labelActivated = ((Label) attributeList[attributeList.length
							- numberOfLabels + label])
							.getValue(currentClassifier);
					if (labelActivated)
						votingTable[label] += classifierNumerosity * fitness;
					else
						votingTable[label] -= classifierNumerosity * fitness;

				}
			}

			int numberOfActiveLabels = 0;
			for (int i = 0; i < votingTable.length; i++) {
				if (votingTable[i] > 0)
					numberOfActiveLabels++;
			}

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

	@Override
	public final float classifyAbilityLabel(final Classifier aClassifier,
			final int instanceIndex, final int label) {
		final int currentLabelIndex = attributeList.length - numberOfLabels
				+ label;
		if (attributeList[currentLabelIndex].isMatch(
				(float) myLcs.instances[instanceIndex][currentLabelIndex],
				aClassifier)) {
			return 1;
		}
		return -1;
	}

}
