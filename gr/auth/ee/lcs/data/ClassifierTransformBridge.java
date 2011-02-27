package gr.auth.ee.lcs.data;

import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ExtendedBitSet;

/**
 * A bridge [GoF] that decouples the chromosome bit representation and the
 * data-set specific meaning. The object holds a static instance that implements
 * the actual transformation. All function calls are diverted to the static
 * instance.
 * 
 * @author Miltos Allamanis
 */
public abstract class ClassifierTransformBridge {

	/**
	 * The singleton instance of the bridge.
	 */
	public static ClassifierTransformBridge instance;

	/**
   * 
   */
	public static double[][] instances;

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
	 * Converts the given classifier to a natural language rule.
	 * 
	 * @param aClassifier
	 *            the classifier to convert to Sting
	 * @return a string representing the classifier
	 */
	public abstract String toNaturalLanguageString(Classifier aClassifier);

	/**
	 * Sets the static instance of the bridge.
	 */
	public static void setInstance(ClassifierTransformBridge aBridge) {
		ClassifierTransformBridge.instance = aBridge;
	}

	/**
	 * Creates a random classifier to cover the visionVector.
	 * 
	 * @param visionVector
	 *            the vision vector to cover
	 * @param advocatingAction
	 *            the action the random classifier will advocate for
	 * @return a random covering classifier
	 */
	public abstract Classifier createRandomCoveringClassifier(
			double[] visionVector, int advocatingAction);

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
	 * Fixes a chromosome bit representation in the correct value range (e.g.
	 * after mutation or crossover)
	 * 
	 * @param aChromosome
	 *            the chromosome to be fixed
	 */
	public abstract void fixChromosome(ExtendedBitSet aChromosome);

	/**
	 * Gets the classification as specified by the representation.
	 * 
	 * @param aClassifier
	 *            the classifier from which to obtain the classification
	 * @return the class TODO: Pending changes on ml
	 */
	public abstract int getClassification(Classifier aClassifier);

	/**
	 * Sets the classification as specified by the representation.
	 * 
	 * @param aClassifier
	 *            the classifier to set the classification
	 * @param action
	 *            the action to set on the classifier TODO: Pending changes on
	 *            ml
	 */
	public abstract void setClassification(Classifier aClassifier, int action);

	/**
	 * Calls to the bridge to fix a classifier.
	 * 
	 * @param toBeFixed
	 *            the classifier to be fixed
	 */
	public static void fixClassifier(Classifier toBeFixed) {
		ClassifierTransformBridge.instance.fixChromosome(toBeFixed.chromosome);
	}

	/**
	 * @return the size of the chromosome (used for the chromosome construction)
	 */
	public abstract int getChromosomeSize();

	/**
	 * Converts a classifier to a bitset string.
	 * 
	 * @param classifier
	 *            the classifier to convert
	 * @return a string of the bit set representation of the classifier
	 */
	public abstract String toBitSetString(Classifier classifier);

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
	 * @deprecated Unknown if usefull
	 */
	public abstract void buildRepresentationModel();

}