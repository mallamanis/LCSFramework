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
import gr.auth.ee.lcs.data.representations.complex.StrictMultiLabelRepresentation;
import gr.auth.ee.lcs.data.representations.complex.StrictMultiLabelRepresentation.BestFitnessClassificationStrategy;
import gr.auth.ee.lcs.data.representations.complex.StrictMultiLabelRepresentation.VotingClassificationStrategy;
import gr.auth.ee.lcs.data.updateAlgorithms.UCSUpdateAlgorithm;
import gr.auth.ee.lcs.evaluators.AccuracyEvaluator;
import gr.auth.ee.lcs.evaluators.ExactMatchEvalutor;
import gr.auth.ee.lcs.evaluators.FileLogger;
import gr.auth.ee.lcs.evaluators.HammingLossEvaluator;
import gr.auth.ee.lcs.evaluators.bamevaluators.IdentityBAMEvaluator;
import gr.auth.ee.lcs.evaluators.bamevaluators.PositionBAMEvaluator;
import gr.auth.ee.lcs.geneticalgorithm.IGeneticAlgorithmStrategy;
import gr.auth.ee.lcs.geneticalgorithm.algorithms.SteadyStateGeneticAlgorithm;
import gr.auth.ee.lcs.geneticalgorithm.operators.SinglePointCrossover;
import gr.auth.ee.lcs.geneticalgorithm.operators.UniformBitMutation;
import gr.auth.ee.lcs.geneticalgorithm.selectors.RouletteWheelSelector;
import gr.auth.ee.lcs.utilities.InstanceToDoubleConverter;
import gr.auth.ee.lcs.utilities.LabelFrequencyCalculator;
import gr.auth.ee.lcs.utilities.SettingsLoader;

import java.io.IOException;

/**
 * Multilabel UCS with direct usage of the UCS update algorithm and the strict
 * ml repesentation.
 * 
 * @author Miltos Allamanis
 * 
 */
public class DirectUCS extends AbstractLearningClassifierSystem {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		SettingsLoader.loadSettings();
		final String file = SettingsLoader.getStringSetting("filename", "");
		final int numOfLabels = (int) SettingsLoader.getNumericSetting(
				"numberOfLabels", 1);
		final int iterations = (int) SettingsLoader.getNumericSetting(
				"trainIterations", 1000);
		final int populationSize = (int) SettingsLoader.getNumericSetting(
				"populationSize", 1000);
		for (int i = 0; i < 10; i++) {
			final DirectUCS dmlucs = new DirectUCS(file, iterations,
					populationSize, numOfLabels);
			 dmlucs.train();
		}

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
	 * The GA mutation rate.
	 */
	private final double MUTATION_RATE = (float) SettingsLoader
			.getNumericSetting("mutationRate", .04);

	/**
	 * The GA activation rate.
	 */
	private final int THETA_GA = (int) SettingsLoader.getNumericSetting(
			"thetaGA", 300);

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

	private final float targetLc = (float) SettingsLoader.getNumericSetting(
			"datasetLabelCardinality", 1);

	/**
	 * The number of labels used at the dmlUCS.
	 */
	private final int numberOfLabels;

	/**
	 * The problem representation.
	 */
	private final StrictMultiLabelRepresentation rep;

	private BestFitnessClassificationStrategy clStr;

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
	public DirectUCS(final String filename, final int iterations,
			final int populationSize, final int numOfLabels) throws IOException {
		inputFile = filename;
		this.iterations = iterations;
		this.populationSize = populationSize;
		this.numberOfLabels = numOfLabels;

		final IGeneticAlgorithmStrategy ga = new SteadyStateGeneticAlgorithm(
				new RouletteWheelSelector(
						AbstractUpdateStrategy.COMPARISON_MODE_EXPLORATION,
						true), new SinglePointCrossover(this), CROSSOVER_RATE,
				new UniformBitMutation(MUTATION_RATE), THETA_GA, this);

		rep = new StrictMultiLabelRepresentation(inputFile, PRECISION_BITS,
				numberOfLabels, StrictMultiLabelRepresentation.EXACT_MATCH,
				ATTRIBUTE_GENERALIZATION_RATE, this);
		//clStr = rep.new VotingClassificationStrategy(targetLc);
		clStr = rep.new BestFitnessClassificationStrategy();
		rep.setClassificationStrategy(clStr);

		final UCSUpdateAlgorithm strategy = new UCSUpdateAlgorithm(UCS_ALPHA,
				UCS_N, UCS_ACC0, UCS_LEARNING_RATE, UCS_EXPERIENCE_THRESHOLD,
				MATCHSET_GA_RUN_PROBABILITY, ga, THETA_GA, 1, this);

		this.setElements(rep, strategy);

		rulePopulation = new ClassifierSet(
				new FixedSizeSetWorstFitnessDeletion(
						populationSize,
						new RouletteWheelSelector(
								AbstractUpdateStrategy.COMPARISON_MODE_DELETION,
								true)));

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
			loader.loadInstances(inputFile, false);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		final IEvaluator eval = new ExactMatchEvalutor(this.instances, false,
				this);
		
		myExample.registerHook(new FileLogger(inputFile + "_exDUCS", eval));
		
		AccuracyEvaluator selfEval = new AccuracyEvaluator(loader.trainSet, false,
				this);
		
		myExample.registerHook(new FileLogger(inputFile + "_accDUCS", selfEval));
		myExample.train(iterations, rulePopulation);
		myExample.updatePopulation(
				(int) (iterations * UPDATE_ONLY_ITERATION_PERCENTAGE),
				rulePopulation);
		System.out.println("Post process...");
		final PostProcessPopulationControl postProcess = new PostProcessPopulationControl(
				POSTPROCESS_EXPERIENCE_THRESHOLD,
				POSTPROCESS_COVERAGE_THRESHOLD, POSTPROCESS_FITNESS_THRESHOLD,
				AbstractUpdateStrategy.COMPARISON_MODE_EXPLOITATION);
		final SortPopulationControl sort = new SortPopulationControl(
				AbstractUpdateStrategy.COMPARISON_MODE_EXPLOITATION);
		postProcess.controlPopulation(rulePopulation);
		sort.controlPopulation(rulePopulation);
		rulePopulation.print();
		
		/*ClassifierSet.saveClassifierSet(rulePopulation, "set");

		eval.evaluateSet(rulePopulation);

		System.out.println("Evaluating on test set (Pcut)");
		clStr.proportionalCutCalibration(
				InstanceToDoubleConverter.convert(loader.trainSet),
				rulePopulation);
		ExactMatchEvalutor testEval = new ExactMatchEvalutor(loader.testSet,
				true, this);
		testEval.evaluateSet(rulePopulation);
		HammingLossEvaluator hamEval = new HammingLossEvaluator(loader.testSet,
				true, numberOfLabels, this);
		hamEval.evaluateSet(rulePopulation);
		AccuracyEvaluator accEval = new AccuracyEvaluator(loader.testSet, true,
				this);
		accEval.evaluateSet(rulePopulation);

		System.out.println("Evaluating on test set (Internal Evaluation)");
		InternalValidation ival = new InternalValidation(rulePopulation, clStr,
				eval);
		ival.calibrate(15);
		testEval = new ExactMatchEvalutor(loader.testSet, true, this);
		testEval.evaluateSet(rulePopulation);
		hamEval = new HammingLossEvaluator(loader.testSet, true,
				numberOfLabels, this);
		hamEval.evaluateSet(rulePopulation);
		accEval = new AccuracyEvaluator(loader.testSet, true, this);

		for (double i = 0; i < 1; i += .05) {
			clStr.setThreshold(i);
			System.out.print("Threshold set to " + i);
			testEval = new ExactMatchEvalutor(loader.testSet, true, this);
			testEval.evaluateSet(rulePopulation);
			hamEval = new HammingLossEvaluator(loader.testSet, true,
					numberOfLabels, this);
			hamEval.evaluateSet(rulePopulation);
			accEval = new AccuracyEvaluator(loader.testSet, true, this);
			accEval.evaluateSet(rulePopulation);
		}

		System.out.println("Evaluating on test set (best)");
		rep.setClassificationStrategy(rep.new BestFitnessClassificationStrategy());
		testEval.evaluateSet(rulePopulation);
		hamEval.evaluateSet(rulePopulation);
		accEval.evaluateSet(rulePopulation);

		PositionBAMEvaluator bamEval = new PositionBAMEvaluator(7,
				PositionBAMEvaluator.STRICT_REPRESENTATION, this);
		double result = bamEval.evaluateSet(rulePopulation);
		System.out.println("BAM %:" + result); */

	}

}
