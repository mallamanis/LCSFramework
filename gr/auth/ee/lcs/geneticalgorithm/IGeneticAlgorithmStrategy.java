package gr.auth.ee.lcs.geneticalgorithm;

import gr.auth.ee.lcs.classifiers.ClassifierSet;

/** 
 *  An interface for evolving a set.
 */
public interface IGeneticAlgorithmStrategy {

  public void evolveSet(ClassifierSet evolveSet, ClassifierSet population);

}