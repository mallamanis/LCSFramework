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
import gr.auth.ee.lcs.data.representations.GenericMultiLabelRepresentation.VotingClassificationStrategy;
import gr.auth.ee.lcs.data.updateAlgorithms.SequentialMlUpdateAlgorithm;
import gr.auth.ee.lcs.data.updateAlgorithms.UCSUpdateAlgorithm;
import gr.auth.ee.lcs.evaluators.AccuracyEvaluator;
import gr.auth.ee.lcs.evaluators.ExactMatchEvalutor;
import gr.auth.ee.lcs.evaluators.FileLogger;
import gr.auth.ee.lcs.evaluators.HammingLossEvaluator;
import gr.auth.ee.lcs.evaluators.bamevaluators.PositionBAMEvaluator;
import gr.auth.ee.lcs.geneticalgorithm.IGeneticAlgorithmStrategy;
import gr.auth.ee.lcs.geneticalgorithm.algorithms.SteadyStateGeneticAlgorithm;
import gr.auth.ee.lcs.geneticalgorithm.operators.SinglePointCrossover;
import gr.auth.ee.lcs.geneticalgorithm.operators.UniformBitMutation;
import gr.auth.ee.lcs.geneticalgorithm.selectors.RouletteWheelSelector;
import gr.auth.ee.lcs.utilities.InstanceToDoubleConverter;
import gr.auth.ee.lcs.utilities.SettingsLoader;

import java.io.IOException;

/**
 * An implementation of a multi-label UCS, using the generic Multi-label
 * representation and a per-label (sequential) update UCS.
 * 
 * @author Miltos Allamanis
 * 
 */
public class SequentialGMlUCS extends AbstractLearningClassifierSystem {

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
				"populationSize", 1500);
		final float lc = (float) SettingsLoader.getNumericSetting(
				"datasetLabelCardinality", 1);
		final SequentialGMlUCS sgmlucs = new SequentialGMlUCS(file, iterations,
				populationSize, numOfLabels, SettingsLoader.getNumericSetting(
						"LabelGeneralizationRate", 0.33), lc);
		sgmlucs.train();

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
			"thetaGA", 100);

	/**
	 * The target Label Cardinality of the problem.
	 */
	private final float targetLC;

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
	 * The generalization rate used for labels.
	 */
	private final double labelGeneralizationRate;

	/**
	 * The number of labels used at the dmlUCS.
	 */
	private final int numberOfLabels;

	/**
	 * The problem representation.
	 */
	private final GenericMultiLabelRepresentation rep;

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
	 * @param problemLC
	 *            the problem's target label cardinality
	 * @throws IOException
	 */
	public SequentialGMlUCS(final String filename, final int iterations,
			final int populationSize, final int numOfLabels,
			final double labelGeneralizationProbability, final float problemLC)
			throws IOException {
		inputFile = filename;
		this.iterations = iterations;
		this.populationSize = populationSize;
		this.numberOfLabels = numOfLabels;
		this.labelGeneralizationRate = labelGeneralizationProbability;
		this.targetLC = problemLC;

		final IGeneticAlgorithmStrategy ga = new SteadyStateGeneticAlgorithm(
				new RouletteWheelSelector(
						AbstractUpdateStrategy.COMPARISON_MODE_EXPLORATION,
						true), new SinglePointCrossover(this), CROSSOVER_RATE,
				new UniformBitMutation(MUTATION_RATE), THETA_GA, this);

		rep = new GenericMultiLabelRepresentation(inputFile, PRECISION_BITS,
				numberOfLabels, GenericMultiLabelRepresentation.EXACT_MATCH,
				labelGeneralizationRate, SettingsLoader.getNumericSetting(
						"AttributeGeneralizationRate", 0.33), this);
		rep.setClassificationStrategy(rep.new BestFitnessClassificationStrategy());

		final UCSUpdateAlgorithm updateObj = new UCSUpdateAlgorithm(UCS_ALPHA,
				UCS_N, UCS_ACC0, UCS_LEARNING_RATE, UCS_EXPERIENCE_THRESHOLD,
				SettingsLoader.getNumericSetting("GAMatchSetRunProbability",
						0.01), ga, THETA_GA, 1, this);
		final SequentialMlUpdateAlgorithm strategy = new SequentialMlUpdateAlgorithm(
				updateObj, ga, numberOfLabels);

		this.setElements(rep, strategy);

	}

	/**
	 * Run the SGmlUCS.
	 * 
	 * @throws IOException
	 */
	@Override
	public void train() {
		final LCSTrainTemplate myExample = new LCSTrainTemplate(CALLBACK_RATE,
				this);

		final ClassifierSet rulePopulation = new ClassifierSet(
				new FixedSizeSetWorstFitnessDeletion(
						populationSize,
						new RouletteWheelSelector(
								AbstractUpdateStrategy.COMPARISON_MODE_DELETION,
								true)));

		final ArffLoader loader = new ArffLoader(this);
		try {
			loader.loadInstances(inputFile, true);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		final IEvaluator eval = new ExactMatchEvalutor(this.instances, true,
				this);
		myExample.registerHook(new FileLogger(inputFile + "_resultSGMlUCS",
				eval));
		myExample.train(iterations, rulePopulation);

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
		// ClassifierSet.saveClassifierSet(rulePopulation, "set");

		eval.evaluateSet(rulePopulation);

		System.out.println("Evaluating on test set (best)");
		final ExactMatchEvalutor testEval = new ExactMatchEvalutor(
				loader.testSet, true, this);
		testEval.evaluateSet(rulePopulation);
		final HammingLossEvaluator hamEval = new HammingLossEvaluator(
				loader.testSet, true, numberOfLabels, this);
		hamEval.evaluateSet(rulePopulation);
		final AccuracyEvaluator accEval = new AccuracyEvaluator(loader.testSet,
				true, this);
		accEval.evaluateSet(rulePopulation);

		final VotingClassificationStrategy str = rep.new VotingClassificationStrategy(
				targetLC);
		rep.setClassificationStrategy(str);

		str.proportionalCutCalibration(
				InstanceToDoubleConverter.convert(loader.testSet),
				rulePopulation);

		System.out.println("Evaluating on test set (voting)");
		testEval.evaluateSet(rulePopulation);
		hamEval.evaluateSet(rulePopulation);
		accEval.evaluateSet(rulePopulation);

		PositionBAMEvaluator bamEval = new PositionBAMEvaluator(7,
				PositionBAMEvaluator.GENERIC_REPRESENTATION, this);
		double result = bamEval.evaluateSet(rulePopulation);
		System.out.println("BAM %:" + result);
	}

}
