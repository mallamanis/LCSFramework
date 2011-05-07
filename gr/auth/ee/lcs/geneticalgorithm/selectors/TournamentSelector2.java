/**
 * 
 */
package gr.auth.ee.lcs.geneticalgorithm.selectors;

import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.classifiers.Macroclassifier;
import gr.auth.ee.lcs.geneticalgorithm.INaturalSelector;

import java.util.Arrays;

/**
 * A tournament selecting the best fitness classifier.
 * 
 * @author Miltos Allamanis
 * 
 */
public class TournamentSelector2 implements INaturalSelector {

	/**
	 * The size of the tournaments.
	 */
	private final int tournamentSize;

	/**
	 * The type of the tournaments.
	 */
	private final boolean max;

	/**
	 * The percentage of population size, used for tournament selection.
	 */
	private final double percentSize;

	/**
	 * The comparison mode for the tournaments.
	 */
	private final int mode;

	/**
	 * Constructor.
	 * 
	 * @param sizeOfTournaments
	 *            the tournament size (number of participants)
	 * @param max
	 *            true if the tournament selects the max fitness
	 * @param comparisonMode
	 *            the comparison mode to be used
	 */
	public TournamentSelector2(final double sizeOfTournaments,
			final boolean max, final int comparisonMode) {
		this.tournamentSize = 0;
		this.max = max;
		this.mode = comparisonMode;
		percentSize = sizeOfTournaments;
	}

	/**
	 * The default constructor of the selector.
	 * 
	 * @param sizeOfTournaments
	 *            the size of the tournament
	 * @param max
	 *            if true the we select the max fitness participant, else we
	 *            select the min.
	 * @param comparisonMode
	 *            comparison mode @see
	 *            gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy
	 */
	public TournamentSelector2(final int sizeOfTournaments, final boolean max,
			final int comparisonMode) {
		this.tournamentSize = sizeOfTournaments;
		this.max = max;
		this.mode = comparisonMode;
		percentSize = 0;
	}

	
	private int select(ClassifierSet fromPopulation) {
		int size;
		if (tournamentSize == 0)
			size = (int) Math.floor(fromPopulation.getTotalNumerosity()
					* percentSize);
		else
			size = tournamentSize;

		int[] participants = new int[size];
		// Create random participants
		for (int j = 0; j < participants.length; j++) {
			participants[j] = (int) Math.floor((Math.random() * fromPopulation
					.getTotalNumerosity()));
		}
		return this.tournament(fromPopulation, participants);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gr.auth.ee.lcs.geneticalgorithm.INaturalSelector#select(int,
	 * gr.auth.ee.lcs.classifiers.ClassifierSet,
	 * gr.auth.ee.lcs.classifiers.ClassifierSet)
	 */
	@Override
	public final void select(final int howManyToSelect,
			final ClassifierSet fromPopulation, final ClassifierSet toPopulation) {

		for (int i = 0; i < howManyToSelect; i++) {

			toPopulation.addClassifier(
					new Macroclassifier(fromPopulation.getClassifier(this
							.select(fromPopulation)), 1), false);

		}

	}

	/**
	 * Runs a tournament in the fromPopulation with the size defined in
	 * tournamentSize (during construction) The winner of the tournament is
	 * added to the toPopulation.
	 * 
	 * @param fromPopulation
	 *            the source population to run the tournament in
	 * @param participants
	 *            the int[] of indexes of participants
	 * @return the index of the tournament winner
	 */
	public final int tournament(final ClassifierSet fromPopulation,
			final int[] participants) {

		// Sort by order
		Arrays.sort(participants);

		// Construct cumulative numerosity
		final int populationSize = fromPopulation.getNumberOfMacroclassifiers();
		int[] cumulativeNum = new int[populationSize];
		cumulativeNum[0] = fromPopulation.getClassifierNumerosity(0);
		for (int i = 1; i < populationSize; i++)
			cumulativeNum[i] = cumulativeNum[i - 1]
					+ fromPopulation.getClassifierNumerosity(i);

		int bestMacroclassifierParticipant = 0;
		double winnerFitness = fromPopulation.getClassifier(
				getMacroIndex(participants[0], cumulativeNum))
				.getComparisonValue(mode);
		for (int i = 1; i < participants.length; i++) {
			final int currentMacro = getMacroIndex(participants[i],
					cumulativeNum);
			final double currentFitness = fromPopulation.getClassifier(
					currentMacro).getComparisonValue(mode);
			if (((currentFitness - winnerFitness) * (max ? 1 : -1)) > 0) {
				winnerFitness = currentFitness;
				bestMacroclassifierParticipant = currentMacro;
			}
		}
		return bestMacroclassifierParticipant;

	}

	/**
	 * Convert micro-classifier index to macro-classifier indices.
	 * 
	 * @param microIndex
	 *            the microclassifier index.
	 * @param cumulativeNumerosity
	 *            the cummulative numerosity array
	 * @return the macro-classifier index
	 */
	private int getMacroIndex(int microIndex, int[] cumulativeNumerosity) {
		int macroIndex = 0;
		for (int i = 0; i < cumulativeNumerosity.length; i++) {
			if (cumulativeNumerosity[i] > microIndex)
				break;
			macroIndex++;
		}
		return macroIndex;
	}

}
