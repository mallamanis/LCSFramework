package gr.auth.ee.lcs.geneticalgorithm.operators;

import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ExtendedBitSet;
import gr.auth.ee.lcs.geneticalgorithm.IUnaryGeneticOperator;

/**
 * Implements a mutation operator. Bits of a chromosome are mutated by following
 * a uniform distribution for each one.
 * 
 * @author Miltos Allamanis
 * 
 */
public class UniformBitMutation implements IUnaryGeneticOperator {

	/**
	 * The rate at which the mutation happens.
	 */
	private double mutationRate;

	/**
	 * The default constructor.
	 * 
	 * @param mutationRate
	 *            the probability that a bit will be flipped Initializes the
	 *            operator's attributes.
	 */
	public UniformBitMutation(double rate) {
		this.mutationRate = rate;
	}

	/**
	 * operates on the given classifier by mutating its bits
	 * 
	 * @return the mutated classifier
	 */
	public Classifier operate(Classifier aClassifier) {
		int chromosomeSize = aClassifier.chromosome.size();
		ExtendedBitSet chromosome = aClassifier.getChromosome();
		for (int i = 0; i < chromosomeSize; i++) {
			if (Math.random() < mutationRate)
				chromosome.invert(i);
		}
		return aClassifier;
	}

}