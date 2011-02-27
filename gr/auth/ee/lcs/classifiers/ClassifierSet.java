package gr.auth.ee.lcs.classifiers;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Vector;

/**
 * Implement set of Classifiers, counting numerosity for classifiers. This
 * object is serializable
 * 
 * @author Miltos Allamanis
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
	public static ClassifierSet openClassifierSet(String path,
			ISizeControlStrategy sizeControlStrategy) {
		FileInputStream fis = null;
		ObjectInputStream in = null;
		ClassifierSet opened = null;

		try {
			fis = new FileInputStream(path);
			in = new ObjectInputStream(fis);

			opened = (ClassifierSet) in.readObject();
			opened.myISizeControlStrategy = sizeControlStrategy;

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
	public static void saveClassifierSet(ClassifierSet toSave, String filename) {
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
	private Vector<Macroclassifier> myMacroclassifiers;

	/**
	 * An interface for a strategy on deleting classifiers from the set. This
	 * attribute is transient and therefore not serializable
	 */
	transient private ISizeControlStrategy myISizeControlStrategy;

	/**
	 * The default ClassifierSet constructor.
	 */
	public ClassifierSet(ISizeControlStrategy sizeControlStrategy) {
		this.myISizeControlStrategy = sizeControlStrategy;
		this.myMacroclassifiers = new Vector<Macroclassifier>();

	}

	/**
	 * Adds a classifier with the a given numerosity to the set. It checks if
	 * the classifier already exists and increases its numerosity. It also
	 * checks for subsumption and updates the set's numerosity
	 * 
	 * @param thoroughAdd
	 *            to thoroughly check addition
	 */
	public void addClassifier(Macroclassifier macro, boolean thoroughAdd) {

		int numerosity = macro.numerosity;
		// Add numerosity to the Set
		this.totalNumerosity += numerosity;

		// Subsume if possible
		if (thoroughAdd) {
			Classifier aClassifier = macro.myClassifier;
			for (int i = 0; i < myMacroclassifiers.size(); i++) {
				Classifier theClassifier = myMacroclassifiers.elementAt(i).myClassifier;
				if (theClassifier.canSubsume) {
					if (theClassifier.isMoreGeneral(aClassifier)) {
						// Subsume and control size...
						myMacroclassifiers.elementAt(i).numerosity += numerosity;
						if (myISizeControlStrategy != null)
							myISizeControlStrategy.controlSize(this);
						return;
					}
				} else if (theClassifier.equals(aClassifier)) { // Or it can't
																// subsume but
																// it is equal
					myMacroclassifiers.elementAt(i).numerosity += numerosity;
					if (myISizeControlStrategy != null)
						myISizeControlStrategy.controlSize(this);
					return;
				}
			}
		}

		/*
		 * No matching or subsumable more general classifier found. Add and
		 * control size...
		 */
		this.myMacroclassifiers.add(macro);
		if (myISizeControlStrategy != null)
			myISizeControlStrategy.controlSize(this);
	}

	/**
	 * removes a micro-classifier from the set. It either completely deletes it
	 * (if the classsifier's numerosity is 0) or by decreasing the numerosity
	 */
	public void deleteClassifier(Classifier aClassifier) {

		int index;
		for (index = 0; index < myMacroclassifiers.size(); index++) {
			if (myMacroclassifiers.elementAt(index).myClassifier.getSerial() == aClassifier
					.getSerial())
				break;
		}

		if (index == myMacroclassifiers.size())
			return;
		deleteClassifier(index);

	}

	/**
	 * Deletes a classifier with the given index. If the macroclassifier at the
	 * given index contains more than one classifier the numerosity is increased
	 * 
	 * @param index
	 *            the index of the classifier's macroclassifier to delete
	 */
	public void deleteClassifier(int index) {
		this.totalNumerosity--;
		if (this.myMacroclassifiers.elementAt(index).numerosity > 1)
			this.myMacroclassifiers.elementAt(index).numerosity--;
		else
			this.myMacroclassifiers.remove(index);
	}

	/**
	 * @return the classifier at the specified index
	 */
	public Classifier getClassifier(int index) {
		return this.myMacroclassifiers.elementAt(index).myClassifier;
	}

	/**
	 * returns a classifier's numerosity (the number of microclassifiers).
	 * 
	 * @return the given classifier's numerosity
	 */
	public int getClassifierNumerosity(Classifier aClassifier) {
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
	public int getClassifierNumerosity(int index) {
		return this.myMacroclassifiers.elementAt(index).numerosity;
	}

	/**
	 * Returns the macroclassifier at the given index.
	 * 
	 * @param index
	 * @return
	 */
	public Macroclassifier getMacroclassifier(int index) {
		return this.myMacroclassifiers.elementAt(index);
	}

	/**
	 * Getter.
	 * 
	 * @return the number of macroclassifiers in the set
	 */
	public int getNumberOfMacroclassifiers() {
		return this.myMacroclassifiers.size();
	}

	/**
	 * returns the set's total numerosity (the total number of
	 * microclassifiers).
	 */
	public int getTotalNumerosity() {
		return this.totalNumerosity;
	}

	/**
	 * @return true if the set is empty
	 */
	public boolean isEmpty() {
		return this.myMacroclassifiers.isEmpty();
	}

	/**
	 * Postprocessing in the classifier set. Simply remove all classifiers with
	 * less than a minimum experience and minimum fitness
	 * 
	 * @param minExperience
	 *            the minimum experience of the classifiers
	 * @param minFitness
	 *            the minimum fitness of the classifiers
	 */
	public void postProcessThreshold(int minExperience, float minFitness) {
		for (int i = this.myMacroclassifiers.size() - 1; i >= 0; i--) {
			if (this.myMacroclassifiers.elementAt(i).myClassifier.experience < minExperience
					|| this.myMacroclassifiers.elementAt(i).myClassifier.fitness < minFitness)
				this.myMacroclassifiers.removeElementAt(i);
		}

	}

	/**
	 * Self subsume.
	 */
	public void selfSubsume() {
		for (int i = 0; i < this.getNumberOfMacroclassifiers(); i++) {
			Macroclassifier cl = this.getMacroclassifier(0);
			int numerosity = cl.numerosity;
			this.myMacroclassifiers.remove(0);
			this.totalNumerosity -= numerosity;
			this.addClassifier(cl, true);
		}
	}

}