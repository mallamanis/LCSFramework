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
package gr.auth.ee.lcs.data;

import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.utilities.ExtendedBitSet;

/**
 * A bridge [GoF] that decouples the chromosome bit representation and the
 * data-set specific meaning. The object holds a static instance that implements
 * the actual transformation. All function calls are diverted to the static
 * instance.
 * 
 * @stereotype BridgeImplementor
 * 
 * @author Miltos Allamanis
 */
public abstract class ClassifierTransformBridge {

	/**
	 * Tests if two classifiers are equal.
	 * 
	 * @param cl1
	 *            the first classifier to be compared
	 * @param cl2
	 *            the second classifier to be compared
	 * @return true if classifiers are equal, else false
	 */
	public abstract boolean areEqual(Classifier cl1, Classifier cl2);
	
	/**
	 * @deprecated Unknown if useful
	 */
	@Deprecated
	public abstract void buildRepresentationModel();

	/**
	 * Classify a specific vision vector form a set of classifiers.
	 * 
	 * @param aSet
	 *            the set of classifiers used for classification
	 * @param visionVector
	 *            the vision vector of the instance used for classification
	 * @return an int[] containing the classes/ labels that the vision Vector
	 *         has been classified
	 */
	public abstract int[] classify(ClassifierSet aSet, double[] visionVector);

	/**
	 * The ability of the classifier to correctly classify the instance at the
	 * given index.
	 * 
	 * @param aClassifier
	 *            the classifier
	 * @param instanceIndex
	 *            the index of the instance
	 * @return a float representing the classification ability
	 */
	public abstract float classifyAbilityAll(Classifier aClassifier,
			int instanceIndex);

	/**
	 * The ability of a classifier to classify a specific instance label.
	 * 
	 * @param aClassifier
	 *            the classifier
	 * @param instanceIndex
	 *            the index of the instance.
	 * @param label
	 *            the label index
	 * @return a float representing the classification ability
	 */
	public abstract float classifyAbilityLabel(Classifier aClassifier,
			int instanceIndex, int label);

	/**
	 * Creates a random classifier to cover the visionVector.
	 * 
	 * @param visionVector
	 *            the vision vector to cover
	 * @return a random covering classifier
	 */
	public abstract Classifier createRandomCoveringClassifier(
			double[] visionVector);

	/**
	 * Fixes a chromosome bit representation in the correct value range (e.g.
	 * after mutation or crossover)
	 * 
	 * @param aChromosome
	 *            the chromosome to be fixed
	 */
	public abstract void fixChromosome(ExtendedBitSet aChromosome);

	/**
	 * @return the size of the chromosome (used for the chromosome construction)
	 */
	public abstract int getChromosomeSize();

	/**
	 * Gets the classification as specified by the representation.
	 * 
	 * @param aClassifier
	 *            the classifier from which to obtain the classification
	 * @return the class
	 */
	public abstract int[] getClassification(Classifier aClassifier);

	/**
	 * Returns all the labels of the specific data instance.
	 * 
	 * @param dataInstance
	 *            the data instance
	 * @return a array of instances
	 */
	public abstract int[] getDataInstanceLabels(final double[] dataInstance);

	/**
	 * Get the name of all labels (or classes).
	 * 
	 * @return a String array containing all label names
	 */
	public abstract String[] getLabelNames();

	/**
	 * Return the number of attributes contained in the problem.
	 * @return
	 */
	public abstract int getNumberOfAttributes();

	/**
	 * Checks if the visionVector matches the condition of the given chromosome.
	 * 
	 * @param visionVector
	 *            the vision input to be tested
	 * @param chromosome
	 *            the chromosome testing to vision
	 * @return true if chromosome matches vision vector
	 */
	public abstract boolean isMatch(double[] visionVector,
			ExtendedBitSet chromosome);

	/**
	 * Tests the given chromosomes if the baseClassifier is a more general
	 * version of the testClassifier.
	 * 
	 * @param baseClassifier
	 *            the base classifier
	 * @param testClassifier
	 *            the test classifier
	 * @return true if base is more general than test
	 */
	public abstract boolean isMoreGeneral(Classifier baseClassifier,
			Classifier testClassifier);

	/**
	 * Sets the classification as specified by the representation.
	 * 
	 * @param aClassifier
	 *            the classifier to set the classification
	 * @param action
	 *            the action to set on the classifier
	 */
	public abstract void setClassification(Classifier aClassifier, int action);

	/**
	 * Each implementation of the ClassifierTransformBridge might choose to save
	 * additional data on each classifier. This data is a Serializable object
	 * and is representation-specific.
	 * 
	 * @param aClassifier
	 *            the classifier to set the object
	 */
	public abstract void setRepresentationSpecificClassifierData(
			Classifier aClassifier);

	/**
	 * Converts a classifier to a bitset string.
	 * 
	 * @param classifier
	 *            the classifier to convert
	 * @return a string of the bit set representation of the classifier
	 */
	public abstract String toBitSetString(Classifier classifier);

	/**
	 * Converts the given classifier to a natural language rule.
	 * 
	 * @param aClassifier
	 *            the classifier to convert to Sting
	 * @return a string representing the classifier
	 */
	public abstract String toNaturalLanguageString(Classifier aClassifier);

}