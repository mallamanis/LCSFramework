package gr.auth.ee.lcs.classifiers;

import gr.auth.ee.lcs.geneticalgorithm.INaturalSelector;

/**
 * A fixed size control strategy. Classifiers are deleted based on the selector tournaments
 * @author Miltos Allamanis
 *
 */
public class FixedSizeSetWorstFitnessDeletion implements ISizeControlStrategy {

	/**
	 * The Natural Selector used to select the the classifier to be deleted
	 */
	private INaturalSelector mySelector;
	
	/**
	 * The fixed population size of the controlled set
	 */
	private int populationSize;
	
	/**
	 * Constructor of deletion strategy
	 * @param populationSize the size that the population will have
	 * @param tournamentSize the size of the tournaments done
	 */
	public FixedSizeSetWorstFitnessDeletion(int populationSize, INaturalSelector selector){
		this.populationSize=populationSize;	
		mySelector=selector;
	}
	
	/**
	 * @see gr.auth.ee.lcs.classifiers.ISizeControlStrategy#controlSize(gr.auth.ee.lcs.classifiers.ClassifierSet)
	 */
	@Override
	public void controlSize(ClassifierSet aSet) {
		ClassifierSet toBeDeleted=new ClassifierSet(null);
		while (aSet.totalNumerosity>populationSize){
			mySelector.select(1, aSet, toBeDeleted) ;
			//System.out.println("Deleting:"+toBeDeleted.getClassifier(0));  //DEBUG
			aSet.deleteClassifier(toBeDeleted.getClassifier(0));
			toBeDeleted.deleteClassifier(0);
		}

	}

}
