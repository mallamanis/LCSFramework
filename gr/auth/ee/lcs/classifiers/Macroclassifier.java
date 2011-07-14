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