/**
 * 
 */
package gr.auth.ee.lcs.data.updateAlgorithms;

import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.classifiers.Macroclassifier;
import gr.auth.ee.lcs.data.AbstractUpdateAlgorithmStrategy;
import gr.auth.ee.lcs.data.ClassifierTransformBridge;
import gr.auth.ee.lcs.geneticalgorithm.IGeneticAlgorithmStrategy;

import java.io.Serializable;
import java.util.Arrays;

/**
 * An Ml-ASLCS update algorithm. The algorithm is the same to AS-LCS apart from
 * ns calculation
 * 
 * @author Miltos Allamanis
 * 
 */
public class MlASLCSUpdateAlgorithm extends AbstractUpdateAlgorithmStrategy {

	/**
	 * A data object for the *SLCS update algorithms.
	 * 
	 * @author Miltos Allamanis
	 * 
	 */
	class SLCSClassifierData implements Serializable {

		/**
		 * serial for versions.
		 */
		private static final long serialVersionUID = -20798032843413916L;

		/**
		 *
		 */
		public double fitness = .5;

		/**
		 * niche set size estimation.
		 */
		public double ns = 100;

		/**
		 * Match Set Appearances.
		 */
		public int msa = 0;

		/**
		 * true positives.
		 */
		public int tp = 0;

		/**
		 * false positives.
		 */
		public int fp = 0;

		/**
		 * Strength.
		 */
		public double str = 0;

	}

	/**
	 * The strictness factor for updating.
	 */
	private final double n;

	private final int numOfLabels;

	/**
	 * Genetic Algorithm.
	 */
	public IGeneticAlgorithmStrategy ga;

	/**
	 * A double indicating the probability that the GA will run on the matchSet
	 * (and not on the correct set).
	 */
	private final double matchSetRunProbability;

	/**
	 * The fitness threshold for subsumption.
	 */
	private final double subsumptionFitnessThreshold;

	/**
	 * The experience threshold for subsumption.
	 */
	private final int subsumptionExperienceThreshold;

	/**
	 * Object's Constructor.
	 * 
	 * @param nParameter
	 *            the strictness factor ν used in updating
	 * @param fitnessThreshold
	 *            the fitness threshold for subsumption
	 * @param experienceThreshold
	 *            the experience threshold for subsumption
	 * @param gaMatchSetRunProbability
	 *            the probability of running the GA on the match set
	 * @param geneticAlgorithm
	 *            the GA
	 * @param labels
	 *            the number of labels in the problem
	 */
	public MlASLCSUpdateAlgorithm(final double nParameter,
			final double fitnessThreshold, final int experienceThreshold,
			double gaMatchSetRunProbability,
			IGeneticAlgorithmStrategy geneticAlgorithm, int labels) {
		this.subsumptionFitnessThreshold = fitnessThreshold;
		this.subsumptionExperienceThreshold = experienceThreshold;
		this.matchSetRunProbability = gaMatchSetRunProbability;
		this.ga = geneticAlgorithm;
		this.n = nParameter;
		numOfLabels = labels;
	}

	/**
	 * Calls covering operator.
	 * 
	 * @param instanceIndex
	 *            the index of the current sample
	 */
	@Override
	public final void cover(final ClassifierSet population,
			final int instanceIndex) {
		Classifier coveringClassifier = ClassifierTransformBridge.getInstance()
				.createRandomCoveringClassifier(
						ClassifierTransformBridge.instances[instanceIndex]);
		population.addClassifier(new Macroclassifier(coveringClassifier, 1),
				false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy#
	 * createStateClassifierObject()
	 */
	@Override
	public Serializable createStateClassifierObject() {
		// TODO: Initial parameters
		return new SLCSClassifierData();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy#getComparisonValue
	 * (gr.auth.ee.lcs.classifiers.Classifier, int)
	 */
	@Override
	public double getComparisonValue(final Classifier aClassifier,
			final int mode) {
		SLCSClassifierData data = (SLCSClassifierData) aClassifier
				.getUpdateDataObject();
		switch (mode) {
		case COMPARISON_MODE_EXPLORATION:
			return data.fitness * (aClassifier.experience < 5 ? 0 : 1);
		case COMPARISON_MODE_DELETION:
			return 1 / (data.fitness
					* ((aClassifier.experience < 20) ? 100. : Math.exp(-(Double
							.isNaN(data.ns) ? 1 : data.ns) + 1)) * ((((aClassifier
					.getCoverage() == 0) || (aClassifier.getCoverage() == 1)) && (aClassifier.experience == 1)) ? 0.
					: 1));
			// TODO: Something else?
		case COMPARISON_MODE_EXPLOITATION:
			final double exploitationFitness = (((double) (data.tp)) / (double) (data.msa));
			return Double.isNaN(exploitationFitness) ? .000001
					: exploitationFitness;
		}
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy#getData(gr.auth
	 * .ee.lcs.classifiers.Classifier)
	 */
	@Override
	public final String getData(final Classifier aClassifier) {
		SLCSClassifierData data = ((SLCSClassifierData) aClassifier
				.getUpdateDataObject());
		return "tp:" + data.tp + "msa:" + data.msa + "str: " + data.str + "ns:"
				+ data.ns;
	}

	@Override
	public void performUpdate(final ClassifierSet matchSet,
			final ClassifierSet correctSet) {
		return; // Not used!
	}

	public void performUpdate(final ClassifierSet matchSet,
			final ClassifierSet correctSet, final int instanceIndex) {
		final int matchSetSize = matchSet.getNumberOfMacroclassifiers();

		final int[] niches = calculateLabelNiches(correctSet, instanceIndex);

		for (int i = 0; i < matchSetSize; i++) {
			Classifier cl = matchSet.getClassifier(i);

			SLCSClassifierData data = ((SLCSClassifierData) cl
					.getUpdateDataObject());

			data.msa++;

			if (correctSet.getClassifierNumerosity(cl) > 0) {
				data.tp += 1; // aClassifier at the correctSet
				data.ns = (data.msa * data.ns + getClassifierNicheSize(cl,
						instanceIndex, niches)) / (data.msa + 1); // TODO:
																	// Correct?
			} else {
				data.fp += 1;
			}

			// Niche set sharing heuristic...
			data.fitness = Math.pow(((double) (data.tp)) / (double) (data.msa),
					n);
			this.updateSubsumption(cl);
			cl.experience++;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy#setComparisonValue
	 * (gr.auth.ee.lcs.classifiers.Classifier, int, double)
	 */
	@Override
	public final void setComparisonValue(Classifier aClassifier, int mode,
			double comparisonValue) {
		SLCSClassifierData data = ((SLCSClassifierData) aClassifier
				.getUpdateDataObject());
		data.fitness = comparisonValue; // TODO: More generic

	}

	@Override
	public final void updateSet(final ClassifierSet population,
			final ClassifierSet matchSet, final int instanceIndex,
			final boolean evolve) {

		ClassifierSet correctSet = generateCorrectSet(matchSet, instanceIndex);

		final int[] niches = calculateLabelNiches(correctSet, instanceIndex);
		boolean emptyLabel = false;
		for (int i = 0; i < niches.length; i++) {
			if (niches[i] == 0)
				emptyLabel = true;
		}

		/*
		 * Cover if necessary
		 */
		if (evolve) {
			if (correctSet.getNumberOfMacroclassifiers() == 0) {
				cover(population, instanceIndex);
				return;
			} else if (emptyLabel) {
				cover(population, instanceIndex);
			}
		}

		performUpdate(matchSet, correctSet, instanceIndex);

		/*
		 * Run GA
		 */
		if (evolve) {
			if (Math.random() < matchSetRunProbability)
				ga.evolveSet(matchSet, population);
			else
				ga.evolveSet(correctSet, population);
		}

	}

	private int[] calculateLabelNiches(final ClassifierSet correctSet,
			final int instanceIndex) {
		final int[] niches = new int[numOfLabels];
		Arrays.fill(niches, 0);

		final int correctSetSize = correctSet.getNumberOfMacroclassifiers();
		for (int i = 0; i < correctSetSize; i++) {
			final Classifier cl = correctSet.getClassifier(i);
			for (int label = 0; label < numOfLabels; label++) {
				if (cl.classifyLabelCorrectly(instanceIndex, label) > 0) {
					niches[label]++;
				}
			}
		}
		return niches;
	}

	/**
	 * Generates the correct set.
	 * 
	 * @param matchSet
	 *            the match set
	 * @param instanceIndex
	 *            the global instance index
	 * @return the correct set
	 */
	private ClassifierSet generateCorrectSet(final ClassifierSet matchSet,
			final int instanceIndex) {
		ClassifierSet correctSet = new ClassifierSet(null);
		final int matchSetSize = matchSet.getNumberOfMacroclassifiers();
		for (int i = 0; i < matchSetSize; i++) {
			Macroclassifier cl = matchSet.getMacroclassifier(i);
			if (cl.myClassifier.classifyCorrectly(instanceIndex) == 1)
				correctSet.addClassifier(cl, false);
		}
		return correctSet;
	}

	private double getClassifierNicheSize(final Classifier aClassifier,
			final int instanceIndex, final int[] niches) {
		int mean = 0;
		int active = 0;
		int minNiche = Integer.MAX_VALUE;
		for (int label = 0; label < numOfLabels; label++) {
			if (aClassifier.classifyLabelCorrectly(instanceIndex, label) > 0) {
				if (niches[label] < minNiche)
					minNiche = niches[label];
				mean += niches[label];
				active++;
			}
		}
		return minNiche;
		/*
		 * final double result = ((double)minNiche) /
		 * (((double)mean)/((double)active)); return Double.isNaN(result)?1000:
		 * result;
		 */
	}

	/**
	 * Implementation of the subsumption strength.
	 * 
	 * @param aClassifier
	 *            the classifier, whose subsumption ability is to be updated
	 */
	protected final void updateSubsumption(final Classifier aClassifier) {
		aClassifier
				.setSubsumptionAbility((aClassifier
						.getComparisonValue(COMPARISON_MODE_EXPLOITATION) > subsumptionFitnessThreshold)
						&& (aClassifier.experience > subsumptionExperienceThreshold));
	}

}
