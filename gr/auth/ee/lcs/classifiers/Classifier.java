package gr.auth.ee.lcs.classifiers;

import gr.auth.ee.lcs.data.ClassifierTransformBridge;
import gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy;

import java.io.Serializable;

/**
 * Represents a single classifier/ rule. Connects to the representation through
 * a visitor pattern.
 * 
 * @author Miltos Allamanis
 */
public class Classifier extends ExtendedBitSet implements Serializable {

	/**
	 * The transform bridge.
	 */
	private static ClassifierTransformBridge transformBridge;

	/**
	 * The initial Fitness of a classifier.
	 */
	private static final double INITIAL_FITNESS = 0.5;

	/**
	 * Set the transform bridge.
	 * 
	 * @param bridge
	 */
	public static void setTransformBridge(final ClassifierTransformBridge bridge) {
		transformBridge = bridge;
	}

	/**
	 * Cache for action.
	 */
	private int actionCache[] = null;

	/**
	 * Serialization code for versioning.
	 */
	private static final long serialVersionUID = 8628765535406768159L;

	/**
	 * An object (of undefined type) that is used by the update algorithms.
	 */
	private Serializable updateData;

	/**
	 * A boolean array indicating which dataset instances the rule matches.
	 */
	private transient byte[] matchInstances;

	/**
	 * A float showing the number of instances that the rule has covered. Used
	 * for calculating coverage.
	 */
	private transient int covered = 0;

	/**
	 * The number of instances we have checked so far. Used for coverage
	 */
	private transient int checked = 0;

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
	private boolean canSubsume = false;

	/**
	 * An object for saving the transformation specific data.
	 */
	public Serializable transformData;

	/**
	 * The default constructor. Creates a chromosome of the given size
	 */
	public Classifier() {
		super(transformBridge.getChromosomeSize());
		setConstructionData();
	}

	/**
	 * Constructor for creating a classifier from a chromosome.
	 * 
	 * @param chromosome
	 *            the chromosome from which to create the classifier
	 */
	public Classifier(final ExtendedBitSet chromosome) {
		super(chromosome);
		setConstructionData();
	}

	/**
	 * Build matches vector (with train instances) and initialize it.
	 */
	public final void buildMatches() {
		this.matchInstances = new byte[ClassifierTransformBridge.instances.length];
		for (int i = 0; i < ClassifierTransformBridge.instances.length; i++)
			matchInstances[i] = -1;
	}

	/**
	 * Getter for the subsumption ability.
	 * 
	 * @return true if the classifier is strong enough to subsume
	 */
	public final boolean canSubsume() {
		return canSubsume;
	}

	/**
	 * A representation specific method representing the classifier's ability to
	 * correctly classify a train instance.
	 * 
	 * @param instanceIndex
	 *            the index of the train instance
	 * @return a number that represents the correctness. This number may be 0,1
	 *         for unilabel classification but it may also be in the range [0,1]
	 */
	public float classifyCorrectly(int instanceIndex) {
		return transformBridge.classifyAbility(this, instanceIndex);
	}

	/**
	 * Clone of the classifier.
	 * 
	 * @return the clone
	 */
	@Override
	public final Object clone() {
		Classifier clone = new Classifier(this);
		return clone;
	}

	/**
	 * @param anotherClassifier
	 *            the classifier against which check for equality
	 * @return true if the classifiers have equal chromosomes
	 */
	public final boolean equals(final Classifier anotherClassifier) {
		return transformBridge.areEqual(this, anotherClassifier);
	}

	/**
	 * Calls the bridge to fix itself.
	 */
	public final void fixChromosome() {
		transformBridge.fixChromosome(this);
	}

	/**
	 * Getter for the advocated action.
	 * 
	 * @return the advocated action
	 */
	public final int[] getActionAdvocated() {
		if (actionCache == null) {
			actionCache = transformBridge.getClassification(this);
		}
		return actionCache;
	}

	/**
	 * Getter of the ExtendedBitSet.
	 * 
	 * @return the classifier's chromosome as an extendedBitSet
	 */
	public final ExtendedBitSet getChromosome() {
		return this;
	}

	/**
	 * Returns a numeric value for comparing the classifier.
	 * 
	 * @param mode
	 *            the mode of comparison
	 * @return the value of comparison
	 */
	public final double getComparisonValue(final int mode) {
		return UpdateAlgorithmFactoryAndStrategy.currentStrategy
				.getComparisonValue(this, mode);
	}

	/**
	 * Returns the classifer's coverage approximation.
	 * 
	 * @return the classifier's coverage as calculated by the current checks
	 */
	public final double getCoverage() {
		if (this.checked == 0)
			return INITIAL_FITNESS;
		return ((double) this.covered) / ((double) this.checked);

	}

	/**
	 * Getter for the classifier's Serial Number.
	 * 
	 * @return the classifier's serial number
	 */
	public final int getSerial() {
		return this.serial;
	}

	/**
	 * @return the update object
	 */
	public final Serializable getUpdateDataObject() {
		return updateData;
	}

	/**
	 * Calls the bridge to detect if the classifier is matching the vision
	 * vector.
	 * 
	 * @param visionVector
	 *            the vision vector to match
	 * @return true if the classifier matches the visionVector
	 */
	public final boolean isMatch(final double[] visionVector) {
		return transformBridge.isMatch(visionVector, this);
	}

	/**
	 * Checks if Classifier is matches an instance vector. Through caching for
	 * performance optimization.
	 * 
	 * @param instanceIndex
	 *            the instance index to check for a match
	 * @return true if the classifier matches the instance of the given index
	 */
	public final boolean isMatch(final int instanceIndex) {
		if (this.matchInstances == null)
			buildMatches();

		// if we haven't cached the answer, then answer...
		if (this.matchInstances[instanceIndex] == -1) {
			this.matchInstances[instanceIndex] = (byte) ((transformBridge
					.isMatch(
							ClassifierTransformBridge.instances[instanceIndex],
							this)) ? 1 : 0);
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
	public final boolean isMoreGeneral(final Classifier testClassifier) {
		return transformBridge.isMoreGeneral(this, testClassifier);
	}

	/**
	 * Setter for advocated action.
	 * 
	 * @param action
	 *            the action to set the classifier to advocate for
	 */
	public final void setActionAdvocated(final int action) {
		transformBridge.setClassification(this, action);
		actionCache = null;
	}

	/**
	 * Call the update strategy for setting value.
	 * 
	 * @param mode
	 *            the mode to set
	 * @param comparisonValue
	 *            the comparison value to set
	 */
	public final void setComparisonValue(final int mode,
			final double comparisonValue) {
		UpdateAlgorithmFactoryAndStrategy.currentStrategy.setComparisonValue(
				this, mode, comparisonValue);
	}

	/**
	 * Sets the classifier's subsumption ability.
	 * 
	 * @param canSubsumeAbility
	 *            true if the classifier is able to subsume
	 */
	public final void setSubsumptionAbility(final boolean canSubsumeAbility) {
		canSubsume = canSubsumeAbility;
	}

	/**
	 * Calls the bridge to divide bits into attributes.
	 * 
	 * @return the bitstring representation of the classifier
	 */
	public final String toBitString() {
		return transformBridge.toBitSetString(this);
	}

	/**
	 * Calls the bridge to convert it self to natural language string.
	 * 
	 * @return the classifier described in a string
	 */
	@Override
	public final String toString() {
		return transformBridge.toNaturalLanguageString(this);
	}

	/**
	 * Sets the update-specific and transform-specific data needed.
	 */
	private void setConstructionData() {
		updateData = UpdateAlgorithmFactoryAndStrategy
				.createDefaultDataObject();

		transformBridge.setRepresentationSpecificClassifierData(this);

		this.serial = currentSerial++;
	}

}