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
package gr.auth.ee.lcs.data.representations;

import gr.auth.ee.lcs.AbstractLearningClassifierSystem;
import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.data.AbstractUpdateStrategy;
import gr.auth.ee.lcs.data.ClassifierTransformBridge;
import gr.auth.ee.lcs.geneticalgorithm.INaturalSelector;
import gr.auth.ee.lcs.geneticalgorithm.selectors.BestClassifierSelector;
import gr.auth.ee.lcs.utilities.ExtendedBitSet;

/**
 * Implements the a simple boolean representation of the chromosomes. Each bit
 * is represented by two bits. The first bit is the activation bit and the
 * second bit is the bit value. When the activation bit is "1" then the bit is
 * active When the activation bit is "0" we have a don't care (#) bit
 * 
 * @author Miltos Allamanis
 * 
 */
public final class SimpleBooleanRepresentation extends
		ClassifierTransformBridge {

	/**
	 * The P# (the covering operator generalization rate).
	 */
	private double coverGeneralizationRate = 0.5;

	/**
	 * Chromosome Size.
	 */
	private int chromosomeSize = 0;

	/**
	 * The LCS instance used.
	 */
	final AbstractLearningClassifierSystem myLcs;

	/**
	 * The costructor.
	 * 
	 * @param coveringGeneralizationRate
	 *            the generalization rate for covering
	 * @param visionBits
	 *            the bits used at vision
	 * @param lcs
	 *            the LCS instance used
	 */
	public SimpleBooleanRepresentation(final double coveringGeneralizationRate,
			final int visionBits, final AbstractLearningClassifierSystem lcs) {
		coverGeneralizationRate = coveringGeneralizationRate;
		myLcs = lcs;
		setVisionSize(visionBits);
	}

	@Override
	/**
	 * @see gr.auth.ee.lcs.data.ClassifierTransformBridge#areEqual(gr.auth.ee.lcs.classifiers.Classifier,gr.auth.ee.lcs.classifiers.Classifier)
	 */
	public boolean areEqual(final Classifier cl1, final Classifier cl2) {
		if (getClassification(cl1)[0] != getClassification(cl2)[0])
			return false;

		final ExtendedBitSet baseChromosome = cl1;
		final ExtendedBitSet testChromosome = cl2;

		// For each chromosome
		for (int i = 0; i < chromosomeSize; i += 2) {

			// if the base classifier is specific and the test is # return false
			if (baseChromosome.get(i) != testChromosome.get(i))
				return false;
			else if ((baseChromosome.get(i + 1) != testChromosome.get(i + 1))
					&& baseChromosome.get(i))
				return false;
		}
		return true;
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

	@Override
	public int[] classify(final ClassifierSet aSet, final double[] dataInstance) {

		final INaturalSelector selector = new BestClassifierSelector(true,
				AbstractUpdateStrategy.COMPARISON_MODE_EXPLOITATION);

		// Generate MatchSet
		final ClassifierSet matchSet = aSet.generateMatchSet(dataInstance);

		if (matchSet.getTotalNumerosity() == 0)
			return null;
		final ClassifierSet results = new ClassifierSet(null);
		selector.select(1, matchSet, results);

		return results.getClassifier(0).getActionAdvocated();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.data.ClassifierTransformBridge#classifyAbility(gr.auth
	 * .ee.lcs.classifiers.Classifier, int)
	 */
	@Override
	public float classifyAbilityAll(final Classifier aClassifier,
			final int instanceIndex) {
		return (myLcs.instances[instanceIndex][myLcs.instances[instanceIndex].length - 1] == ((int[]) (aClassifier.transformData))[0]) ? 1
				: 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.data.ClassifierTransformBridge#classifyAbilityLabel(gr
	 * .auth.ee.lcs.classifiers.Classifier, int, int)
	 */
	@Override
	public float classifyAbilityLabel(final Classifier aClassifier,
			final int instanceIndex, final int label) {
		return classifyAbilityAll(aClassifier, instanceIndex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.data.ClassifierTransformBridge#createRandomCoveringClassifier
	 * (double[])
	 */
	@Override
	public Classifier createRandomCoveringClassifier(final double[] visionVector) {
		final Classifier coverClassifier = myLcs.getNewClassifier();

		// Transform visionVector to BitSet (generalization not-set)
		final ExtendedBitSet chromosome = coverClassifier;
		for (int i = 1; i < chromosomeSize; i += 2) {
			if (visionVector[i / 2] == 0)
				chromosome.clear(i);
			else
				chromosome.set(i);
		}

		// Generalize
		for (int i = 0; i < chromosomeSize; i += 2) {
			if (Math.random() > this.coverGeneralizationRate)
				chromosome.set(i);
			else
				chromosome.clear(i);
		}
		// coverClassifier.actionAdvocated=advocatingAction;
		((int[]) (coverClassifier.transformData))[0] = ((Math.random() < .5) ? 1
				: 0);
		return coverClassifier;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.data.ClassifierTransformBridge#fixChromosome(gr.auth.ee
	 * .lcs.utilities.ExtendedBitSet)
	 */
	@Override
	public void fixChromosome(final ExtendedBitSet aChromosome) {
		return;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gr.auth.ee.lcs.data.ClassifierTransformBridge#getChromosomeSize()
	 */
	@Override
	public int getChromosomeSize() {
		return chromosomeSize;
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
		return ((int[]) (aClassifier.transformData));
	}

	@Override
	public int[] getDataInstanceLabels(final double[] dataInstances) {
		final int[] classes = new int[1];
		classes[0] = (int) dataInstances[dataInstances.length - 1];
		return classes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gr.auth.ee.lcs.data.ClassifierTransformBridge#getLabelNames()
	 */
	@Override
	public String[] getLabelNames() {
		final String[] str = new String[2];
		str[0] = "0";
		str[1] = "1";
		return str;
	}

	@Override
	public int getNumberOfAttributes() {
		return chromosomeSize / 2;
	}

	@Override
	public boolean isAttributeSpecific(final Classifier aClassifier,
			final int attributeIndex) {
		return aClassifier.get(2 * attributeIndex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gr.auth.ee.lcs.data.ClassifierTransformBridge#isMatch(double[],
	 * gr.auth.ee.lcs.utilities.ExtendedBitSet)
	 */
	@Override
	public boolean isMatch(final double[] visionVector,
			final ExtendedBitSet chromosome) {
		for (int i = 0; i < chromosomeSize; i += 2) {
			if (chromosome.get(i)) {
				double test = chromosome.get(i + 1) ? 1 : 0;
				if (Double.compare(visionVector[i / 2], test) != 0)
					return false;
			}
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.data.ClassifierTransformBridge#isMoreGeneral(gr.auth.ee
	 * .lcs.classifiers.Classifier, gr.auth.ee.lcs.classifiers.Classifier)
	 */
	@Override
	public boolean isMoreGeneral(final Classifier baseClassifier,
			final Classifier testClassifier) {
		// If classifiers advocate for different actions, return false
		if (baseClassifier.getActionAdvocated()[0] != testClassifier
				.getActionAdvocated()[0])
			return false;

		final ExtendedBitSet baseChromosome = baseClassifier;
		final ExtendedBitSet testChromosome = testClassifier;

		// For each chromosome
		for (int i = 0; i < chromosomeSize; i += 2) {

			// if the base classifier is specific and the test is # return false
			if (baseChromosome.get(i) && !testChromosome.get(i))
				return false;
			if ((baseChromosome.get(i + 1) != testChromosome.get(i + 1))
					&& baseChromosome.get(i))
				return false;
		}
		return true;
	}

	@Override
	public void setClassification(final Classifier aClassifier, final int action) {
		((int[]) (aClassifier.transformData))[0] = action;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gr.auth.ee.lcs.data.ClassifierTransformBridge#
	 * setRepresentationSpecificClassifierData
	 * (gr.auth.ee.lcs.classifiers.Classifier)
	 */
	@Override
	public void setRepresentationSpecificClassifierData(
			final Classifier aClassifier) {
		aClassifier.transformData = new int[1];
		return;
	}

	/**
	 * Set the number of bits in the input. This size determines the size of the
	 * chromosome In this representation the size of the chromosome is 2*size.
	 * 
	 * @param size
	 *            the size of the vision vector
	 */
	public void setVisionSize(final int size) {
		this.chromosomeSize = 2 * size;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.data.ClassifierTransformBridge#toBitSetString(gr.auth.
	 * ee.lcs.classifiers.Classifier)
	 */
	@Override
	public String toBitSetString(final Classifier classifier) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Returns a string describing in representation specific terms the
	 * classifier.
	 * 
	 * @see gr.auth.ee.lcs.data.ClassifierTransformBridge#toNaturalLanguageString(gr.auth.ee.lcs.classifiers.Classifier)
	 * @param aClassifier
	 *            the classifier to be described
	 * @return a String representing the classifier in Natural Language
	 */
	@Override
	public String toNaturalLanguageString(final Classifier aClassifier) {
		String output = "";
		// Get Chromosome
		final ExtendedBitSet chromosome = aClassifier;
		for (int i = 0; i < chromosomeSize; i += 2) {
			if (chromosome.get(i)) {
				output = (chromosome.get(i + 1) ? "1" : "0") + output;
			} else {
				output = "#" + output;
			}
		}
		output += "=>" + getClassification(aClassifier)[0];
		return output;
	}

}
