/*
 *	Copyright (C) 2011 by Allamanis Miltiadis
 *
 *	Permission is hereby granted, free of charge, to any person obtaining a copy
 *	of this software and associated documentation files (the "Software"), to deal
 *	in the Software without restriction, including without limitation the rights
 *	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *	copies of the Software, and to permit persons to whom the Software is
 *	furnished to do so, subject to the following conditions:
 *
 *	The above copyright notice and this permission notice shall be included in
 *	all copies or substantial portions of the Software.
 *
 *	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *	THE SOFTWARE.
 */
/**
 * 
 */
package gr.auth.ee.lcs.data.updateAlgorithms;

import gr.auth.ee.lcs.AbstractLearningClassifierSystem;
import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.classifiers.Macroclassifier;
import gr.auth.ee.lcs.data.AbstractUpdateStrategy;
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
public final class MlASLCSUpdateAlgorithm extends AbstractUpdateStrategy {

	/**
	 * A data object for the *SLCS update algorithms.
	 * 
	 * @author Miltos Allamanis
	 * 
	 */
	final class SLCSClassifierData implements Serializable {

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

	/**
	 * The number of labels used at the problem.
	 */
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
	 * The LCS instance being used.
	 */
	private final AbstractLearningClassifierSystem myLcs;

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
	 * @param lcs
	 *            the LCS instance used
	 */
	public MlASLCSUpdateAlgorithm(final double nParameter,
			final double fitnessThreshold, final int experienceThreshold,
			double gaMatchSetRunProbability,
			IGeneticAlgorithmStrategy geneticAlgorithm, int labels,
			AbstractLearningClassifierSystem lcs) {
		this.subsumptionFitnessThreshold = fitnessThreshold;
		this.subsumptionExperienceThreshold = experienceThreshold;
		this.matchSetRunProbability = gaMatchSetRunProbability;
		this.ga = geneticAlgorithm;
		this.n = nParameter;
		numOfLabels = labels;
		myLcs = lcs;
	}

	/**
	 * Calculates the label niches.
	 * 
	 * @param correctSet
	 *            the correct set
	 * @param instanceIndex
	 *            the index instance
	 * @return the label niche set size per label
	 */
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
	 * Calls covering operator.
	 * 
	 * @param instanceIndex
	 *            the index of the current sample
	 */
	@Override
	public void cover(final ClassifierSet population, final int instanceIndex) {
		final Classifier coveringClassifier = myLcs
				.getClassifierTransformBridge().createRandomCoveringClassifier(
						myLcs.instances[instanceIndex]);
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
		return new SLCSClassifierData();
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
		final ClassifierSet correctSet = new ClassifierSet(null);
		final int matchSetSize = matchSet.getNumberOfMacroclassifiers();
		for (int i = 0; i < matchSetSize; i++) {
			Macroclassifier cl = matchSet.getMacroclassifier(i);
			if (cl.myClassifier.classifyCorrectly(instanceIndex) == 1)
				correctSet.addClassifier(cl, false);
		}
		return correctSet;
	}

	/**
	 * Returns classifier's niche size per label.
	 * 
	 * @param aClassifier
	 *            the classifier to return
	 * @param instanceIndex
	 *            the index of the instance to b
	 * @param niches
	 *            the
	 * @return
	 */
	private double getClassifierNicheSize(final Classifier aClassifier,
			final int instanceIndex, final int[] niches) {
		// TODO: Does this even make sense?
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
		final SLCSClassifierData data = (SLCSClassifierData) aClassifier
				.getUpdateDataObject();
		switch (mode) {
		case COMPARISON_MODE_EXPLORATION:
			return data.fitness * ((aClassifier.experience < 5) ? 0 : 1);
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
		default:
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
	public String getData(final Classifier aClassifier) {
		final SLCSClassifierData data = ((SLCSClassifierData) aClassifier
				.getUpdateDataObject());
		return "tp:" + data.tp + "msa:" + data.msa + "str: " + data.str + "ns:"
				+ data.ns;
	}

	@Override
	public void performUpdate(final ClassifierSet matchSet,
			final ClassifierSet correctSet) {
		return; // Not used!
	}

	/**
	 * Perform an update on the given sets.
	 * 
	 * @param matchSet
	 *            the match set [M]
	 * @param correctSet
	 *            the correct set
	 * @param instanceIndex
	 *            the instance index
	 */
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
	public void setComparisonValue(final Classifier aClassifier,
			final int mode, final double comparisonValue) {
		final SLCSClassifierData data = ((SLCSClassifierData) aClassifier
				.getUpdateDataObject());
		data.fitness = comparisonValue; // TODO: More generic

	}

	@Override
	public void updateSet(final ClassifierSet population,
			final ClassifierSet matchSet, final int instanceIndex,
			final boolean evolve) {

		final ClassifierSet correctSet = generateCorrectSet(matchSet,
				instanceIndex);

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

	/**
	 * Implementation of the subsumption strength.
	 * 
	 * @param aClassifier
	 *            the classifier, whose subsumption ability is to be updated
	 */
	protected void updateSubsumption(final Classifier aClassifier) {
		aClassifier
				.setSubsumptionAbility((aClassifier
						.getComparisonValue(COMPARISON_MODE_EXPLOITATION) > subsumptionFitnessThreshold)
						&& (aClassifier.experience > subsumptionExperienceThreshold));
	}

}
