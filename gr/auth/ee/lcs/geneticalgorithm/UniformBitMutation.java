package gr.auth.ee.lcs.geneticalgorithm;

import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ExtendedBitSet;
/**
 * Implements a mutation operator. Bits of a chromosome are mutated by following a uniform distribution 
 * for each one.
 * @author Miltos Allamanis
 *
 */
public class UniformBitMutation implements IUnaryGeneticOperator {

  public double mutationRate;

  /**
   * operates on the given classifier by mutating its bits
   * @return the mutated classifier
   */
  public Classifier operate(Classifier aClassifier) {
	  int chromosomeSize=aClassifier.chromosome.size();
	  ExtendedBitSet chromosome=aClassifier.getChromosome();
	  for (int i=0;i<chromosomeSize;i++){
		  if (Math.random()<mutationRate)
			  chromosome.invert(i);
	  }
	  return aClassifier;
  }

}