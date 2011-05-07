/**
 * 
 */
package gr.auth.ee.lcs.data.representations;

import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.data.AbstractUpdateAlgorithmStrategy;
import gr.auth.ee.lcs.geneticalgorithm.INaturalSelector;
import gr.auth.ee.lcs.geneticalgorithm.selectors.BestClassifierSelector;
import gr.auth.ee.lcs.utilities.ExtendedBitSet;

import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;

import weka.core.Instances;

/**
 * A unilabel representation.
 * 
 * @author Miltos Allamanis
 * 
 */
public final class SingleClassRepresentation extends ComplexRepresentation {

	/**
	 * Call superclass's constructor.
	 * 
	 * @param generalizationRate
	 *            the P# for generalizing attibutes
	 * @param attributes
	 *            the attributes of the representation
	 * @param ruleConsequents
	 *            the names of the rule consequents
	 */
	public SingleClassRepresentation(final AbstractAttribute[] attributes,
			final String[] ruleConsequents, final double generalizationRate) {
		super(attributes, ruleConsequents, 1, generalizationRate);
	}

	/**
	 * Call superclass's constructor.
	 * 
	 * @param inputArff
	 *            the filename of the input .arff
	 * @param precision
	 *            the precision for the interval rules
	 * @param generalizationRate
	 *            the generalization rate used for the attibutes (P#)
	 * @throws IOException
	 *             when file cannot be read
	 * 
	 */
	public SingleClassRepresentation(final String inputArff,
			final int precision, final double generalizationRate)
			throws IOException {
		super(inputArff, precision, 1, generalizationRate);
	}

	/**
	 * Constructs a Single class representation
	 * 
	 * @param inputArff
	 *            the input .arff file
	 * @param precision
	 *            the precision bits to be for representing numerical attributed
	 * @param attributeToIgnore
	 *            the number of attributes to ignore at the end
	 * @param generalizationRate
	 *            the attribute generalization rate
	 * @throws IOException
	 *             when file is not found
	 */
	public SingleClassRepresentation(final String inputArff,
			final int precision, final int attributeToIgnore,
			final double generalizationRate) throws IOException {
		super(inputArff, precision, attributeToIgnore, generalizationRate);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.data.ComplexRepresentation#createClassRepresentation()
	 */
	@Override
	protected void createClassRepresentation(final Instances instances) {

		if (instances.classIndex() < 0)
			instances.setClassIndex(instances.numAttributes() - 1);

		// Rule Consequents
		Enumeration<?> classNames = instances.classAttribute()
				.enumerateValues();
		String[] ruleConsequents = new String[instances.numClasses()];
		this.ruleConsequents = ruleConsequents;
		for (int i = 0; i < instances.numClasses(); i++)
			ruleConsequents[i] = (String) classNames.nextElement();

		attributeList[attributeList.length - 1] = new UniLabel(chromosomeSize,
				"class", ruleConsequents);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.data.ClassifierTransformBridge#getClassification(gr.auth
	 * .ee.lcs.classifiers.Classifier)
	 */
	@Override
	public int[] getClassification(final Classifier aClassifier) {
		int[] result = new int[1];
		result[0] = ((UniLabel) attributeList[attributeList.length - 1])
				.getValue(aClassifier);
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

	/**
	 * A representation of the class "attribute".
	 * 
	 * @author Miltos Allamanis
	 * 
	 */
	public class UniLabel extends AbstractAttribute {

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
			int coverClass = (int) attributeValue;
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

	@Override
	public float classifyAbilityAll(Classifier aClassifier, int instanceIndex) {
		return ((UniLabel) attributeList[attributeList.length - 1])
				.getValue(aClassifier) == instances[instanceIndex][instances[instanceIndex].length - 1] ? 1
				: 0;
	}

	@Override
	public int[] getDataInstanceLabels(double[] dataInstance) {
		int[] classes = new int[1];
		classes[0] = (int) dataInstance[dataInstance.length - 1];
		return classes;
	}

	/**
	 * Inner class for classifying using only the the exploitation fitness.
	 * 
	 * @author Miltos Allamanis
	 * 
	 */
	public class BestFitnessClassificationStrategy implements
			IClassificationStrategy {

		/*
		 * (non-Javadoc)
		 * 
		 * @see gr.auth.ee.lcs.data.representations.ComplexRepresentation.
		 * IClassificationStrategy
		 * #classify(gr.auth.ee.lcs.classifiers.ClassifierSet, double[])
		 */
		@Override
		public int[] classify(ClassifierSet aSet, double[] visionVector) {
			INaturalSelector selector = new BestClassifierSelector(
					true,
					AbstractUpdateAlgorithmStrategy.COMPARISON_MODE_EXPLOITATION);

			// Generate MatchSet
			ClassifierSet matchSet = aSet.generateMatchSet(visionVector);

			if (matchSet.getTotalNumerosity() == 0)
				return null;
			ClassifierSet results = new ClassifierSet(null);
			selector.select(1, matchSet, results);

			return results.getClassifier(0).getActionAdvocated();
		}

	}

	/**
	 * A Classification strategy using voting.
	 * 
	 * @author Miltos Allamanis
	 * 
	 */
	public class VotingClassificationStrategy implements
			IClassificationStrategy {

		@Override
		public int[] classify(ClassifierSet aSet, double[] visionVector) {

			// Initialize table
			final int numOfClasses = ((UniLabel) attributeList[attributeList.length - 1]).classes.length;
			double[] votingTable = new double[numOfClasses];
			Arrays.fill(votingTable, 0);

			final ClassifierSet matchSet = aSet.generateMatchSet(visionVector);

			// Let each classifier vote
			final int setSize = matchSet.getNumberOfMacroclassifiers();
			for (int i = 0; i < setSize; i++) {
				final int advocatingClass = ((UniLabel) attributeList[attributeList.length - 1])
						.getValue(matchSet.getClassifier(i));
				votingTable[advocatingClass] += matchSet
						.getClassifierNumerosity(i)
						* matchSet
								.getClassifier(i)
								.getComparisonValue(
										AbstractUpdateAlgorithmStrategy.COMPARISON_MODE_EXPLOITATION);
			}

			// Find max
			double maxVotes = votingTable[0];
			int maxIndex = 0;
			for (int i = 1; i < numOfClasses; i++) {
				if (maxVotes < votingTable[i]) {
					maxIndex = i;
					maxVotes = votingTable[i];
				}
			}

			if (maxVotes == 0) {
				// TODO: Select majority class
			}

			// Wrap it
			int[] results = new int[1];
			results[0] = maxIndex;
			return results;
		}

	}

	@Override
	public final float classifyAbilityLabel(final Classifier aClassifier,
			final int instanceIndex, final int label) {
		return classifyAbilityAll(aClassifier, instanceIndex);
	}

}
