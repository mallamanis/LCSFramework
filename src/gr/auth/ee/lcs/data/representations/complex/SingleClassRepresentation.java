/*
 *	Copyright (C) 2011 by Allamanis Miltiadis
 *
 *	Permission is hereby granted, free of charge, to any person obtaining a copy
 *	of this software and associated documentation files (the "Software"), to deal
 *	in the Software without restriction, including without limitation the rights
 *	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *	copies of the Software, and to permit persons to whom the Software is
 *	furnished to do so, subject to the following conditions:
 *
 *	The above copyright notice and this permission notice shall be included in
 *	all copies or substantial portions of the Software.
 *
 *	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *	THE SOFTWARE.
 */
/**
 * 
 */
package gr.auth.ee.lcs.data.representations.complex;

import gr.auth.ee.lcs.AbstractLearningClassifierSystem;
import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.data.AbstractUpdateStrategy;
import gr.auth.ee.lcs.data.IClassificationStrategy;
import gr.auth.ee.lcs.geneticalgorithm.INaturalSelector;
import gr.auth.ee.lcs.geneticalgorithm.selectors.BestClassifierSelector;
import gr.auth.ee.lcs.utilities.ExtendedBitSet;
import gr.auth.ee.lcs.utilities.InstancesUtility;

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
	 * Inner class for classifying using only the the exploitation fitness.
	 * 
	 * @author Miltos Allamanis
	 * 
	 */
	public final static class BestFitnessClassificationStrategy implements
			IClassificationStrategy {

		/*
		 * (non-Javadoc)
		 * 
		 * @see gr.auth.ee.lcs.data.representations.ComplexRepresentation.
		 * IClassificationStrategy
		 * #classify(gr.auth.ee.lcs.classifiers.ClassifierSet, double[])
		 */
		@Override
		public int[] classify(final ClassifierSet aSet,
				final double[] visionVector) {
			final INaturalSelector selector = new BestClassifierSelector(true,
					AbstractUpdateStrategy.COMPARISON_MODE_EXPLOITATION);

			// Generate MatchSet
			final ClassifierSet matchSet = aSet.generateMatchSet(visionVector);

			if (matchSet.getTotalNumerosity() == 0)
				return null;
			final ClassifierSet results = new ClassifierSet(null);
			selector.select(1, matchSet, results);

			return results.getClassifier(0).getActionAdvocated();
		}

		@Override
		public void setThreshold(double threshold) {
			// No threshold for this type.
		}

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
		private final String[] classes;

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
		public final void fixAttributeRepresentation(
				final ExtendedBitSet generatedClassifier) {
			if (generatedClassifier
					.getIntAt(positionInChromosome, lengthInBits) >= classes.length) {

				final int randClass = (int) Math.floor(Math.random()
						* classes.length);
				generatedClassifier.setIntAt(positionInChromosome,
						lengthInBits, randClass);
			}

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

		@Override
		public final boolean isEqual(final ExtendedBitSet baseChromosome,
				final ExtendedBitSet testChromosome) {
			return (baseChromosome.getIntAt(positionInChromosome, lengthInBits) == testChromosome
					.getIntAt(positionInChromosome, lengthInBits));
		}

		@Override
		public final boolean isMatch(final float attributeVision,
				final ExtendedBitSet testedChromosome) {
			return testedChromosome
					.getIntAt(positionInChromosome, lengthInBits) == (int) attributeVision;
		}

		@Override
		public final boolean isMoreGeneral(final ExtendedBitSet baseChromosome,
				final ExtendedBitSet testChromosome) {
			return (baseChromosome.getIntAt(positionInChromosome, lengthInBits) == testChromosome
					.getIntAt(positionInChromosome, lengthInBits));

		}

		@Override
		public final void randomCoveringValue(final float attributeValue,
				final Classifier generatedClassifier) {
			final int coverClass = (int) attributeValue;
			generatedClassifier.setIntAt(positionInChromosome, lengthInBits,
					coverClass);
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

		@Override
		public final String toString(final ExtendedBitSet convertingClassifier) {
			final int index = convertingClassifier.getIntAt(
					positionInChromosome, lengthInBits);
			return classes[index];
		}

	}

	/**
	 * A Classification strategy using voting.
	 * 
	 * @author Miltos Allamanis
	 * 
	 */
	public final class VotingClassificationStrategy implements
			IClassificationStrategy {

		@Override
		public int[] classify(final ClassifierSet aSet,
				final double[] visionVector) {

			// Initialize table
			final int numOfClasses = ((UniLabel) attributeList[attributeList.length - 1]).classes.length;
			final double[] votingTable = new double[numOfClasses];
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
										AbstractUpdateStrategy.COMPARISON_MODE_EXPLOITATION);
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
			final int[] results = new int[1];
			results[0] = maxIndex;
			return results;
		}

		@Override
		public void setThreshold(double threshold) {
			// No threshold here...

		}

	}

	/**
	 * Call superclass's constructor.
	 * 
	 * @param generalizationRate
	 *            the P# for generalizing attibutes
	 * @param attributes
	 *            the attributes of the representation
	 * @param ruleConsequents
	 *            the names of the rule consequents
	 * @param lcs
	 *            the LCS instance used
	 */
	public SingleClassRepresentation(final AbstractAttribute[] attributes,
			final String[] ruleConsequents, final double generalizationRate,
			final AbstractLearningClassifierSystem lcs) {
		super(attributes, ruleConsequents, 1, generalizationRate, lcs);
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
	 * @param lcs
	 *            the LCS instance used
	 * @throws IOException
	 *             when file cannot be read
	 * 
	 */
	public SingleClassRepresentation(final String inputArff,
			final int precision, final double generalizationRate,
			final AbstractLearningClassifierSystem lcs) throws IOException {
		super(inputArff, precision, 1, generalizationRate, lcs);
		buildRepresentationFromInstance(InstancesUtility
				.openInstance(inputArff));
	}

	/**
	 * Constructs a Single class representation.
	 * 
	 * @param inputArff
	 *            the input .arff file
	 * @param precision
	 *            the precision bits to be for representing numerical attributed
	 * @param attributeToIgnore
	 *            the number of attributes to ignore at the end
	 * @param generalizationRate
	 *            the attribute generalization rate
	 * @param lcs
	 *            the LCS instance used
	 * @throws IOException
	 *             when file is not found
	 */
	public SingleClassRepresentation(final String inputArff,
			final int precision, final int attributeToIgnore,
			final double generalizationRate,
			final AbstractLearningClassifierSystem lcs) throws IOException {
		super(inputArff, precision, attributeToIgnore, generalizationRate, lcs);
		buildRepresentationFromInstance(InstancesUtility
				.openInstance(inputArff));
	}

	@Override
	public float classifyAbilityAll(final Classifier aClassifier,
			final int instanceIndex) {
		return (((UniLabel) attributeList[attributeList.length - 1])
				.getValue(aClassifier) == myLcs.instances[instanceIndex][myLcs.instances[instanceIndex].length - 1]) ? 1
				: 0;
	}

	@Override
	public float classifyAbilityLabel(final Classifier aClassifier,
			final int instanceIndex, final int label) {
		return classifyAbilityAll(aClassifier, instanceIndex);
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
		final Enumeration<?> classNames = instances.classAttribute()
				.enumerateValues();
		final String[] ruleConsequents = new String[instances.numClasses()];
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
		final int[] result = new int[1];
		result[0] = ((UniLabel) attributeList[attributeList.length - 1])
				.getValue(aClassifier);
		return result;
	}

	@Override
	public int[] getDataInstanceLabels(final double[] dataInstance) {
		final int[] classes = new int[1];
		classes[0] = (int) dataInstance[dataInstance.length - 1];
		return classes;
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

}
