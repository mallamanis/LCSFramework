package gr.auth.ee.lcs.data.updateAlgorithms;

import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.classifiers.Macroclassifier;
import gr.auth.ee.lcs.data.AbstractUpdateAlgorithmStrategy;
import gr.auth.ee.lcs.data.ClassifierTransformBridge;
import gr.auth.ee.lcs.geneticalgorithm.IGeneticAlgorithmStrategy;

import java.io.Serializable;

/**
 * An abstract *S-LCS update algorithm as described in Tzima-Mitkas paper.
 */
public abstract class AbstractSLCSUpdateAlgorithm extends
		AbstractUpdateAlgorithmStrategy {

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
		public double ns = 0;

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
	 * @param subsumptionFitness
	 *            the fitness threshold for subsumption
	 * @param subsumptionExperience
	 *            the experience threshold for subsumption
	 * @param gaMatchSetRunProbability
	 *            the probability of running the GA at the matchset
	 * @param geneticAlgorithm
	 *            the GA to use
	 */
	public AbstractSLCSUpdateAlgorithm(final double subsumptionFitness,
			final int subsumptionExperience,
			final double gaMatchSetRunProbability,
			final IGeneticAlgorithmStrategy geneticAlgorithm) {
		this.subsumptionFitnessThreshold = subsumptionFitness;
		this.subsumptionExperienceThreshold = subsumptionExperience;
		this.matchSetRunProbability = gaMatchSetRunProbability;
		this.ga = geneticAlgorithm;
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
	 * gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy#getData(gr.auth
	 * .ee.lcs.classifiers.Classifier)
	 */
	@Override
	public String getData(Classifier aClassifier) {
		SLCSClassifierData data = ((SLCSClassifierData) aClassifier
				.getUpdateDataObject());
		return "tp:" + data.tp + "msa:" + data.msa + "str: " + data.str + "ns:"
				+ data.ns;
	}

	@Override
	public void performUpdate(final ClassifierSet matchSet,
			final ClassifierSet correctSet) {
		final int matchSetSize = matchSet.getNumberOfMacroclassifiers();
		final int correctSetNumerosity = correctSet.getTotalNumerosity();
		for (int i = 0; i < matchSetSize; i++) {
			Classifier cl = matchSet.getClassifier(i);
			SLCSClassifierData data = ((SLCSClassifierData) cl
					.getUpdateDataObject());
			data.ns = (data.msa * data.ns + correctSetNumerosity)
					/ (data.msa + 1);
			data.msa++;

			updateFitness(cl, matchSet.getClassifierNumerosity(i), correctSet);
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

	/**
	 * The abstract function used to calculate the fitness of a classifier.
	 * 
	 * @param aClassifier
	 *            the classifier to calculate the fitness
	 * @param numerosity
	 *            the numerosity of the given classifier
	 * @param correctSet
	 *            the correct set, used at updating the fitness
	 */
	public abstract void updateFitness(Classifier aClassifier, int numerosity,
			ClassifierSet correctSet);

	@Override
	public final void updateSet(final ClassifierSet population,
			final ClassifierSet matchSet, final int instanceIndex,
			final boolean evolve) {

		ClassifierSet correctSet = generateCorrectSet(matchSet, instanceIndex);

		/*
		 * Cover if necessary
		 */
		if (correctSet.getNumberOfMacroclassifiers() == 0) {
			if (evolve)
				cover(population, instanceIndex);
			return;
		}

		performUpdate(matchSet, correctSet);

		if (!evolve)
			return;
		/*
		 * Run GA
		 */
		if (Math.random() < matchSetRunProbability)
			ga.evolveSet(matchSet, population);
		else
			ga.evolveSet(correctSet, population);

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