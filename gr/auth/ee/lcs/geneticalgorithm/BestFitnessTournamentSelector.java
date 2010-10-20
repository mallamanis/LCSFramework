/**
 * 
 */
package gr.auth.ee.lcs.geneticalgorithm;

import gr.auth.ee.lcs.classifiers.ClassifierSet;
import java.util.Arrays;

/**
 * A tournament selecting the best fitness classifier
 * @author Miltos Allamanis
 *
 */
public class BestFitnessTournamentSelector implements INaturalSelector {

	/**
	 * The size of the tournaments
	 */
	private int tournamentSize;
	
	public BestFitnessTournamentSelector(int sizeOfTournaments){
		this.tournamentSize=sizeOfTournaments;
	}
	
	/**
	 * @see gr.auth.ee.lcs.geneticalgorithm.INaturalSelector#select(int, gr.auth.ee.lcs.classifiers.ClassifierSet, gr.auth.ee.lcs.classifiers.ClassifierSet)
	 */
	@Override
	public void select(int howManyToSelect, ClassifierSet fromPopulation,
			ClassifierSet toPopulation) {
		
		// TODO Auto-generated method stub

	}
	
	/**
	 * 
	 * @param fromPopulation
	 * @param toPopulation
	 * @return
	 */
	private void tournament( ClassifierSet fromPopulation, ClassifierSet toPopulation){
		//Generate random participants
		int participants[]=new int[tournamentSize];
		for (int i=0;i<tournamentSize;i++){
			participants[i]=(int) Math.ceil((Math.random()*fromPopulation.totalNumerosity));
		}
		//Sort by order
		Arrays.sort(participants);
		float bestFitness=0;
		int bestParticipant=-1;
		//TODO scan for best...
	}

}
