package gr.auth.ee.lcs.classifiers;

import gr.auth.ee.lcs.data.ClassifierTransformBridge;
import gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy;

import java.io.Serializable;

/**
 * Represents a single classifier/ rule.
 * 
 * @author Miltos Allamanis
 */
public class Classifier implements Serializable {

	/**
	 * Cache for action.
	 */
	private int actionCache = -1;

	/**
	 * Serialization code for versioning.
	 */
	private static final long serialVersionUID = 8628765535406768159L;

	/**
	 * The fitness of the classifier.
	 */
	public double fitness = .5; // TODO: Through setters-getters

	/**
	 * An object (of undefined type) that is used by the update algorithms.
	 */
	public Serializable updateData;

	/**
	 * A boolean array indicating which dataset instances the rule matches.
	 */
	transient private byte[] matchInstances;

	/**
	 * A float showing the number of instances that the rule has covered. Used
	 * for calculating coverage.
	 */
	transient protected int covered = 0;

	/**
	 * The number of instances we have checked so far. Used for coverage
	 */
	transient protected int checked = 0;

	/**
	 * The serial number of last classifier (start from the lowest & increment).
	 */
	private static int currentSerial = Integer.MIN_VALUE;
	/**
	 * The serial number of the classifier.
	 */
	private int serial;

	/**
	 * The classifier's experience.
	 */
	public int experience = 1;

	/**
	 * The timestamp is the last iteration the classifier has participated in a
	 * GA Evolution.
	 */
	public int timestamp = 0;

	/**
	 * A boolean representing the classifier's ability to subsume.
	 */
	public boolean canSubsume = false;

	/**
	 * An object for saving the transformation specific dat.a
	 */
	public Serializable transformData;

	/**
	 * The classifier's chromosome.
	 */
	public ExtendedBitSet chromosome;

	/**
	 * a getter for the fitness of the classifier.
	 */
	public double getFitness() {
		return fitness;
	}

	/**
	 * The default constructor. Creates a chromosome of the given size
	 */
	public Classifier() {
		chromosome = new ExtendedBitSet(
				ClassifierTransformBridge.instance.getChromosomeSize());

		updateData = UpdateAlgorithmFactoryAndStrategy
				.createDefaultDataObject();

		ClassifierTransformBridge.instance
				.setRepresentationSpecificClassifierData(this);

		this.serial = currentSerial++;
	}

	/**
	 * Getter for the advocated action.
	 * 
	 * @return
	 */
	public int getActionAdvocated() {
		if (actionCache < 0)
			actionCache = ClassifierTransformBridge.instance
					.getClassification(this);
		return actionCache;
	}

	/**
	 * Setter for advocated action.
	 */
	public void setActionAdvocated(int action) {
		ClassifierTransformBridge.instance.setClassification(this, action);
		actionCache = -1;
	}

	/**
	 * @param anotherClassifier
	 * @return true if the classifiers have equal chromosomes
	 */
	public boolean equals(Classifier anotherClassifier) {
		return ClassifierTransformBridge.instance.areEqual(this,
				anotherClassifier);
	}

	/**
	 * Getter for the classifier's Serial Number
	 * 
	 * @return
	 */
	public int getSerial() {
		return this.serial;
	}

	/**
	 * Calls the bridge to detect if the classifier is matching the vision
	 * vector
	 */
	public boolean isMatch(double[] visionVector) {
		return ClassifierTransformBridge.instance.isMatch(visionVector,
				chromosome);
	}

	/**
	 * Checks if Classifier is matches an instance vector
	 */
	public boolean isMatch(int instanceIndex) {
		if (this.matchInstances == null)
			buildMatches();

		// if we haven't cached the answer, then answer...
		if (this.matchInstances[instanceIndex] == -1) {
			this.matchInstances[instanceIndex] = (byte) ((ClassifierTransformBridge.instance
					.isMatch(
							ClassifierTransformBridge.instances[instanceIndex],
							this.chromosome)) ? 1 : 0);
			this.checked++;
			this.covered += this.matchInstances[instanceIndex];
		}

		return this.matchInstances[instanceIndex] == 1;
	}

	/**
	 * Return if this classifier is more general than the testClassifier.
	 * 
	 * @param testClassifier
	 *            the test classifier
	 * @return true if the classifier is more general
	 */
	public boolean isMoreGeneral(Classifier testClassifier) {
		return ClassifierTransformBridge.instance.isMoreGeneral(this,
				testClassifier);
	}

	/**
	 * Calls the bridge to convert it self to natural language string.
	 */
	public String toString() {
		return ClassifierTransformBridge.instance.toNaturalLanguageString(this);
	}

	/**
	 * Calls the bridge to fix itself.
	 */
	public void fixChromosome() {
		ClassifierTransformBridge.instance.fixChromosome(chromosome);
	}

	/**
	 * Calls the bridge to divide bits into attributes.
	 */
	public String toBitString() {
		return ClassifierTransformBridge.instance.toBitSetString(this);
	}

	/**
	 * Getter of the ExtendedBitSet
	 * 
	 * @return the classifier's chromosome as an extendedBitSet
	 */
	public ExtendedBitSet getChromosome() {
		return chromosome;
	}

	public double getComparisonValue(int mode) {
		return UpdateAlgorithmFactoryAndStrategy.currentStrategy
				.getComparisonValue(this, mode);
	}

	/**
	 * Build matches vector and initialize it.
	 */
	public void buildMatches() {
		this.matchInstances = new byte[ClassifierTransformBridge.instances.length];
		for (int i = 0; i < ClassifierTransformBridge.instances.length; i++)
			matchInstances[i] = -1;
	}

	public double getCoverage() {
		if (this.checked == 0)
			return .5;
		return ((double) this.covered) / ((double) this.checked);

	}

}