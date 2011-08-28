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
package gr.auth.ee.lcs.impementations;

import gr.auth.ee.lcs.AbstractLearningClassifierSystem;
import gr.auth.ee.lcs.FoldEvaluator;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.classifiers.populationcontrol.FixedSizeSetWorstFitnessDeletion;
import gr.auth.ee.lcs.data.AbstractUpdateStrategy;
import gr.auth.ee.lcs.data.representations.complex.SingleClassRepresentation;
import gr.auth.ee.lcs.data.updateAlgorithms.ASLCSUpdateAlgorithm;
import gr.auth.ee.lcs.evaluators.ExactMatchEvalutor;
import gr.auth.ee.lcs.geneticalgorithm.IGeneticAlgorithmStrategy;
import gr.auth.ee.lcs.geneticalgorithm.algorithms.SteadyStateGeneticAlgorithm;
import gr.auth.ee.lcs.geneticalgorithm.operators.SinglePointCrossover;
import gr.auth.ee.lcs.geneticalgorithm.operators.UniformBitMutation;
import gr.auth.ee.lcs.geneticalgorithm.selectors.RouletteWheelSelector;
import gr.auth.ee.lcs.utilities.SettingsLoader;

import java.io.IOException;

import weka.core.Instances;

/**
 * An AS-LCS implementation.
 * 
 * @author Miltiadis Allamanis
 * 
 */
public final class ASLCS extends AbstractLearningClassifierSystem {
	/**
	 * The main for running AS-LCS.
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		SettingsLoader.loadSettings();
		final String file = SettingsLoader.getStringSetting("filename", "");

		final ASLCS aslcs = new ASLCS();
		final FoldEvaluator loader = new FoldEvaluator(10, aslcs, file);
		loader.evaluate();
	}

	/**
	 * The input file used (.arff).
	 */
	private final String inputFile;

	/**
	 * The number of full iterations to train the AS-LCS.
	 */
	private final int iterations;

	/**
	 * The size of the population to use.
	 */
	private final int populationSize;

	/**
	 * The GA crossover rate.
	 */
	private final float CROSSOVER_RATE = (float) SettingsLoader
			.getNumericSetting("crossoverRate", .8);

	/**
	 * The GA mutation rate.
	 */
	private final double MUTATION_RATE = (float) SettingsLoader
			.getNumericSetting("mutationRate", .04);

	/**
	 * The GA activation rate.
	 */
	private final int THETA_GA = (int) SettingsLoader.getNumericSetting(
			"thetaGA", 100);

	/**
	 * The number of bits to use for representing continuous variables.
	 */
	private final int PRECISION_BITS = (int) SettingsLoader.getNumericSetting(
			"precisionBits", 5);

	/**
	 * The UCS n power parameter.
	 */
	private final int ASLCS_N = (int) SettingsLoader.getNumericSetting(
			"ASLCS_N", 10);

	/**
	 * The accuracy threshold parameter.
	 */
	private final double ASLCS_ACC0 = SettingsLoader.getNumericSetting(
			"ASLCS_Acc0", .99);

	/**
	 * The UCS experience threshold.
	 */
	private final int ASLCS_EXPERIENCE_THRESHOLD = (int) SettingsLoader
			.getNumericSetting("ASLCS_ExperienceTheshold", 10);

	/**
	 * The attribute generalization rate.
	 */
	private final double ATTRIBUTE_GENERALIZATION_RATE = SettingsLoader
			.getNumericSetting("AttributeGeneralizationRate", 0.33);

	/**
	 * The matchset GA run probability.
	 */
	private final double MATCHSET_GA_RUN_PROBABILITY = SettingsLoader
			.getNumericSetting("GAMatchSetRunProbability", 0.01);

	/**
	 * Percentage of only updates (and no exploration).
	 */
	private final double UPDATE_ONLY_ITERATION_PERCENTAGE = SettingsLoader
			.getNumericSetting("UpdateOnlyPercentage", .1);

	/**
	 * The problem representation.
	 */
	private final SingleClassRepresentation rep;

	/**
	 * The AS-LCS constructor.
	 * 
	 * @param filename
	 *            the filename to open
	 * @param iterations
	 *            the number of iterations to run the training
	 * @param populationSize
	 *            the population size to use
	 * @throws IOException
	 */
	public ASLCS() throws IOException {
		inputFile = SettingsLoader.getStringSetting("filename", "");
		iterations = (int) SettingsLoader.getNumericSetting("trainIterations",
				1000);
		populationSize = (int) SettingsLoader.getNumericSetting(
				"populationSize", 1500);

		final IGeneticAlgorithmStrategy ga = new SteadyStateGeneticAlgorithm(
				new RouletteWheelSelector(
						AbstractUpdateStrategy.COMPARISON_MODE_EXPLORATION,
						true), new SinglePointCrossover(this), CROSSOVER_RATE,
				new UniformBitMutation(MUTATION_RATE), THETA_GA, this);

		rep = new SingleClassRepresentation(inputFile, PRECISION_BITS,
				ATTRIBUTE_GENERALIZATION_RATE, this);
		rep.setClassificationStrategy(rep.new VotingClassificationStrategy());

		final ASLCSUpdateAlgorithm strategy = new ASLCSUpdateAlgorithm(ASLCS_N,
				ASLCS_ACC0, ASLCS_EXPERIENCE_THRESHOLD,
				MATCHSET_GA_RUN_PROBABILITY, ga, this);

		this.setElements(rep, strategy);

		rulePopulation = new ClassifierSet(
				new FixedSizeSetWorstFitnessDeletion(
						populationSize,
						new RouletteWheelSelector(
								AbstractUpdateStrategy.COMPARISON_MODE_DELETION,
								true)));
	}

	@Override
	public int[] classifyInstance(double[] instance) {
		return getClassifierTransformBridge().classify(
				this.getRulePopulation(), instance);
	}

	@Override
	public AbstractLearningClassifierSystem createNew() {
		try {
			return new ASLCS();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String[] getEvaluationNames() {
		final String[] names = { "Accuracy" };
		return names;
	}

	@Override
	public double[] getEvaluations(Instances testSet) {
		final double[] result = new double[1];
		final ExactMatchEvalutor testEval = new ExactMatchEvalutor(testSet,
				true, this);
		result[0] = testEval.evaluateLCS(this);
		return result;
	}

	/**
	 * Run the AS-LCS.
	 * 
	 * @throws IOException
	 *             if file not found
	 */
	@Override
	public void train() {

		trainSet(iterations, rulePopulation);

		updatePopulation((int) (iterations * UPDATE_ONLY_ITERATION_PERCENTAGE),
				rulePopulation);

	}
}
