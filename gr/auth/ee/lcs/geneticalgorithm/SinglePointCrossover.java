package gr.auth.ee.lcs.geneticalgorithm;

import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ExtendedBitSet;

public class SinglePointCrossover implements IBinaryGeneticOperator {

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
  
  protected ExtendedBitSet performCrossover(ExtendedBitSet chromosomeA,ExtendedBitSet chromosomeB,int position){
	  ExtendedBitSet child=(ExtendedBitSet) chromosomeA.clone();
	  child.setSubSet(position, chromosomeB.getSubSet(position, chromosomeB.size()-position));
	  return child;
  }

}