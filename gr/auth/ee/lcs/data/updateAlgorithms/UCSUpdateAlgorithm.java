/**
 * 
 */
package gr.auth.ee.lcs.data.updateAlgorithms;

import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.classifiers.Macroclassifier;
import gr.auth.ee.lcs.data.ClassifierTransformBridge;
import gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy;
import gr.auth.ee.lcs.geneticalgorithm.IGeneticAlgorithmStrategy;

import java.io.Serializable;

/**
 * A UCS implementation with fitness sharing.
 * 
 * @author Miltos Allamanis
 */
public class UCSUpdateAlgorithm extends UpdateAlgorithmFactoryAndStrategy {

	/**
	 * A data object for the UCS update algorithm.
	 * 
	 * @author Miltos Allamanis
	 * 
	 */
	class UCSClassifierData implements Serializable {

		/**
		 * Serial code for serialization.
		 */
		private static final long serialVersionUID = 3098073593334379507L;

		/**
		 *
		 */
		private double fitness = .5;

		/**
		 * niche set size estimation.
		 */
		private double cs = 1;

		/**
		 * 
		 */
		private double msAvgFitness;

		/**
		 * Match Set Appearances.
		 */
		private int msa = 0;

		/**
		 * true positives.
		 */
		private int tp = 0;

		/**
		 * false positives.
		 */
		private int fp = 0;

		/**
		 * Strength.
		 */
		private double fitness0 = 0;

	}

	/**
	 * Genetic Algorithm.
	 */
	public IGeneticAlgorithmStrategy ga;

	/**
	 * Private variables: the UCS parameter sharing. accuracy0 is considered the
	 * subsumption fitness threshold
	 */
	private final double a, accuracy0, n, b;

	/**
	 * A double indicating the probability that the GA will run on the matchSet
	 * (and not on the correct set).
	 */
	private final double matchSetRunProbability;

	/**
	 * The experience threshold for subsumption.
	 */
	private final int subsumptionExperienceThreshold;

	/**
	 * Default constructor.
	 * 
	 * @param alpha
	 *            used in fitness sharing
	 * @param nParameter
	 *            used in fitness sharing
	 * @param acc0
	 *            used in fitness sharing: the minimum "good" accuracy
	 * @param learningRate
	 *            the beta of UCS
	 * @param experienceThreshold
	 *            the experience threshold for subsumption
	 * @param gaMatchSetRunProbability
	 *            the probability of running the GA at the matchset
	 * @param geneticAlgorithm
	 *            the genetic algorithm to be used for evolving
	 */
	public UCSUpdateAlgorithm(final double alpha, final double nParameter,
			final double acc0, final double learningRate,
			final int experienceThreshold, double gaMatchSetRunProbability,
			IGeneticAlgorithmStrategy geneticAlgorithm) {
		this.a = alpha;
		this.n = nParameter;
		this.accuracy0 = acc0;
		this.b = learningRate;
		subsumptionExperienceThreshold = experienceThreshold;
		this.matchSetRunProbability = gaMatchSetRunProbability;
		this.ga = geneticAlgorithm;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy#getComparisonValue
	 * (gr.auth.ee.lcs.classifiers.Classifier, int)
	 */
	@Override
	public final double getComparisonValue(final Classifier aClassifier,
			final int mode) {
		UCSClassifierData data = (UCSClassifierData) aClassifier
				.getUpdateDataObject();

		switch (mode) {
		case COMPARISON_MODE_EXPLORATION:
			final double value = data.fitness
					* (aClassifier.experience < 10 ? 0 : 1);
			return Double.isNaN(value) ? 0 : value;
		case COMPARISON_MODE_DELETION:

			if ((aClassifier.experience < 15)) {
				final double result = data.cs / data.fitness;
				return Double.isNaN(result) ? 1 : result;
			}

			return data.cs;

		case COMPARISON_MODE_EXPLOITATION:
			final double acc = (((double) (data.tp)) / (double) (data.msa));
			final double exploitValue = acc
					* (aClassifier.experience < 10 ? 0 : 1);
			return Double.isNaN(exploitValue) ? 0 : exploitValue;
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
		UCSClassifierData data = ((UCSClassifierData) aClassifier
				.getUpdateDataObject());
		return "tp:" + data.tp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy#setComparisonValue
	 * (gr.auth.ee.lcs.classifiers.Classifier, int, double)
	 */
	@Override
	public final void setComparisonValue(final Classifier aClassifier,
			final int mode, final double comparisonValue) {
		UCSClassifierData data = ((UCSClassifierData) aClassifier
				.getUpdateDataObject());
		data.fitness = comparisonValue;
	}

	/**
	 * Calls covering operator.
	 * 
	 * @param instanceIndex
	 *            the index of the current sample
	 */
	private void cover(final ClassifierSet population, final int instanceIndex) {
		Classifier coveringClassifier = ClassifierTransformBridge.getInstance()
				.createRandomCoveringClassifier(
						ClassifierTransformBridge.instances[instanceIndex]);
		population.addClassifier(new Macroclassifier(coveringClassifier, 1),
				false);
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
	 * Perform an update to the set.
	 * 
	 * @param matchSet
	 * @param correctSet
	 */
	private void performUpdate(final ClassifierSet matchSet,
			final ClassifierSet correctSet) {
		double strengthSum = 0;
		final int matchSetMacroclassifiers = matchSet
				.getNumberOfMacroclassifiers();
		final int correctSetSize = correctSet.getTotalNumerosity();
		for (int i = 0; i < matchSetMacroclassifiers; i++) {
			Classifier cl = matchSet.getClassifier(i);
			UCSClassifierData data = ((UCSClassifierData) cl
					.getUpdateDataObject());
			cl.experience++;
			data.msa += 1;
			data.cs = data.cs + 0.1 * (correctSetSize - data.cs);
			if (correctSet.getClassifierNumerosity(cl) > 0) {
				data.tp += 1;
				final double accuracy = ((double) data.tp)
						/ ((double) data.msa);
				if (accuracy > accuracy0) {
					data.fitness0 = 1;

					// Check subsumption
					if (cl.experience >= this.subsumptionExperienceThreshold)
						cl.setSubsumptionAbility(true);

				} else {
					data.fitness0 = a * Math.pow(accuracy / accuracy0, n);
					cl.setSubsumptionAbility(false);
				}

				strengthSum += data.fitness0
						* matchSet.getClassifierNumerosity(i);
			} else {
				data.fp += 1;
				data.fitness0 = 0;
			}

		}

		// Fix for avoiding problems...
		if (strengthSum == 0)
			strengthSum = 1;

		// double fitnessSum = 0;
		final int msSize = matchSet.getNumberOfMacroclassifiers();
		for (int i = 0; i < msSize; i++) {
			Classifier cl = matchSet.getClassifier(i);
			UCSClassifierData data = ((UCSClassifierData) cl
					.getUpdateDataObject());
			data.fitness += b * (data.fitness0 / strengthSum - data.fitness);// TODO:
																				// Something
																				// else?
			// fitnessSum += data.fitness * matchSet.getClassifierNumerosity(i);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy#
	 * createStateClassifierObject()
	 */
	@Override
	protected final Serializable createStateClassifierObject() {
		return new UCSClassifierData();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy#updateSet(gr.auth
	 * .ee.lcs.classifiers.ClassifierSet,
	 * gr.auth.ee.lcs.classifiers.ClassifierSet) Updates the set setA is the
	 * match set setB is the correct set
	 */
	@Override
	protected final void updateSet(final ClassifierSet population,
			final ClassifierSet matchSet, final int instanceIndex) {
		/*
		 * Generate correct set
		 */
		ClassifierSet correctSet = generateCorrectSet(matchSet, instanceIndex);

		/*
		 * Cover if necessary
		 */
		if (correctSet.getNumberOfMacroclassifiers() == 0) {
			cover(population, instanceIndex);
			return;
		}

		/*
		 * Update
		 */
		performUpdate(matchSet, correctSet);

		/*
		 * Run GA
		 */
		if (Math.random() < matchSetRunProbability)
			ga.evolveSet(matchSet, population);
		else
			ga.evolveSet(correctSet, population);

	}

}
