/**
 * 
 */
package gr.auth.ee.lcs.data.representations;

import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.classifiers.ExtendedBitSet;
import gr.auth.ee.lcs.data.ClassifierTransformBridge;
import gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy;

import java.io.IOException;
import java.util.Arrays;

import weka.core.Instances;

/**
 * A multi-label representation using only one label.
 * 
 * @author Miltos Allamanis
 * 
 */
public class UniLabelRepresentation extends ComplexRepresentation {

	/**
	 * A representation of the class "attribute".
	 * 
	 * @author Miltos Allamanis
	 * 
	 */
	public class UniLabel extends Attribute {

		/**
		 * The classes' names.
		 */
		private String[] classes;

		/**
		 * The constructor.
		 * 
		 * @param startPosition
		 *            the starting position at the gene
		 * @param attributeName
		 *            the name of the attribute
		 * @param classNames
		 *            a String[] containing the names of the classes.
		 */
		public UniLabel(final int startPosition, final String attributeName,
				final String[] classNames) {
			super(startPosition, attributeName, 0);
			lengthInBits = (int) Math.ceil(Math.log10(classNames.length)
					/ Math.log10(2));
			chromosomeSize += lengthInBits;
			classes = classNames;
		}

		@Override
		public final String toString(final ExtendedBitSet convertingClassifier) {
			int index = convertingClassifier.getIntAt(positionInChromosome,
					lengthInBits);
			return classes[index];
		}

		@Override
		public final boolean isMatch(final float attributeVision,
				final ExtendedBitSet testedChromosome) {
			return testedChromosome
					.getIntAt(positionInChromosome, lengthInBits) == (int) attributeVision;
		}

		@Override
		public final void randomCoveringValue(final float attributeValue,
				final Classifier generatedClassifier) {
			int coverClass = (int) (Math.random() * classes.length);
			generatedClassifier.setIntAt(positionInChromosome, lengthInBits,
					coverClass);
		}

		@Override
		public final void fixAttributeRepresentation(
				final ExtendedBitSet generatedClassifier) {
			if (generatedClassifier
					.getIntAt(positionInChromosome, lengthInBits) >= classes.length) {

				int randClass = (int) Math
						.floor(Math.random() * classes.length);
				generatedClassifier.setIntAt(positionInChromosome,
						lengthInBits, randClass);
			}

		}

		@Override
		public final boolean isMoreGeneral(final ExtendedBitSet baseChromosome,
				final ExtendedBitSet testChromosome) {
			return (baseChromosome.getIntAt(positionInChromosome, lengthInBits) == testChromosome
					.getIntAt(positionInChromosome, lengthInBits));

		}

		@Override
		public final boolean isEqual(final ExtendedBitSet baseChromosome,
				final ExtendedBitSet testChromosome) {
			return (baseChromosome.getIntAt(positionInChromosome, lengthInBits) == testChromosome
					.getIntAt(positionInChromosome, lengthInBits));
		}

		/**
		 * Gets the label value.
		 * 
		 * @param chromosome
		 *            the chromosome
		 * @return the value of the label at the chromosome
		 */
		public final int getValue(final ExtendedBitSet chromosome) {
			return chromosome.getIntAt(positionInChromosome, lengthInBits);
		}

		/**
		 * Sets the label value.
		 * 
		 * @param chromosome
		 *            the chromosome to set the label
		 * @param value
		 *            the value to set
		 */
		public final void setValue(final ExtendedBitSet chromosome,
				final int value) {
			chromosome.setIntAt(positionInChromosome, lengthInBits, value);
		}

	}

	/**
	 * A Dummy label used to hide attributes as specified by the
	 * ComplexRepresentation implementation.
	 * 
	 * @author Miltiadis Allamanis
	 * 
	 */
	public class DummyLabel extends Attribute {

		/**
		 * Constructor.
		 * 
		 * @param startPosition
		 *            the starting position of the chromosome.
		 * @param attributeName
		 *            the name of the attribute
		 * @param generalizationRate
		 *            the generalization Rate
		 */
		public DummyLabel(int startPosition, String attributeName,
				double generalizationRate) {
			super(startPosition, attributeName, generalizationRate);
		}

		@Override
		public final void fixAttributeRepresentation(
				final ExtendedBitSet generatedClassifier) {
			return;
		}

		@Override
		public final boolean isEqual(final ExtendedBitSet baseChromosome,
				final ExtendedBitSet testChromosome) {
			return true;
		}

		@Override
		public final boolean isMatch(final float attributeVision,
				final ExtendedBitSet testedChromosome) {
			return true;
		}

		@Override
		public final boolean isMoreGeneral(final ExtendedBitSet baseChromosome,
				final ExtendedBitSet testChromosome) {
			return true;
		}

		@Override
		public final void randomCoveringValue(final float attributeValue,
				final Classifier generatedClassifier) {
			return;
		}

		@Override
		public final String toString(final ExtendedBitSet convertingClassifier) {
			return "";
		}

	}

	/**
	 * The constructor.
	 * 
	 * @param inputArff
	 *            the input .arff file name
	 * @param precision
	 *            the number of precision bits to be used
	 * @param labels
	 *            the number of labels used at the problem
	 * @throws IOException
	 *             when file is not found
	 */
	public UniLabelRepresentation(final String inputArff, final int precision,
			final int labels) throws IOException {
		super(inputArff, precision, labels);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.data.ClassifierTransformBridge#classifyAbilityAll(gr.auth
	 * .ee.lcs.classifiers.Classifier, int)
	 */
	@Override
	public float classifyAbilityAll(final Classifier aClassifier,
			final int instanceIndex) {
		int[] possibleLabels = getDataInstanceLabels(ClassifierTransformBridge.instances[instanceIndex]);
		int ruleLabel = getClassification(aClassifier)[0];

		if (Arrays.binarySearch(possibleLabels, ruleLabel) < 0)
			return 0;
		return 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.data.ClassifierTransformBridge#classifyAbilityLabel(gr
	 * .auth.ee.lcs.classifiers.Classifier, int, int)
	 */
	@Override
	public final float classifyAbilityLabel(final Classifier aClassifier,
			final int instanceIndex, final int label) {
		if (getClassification(aClassifier)[0] == label)
			return 1;
		else
			return -1;
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
		int[] label = new int[1];
		label[0] = ((UniLabel) attributeList[attributeList.length
				- numberOfLabels]).getValue(aClassifier);
		return label;
	}

	/**
	 * A thresholding classification function.
	 * 
	 * @author Miltos Allamanis
	 * 
	 */
	public final class ThresholdClassificationStrategy implements
			IClassificationStrategy {

		/**
		 * The internal threshold used at classification.
		 */
		private double threshold = 0.25;

		/**
		 * The number of steps for refining the threshold.
		 */
		private final int steps = 15;

		/**
		 * Build the normalized confidence vector for a given instance.
		 * 
		 * @param aSet
		 *            the set of classifier (rules)
		 * @param visionVector
		 *            the instance that the set will produce the confidence
		 *            levels on
		 * @return a float array that contains |L| the confidence (of the
		 *         classifier set) for each label
		 */
		private float[] buildConfidence(final ClassifierSet aSet,
				final double[] visionVector) {
			float[] lblProbs = new float[numberOfLabels];
			Arrays.fill(lblProbs, 0);

			final ClassifierSet matchSet = aSet.generateMatchSet(visionVector);
			final int matchSetSize = matchSet.getNumberOfMacroclassifiers();

			for (int i = 0; i < matchSetSize; i++) {
				final Classifier cl = matchSet.getClassifier(i);
				final int numerosity = matchSet.getClassifierNumerosity(i);

				final int classification = getClassification(cl)[0];

				lblProbs[classification] += numerosity
						* cl.getComparisonValue(UpdateAlgorithmFactoryAndStrategy.COMPARISON_MODE_EXPLOITATION);

			}

			// Normalize
			float sum = 0;
			for (int i = 0; i < lblProbs.length; i++)
				sum += lblProbs[i];

			for (int i = 0; i < lblProbs.length; i++)
				lblProbs[i] /= sum;

			return lblProbs;
		}

		/**
		 * Returns the number of active labels for a given threshold.
		 * 
		 * @param lblProbs
		 *            the normalized confidence array of all labels.
		 * @param th
		 *            the threshold
		 * @return an integer indicating the number of labels that are active
		 */
		private int getNumberOfActiveLabels(final float[] lblProbs,
				final float th) {
			// Classify
			int activeLabels = 0;
			for (int i = 0; i < lblProbs.length; i++)
				if (lblProbs[i] > th)
					activeLabels++;
			return activeLabels;
		}

		/**
		 * Get the proportional cut difference with a given target label
		 * cardinality, threshold and instance confidence valies.
		 * 
		 * @param confidenceValues
		 *            the normalized array of confidence values for all
		 *            instances and labels
		 * @param targetLc
		 *            the target LC (Label Cardinality) that we are trying to
		 *            achieve.
		 * @param th
		 *            the threshold
		 * @return a float indicating the absolute difference
		 */
		private float getPcutDiff(final float[][] confidenceValues,
				final float targetLc, final float th) {
			int sumOfActive = 0;
			for (int i = 0; i < confidenceValues.length; i++)
				sumOfActive += getNumberOfActiveLabels(confidenceValues[i], th);

			// Compare diff
			final double diff = Math.abs(((double) sumOfActive)
					/ ((double) confidenceValues.length) - targetLc);
			return (float) diff;
		}

		/**
		 * Calibrate threshold with a given step for threshold values.
		 * 
		 * @param confidenceValues
		 *            the confidence value array (NxL)
		 * @param targetLc
		 *            the target LC (Label Cardinality) we are trying to achieve
		 * @param pCutStep
		 *            the step used for the pCut
		 */
		private void calibrateThreshold(final float[][] confidenceValues,
				final float targetLc, final float pCutStep) {
			float downLimit = (float) this.threshold - pCutStep;
			if (downLimit < 0)
				downLimit = 0;
			float upLimit = (float) this.threshold + pCutStep;
			if (upLimit > .5)
				upLimit = (float) .5;
			float bestDif = Float.MAX_VALUE;
			for (float th = downLimit; th <= upLimit; th += (pCutStep / 2)) {
				final float diff = getPcutDiff(confidenceValues, targetLc, th);
				if (diff < bestDif) {
					bestDif = (float) diff;
					this.threshold = th;
				}
			}
		}

		/**
		 * Perform a proportional Cut (Pcut) on a set of instances to calibrate
		 * threshold.
		 * 
		 * @param instances
		 *            the instances to calibrate threshold on
		 * @param rules
		 *            the rules used to classify the instances and provide
		 *            confidence values.
		 * @param targetLc
		 *            the target Label Cardinality (LC) we are tring to achieve.
		 */
		public void proportionalCutCalibration(final double[][] instances,
				final ClassifierSet rules, final float targetLc) {
			float[][] confidenceValues = new float[instances.length][];
			for (int i = 0; i < instances.length; i++) {
				confidenceValues[i] = buildConfidence(rules, instances[i]);
			}
			float pCutStep = (float) .25;
			for (int i = 0; i < steps; i++) {
				calibrateThreshold(confidenceValues, targetLc, pCutStep);
				pCutStep /= 2;
			}

			System.out.println("Threshold set to " + this.threshold);

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see gr.auth.ee.lcs.data.representations.ComplexRepresentation.
		 * IClassificationStrategy
		 * #classify(gr.auth.ee.lcs.classifiers.ClassifierSet, double[])
		 */
		public final int[] classify(final ClassifierSet aSet,
				final double[] visionVector) {
			float[] lblProbs = buildConfidence(aSet, visionVector);

			int[] result = new int[getNumberOfActiveLabels(lblProbs,
					(float) this.threshold)];

			int currentIndex = 0;
			for (int i = 0; i < lblProbs.length; i++)
				if (lblProbs[i] > threshold) {
					result[currentIndex] = i;
					currentIndex++;
				}
			return result;
		}
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
			final int currentLabelIndex = dataInstance.length - numberOfLabels
					+ i;
			if (dataInstance[currentLabelIndex] == 1)
				numOfLabels++;
		}
		int[] result = new int[numOfLabels];
		int resultIndex = 0;
		for (int i = 0; i < numberOfLabels; i++) {
			final int currentLabelIndex = dataInstance.length - numberOfLabels
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
	public void setClassification(final Classifier aClassifier, final int action) {
		((UniLabel) attributeList[attributeList.length - 1]).setValue(
				aClassifier, action);
	}

	@Override
	protected void createClassRepresentation(final Instances instances) {
		String[] ruleConsequents = new String[numberOfLabels];
		this.ruleConsequents = ruleConsequents;

		for (int i = 0; i < numberOfLabels; i++)
			ruleConsequents[i] = "" + i;

		attributeList[attributeList.length - numberOfLabels] = new UniLabel(
				chromosomeSize, "label", ruleConsequents);

		for (int i = attributeList.length - numberOfLabels + 1; i < attributeList.length; i++) {
			attributeList[i] = new DummyLabel(chromosomeSize, "", 0);
		}

	}

}
