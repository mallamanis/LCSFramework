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
 * A strength based multi-label update algorithm.
 * 
 * @author Miltos Allamanis
 * 
 */
public final class MlSSLCSUpdateAlgorithm extends AbstractUpdateStrategy {

	/**
	 * The Ml-SS-LCS classifier data object.
	 * 
	 * @author Miltos Allamanis
	 * 
	 */
	public final class MlSSLCSClassifierData implements Serializable {

		/**
		 * The serial for serialization.
		 */
		private static final long serialVersionUID = 2060323742317299358L;

		/**
		 * Classifier fitness.
		 */
		public double fitness = Double.NEGATIVE_INFINITY;

		/**
		 * niche set size estimation.
		 */
		public double[] ns;

		/**
		 * Match Set Appearances.
		 */
		public int msa = 1;

		/**
		 * true positives.
		 */
		public int tp = 1;

		/**
		 * false positives.
		 */
		public int fp = 0;

		/**
		 * Strength.
		 */
		public double str = 0;

		/**
		 * Number of active labels.
		 */
		public int activeLabels = -1;

		/**
		 * Constructor.
		 */
		public MlSSLCSClassifierData() {
			ns = new double[numberOfLabels];
			Arrays.fill(ns, 1);
		}

	}

	/**
	 * The number of labels used.
	 */
	private final int numberOfLabels;

	/**
	 * The subsumption experience threshold.
	 */
	private final int subsumptionExperienceThreshold;

	/**
	 * The subsumption accuracy threshold.
	 */
	private final double subsumptionAccuracyThreshold;

	/**
	 * Genetic Algorithm.
	 */
	private final IGeneticAlgorithmStrategy ga;

	/**
	 * Reward and penalty percentage parameters.
	 */
	private final double strengthReward, penalty;

	/**
	 * The LCS instance used by the update algorithm.
	 */
	private final AbstractLearningClassifierSystem myLcs;

	/**
	 * Constructor.
	 * 
	 * @param reward
	 *            the reward to be used
	 * @param penaltyPercent
	 *            the percent of penalty to incur on wrong classifiers
	 * @param labels
	 *            the number of labels in the problem
	 * @param geneticAlgorithm
	 *            the GA to be used for evolving
	 * @param subsumptionExperience
	 *            the subsumption experience
	 * @param subsumptionAccuracy
	 *            the subsumtion minimum accuracy
	 * @param lcs
	 *            the LCS instance used
	 */
	public MlSSLCSUpdateAlgorithm(final double reward,
			final double penaltyPercent, final int labels,
			final IGeneticAlgorithmStrategy geneticAlgorithm,
			final int subsumptionExperience, final double subsumptionAccuracy,
			final AbstractLearningClassifierSystem lcs) {
		numberOfLabels = labels;
		strengthReward = reward;
		ga = geneticAlgorithm;
		penalty = penaltyPercent;
		subsumptionExperienceThreshold = subsumptionExperience;
		subsumptionAccuracyThreshold = subsumptionAccuracy;
		myLcs = lcs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy#cover(gr.auth.ee
	 * .lcs.classifiers.ClassifierSet, int)
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
		return new MlSSLCSClassifierData();
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
		final MlSSLCSClassifierData data = (MlSSLCSClassifierData) aClassifier
				.getUpdateDataObject();

		switch (mode) {
		case COMPARISON_MODE_DELETION:
			// TODO: Something else?
			final double prob = 1 / (data.fitness * data.activeLabels / numberOfLabels);
			if (data.activeLabels == 0)
				return 1000;
			return prob;
		case COMPARISON_MODE_EXPLOITATION:
			return ((double) data.tp) / ((double) (data.tp + data.fp));// data.str;
																		// //
																		// TODO:
																		// Or
																		// maybe
																		// tp/(tp+fp)?
		case COMPARISON_MODE_EXPLORATION:
			return (aClassifier.experience < 10) ? 0 : (data.str);
		default:
			return 0;
		}
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
		final MlSSLCSClassifierData data = ((MlSSLCSClassifierData) aClassifier
				.getUpdateDataObject());
		return "tp:" + data.tp + "msa:" + data.msa + " str:" + data.str;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy#performUpdate(gr
	 * .auth.ee.lcs.classifiers.ClassifierSet,
	 * gr.auth.ee.lcs.classifiers.ClassifierSet)
	 */
	@Override
	public void performUpdate(final ClassifierSet matchSet,
			final ClassifierSet correctSet) {
		return;
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
		final MlSSLCSClassifierData data = ((MlSSLCSClassifierData) aClassifier
				.getUpdateDataObject());
		data.fitness = comparisonValue;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy#updateSet(gr.auth
	 * .ee.lcs.classifiers.ClassifierSet,
	 * gr.auth.ee.lcs.classifiers.ClassifierSet, int)
	 */
	@Override
	public void updateSet(final ClassifierSet population,
			final ClassifierSet matchSet, final int instanceIndex,
			final boolean evolve) {
		final int matchSetSize = matchSet.getNumberOfMacroclassifiers();

		for (int lbl = 0; lbl < numberOfLabels; lbl++) {
			int totalCorrectRules = 0;

			for (int i = 0; i < matchSetSize; i++) {
				if (matchSet.getClassifier(i).classifyLabelCorrectly(
						instanceIndex, lbl) == 1)
					totalCorrectRules += matchSet.getClassifierNumerosity(i);
			}

			for (int i = 0; i < matchSetSize; i++) {
				final Classifier currentClassifier = matchSet.getClassifier(i);
				final MlSSLCSClassifierData data = ((MlSSLCSClassifierData) currentClassifier
						.getUpdateDataObject());
				final float classificationAbility = currentClassifier
						.classifyLabelCorrectly(instanceIndex, lbl);

				if (classificationAbility > 0 && totalCorrectRules > 0) {
					data.str += (strengthReward) / (totalCorrectRules);
					if (Double.isInfinite(data.str))
						data.str = 10;
					data.tp += 1;
					data.msa += 1;
					data.ns[lbl] += .1 * (data.ns[lbl] - totalCorrectRules);
				} else if (classificationAbility < 0) {
					data.str -= penalty * (strengthReward) / (data.ns[lbl]);
					data.fp += 1;
					data.msa += 1;
				}
			}

			if ((totalCorrectRules == 0) && evolve) {
				this.cover(population, instanceIndex);
			}
		}

		for (int i = 0; i < matchSetSize; i++) {
			final Classifier currentClassifier = matchSet.getClassifier(i);
			final MlSSLCSClassifierData data = ((MlSSLCSClassifierData) currentClassifier
					.getUpdateDataObject());
			data.activeLabels = 0;
			for (int lbl = 0; lbl < numberOfLabels; lbl++) {
				final float classificationAbility = currentClassifier
						.classifyLabelCorrectly(instanceIndex, lbl);
				if (classificationAbility != 0)
					data.activeLabels += 1;
			}

			if (data.msa > 0) {
				currentClassifier.experience++;
			}

			data.fitness = (data.str < 0 || data.activeLabels == 0) ? 0
					: (data.str / ((double) data.msa));
			if ((((double) data.tp) / ((double) (data.tp + data.fp)) > subsumptionAccuracyThreshold)
					&& (currentClassifier.experience > subsumptionExperienceThreshold))
				currentClassifier.setSubsumptionAbility(true);
			else
				currentClassifier.setSubsumptionAbility(false);
		}

		if ((matchSetSize > 0) && evolve)
			ga.evolveSet(matchSet, population);

		// Delete classifiers that are only #'s

		for (int i = (matchSetSize - 1); i >= 0; i--) {
			final Classifier cl = matchSet.getClassifier(i);
			if (cl.experience == 0)
				population.deleteClassifier(cl);
		}

	}

}
