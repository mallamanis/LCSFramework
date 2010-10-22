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
		
		for (int i=0;i<howManyToSelect;i++)
			this.tournament(fromPopulation, toPopulation);

	}
	
	/**
	 * Runs a tournament in the fromPopulation with the size defined in tournamentSize (during construction)
	 * The winner of the tournament is added to the toPopulation
	 * @param fromPopulation the source population to run the tournament in
	 * @param toPopulation the destination population of the tournament winners
	 * 
	 */
	private void tournament( ClassifierSet fromPopulation, ClassifierSet toPopulation){
		//Generate random participants
		int participants[]=new int[tournamentSize];
		for (int i=0;i<tournamentSize;i++){
			participants[i]=(int) Math.ceil((Math.random()*fromPopulation.totalNumerosity));
		}
		
		//Sort by order
		Arrays.sort(participants);
		double bestFitness=0; //Best fitness found in tournament
		int currentParticipantIndex=0; //the index in the participants array of the current tournament participant
		int bestParticipant=-1; //The current best participant
		int currentClassifierIndex=0; //the index of the actual classifier (counting numerosity)
		int currentMacroclassifierIndex=0; //The index of the macroclassifier being used
		//Run tournament
		do{
			currentClassifierIndex+=fromPopulation.getClassifierNumerosity(currentMacroclassifierIndex);
			//Is the current macroclassifier the participant?
			if (currentClassifierIndex>=participants[currentParticipantIndex]){
				//Find best
				if (fromPopulation.getClassifier(currentMacroclassifierIndex).fitness>bestFitness){
					bestFitness=fromPopulation.getClassifier(currentMacroclassifierIndex).fitness;
					bestParticipant=currentMacroclassifierIndex;
				}
					
				currentParticipantIndex++;
			}else{			
				currentMacroclassifierIndex++;
			}
		}while(currentParticipantIndex<tournamentSize);
		
		//Add winner to set
		toPopulation.addClassifier(fromPopulation.getClassifier(bestParticipant), 1);
		
	}

}
