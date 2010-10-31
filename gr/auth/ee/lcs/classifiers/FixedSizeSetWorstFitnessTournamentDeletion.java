/**
 * 
 */
package gr.auth.ee.lcs.classifiers;

import gr.auth.ee.lcs.geneticalgorithm.INaturalSelector;

/**
 * A fixed size control strategy. Classifiers are deleted by worst-tournaments
 * @author Miltos Allamanis
 *
 */
public class FixedSizeSetWorstFitnessTournamentDeletion implements ISizeControlStrategy {

	private INaturalSelector mySelector;
	private int populationSize;
	
	/**
	 * Constructor of deletion strategy
	 * @param populationSize the size that the population will have
	 * @param tournamentSize the size of the tournaments done
	 */
	public FixedSizeSetWorstFitnessTournamentDeletion(int populationSize, INaturalSelector selector){
		this.populationSize=populationSize;	
		mySelector=selector;
	}
	
	/**
	 * @see gr.auth.ee.lcs.classifiers.ISizeControlStrategy#controlSize(gr.auth.ee.lcs.classifiers.ClassifierSet)
	 */
	@Override
	public void controlSize(ClassifierSet aSet) {
		ClassifierSet toBeDeleted=new ClassifierSet(new DummySizeControlStrategy());
		while (aSet.totalNumerosity>populationSize){
			mySelector.select(1, aSet, toBeDeleted) ;
			//System.out.println("Deleting:"+toBeDeleted.getClassifier(0));  //DEBUG
			aSet.deleteClassifier(toBeDeleted.getClassifier(0));
			toBeDeleted.deleteClassifier(0);
		}

	}

}
