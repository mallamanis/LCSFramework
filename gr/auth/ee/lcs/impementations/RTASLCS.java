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
import gr.auth.ee.lcs.data.AbstractUpdateAlgorithmStrategy;
import gr.auth.ee.lcs.data.representations.UniLabelRepresentation;
import gr.auth.ee.lcs.data.representations.UniLabelRepresentation.ThresholdClassificationStrategy;
import gr.auth.ee.lcs.data.updateAlgorithms.ASLCSUpdateAlgorithm;
import gr.auth.ee.lcs.evaluators.AccuracyEvaluator;
import gr.auth.ee.lcs.evaluators.ExactMatchEvalutor;
import gr.auth.ee.lcs.evaluators.FileLogger;
import gr.auth.ee.lcs.evaluators.HammingLossEvaluator;
import gr.auth.ee.lcs.geneticalgorithm.IGeneticAlgorithmStrategy;
import gr.auth.ee.lcs.geneticalgorithm.algorithms.SteadyStateGeneticAlgorithm;
import gr.auth.ee.lcs.geneticalgorithm.operators.SinglePointCrossover;
import gr.auth.ee.lcs.geneticalgorithm.operators.UniformBitMutation;
import gr.auth.ee.lcs.geneticalgorithm.selectors.RouletteWheelSelector;
import gr.auth.ee.lcs.utilities.InstanceToDoubleConverter;

import java.io.IOException;

/**
 * An Rank-and-Threshold AS-LCS Update Algorithm
 * 
 * @author Miltos Allamanis
 * 
 */
public class RTASLCS {
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		final String file = "/home/miltiadis/Desktop/datasets/genbase2.arff";
		final int numOfLabels = 27;
		final int iterations = 200;
		final int populationSize = 6000;
		RTASLCS rtaslcs = new RTASLCS(file, iterations, populationSize,
				numOfLabels);
		rtaslcs.run();

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
	private final int THETA_GA = 900;

	/**
	 * The frequency at which callbacks will be called for evaluation.
	 */
	private final int CALLBACK_RATE = 200;

	/**
	 * The number of bits to use for representing continuous variables
	 */
	private final int PRECISION_BITS = 7;

	/**
	 * The ASLCS n power parameter.
	 */
	private final int ASLCS_N = 10;

	/**
	 * The accuracy threshold parameter.
	 */
	private final double ASLCS_ACC0 = .99;

	/**
	 * The ASLCS experience threshold.
	 */
	private final int ASLCS_EXPERIENCE_THRESHOLD = 20;

	/**
	 * The post-process experience threshold used.
	 */
	private final int POSTPROCESS_EXPERIENCE_THRESHOLD = 5;

	/**
	 * Coverage threshold for post processing.
	 */
	private final int POSTPROCESS_COVERAGE_THRESHOLD = 0;

	/**
	 * Post-process threshold for fitness;
	 */
	private final double POSTPROCESS_FITNESS_THRESHOLD = 0.0;

	/**
	 * The number of labels used at the dmlUCS.
	 */
	private final int numberOfLabels;

	/**
	 * Constructor.
	 * 
	 * @param filename
	 *            the filename of the ASLCS
	 * @param iterations
	 *            the number of iterations to run
	 * @param populationSize
	 *            the size of the population to use
	 * @param numOfLabels
	 *            the number of labels in the problem
	 */
	public RTASLCS(final String filename, final int iterations,
			final int populationSize, final int numOfLabels) {
		inputFile = filename;
		this.iterations = iterations;
		this.populationSize = populationSize;
		this.numberOfLabels = numOfLabels;
	}

	/**
	 * Runs the Direct-ML-UCS.
	 * 
	 * @throws IOException
	 */
	public void run() throws IOException {
		LCSTrainTemplate myExample = new LCSTrainTemplate(CALLBACK_RATE);
		IGeneticAlgorithmStrategy ga = new SteadyStateGeneticAlgorithm(
				new RouletteWheelSelector(
						AbstractUpdateAlgorithmStrategy.COMPARISON_MODE_EXPLORATION,
						true), new SinglePointCrossover(), CROSSOVER_RATE,
				new UniformBitMutation(MUTATION_RATE), THETA_GA);

		UniLabelRepresentation rep = new UniLabelRepresentation(inputFile,
				PRECISION_BITS, numberOfLabels, .7);
		ThresholdClassificationStrategy str = rep.new ThresholdClassificationStrategy();
		rep.setClassificationStrategy(str);
		ClassifierTransformBridge.setInstance(rep);

		AbstractUpdateAlgorithmStrategy.currentStrategy = new ASLCSUpdateAlgorithm(
				ASLCS_N, ASLCS_ACC0, ASLCS_EXPERIENCE_THRESHOLD, .01, ga);

		ClassifierSet rulePopulation = new ClassifierSet(
				new FixedSizeSetWorstFitnessDeletion(
						populationSize,
						new RouletteWheelSelector(
								AbstractUpdateAlgorithmStrategy.COMPARISON_MODE_DELETION,
								true)));

		ArffLoader loader = new ArffLoader();
		loader.loadInstances(inputFile, true);
		AccuracyEvaluator acc = new AccuracyEvaluator(loader.trainSet, true);
		final IEvaluator eval = new ExactMatchEvalutor(
				ClassifierTransformBridge.instances, true);
		myExample.registerHook(new FileLogger(inputFile + "_result.txt", eval));
		myExample.registerHook(acc);
		myExample.train(iterations, rulePopulation);

		// rulePopulation.print();
		System.out.println("Post process...");
		// rulePopulation.print();
		PostProcessPopulationControl postProcess = new PostProcessPopulationControl(
				POSTPROCESS_EXPERIENCE_THRESHOLD,
				POSTPROCESS_COVERAGE_THRESHOLD, POSTPROCESS_FITNESS_THRESHOLD,
				AbstractUpdateAlgorithmStrategy.COMPARISON_MODE_EXPLOITATION);
		SortPopulationControl sort = new SortPopulationControl(
				AbstractUpdateAlgorithmStrategy.COMPARISON_MODE_EXPLOITATION);
		postProcess.controlPopulation(rulePopulation);
		sort.controlPopulation(rulePopulation);
		eval.evaluateSet(rulePopulation);

		System.out.println("Evaluating on test set (pre-calibration)");
		ExactMatchEvalutor testEval = new ExactMatchEvalutor(loader.testSet,
				true);
		testEval.evaluateSet(rulePopulation);
		HammingLossEvaluator hamEval = new HammingLossEvaluator(loader.testSet,
				true, numberOfLabels);
		hamEval.evaluateSet(rulePopulation);
		AccuracyEvaluator accEval = new AccuracyEvaluator(loader.testSet, true);
		accEval.evaluateSet(rulePopulation);

		str.proportionalCutCalibration(ClassifierTransformBridge.instances,
				rulePopulation, (float) 1.252);
		// rulePopulation.print();
		// ClassifierSet.saveClassifierSet(rulePopulation, "set");

		eval.evaluateSet(rulePopulation);

		System.out.println("Evaluating on test set");

		testEval.evaluateSet(rulePopulation);

		hamEval.evaluateSet(rulePopulation);
		accEval.evaluateSet(rulePopulation);

		str.proportionalCutCalibration(
				InstanceToDoubleConverter.convert(loader.testSet),
				rulePopulation, (float) 1.252);

		System.out.println("Evaluating on test set (Pcut on test)");

		testEval.evaluateSet(rulePopulation);

		hamEval.evaluateSet(rulePopulation);
		accEval.evaluateSet(rulePopulation);

	}
}
