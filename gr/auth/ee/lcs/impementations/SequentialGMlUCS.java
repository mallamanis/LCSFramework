/**
 * 
 */
package gr.auth.ee.lcs.impementations;

import gr.auth.ee.lcs.ArffLoader;
import gr.auth.ee.lcs.LCSTrainTemplate;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.classifiers.populationcontrol.FixedSizeSetWorstFitnessDeletion;
import gr.auth.ee.lcs.classifiers.populationcontrol.PostProcessPopulationControl;
import gr.auth.ee.lcs.classifiers.populationcontrol.SortPopulationControl;
import gr.auth.ee.lcs.data.ClassifierTransformBridge;
import gr.auth.ee.lcs.data.IEvaluator;
import gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy;
import gr.auth.ee.lcs.data.representations.GenericMultiLabelRepresentation;
import gr.auth.ee.lcs.data.updateAlgorithms.SequentialMlUpdateAlgorithm;
import gr.auth.ee.lcs.data.updateAlgorithms.UCSUpdateAlgorithm;
import gr.auth.ee.lcs.evaluators.AccuracyEvaluator;
import gr.auth.ee.lcs.evaluators.ExactMatchEvalutor;
import gr.auth.ee.lcs.evaluators.ExactMatchSelfEvaluator;
import gr.auth.ee.lcs.evaluators.FileLogger;
import gr.auth.ee.lcs.evaluators.HammingLossEvaluator;
import gr.auth.ee.lcs.geneticalgorithm.IGeneticAlgorithmStrategy;
import gr.auth.ee.lcs.geneticalgorithm.algorithms.SteadyStateGeneticAlgorithm;
import gr.auth.ee.lcs.geneticalgorithm.operators.SinglePointCrossover;
import gr.auth.ee.lcs.geneticalgorithm.operators.UniformBitMutation;
import gr.auth.ee.lcs.geneticalgorithm.selectors.RouletteWheelSelector;

import java.io.IOException;

/**
 * An implementation of a multi-label UCS, using the generic Multi-label
 * representation and a per-label (sequential) update UCS.
 * 
 * @author Miltos Allamanis
 * 
 */
public class SequentialGMlUCS {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		final String file = "/home/miltiadis/Desktop/datasets/mlTestbeds/mlposition7.arff";
		final int numOfLabels = 7;
		final int iterations = 300;
		final int populationSize = 1000;
		SequentialGMlUCS sgmlucs = new SequentialGMlUCS(file, iterations,
				populationSize, numOfLabels, .03);
		sgmlucs.run();

	}

	/**
	 * The input file used (.arff).
	 */
	private final String inputFile;

	/**
	 * The number of full iterations to train the UCS.
	 */
	private final int iterations;

	/**
	 * The size of the population to use.
	 */
	private final int populationSize;

	/**
	 * The GA crossover rate.
	 */
	private final float CROSSOVER_RATE = (float) 0.8;

	/**
	 * The GA mutation rate.
	 */
	private final double MUTATION_RATE = (float) .04;

	/**
	 * The GA activation rate.
	 */
	private final int THETA_GA = 100;

	/**
	 * The frequency at which callbacks will be called for evaluation.
	 */
	private final int CALLBACK_RATE = 10;

	/**
	 * The number of bits to use for representing continuous variables
	 */
	private final int PRECISION_BITS = 7;

	/**
	 * The UCS alpha parameter.
	 */
	private final double UCS_ALPHA = .1;

	/**
	 * The UCS n power parameter.
	 */
	private final int UCS_N = 10;

	/**
	 * The accuracy threshold parameter.
	 */
	private final double UCS_ACC0 = .99;

	/**
	 * The learning rate (beta) parameter.
	 */
	private final double UCS_LEARNING_RATE = .1;

	/**
	 * The UCS experience threshold.
	 */
	private final int UCS_EXPERIENCE_THRESHOLD = 50;

	/**
	 * The post-process experince threshold used.
	 */
	private final int POSTPROCESS_EXPERIENCE_THRESHOLD = 10;

	/**
	 * Coverage threshold for post processing.
	 */
	private final int POSTPROCESS_COVERAGE_THRESHOLD = 0;

	/**
	 * Post-process threshold for fitness;
	 */
	private final double POSTPROCESS_FITNESS_THRESHOLD = .5;

	/**
	 * The generalization rate used for labels.
	 */
	private final double labelGeneralizationRate;

	/**
	 * The number of labels used at the dmlUCS.
	 */
	private final int numberOfLabels;

	/**
	 * Constructor.
	 * 
	 * @param filename
	 *            the filename of the trainset
	 * @param iterations
	 *            the number of iterations to run
	 * @param populationSize
	 *            the size of the population to use
	 * @param numOfLabels
	 *            the number of labels in the problem
	 * @param labelGeneralizationProbability
	 *            the probability of generalizing a label (during coverage)
	 */
	public SequentialGMlUCS(final String filename, final int iterations,
			final int populationSize, final int numOfLabels,
			final double labelGeneralizationProbability) {
		inputFile = filename;
		this.iterations = iterations;
		this.populationSize = populationSize;
		this.numberOfLabels = numOfLabels;
		this.labelGeneralizationRate = labelGeneralizationProbability;
	}

	public void run() throws IOException {
		LCSTrainTemplate myExample = new LCSTrainTemplate(CALLBACK_RATE);
		IGeneticAlgorithmStrategy ga = new SteadyStateGeneticAlgorithm(
				new RouletteWheelSelector(
						UpdateAlgorithmFactoryAndStrategy.COMPARISON_MODE_EXPLORATION,
						true), new SinglePointCrossover(), CROSSOVER_RATE,
				new UniformBitMutation(MUTATION_RATE), THETA_GA);

		GenericMultiLabelRepresentation rep = new GenericMultiLabelRepresentation(
				inputFile, PRECISION_BITS, numberOfLabels,
				GenericMultiLabelRepresentation.EXACT_MATCH,
				labelGeneralizationRate);
		rep.setClassificationStrategy(rep.new VotingClassificationStrategy());
		ClassifierTransformBridge.setInstance(rep);

		UCSUpdateAlgorithm updateObj = new UCSUpdateAlgorithm(UCS_ALPHA, UCS_N,
				UCS_ACC0, UCS_LEARNING_RATE, UCS_EXPERIENCE_THRESHOLD, 0.01,
				ga, THETA_GA, 1);
		UpdateAlgorithmFactoryAndStrategy.currentStrategy = new SequentialMlUpdateAlgorithm(
				updateObj, ga, numberOfLabels);

		ClassifierSet rulePopulation = new ClassifierSet(
				new FixedSizeSetWorstFitnessDeletion(
						populationSize,
						new RouletteWheelSelector(
								UpdateAlgorithmFactoryAndStrategy.COMPARISON_MODE_DELETION,
								true)));

		ArffLoader loader = new ArffLoader();
		loader.loadInstances(inputFile, true);
		final IEvaluator eval = new ExactMatchSelfEvaluator(true, true);
		myExample.registerHook(new FileLogger(inputFile + "_resultSGMlUCS.txt",
				eval));
		myExample.train(iterations, rulePopulation);

		for (int i = 0; i < rulePopulation.getNumberOfMacroclassifiers(); i++) {
			System.out
					.println(rulePopulation.getClassifier(i).toString()
							+ " fit:"
							+ rulePopulation
									.getClassifier(i)
									.getComparisonValue(
											UpdateAlgorithmFactoryAndStrategy.COMPARISON_MODE_EXPLOITATION)
							+ " exp:"
							+ rulePopulation.getClassifier(i).experience
							+ " num:"
							+ rulePopulation.getClassifierNumerosity(i));
		}
		System.out.println("Post process...");
		PostProcessPopulationControl postProcess = new PostProcessPopulationControl(
				POSTPROCESS_EXPERIENCE_THRESHOLD,
				POSTPROCESS_COVERAGE_THRESHOLD, POSTPROCESS_FITNESS_THRESHOLD,
				UpdateAlgorithmFactoryAndStrategy.COMPARISON_MODE_EXPLOITATION);
		SortPopulationControl sort = new SortPopulationControl(
				UpdateAlgorithmFactoryAndStrategy.COMPARISON_MODE_EXPLOITATION);
		postProcess.controlPopulation(rulePopulation);
		sort.controlPopulation(rulePopulation);
		for (int i = 0; i < rulePopulation.getNumberOfMacroclassifiers(); i++) {
			System.out
					.println(rulePopulation.getClassifier(i).toString()
							+ " fit:"
							+ rulePopulation
									.getClassifier(i)
									.getComparisonValue(
											UpdateAlgorithmFactoryAndStrategy.COMPARISON_MODE_EXPLOITATION)
							+ " exp:"
							+ rulePopulation.getClassifier(i).experience
							+ " num:"
							+ rulePopulation.getClassifierNumerosity(i)
							+ "cov:"
							+ rulePopulation.getClassifier(i).getCoverage());
			System.out
					.println(UpdateAlgorithmFactoryAndStrategy.currentStrategy
							.getData((rulePopulation.getClassifier(i))));
		}
		// ClassifierSet.saveClassifierSet(rulePopulation, "set");

		eval.evaluateSet(rulePopulation);

		System.out.println("Evaluating on test set");
		ExactMatchEvalutor testEval = new ExactMatchEvalutor(loader.testSet,
				true);
		testEval.evaluateSet(rulePopulation);
		HammingLossEvaluator hamEval = new HammingLossEvaluator(loader.testSet,
				true, numberOfLabels);
		hamEval.evaluateSet(rulePopulation);
		AccuracyEvaluator accEval = new AccuracyEvaluator(loader.testSet, true);
		accEval.evaluateSet(rulePopulation);

	}

}
