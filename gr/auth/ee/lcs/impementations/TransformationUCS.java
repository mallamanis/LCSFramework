/**
 * 
 */
package gr.auth.ee.lcs.impementations;

import gr.auth.ee.lcs.AbstractLearningClassifierSystem;
import gr.auth.ee.lcs.ArffLoader;
import gr.auth.ee.lcs.LCSTrainTemplate;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.classifiers.populationcontrol.FixedSizeSetWorstFitnessDeletion;
import gr.auth.ee.lcs.classifiers.populationcontrol.PostProcessPopulationControl;
import gr.auth.ee.lcs.classifiers.populationcontrol.SortPopulationControl;
import gr.auth.ee.lcs.data.AbstractUpdateStrategy;
import gr.auth.ee.lcs.data.IEvaluator;
import gr.auth.ee.lcs.data.representations.GenericMultiLabelRepresentation;
import gr.auth.ee.lcs.data.representations.GenericMultiLabelRepresentation.BestFitnessClassificationStrategy;
import gr.auth.ee.lcs.data.representations.GenericMultiLabelRepresentation.VotingClassificationStrategy;
import gr.auth.ee.lcs.data.updateAlgorithms.UCSUpdateAlgorithm;
import gr.auth.ee.lcs.evaluators.AccuracyEvaluator;
import gr.auth.ee.lcs.evaluators.AllSingleLabelEvaluator;
import gr.auth.ee.lcs.evaluators.ExactMatchEvalutor;
import gr.auth.ee.lcs.evaluators.FileLogger;
import gr.auth.ee.lcs.evaluators.HammingLossEvaluator;
import gr.auth.ee.lcs.geneticalgorithm.IGeneticAlgorithmStrategy;
import gr.auth.ee.lcs.geneticalgorithm.algorithms.SteadyStateGeneticAlgorithm;
import gr.auth.ee.lcs.geneticalgorithm.operators.SinglePointCrossover;
import gr.auth.ee.lcs.geneticalgorithm.operators.UniformBitMutation;
import gr.auth.ee.lcs.geneticalgorithm.selectors.RouletteWheelSelector;
import gr.auth.ee.lcs.utilities.BinaryRelevanceSelector;
import gr.auth.ee.lcs.utilities.ILabelSelector;
import gr.auth.ee.lcs.utilities.SettingsLoader;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A Binary Relevance Problem Transformation UCS.
 * 
 * @author Miltos Allamanis
 * 
 */
public class TransformationUCS extends AbstractLearningClassifierSystem {
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		SettingsLoader.loadSettings();
		Handler fileLogging = new FileHandler("output.log");

		Logger.getLogger("").setLevel(Level.CONFIG);
		Logger.getLogger("").addHandler(fileLogging);
		final String file = SettingsLoader.getStringSetting("filename", "");
		final int numOfLabels = (int) SettingsLoader.getNumericSetting(
				"numberOfLabels", 1);
		final int iterations = (int) SettingsLoader.getNumericSetting(
				"trainIterations", 1000);
		final int populationSize = (int) SettingsLoader.getNumericSetting(
				"populationSize", 1500);
		final float lc = (float) SettingsLoader.getNumericSetting(
				"datasetLabelCardinality", 1);
		BinaryRelevanceSelector selector = new BinaryRelevanceSelector(
				numOfLabels);
		TransformationUCS trucs = new TransformationUCS(file, iterations,
				populationSize, numOfLabels, lc, selector);
		trucs.train();

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
	private final float CROSSOVER_RATE = (float) SettingsLoader
			.getNumericSetting("crossoverRate", .8);

	/**
	 * Label Selector to be used.
	 */
	private final ILabelSelector selector;

	/**
	 * The target label cardinality.
	 */
	private final float targetLC;

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
	 * The frequency at which callbacks will be called for evaluation.
	 */
	private final int CALLBACK_RATE = (int) SettingsLoader.getNumericSetting(
			"callbackRate", 100);

	/**
	 * The number of bits to use for representing continuous variables.
	 */
	private final int PRECISION_BITS = (int) SettingsLoader.getNumericSetting(
			"precisionBits", 5);

	/**
	 * The UCS alpha parameter.
	 */
	private final double UCS_ALPHA = SettingsLoader.getNumericSetting(
			"UCS_Alpha", .1);

	/**
	 * The UCS n power parameter.
	 */
	private final int UCS_N = (int) SettingsLoader.getNumericSetting("UCS_N",
			10);

	/**
	 * The accuracy threshold parameter.
	 */
	private final double UCS_ACC0 = SettingsLoader.getNumericSetting(
			"UCS_Acc0", .99);

	/**
	 * The learning rate (beta) parameter.
	 */
	private final double UCS_LEARNING_RATE = SettingsLoader.getNumericSetting(
			"UCS_beta", .1);

	/**
	 * The UCS experience threshold.
	 */
	private final int UCS_EXPERIENCE_THRESHOLD = (int) SettingsLoader
			.getNumericSetting("UCS_Experience_Theshold", 10);

	/**
	 * The post-process experience threshold used.
	 */
	private final int POSTPROCESS_EXPERIENCE_THRESHOLD = (int) SettingsLoader
			.getNumericSetting("PostProcess_Experience_Theshold", 0);

	/**
	 * Coverage threshold for post processing.
	 */
	private final int POSTPROCESS_COVERAGE_THRESHOLD = (int) SettingsLoader
			.getNumericSetting("PostProcess_Coverage_Theshold", 0);

	/**
	 * Post-process threshold for fitness.
	 */
	private final double POSTPROCESS_FITNESS_THRESHOLD = SettingsLoader
			.getNumericSetting("PostProcess_Fitness_Theshold", 0);

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
	 * The number of labels used at the dmlUCS.
	 */
	private final int numberOfLabels;

	/**
	 * The representation used.
	 */
	GenericMultiLabelRepresentation rep;

	/**
	 * The classification strategy.
	 */
	VotingClassificationStrategy vs;

	/**
	 * Constructor.
	 * 
	 * @param filename
	 *            the filename of the UCS
	 * @param iterations
	 *            the number of iterations to run
	 * @param populationSize
	 *            the size of the population to use
	 * @param numOfLabels
	 *            the number of labels in the problem
	 * @throws IOException
	 */
	public TransformationUCS(final String filename, final int iterations,
			final int populationSize, final int numOfLabels,
			final float problemLC, ILabelSelector transformSelector)
			throws IOException {
		inputFile = filename;
		this.iterations = iterations;
		this.populationSize = populationSize;
		this.numberOfLabels = numOfLabels;
		this.targetLC = problemLC;
		this.selector = transformSelector;

		IGeneticAlgorithmStrategy ga = new SteadyStateGeneticAlgorithm(
				new RouletteWheelSelector(
						AbstractUpdateStrategy.COMPARISON_MODE_EXPLORATION,
						true), new SinglePointCrossover(this), CROSSOVER_RATE,
				new UniformBitMutation(MUTATION_RATE), THETA_GA, this);

		rep = new GenericMultiLabelRepresentation(inputFile, PRECISION_BITS,
				numberOfLabels, GenericMultiLabelRepresentation.EXACT_MATCH, 0,
				ATTRIBUTE_GENERALIZATION_RATE, this);
		vs = rep.new VotingClassificationStrategy(targetLC);
		rep.setClassificationStrategy(vs);

		UCSUpdateAlgorithm ucsStrategy = new UCSUpdateAlgorithm(UCS_ALPHA,
				UCS_N, UCS_ACC0, UCS_LEARNING_RATE, UCS_EXPERIENCE_THRESHOLD,
				MATCHSET_GA_RUN_PROBABILITY, ga, THETA_GA, 1, this);

		this.setElements(rep, ucsStrategy);

	}

	/**
	 * Runs the Direct-ML-UCS.
	 * 
	 * @throws IOException
	 */
	@Override
	public void train() {
		LCSTrainTemplate myExample = new LCSTrainTemplate(CALLBACK_RATE, this);

		ClassifierSet rulePopulation = new ClassifierSet(null);

		ArffLoader loader = new ArffLoader(this);
		try {
			loader.loadInstances(inputFile, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		final IEvaluator eval = new ExactMatchEvalutor(this.instances, true,
				this);
		myExample.registerHook(new FileLogger(inputFile + "_result", eval));

		do {
			System.out.println("Training Classifier Set");
			rep.activateLabel(selector);
			ClassifierSet brpopulation = new ClassifierSet(
					new FixedSizeSetWorstFitnessDeletion(
							populationSize,
							new RouletteWheelSelector(
									AbstractUpdateStrategy.COMPARISON_MODE_DELETION,
									true)));
			myExample.train(iterations, brpopulation);
			myExample.updatePopulation(
					(int) (iterations * UPDATE_ONLY_ITERATION_PERCENTAGE),
					brpopulation);
			AllSingleLabelEvaluator seval = new AllSingleLabelEvaluator(
					loader.trainSet, numberOfLabels, true, this);
			seval.evaluateSet(brpopulation);
			rep.reinforceDeactivatedLabels(brpopulation);
			rulePopulation.merge(brpopulation);

		} while (selector.next());
		rep.activateAllLabels();

		ExactMatchEvalutor trainEval = new ExactMatchEvalutor(loader.trainSet,
				true, this);
		trainEval.evaluateSet(rulePopulation);
		HammingLossEvaluator trainhamEval = new HammingLossEvaluator(
				loader.trainSet, true, numberOfLabels, this);
		trainhamEval.evaluateSet(rulePopulation);
		AccuracyEvaluator trainaccEval = new AccuracyEvaluator(loader.trainSet,
				true, this);
		trainaccEval.evaluateSet(rulePopulation);

		System.out.println("Evaluating on test set");
		AllSingleLabelEvaluator teEval = new AllSingleLabelEvaluator(
				loader.testSet, numberOfLabels, true, this);
		teEval.evaluateSet(rulePopulation);
		ExactMatchEvalutor testEval = new ExactMatchEvalutor(loader.testSet,
				true, this);
		testEval.evaluateSet(rulePopulation);
		HammingLossEvaluator hamEval = new HammingLossEvaluator(loader.testSet,
				true, numberOfLabels, this);
		hamEval.evaluateSet(rulePopulation);
		AccuracyEvaluator accEval = new AccuracyEvaluator(loader.testSet, true,
				this);
		accEval.evaluateSet(rulePopulation);

		System.out.println("Post process...");
		PostProcessPopulationControl postProcess = new PostProcessPopulationControl(
				POSTPROCESS_EXPERIENCE_THRESHOLD,
				POSTPROCESS_COVERAGE_THRESHOLD, POSTPROCESS_FITNESS_THRESHOLD,
				AbstractUpdateStrategy.COMPARISON_MODE_EXPLOITATION);
		SortPopulationControl sort = new SortPopulationControl(
				AbstractUpdateStrategy.COMPARISON_MODE_EXPLOITATION);
		postProcess.controlPopulation(rulePopulation);
		sort.controlPopulation(rulePopulation);
		// rulePopulation.print();
		ClassifierSet.saveClassifierSet(rulePopulation, "set");

		eval.evaluateSet(rulePopulation);

		System.out.println("Evaluating on test set (before calibration)");
		testEval.evaluateSet(rulePopulation);
		hamEval.evaluateSet(rulePopulation);
		accEval.evaluateSet(rulePopulation);

		vs.proportionalCutCalibration(this.instances, rulePopulation);
		System.out.println("Evaluating on test set (after calibration)");
		testEval.evaluateSet(rulePopulation);
		hamEval.evaluateSet(rulePopulation);
		accEval.evaluateSet(rulePopulation);

		BestFitnessClassificationStrategy str = rep.new BestFitnessClassificationStrategy();
		rep.setClassificationStrategy(str);

		System.out.println("Evaluating on test set (best)");
		testEval.evaluateSet(rulePopulation);
		hamEval.evaluateSet(rulePopulation);
		accEval.evaluateSet(rulePopulation);

	}

}
