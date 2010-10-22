package gr.auth.ee.lcs.geneticalgorithm;

import gr.auth.ee.lcs.classifiers.ClassifierSet;

/**
 * Selects and adds the best classifier (based on fitness) from the inital ClassifierSet to the target set.
 * It adds the best classifier with howManyToSelect numerosity
 * @author Miltos Allamanis
 *
 */
public class BestClassifierSelector implements INaturalSelector {

	/**
	 * Implementation of abstract method
	 * Selects the best classifier in the fromPopulation and adds it to toPopulation
	 * @param howManyToSelect the numerosity that the best classifier is going to be added
	 * @param fromPopulation the source set of classifiers
	 * @param toPopulation the target set of classifiers. In this set the best classifier will be added
	 */
	public void select(int howManyToSelect, ClassifierSet fromPopulation, ClassifierSet toPopulation) {
	 
	  //Search for the best classifier
	  double bestFitness=-1;
	  int bestIndex=-1;
	  for (int i=0;i<fromPopulation.getNumberOfMacroclassifiers();i++){
		  double temp=fromPopulation.getClassifier(i).fitness;
		  if (temp>bestFitness){
			  bestFitness=temp;
			  bestIndex=i;
		  }
	  }
	  //Add it toPopulation
	  toPopulation.addClassifier(fromPopulation.getClassifier(bestIndex), howManyToSelect);
  }

}