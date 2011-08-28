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

import gr.auth.ee.lcs.AbstractLearningClassifierSystem;
import gr.auth.ee.lcs.data.AbstractUpdateStrategy;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Vector;

/**
 * Implement set of Classifiers, counting numerosity for classifiers. This
 * object is serializable.
 * 
 * @author Miltos Allamanis
 * 
 * @has 1 - * Macroclassifier
 * @has 1 - 1 IPopulationControlStrategy
 */
public class ClassifierSet implements Serializable {

	/**
	 * Serialization id for versioning.
	 */
	private static final long serialVersionUID = 2664983888922912954L;

	/**
	 * Open a saved (and serialized) ClassifierSet.
	 * 
	 * @param path
	 *            the path of the ClassifierSet to be opened
	 * @param sizeControlStrategy
	 *            the ClassifierSet's
	 * @return the opened classifier set
	 */
	public static ClassifierSet openClassifierSet(final String path,
			final IPopulationControlStrategy sizeControlStrategy,
			final AbstractLearningClassifierSystem lcs) {
		FileInputStream fis = null;
		ObjectInputStream in = null;
		ClassifierSet opened = null;

		try {
			fis = new FileInputStream(path);
			in = new ObjectInputStream(fis);

			opened = (ClassifierSet) in.readObject();
			opened.myISizeControlStrategy = sizeControlStrategy;

			for (int i = 0; i < opened.getNumberOfMacroclassifiers(); i++) {
				final Classifier cl = opened.getClassifier(i);
				cl.setLCS(lcs);
			}

			in.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}

		return opened;
	}

	/**
	 * A static function to save the classifier set.
	 * 
	 * @param toSave
	 *            the set to be saved
	 * @param filename
	 *            the path to save the set
	 */
	public static void saveClassifierSet(final ClassifierSet toSave,
			final String filename) {
		FileOutputStream fos = null;
		ObjectOutputStream out = null;

		try {
			fos = new FileOutputStream(filename);
			out = new ObjectOutputStream(fos);
			out.writeObject(toSave);
			out.close();

		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

	/**
	 * The total numerosity of all classifiers in set.
	 */
	private int totalNumerosity = 0;

	/**
	 * Macroclassifier vector.
	 */
	private final Vector<Macroclassifier> myMacroclassifiers;

	/**
	 * An interface for a strategy on deleting classifiers from the set. This
	 * attribute is transient and therefore not serializable.
	 */
	private transient IPopulationControlStrategy myISizeControlStrategy;

	/**
	 * The default ClassifierSet constructor.
	 * 
	 * @param sizeControlStrategy
	 *            the size control strategy to use for controlling the set
	 */
	public ClassifierSet(final IPopulationControlStrategy sizeControlStrategy) {
		this.myISizeControlStrategy = sizeControlStrategy;
		this.myMacroclassifiers = new Vector<Macroclassifier>();

	}

	/**
	 * Adds a classifier with the a given numerosity to the set. It checks if
	 * the classifier already exists and increases its numerosity. It also
	 * checks for subsumption and updates the set's numerosity.
	 * 
	 * @param thoroughAdd
	 *            to thoroughly check addition
	 * @param macro
	 *            the macroclassifier to add to the set
	 */
	public final void addClassifier(final Macroclassifier macro,
			final boolean thoroughAdd) {

		final int numerosity = macro.numerosity;
		// Add numerosity to the Set
		this.totalNumerosity += numerosity;

		// Subsume if possible
		if (thoroughAdd) {
			final Classifier aClassifier = macro.myClassifier;
			for (int i = 0; i < myMacroclassifiers.size(); i++) {
				final Classifier theClassifier = myMacroclassifiers
						.elementAt(i).myClassifier;
				if (theClassifier.canSubsume()) {
					if (theClassifier.isMoreGeneral(aClassifier)) {
						// Subsume and control size...
						myMacroclassifiers.elementAt(i).numerosity += numerosity;
						if (myISizeControlStrategy != null) {
							myISizeControlStrategy.controlPopulation(this);
						}
						return;
					}
				} else if (theClassifier.equals(aClassifier)) { // Or it can't
																// subsume but
																// it is equal
					myMacroclassifiers.elementAt(i).numerosity += numerosity;
					if (myISizeControlStrategy != null) {
						myISizeControlStrategy.controlPopulation(this);
					}
					return;
				}
			}
		}

		/*
		 * No matching or subsumable more general classifier found. Add and
		 * control size...
		 */
		this.myMacroclassifiers.add(macro);
		if (myISizeControlStrategy != null) {
			myISizeControlStrategy.controlPopulation(this);
		}
	}

	/**
	 * Removes a micro-classifier from the set. It either completely deletes it
	 * (if the classsifier's numerosity is 0) or by decreasing the numerosity.
	 * 
	 * @param aClassifier
	 *            the classifier to delete
	 */
	public final void deleteClassifier(final Classifier aClassifier) {

		int index;
		final int macroSize = myMacroclassifiers.size();
		for (index = 0; index < macroSize; index++) {
			if (myMacroclassifiers.elementAt(index).myClassifier.getSerial() == aClassifier
					.getSerial())
				break;
		}

		if (index == macroSize)
			return;
		deleteClassifier(index);

	}

	/**
	 * Deletes a classifier with the given index. If the macroclassifier at the
	 * given index contains more than one classifier the numerosity is decreased
	 * by one.
	 * 
	 * @param index
	 *            the index of the classifier's macroclassifier to delete
	 */
	public final void deleteClassifier(final int index) {
		this.totalNumerosity--;
		if (this.myMacroclassifiers.elementAt(index).numerosity > 1) {
			this.myMacroclassifiers.elementAt(index).numerosity--;
		} else {
			this.myMacroclassifiers.remove(index);
		}
	}

	/**
	 * Generate a match set for a given instance.
	 * 
	 * @param dataInstance
	 *            the instance to be matched
	 * @return a ClassifierSet containing the match set
	 */
	public final ClassifierSet generateMatchSet(final double[] dataInstance) {
		final ClassifierSet matchSet = new ClassifierSet(null);
		final int populationSize = this.getNumberOfMacroclassifiers();
		// TODO: Parallelize for performance increase
		for (int i = 0; i < populationSize; i++) {
			if (this.getClassifier(i).isMatch(dataInstance)) {
				matchSet.addClassifier(this.getMacroclassifier(i), false);
			}
		}
		return matchSet;
	}

	/**
	 * Generate match set from data instance.
	 * 
	 * @param dataInstanceIndex
	 *            the index of the instance
	 * @return the match set
	 */
	public final ClassifierSet generateMatchSet(final int dataInstanceIndex) {
		final ClassifierSet matchSet = new ClassifierSet(null);
		final int populationSize = this.getNumberOfMacroclassifiers();
		// TODO: Parallelize for performance increase
		for (int i = 0; i < populationSize; i++) {
			if (this.getClassifier(i).isMatch(dataInstanceIndex)) {
				matchSet.addClassifier(this.getMacroclassifier(i), false);
			}
		}
		return matchSet;
	}

	/**
	 * Return the classifier at a given index of the macroclassifier vector.
	 * 
	 * @param index
	 *            the index of the macroclassifier
	 * @return the classifier at the specified index
	 */
	public final Classifier getClassifier(final int index) {
		return this.myMacroclassifiers.elementAt(index).myClassifier;
	}

	/**
	 * Returns a classifier's numerosity (the number of microclassifiers).
	 * 
	 * @param aClassifier
	 *            the classifier
	 * @return the given classifier's numerosity
	 */
	public final int getClassifierNumerosity(final Classifier aClassifier) {
		for (int i = 0; i < myMacroclassifiers.size(); i++) {
			if (myMacroclassifiers.elementAt(i).myClassifier.getSerial() == aClassifier
					.getSerial())
				return this.myMacroclassifiers.elementAt(i).numerosity;
		}
		return 0;
	}

	/**
	 * Overloaded function for getting a numerosity.
	 * 
	 * @param index
	 *            the index of the macroclassifier
	 * @return the index'th macroclassifier numerosity
	 */
	public final int getClassifierNumerosity(final int index) {
		return this.myMacroclassifiers.elementAt(index).numerosity;
	}

	/**
	 * Returns the macroclassifier at the given index.
	 * 
	 * @param index
	 *            the index of the macroclassifier vector
	 * @return the macroclassifier at a given index
	 */
	public final Macroclassifier getMacroclassifier(final int index) {
		return new Macroclassifier(this.myMacroclassifiers.elementAt(index));
	}

	/**
	 * Getter.
	 * 
	 * @return the number of macroclassifiers in the set
	 */
	public final int getNumberOfMacroclassifiers() {
		return this.myMacroclassifiers.size();
	}

	/**
	 * Get the set's population control strategy
	 * 
	 * @return the set's population control strategy
	 */
	public final IPopulationControlStrategy getPopulationControlStrategy() {
		return myISizeControlStrategy;
	}

	/**
	 * Returns the set's total numerosity (the total number of
	 * microclassifiers).
	 * 
	 * @return the sets total numerosity
	 */
	public final int getTotalNumerosity() {
		return this.totalNumerosity;
	}

	/**
	 * @return true if the set is empty
	 */
	public final boolean isEmpty() {
		return this.myMacroclassifiers.isEmpty();
	}

	/**
	 * Merge a set into this set.
	 * 
	 * @param aSet
	 *            the set to be merged.
	 */
	public final void merge(final ClassifierSet aSet) {
		final int setSize = aSet.getNumberOfMacroclassifiers();
		for (int i = 0; i < setSize; i++) {
			final Macroclassifier ml = aSet.getMacroclassifier(i);
			this.addClassifier(ml, false);
		}
	}

	/**
	 * Print all classifiers in the set.
	 */
	public final void print() {
		System.out.println(toString());
	}

	/**
	 * Remove all set's macroclassifiers.
	 */
	public final void removeAllMacroclassifiers() {
		this.myMacroclassifiers.clear();
		this.totalNumerosity = 0;
	}

	/**
	 * Self subsume.
	 */
	public final void selfSubsume() {
		for (int i = 0; i < this.getNumberOfMacroclassifiers(); i++) {
			final Macroclassifier cl = this.getMacroclassifier(0);
			final int numerosity = cl.numerosity;
			this.myMacroclassifiers.remove(0);
			this.totalNumerosity -= numerosity;
			this.addClassifier(cl, true);
		}
	}

	@Override
	public String toString() {
		StringBuffer response = new StringBuffer();
		for (int i = 0; i < this.getNumberOfMacroclassifiers(); i++) {
			response.append(this.getClassifier(i).toString()
					+ " fit:"
					+ this.getClassifier(i)
							.getComparisonValue(
									AbstractUpdateStrategy.COMPARISON_MODE_EXPLOITATION)
					+ " exp:" + this.getClassifier(i).experience + " num:"
					+ this.getClassifierNumerosity(i) + "cov:"
					+ this.getClassifier(i).getCoverage() + "\n");
			response.append(this.getClassifier(i).getUpdateSpecificData()
					+ "\n");
		}
		return response.toString();
	}

}