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
import java.util.Random;

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
			if (baseChromosome.getIntAt(positionInChromosome, lengthInBits) == testChromosome
					.getIntAt(positionInChromosome, lengthInBits))
				return true;
			else
				return false;
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

	public class DummyLabel extends Attribute {

		public DummyLabel(int startPosition, String attributeName,
				double generalizationRate) {
			super(startPosition, attributeName, generalizationRate);
		}

		@Override
		public void fixAttributeRepresentation(
				ExtendedBitSet generatedClassifier) {
			return;
		}

		@Override
		public boolean isEqual(ExtendedBitSet baseChromosome,
				ExtendedBitSet testChromosome) {
			return true;
		}

		@Override
		public boolean isMatch(float attributeVision,
				ExtendedBitSet testedChromosome) {
			return true;
		}

		@Override
		public boolean isMoreGeneral(ExtendedBitSet baseChromosome,
				ExtendedBitSet testChromosome) {
			return true;
		}

		@Override
		public void randomCoveringValue(float attributeValue,
				Classifier generatedClassifier) {
			return;
		}

		@Override
		public String toString(ExtendedBitSet convertingClassifier) {
			return "";
		}

	}

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
	public float classifyAbilityAll(Classifier aClassifier, int instanceIndex) {
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
	public float classifyAbilityLabel(Classifier aClassifier,
			int instanceIndex, int label) {
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
	public int[] getClassification(Classifier aClassifier) {
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
		public int[] classify(ClassifierSet aSet, double[] visionVector) {
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

			// TODO: t as parameter
			double t = 0.35;
			// Classify
			int activeLabels = 0;
			for (int i = 0; i < lblProbs.length; i++)
				if (lblProbs[i] > t)
					activeLabels++;

			int[] result = new int[activeLabels];
			int currentIndex = 0;
			for (int i = 0; i < lblProbs.length; i++)
				if (lblProbs[i] > t) {
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
	public int[] getDataInstanceLabels(double[] dataInstance) {
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
	public void setClassification(Classifier aClassifier, int action) {
		((UniLabel) attributeList[attributeList.length - 1]).setValue(
				aClassifier, action);
	}

	@Override
	protected void createClassRepresentation(Instances instances) {
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
