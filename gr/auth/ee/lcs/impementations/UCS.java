/**
 * 
 */
package gr.auth.ee.lcs.impementations;

import gr.auth.ee.lcs.ArffLoader;
import gr.auth.ee.lcs.LCSTrainTemplate;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.classifiers.FixedSizeSetWorstFitnessDeletion;
import gr.auth.ee.lcs.classifiers.PostProcessPopulationControl;
import gr.auth.ee.lcs.classifiers.SortPopulationControl;
import gr.auth.ee.lcs.data.ClassifierTransformBridge;
import gr.auth.ee.lcs.data.IEvaluator;
import gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy;
import gr.auth.ee.lcs.data.representations.SingleClassRepresentation;
import gr.auth.ee.lcs.data.updateAlgorithms.UCSUpdateAlgorithm;
import gr.auth.ee.lcs.evaluators.ExactMatchEvalutor;
import gr.auth.ee.lcs.evaluators.ExactMatchSelfEvaluator;
import gr.auth.ee.lcs.evaluators.ConfusionMatrixEvaluator;
import gr.auth.ee.lcs.evaluators.FileLogger;
import gr.auth.ee.lcs.geneticalgorithm.IGeneticAlgorithmStrategy;
import gr.auth.ee.lcs.geneticalgorithm.algorithms.SteadyStateGeneticAlgorithm;
import gr.auth.ee.lcs.geneticalgorithm.operators.SinglePointCrossover;
import gr.auth.ee.lcs.geneticalgorithm.operators.UniformBitMutation;
import gr.auth.ee.lcs.geneticalgorithm.selectors.RouletteWheelSelector;

import java.io.IOException;

/**
 * A UCS Learning Classifier System.
 * 
 * @author Miltos Allamanis
 * 
 */
public class UCS {

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
	 * The UCS constructor.
	 * 
	 * @param filename
	 *            the filename to open
	 * @param iterations
	 *            the number of iterations to run the training
	 * @param populationSize
	 *            the population size to use
	 */
	public UCS(String filename, int iterations, int populationSize) {
		inputFile = filename;
		this.iterations = iterations;
		this.populationSize = populationSize;
	}

	public static void main(String[] args) throws IOException {
		final String file = "/home/miltiadis/Desktop/datasets/car.arff";
		final int iterations = 500;
		final int populationSize = 2000;
		UCS ucs = new UCS(file, iterations, populationSize);
		ucs.run();
	}

	/**
	 * Run the UCS.
	 * 
	 * @throws IOException
	 *             if file not found
	 */
	public void run() throws IOException {
		LCSTrainTemplate myExample = new LCSTrainTemplate(CALLBACK_RATE);
		IGeneticAlgorithmStrategy ga = new SteadyStateGeneticAlgorithm(
				new RouletteWheelSelector(
						UpdateAlgorithmFactoryAndStrategy.COMPARISON_MODE_EXPLORATION,
						true), new SinglePointCrossover(), CROSSOVER_RATE,
				new UniformBitMutation(MUTATION_RATE), THETA_GA);

		SingleClassRepresentation rep = new SingleClassRepresentation(inputFile,
				PRECISION_BITS);
		rep.setClassificationStrategy(rep.new VotingClassificationStrategy());
		ClassifierTransformBridge.setInstance(rep);

		UpdateAlgorithmFactoryAndStrategy.currentStrategy = new UCSUpdateAlgorithm(
				UCS_ALPHA, UCS_N, UCS_ACC0, UCS_LEARNING_RATE,
				UCS_EXPERIENCE_THRESHOLD, 0.01, ga, THETA_GA, 1);

		ClassifierSet rulePopulation = new ClassifierSet(
				new FixedSizeSetWorstFitnessDeletion(
						populationSize,
						new RouletteWheelSelector(
								UpdateAlgorithmFactoryAndStrategy.COMPARISON_MODE_DELETION,
								true)));

		ArffLoader trainer = new ArffLoader();
		trainer.loadInstances(inputFile, true);
		final IEvaluator eval = new ExactMatchSelfEvaluator(true, true);
		myExample.registerHook(new FileLogger(inputFile + "_result.txt", eval));
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
		ConfusionMatrixEvaluator conf = new ConfusionMatrixEvaluator(
				rep.getLabelNames(), ClassifierTransformBridge.instances);
		conf.evaluateSet(rulePopulation);

		System.out.println("Evaluating on test set");
		ExactMatchEvalutor testEval = new ExactMatchEvalutor(
				trainer.testSet, true);
		testEval.evaluateSet(rulePopulation);

	}
}
