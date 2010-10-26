package gr.auth.ee.lcs.geneticalgorithm;

import gr.auth.ee.lcs.classifiers.ClassifierSet;



/** 
 *  A steady-stage GA that selects two individuals from a set (with probability proportional to their total fitness) and performs a crossover and mutation corrects the classifier (if needed) and adds it to the set
 *  @author Miltos Allamanis
 * 
 */
public class NichedSteadyStateFitnessProportionalGeneticAlgorithm implements IGeneticAlgorithmStrategy {

  public double subsumptionStrengthThreshold;

  public Integer timeForGAActivation;

  @Override
  public void evolveSet(ClassifierSet evolveSet, ClassifierSet population) {
	// TODO Auto-generated method stub
	
  }

}