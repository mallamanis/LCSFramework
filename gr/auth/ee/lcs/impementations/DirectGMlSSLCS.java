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
import gr.auth.ee.lcs.data.updateAlgorithms.MlSSLCSUpdateAlgorithm;
import gr.auth.ee.lcs.evaluators.AccuracyEvaluator;
import gr.auth.ee.lcs.evaluators.ExactMatchEvalutor;
import gr.auth.ee.lcs.evaluators.FileLogger;
import gr.auth.ee.lcs.evaluators.HammingLossEvaluator;
import gr.auth.ee.lcs.geneticalgorithm.IGeneticAlgorithmStrategy;
import gr.auth.ee.lcs.geneticalgorithm.algorithms.SteadyStateGeneticAlgorithm;
import gr.auth.ee.lcs.geneticalgorithm.operators.SinglePointCrossover;
import gr.auth.ee.lcs.geneticalgorithm.operators.UniformBitMutation;
import gr.auth.ee.lcs.geneticalgorithm.selectors.TournamentSelector;
import gr.auth.ee.lcs.utilities.SettingsLoader;

import java.io.IOException;

/**
 * A generic-representation SS-LCS like LCS.
 * 
 * @author Miltos Allamanis
 * 
 */
public class DirectGMlSSLCS extends AbstractLearningClassifierSystem {
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

		final float attributeGeneralizationRate = (float) SettingsLoader
				.getNumericSetting("LabelGeneralizationRate", 0.33);

		DirectGMlSSLCS sgmlucs = new DirectGMlSSLCS(file, iterations,
				populationSize, numOfLabels, attributeGeneralizationRate, lc);
		sgmlucs.train();

	}

	/**
	 * The input file used (.arff).
	 */
	private final String inputFile;

	/**
	 * The target label cardinality.
	 */
	private final float targetLC;

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
	 * The SSLCS penalty parameter.
	 */
	private final double SSLCS_PENALTY = SettingsLoader.getNumericSetting(
			"SSLCSPenaltyPercent", 10);

	/**
	 * The accuracy threshold parameter.
	 */
	private final double SSLCS_REWARD = SettingsLoader.getNumericSetting(
			"SSLCS_REWARD", 1);

	/**
	 * The SSLCS subsumption experience threshold.
	 */
	private final int SSLCS_EXPERIENCE_THRESHOLD = (int) SettingsLoader
			.getNumericSetting("SSLCSExperienceThreshold", 10);

	/**
	 * The SSLCS subsumption experience threshold.
	 */
	private final double SSLCS_FITNESS_THRESHOLD = SettingsLoader
			.getNumericSetting("SSLCSFitnessThreshold", .99);

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
	 * The voting classification strategy.
	 */
	private final VotingClassificationStrategy str;

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
	 * @throws IOException
	 */
	public DirectGMlSSLCS(final String filename, final int iterations,
			final int populationSize, final int numOfLabels,
			final double labelGeneralizationProbability, final float problemLC)
			throws IOException {
		inputFile = filename;
		this.iterations = iterations;
		this.populationSize = populationSize;
		this.numberOfLabels = numOfLabels;
		this.labelGeneralizationRate = labelGeneralizationProbability;
		this.targetLC = problemLC;

		IGeneticAlgorithmStrategy ga = new SteadyStateGeneticAlgorithm(
				new TournamentSelector(50, true,
						AbstractUpdateStrategy.COMPARISON_MODE_EXPLORATION),
				new SinglePointCrossover(this), CROSSOVER_RATE,
				new UniformBitMutation(MUTATION_RATE), THETA_GA, this);

		rep = new GenericMultiLabelRepresentation(inputFile, PRECISION_BITS,
				numberOfLabels, GenericMultiLabelRepresentation.EXACT_MATCH,
				labelGeneralizationRate, ATTRIBUTE_GENERALIZATION_RATE, this);
		str = rep.new VotingClassificationStrategy(targetLC);
		rep.setClassificationStrategy(str);

		MlSSLCSUpdateAlgorithm strategy = new MlSSLCSUpdateAlgorithm(
				SSLCS_REWARD, SSLCS_PENALTY, numberOfLabels, ga,
				SSLCS_EXPERIENCE_THRESHOLD, SSLCS_FITNESS_THRESHOLD, this);

		this.setElements(rep, strategy);

	}

	/**
	 * Run the SGmlUCS.
	 * 
	 * @throws IOException
	 */
	@Override
	public void train() {
		LCSTrainTemplate myExample = new LCSTrainTemplate(CALLBACK_RATE, this);

		ClassifierSet rulePopulation = new ClassifierSet(
				new FixedSizeSetWorstFitnessDeletion(
						populationSize,
						new TournamentSelector(40, true,
								AbstractUpdateStrategy.COMPARISON_MODE_DELETION)));

		ArffLoader loader = new ArffLoader(this);
		try {
			loader.loadInstances(inputFile, true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		final IEvaluator eval = new ExactMatchEvalutor(this.instances, true,
				this);
		myExample.registerHook(new FileLogger(inputFile + "_resultDGMlSSLCS",
				eval));
		myExample.train(iterations, rulePopulation);
		myExample.updatePopulation(
				(int) (iterations * UPDATE_ONLY_ITERATION_PERCENTAGE),
				rulePopulation);

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
		// ClassifierSet.saveClassifierSet(rulePopulation, "set");

		eval.evaluateSet(rulePopulation);

		// TODO: Calibrate on set
		str.proportionalCutCalibration(this.instances, rulePopulation);
		System.out.println("Evaluating on test set");
		ExactMatchEvalutor testEval = new ExactMatchEvalutor(loader.testSet,
				true, this);
		testEval.evaluateSet(rulePopulation);
		HammingLossEvaluator hamEval = new HammingLossEvaluator(loader.testSet,
				true, numberOfLabels, this);
		hamEval.evaluateSet(rulePopulation);
		AccuracyEvaluator accEval = new AccuracyEvaluator(loader.testSet, true,
				this);
		accEval.evaluateSet(rulePopulation);

	}
}
