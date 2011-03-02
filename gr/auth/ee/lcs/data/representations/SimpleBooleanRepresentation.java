/**
 * 
 */
package gr.auth.ee.lcs.data.representations;

import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ExtendedBitSet;
import gr.auth.ee.lcs.data.ClassifierTransformBridge;

/**
 * Implements the a simple boolean representation of the chromosomes. Each bit
 * is represented by two bits. The first bit is the activation bit and the
 * second bit is the bit value. When the activation bit is "1" then the bit is
 * active When the activation bit is "0" we have a don't care (#) bit
 * 
 * @author Miltos Allamanis
 * 
 */
public class SimpleBooleanRepresentation extends ClassifierTransformBridge {

	/**
	 * The P# (the covering operator generalization rate).
	 */
	private double coverGeneralizationRate = 0.5;

	/**
	 * The costructor.
	 * 
	 * @param coveringGeneralizationRate
	 *            the generalization rate for covering
	 * @param visionBits
	 *            the bits used at vision
	 */
	public SimpleBooleanRepresentation(double coveringGeneralizationRate,
			int visionBits) {
		coverGeneralizationRate = coveringGeneralizationRate;
		setVisionSize(visionBits);
	}

	/**
	 * Chromosome Size.
	 */
	private int chromosomeSize = 0;

	/**
	 * Set the number of bits in the input. This size determines the size of the
	 * chormosome In this representation the size of the chromosome is 2*size
	 */
	public final void setVisionSize(final int size) {
		this.chromosomeSize = 2 * size;
	}

	/**
	 * Returns true if the input visionVector matches the chromosome.
	 * 
	 * @see gr.auth.ee.lcs.data.ClassifierTransformBridge#isMatch(double[],
	 *      gr.auth.ee.lcs.classifiers.ExtendedBitSet)
	 */
	@Override
	public boolean isMatch(double[] visionVector, ExtendedBitSet chromosome) {
		for (int i = 0; i < chromosomeSize; i += 2) {
			if (chromosome.get(i)) {
				double test = chromosome.get(i + 1) ? 1 : 0;
				if (visionVector[i / 2] != test)
					return false;
			}
		}
		return true;
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
	public String toNaturalLanguageString(Classifier aClassifier) {
		String output = "";
		// Get Chromosome
		ExtendedBitSet chromosome = aClassifier.getChromosome();
		for (int i = 0; i < chromosomeSize; i += 2) {
			if (chromosome.get(i)) {
				output = (chromosome.get(i + 1) ? "1" : "0") + output;
			} else {
				output = "#" + output;
			}
		}
		output += "=>" + aClassifier.getActionAdvocated();
		return output;
	}

	/**
	 * Creates a simple random covering classifier.
	 * 
	 * @see gr.auth.ee.lcs.data.ClassifierTransformBridge#createRandomCoveringClassifier(double[],int)
	 */
	@Override
	public Classifier createRandomCoveringClassifier(double[] visionVector) {
		Classifier coverClassifier = new Classifier();

		// Transform visionVector to BitSet (generalization not-set)
		ExtendedBitSet chromosome = coverClassifier.getChromosome();
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
		((int[]) (coverClassifier.transformData))[0] = (Math.random() < .5 ? 1
				: 0);
		return coverClassifier;
	}

	/**
	 * @see gr.auth.ee.lcs.data.ClassifierTransformBridge#isMoreGeneral(gr.auth.ee.lcs.classifiers.Classifier,
	 *      gr.auth.ee.lcs.classifiers.Classifier)
	 */
	@Override
	public boolean isMoreGeneral(Classifier baseClassifier,
			Classifier testClassifier) {
		// If classifiers advocate for different actions, return false
		if (baseClassifier.getActionAdvocated() != testClassifier
				.getActionAdvocated())
			return false;

		ExtendedBitSet baseChromosome = baseClassifier.getChromosome();
		ExtendedBitSet testChromosome = testClassifier.getChromosome();

		// For each chromosome
		for (int i = 0; i < chromosomeSize; i += 2) {

			// if the base classifier is specific and the test is # return false
			if (baseChromosome.get(i) && !testChromosome.get(i))
				return false;
			if (baseChromosome.get(i + 1) != testChromosome.get(i + 1)
					&& baseChromosome.get(i))
				return false;
		}
		return true;
	}

	/**
	 * In this representation there is nothing to fix. It does nothing
	 * 
	 * @see gr.auth.ee.lcs.data.ClassifierTransformBridge#fixChromosome(gr.auth.ee.lcs.classifiers.ExtendedBitSet)
	 */
	@Override
	public void fixChromosome(ExtendedBitSet aChromosome) {
		return;
	}

	/**
	 * Returns the size of the chromosome needed for this representation.
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
	 * gr.auth.ee.lcs.data.ClassifierTransformBridge#toBitSetString(gr.auth.
	 * ee.lcs.classifiers.Classifier)
	 */
	@Override
	public String toBitSetString(Classifier classifier) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gr.auth.ee.lcs.data.ClassifierTransformBridge#
	 * setRepresentationSpecificClassifierData
	 * (gr.auth.ee.lcs.classifiers.Classifier)
	 */
	@Override
	public void setRepresentationSpecificClassifierData(Classifier aClassifier) {
		aClassifier.transformData = new int[1];
		return;
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
	/**
	 * @see gr.auth.ee.lcs.data.ClassifierTransformBridge#areEqual(gr.auth.ee.lcs.classifiers.Classifier,gr.auth.ee.lcs.classifiers.Classifier)
	 */
	public boolean areEqual(Classifier cl1, Classifier cl2) {
		if (cl1.getActionAdvocated() != cl2.getActionAdvocated())
			return false;

		ExtendedBitSet baseChromosome = cl1.getChromosome();
		ExtendedBitSet testChromosome = cl2.getChromosome();

		// For each chromosome
		for (int i = 0; i < chromosomeSize; i += 2) {

			// if the base classifier is specific and the test is # return false
			if (baseChromosome.get(i) != testChromosome.get(i))
				return false;
			else if (baseChromosome.get(i + 1) != testChromosome.get(i + 1)
					&& baseChromosome.get(i))
				return false;
		}
		return true;
	}

	@Override
	public int getClassification(Classifier aClassifier) {
		return ((int[]) (aClassifier.transformData))[0];
	}

	@Override
	public void setClassification(Classifier aClassifier, int action) {
		((int[]) (aClassifier.transformData))[0] = action;

	}

	@Override
	public boolean classifiesCorrectly(Classifier aClassifier, int instanceIndex) {
		return this.instances[instanceIndex][this.instances[instanceIndex].length - 1] == ((int[]) (aClassifier.transformData))[0];
	}

	@Override
	public boolean classifiesCorrectly(Classifier aClassifier, double[] vision) {
		return ((int[]) (aClassifier.transformData))[0] == vision[vision.length - 1];
	}

}
