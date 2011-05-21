/**
 * 
 */
package gr.auth.ee.lcs.impementations;

import gr.auth.ee.lcs.AbstractLearningClassifierSystem;
import gr.auth.ee.lcs.ArffLoader;
import gr.auth.ee.lcs.LCSTrainTemplate;
import gr.auth.ee.lcs.calibration.InternalValidation;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.classifiers.populationcontrol.FixedSizeSetWorstFitnessDeletion;
import gr.auth.ee.lcs.classifiers.populationcontrol.PostProcessPopulationControl;
import gr.auth.ee.lcs.classifiers.populationcontrol.SortPopulationControl;
import gr.auth.ee.lcs.data.AbstractUpdateStrategy;
import gr.auth.ee.lcs.data.IEvaluator;
import gr.auth.ee.lcs.data.representations.complex.GenericMultiLabelRepresentation;
import gr.auth.ee.lcs.data.representations.complex.GenericMultiLabelRepresentation.BestFitnessClassificationStrategy;
import gr.auth.ee.lcs.data.representations.complex.GenericMultiLabelRepresentation.VotingClassificationStrategy;
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
import gr.auth.ee.lcs.utilities.LabelFrequencyCalculator;
import gr.auth.ee.lcs.utilities.SettingsLoader;

import java.io.IOException;
import java.util.TreeMap;
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
		final Handler fileLogging = new FileHandler("output.log");

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
		final BinaryRelevanceSelector selector = new BinaryRelevanceSelector(
				numOfLabels);
		final TransformationUCS trucs = new TransformationUCS(file, iterations,
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
	private final int THETA_GA_IMBALANCE_MULTIPLIER = (int) SettingsLoader
			.getNumericSetting("thetaGAImbalanceMultiplier", 10);

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
	 * The GA to be used.
	 */
	final SteadyStateGeneticAlgorithm ga;

	/**
	 * The GA activation rate.
	 */
	private final int THETA_GA = (int) SettingsLoader.getNumericSetting(
			"thetaGA", 300);

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

		ga = new SteadyStateGeneticAlgorithm(new RouletteWheelSelector(
				AbstractUpdateStrategy.COMPARISON_MODE_EXPLORATION, true),
				new SinglePointCrossover(this), CROSSOVER_RATE,
				new UniformBitMutation(MUTATION_RATE), 0, this);

		rep = new GenericMultiLabelRepresentation(inputFile, PRECISION_BITS,
				numberOfLabels, GenericMultiLabelRepresentation.EXACT_MATCH, 0,
				ATTRIBUTE_GENERALIZATION_RATE, this);
		vs = rep.new VotingClassificationStrategy(targetLC);
		rep.setClassificationStrategy(vs);

		final UCSUpdateAlgorithm ucsStrategy = new UCSUpdateAlgorithm(
				UCS_ALPHA, UCS_N, UCS_ACC0, UCS_LEARNING_RATE,
				UCS_EXPERIENCE_THRESHOLD, MATCHSET_GA_RUN_PROBABILITY, ga,
				THETA_GA, 1, this);

		this.setElements(rep, ucsStrategy);

		rulePopulation = new ClassifierSet(null);

	}

	/**
	 * Runs the Direct-ML-UCS.
	 * 
	 * @throws IOException
	 */
	@Override
	public void train() {
		final LCSTrainTemplate myExample = new LCSTrainTemplate(CALLBACK_RATE,
				this);

		final ArffLoader loader = new ArffLoader(this);
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
			TreeMap<String, Integer> fr = LabelFrequencyCalculator
					.createCombinationMap(selector.activeIndexes(),
							numberOfLabels, instances);
			final double imbalance = LabelFrequencyCalculator.ImbalanceRate(fr);
			ga.setThetaGA((int) (imbalance * THETA_GA_IMBALANCE_MULTIPLIER));
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

		final ExactMatchEvalutor trainEval = new ExactMatchEvalutor(
				loader.trainSet, true, this);
		trainEval.evaluateSet(rulePopulation);
		final HammingLossEvaluator trainhamEval = new HammingLossEvaluator(
				loader.trainSet, true, numberOfLabels, this);
		trainhamEval.evaluateSet(rulePopulation);
		final AccuracyEvaluator trainaccEval = new AccuracyEvaluator(
				loader.trainSet, true, this);
		trainaccEval.evaluateSet(rulePopulation);

		System.out.println("Evaluating on test set");
		final AllSingleLabelEvaluator teEval = new AllSingleLabelEvaluator(
				loader.testSet, numberOfLabels, true, this);
		teEval.evaluateSet(rulePopulation);
		final ExactMatchEvalutor testEval = new ExactMatchEvalutor(
				loader.testSet, true, this);
		testEval.evaluateSet(rulePopulation);
		final HammingLossEvaluator hamEval = new HammingLossEvaluator(
				loader.testSet, true, numberOfLabels, this);
		hamEval.evaluateSet(rulePopulation);
		final AccuracyEvaluator accEval = new AccuracyEvaluator(loader.testSet,
				true, this);
		accEval.evaluateSet(rulePopulation);

		System.out.println("Post process...");
		final PostProcessPopulationControl postProcess = new PostProcessPopulationControl(
				POSTPROCESS_EXPERIENCE_THRESHOLD,
				POSTPROCESS_COVERAGE_THRESHOLD, POSTPROCESS_FITNESS_THRESHOLD,
				AbstractUpdateStrategy.COMPARISON_MODE_EXPLOITATION);
		final SortPopulationControl sort = new SortPopulationControl(
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

		for (double i = 0; i < 1; i += .05) {
			vs.setThreshold(i);
			System.out.println("====Threshold set to " + i + "====");
			System.out.println("-----Train-----");
			trainEval.evaluateSet(rulePopulation);
			trainhamEval.evaluateSet(rulePopulation);
			;
			trainaccEval.evaluateSet(rulePopulation);

			System.out.println("-----Test------");
			testEval.evaluateSet(rulePopulation);
			hamEval.evaluateSet(rulePopulation);
			accEval.evaluateSet(rulePopulation);
		}

		System.out.println("Evaluating on test set (Internal Evaluation)");
		final AccuracyEvaluator accTrain = new AccuracyEvaluator(
				loader.trainSet, false, this);
		InternalValidation ival = new InternalValidation(rulePopulation, vs,
				accTrain);
		ival.calibrate(15);
		testEval.evaluateSet(rulePopulation);
		hamEval.evaluateSet(rulePopulation);
		accEval.evaluateSet(rulePopulation);

		final BestFitnessClassificationStrategy str = rep.new BestFitnessClassificationStrategy();
		rep.setClassificationStrategy(str);

		System.out.println("Evaluating on test set (best)");
		testEval.evaluateSet(rulePopulation);
		hamEval.evaluateSet(rulePopulation);
		accEval.evaluateSet(rulePopulation);

	}

}
