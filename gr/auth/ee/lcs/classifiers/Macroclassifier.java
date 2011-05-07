package gr.auth.ee.lcs.classifiers;

import java.io.Serializable;

/**
 * Represents a macroclassifier. A macroclassifier is a classifier with a
 * numerosity.
 * 
 * @has 1 - 1 Classifier
 */
public class Macroclassifier implements Serializable {

	/**
	 * Serialization id for versioning.
	 */
	private static final long serialVersionUID = 1705517271818439866L;

	/**
	 * The myClassifier's numerosity.
	 */
	public int numerosity;

	/**
	 * The (micro-)classifier of the macroclassifier.
	 */
	public Classifier myClassifier;

	/**
	 * The Macroclassifier object constructor.
	 * 
	 * @param newClassifier
	 *            the microclassifier
	 * @param clNumerosity
	 *            it's numerosity
	 */
	public Macroclassifier(final Classifier newClassifier,
			final int clNumerosity) {
		myClassifier = newClassifier;
		numerosity = clNumerosity;
	}

	/**
	 * Copy constructor.
	 * 
	 * @param copy
	 *            the macroclassifier to copy
	 */
	public Macroclassifier(final Macroclassifier copy) {
		myClassifier = copy.myClassifier;
		numerosity = copy.numerosity;
	}

	/**
	 * A wrapper of the equals method.
	 * 
	 * @param aClassifier
	 *            the classifier to be tested towards this macroclassifier
	 * @return true if the classifier and the macro-classifier have the same
	 *         rule
	 */
	public final boolean equals(final Classifier aClassifier) {
		return this.myClassifier.equals(aClassifier);
	}

	/**
	 * An overloaded function of the equals.
	 * 
	 * @param macroClassifier
	 *            the macroclassifier to be tested for equality to this
	 *            macroclassifier
	 * @return true if the classifier and the macro-classifier represent the
	 *         same rule
	 */
	public final boolean equals(final Macroclassifier macroClassifier) {
		return this.myClassifier.equals(macroClassifier.myClassifier);
	}

}