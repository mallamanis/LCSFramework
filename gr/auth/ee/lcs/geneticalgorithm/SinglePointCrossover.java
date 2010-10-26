package gr.auth.ee.lcs.geneticalgorithm;

import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ExtendedBitSet;

/**
 *  A binary genetic operator that performs gene crossover at a sigle point
 * @author Miltos Allamanis
 *
 */
public class SinglePointCrossover implements IBinaryGeneticOperator {

	/**
	 * The implementation of the abstract method
	 * @see gr.auth.ee.geneticalgorithm.IBinaryGeneticOperator
	 */
	public Classifier operate(Classifier classifierA, Classifier classifierB) {
	  int chromosomeSize=classifierB.chromosome.size();
	  Classifier child=new Classifier();
	  /*
	   * The point at which the crossover will occur 
	   */
	  int mutationPoint=(int) Math.round(Math.random()*chromosomeSize-1);
	  child.chromosome=performCrossover(classifierA.getChromosome(),classifierB.getChromosome(),mutationPoint);
	  
	  return child;
  }
  
	/**
	 * A protected function that performs a single point crossover
	 * @param chromosomeA the fisrt chromosome to crossover
	 * @param chromosomeB the second chromosome to crossover
	 * @param position the position (bit) to perform the crossover
	 * @return the new corssovered (child) chromosome
	 */
	protected ExtendedBitSet performCrossover(ExtendedBitSet chromosomeA,ExtendedBitSet chromosomeB,int position){
	  ExtendedBitSet child=(ExtendedBitSet) chromosomeA.clone();
	  child.setSubSet(position, chromosomeB.getSubSet(position, chromosomeB.size()-position));
	  return child;
  }

}