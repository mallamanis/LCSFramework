/**
 * 
 */
package gr.auth.ee.lcs.impementations;

import gr.auth.ee.lcs.AbstractLearningClassifierSystem;
import gr.auth.ee.lcs.FoldEvaluator;
import gr.auth.ee.lcs.calibration.InternalValidation;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.classifiers.populationcontrol.FixedSizeSetWorstFitnessDeletion;
import gr.auth.ee.lcs.data.AbstractUpdateStrategy;
import gr.auth.ee.lcs.data.representations.complex.GenericMultiLabelRepresentation;
import gr.auth.ee.lcs.data.representations.complex.GenericMultiLabelRepresentation.VotingClassificationStrategy;
import gr.auth.ee.lcs.data.updateAlgorithms.UCSUpdateAlgorithm;
import gr.auth.ee.lcs.evaluators.AccuracyRecallEvaluator;
import gr.auth.ee.lcs.evaluators.ExactMatchEvalutor;
import gr.auth.ee.lcs.evaluators.HammingLossEvaluator;
import gr.auth.ee.lcs.geneticalgorithm.algorithms.SteadyStateGeneticAlgorithm;
import gr.auth.ee.lcs.geneticalgorithm.operators.SinglePointCrossover;
import gr.auth.ee.lcs.geneticalgorithm.operators.UniformBitMutation;
import gr.auth.ee.lcs.geneticalgorithm.selectors.RouletteWheelSelector;
import gr.auth.ee.lcs.utilities.BinaryRelevanceSelector;
import gr.auth.ee.lcs.utilities.ILabelSelector;
import gr.auth.ee.lcs.utilities.LabelFrequencyCalculator;
import gr.auth.ee.lcs.utilities.SettingsLoader;

import java.io.IOException;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import weka.core.Instances;

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

		final TransformationUCS trucs = new TransformationUCS();
		FoldEvaluator loader = new FoldEvaluator(10, trucs, file);
		loader.evaluate();

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
	 * @throws IOException
	 * 
	 */
	public TransformationUCS() throws IOException {

		inputFile = SettingsLoader.getStringSetting("filename", "");
		numberOfLabels = (int) SettingsLoader.getNumericSetting(
				"numberOfLabels", 1);
		iterations = (int) SettingsLoader.getNumericSetting("trainIterations",
				1000);
		populationSize = (int) SettingsLoader.getNumericSetting(
				"populationSize", 1500);
		targetLC = (float) SettingsLoader.getNumericSetting(
				"datasetLabelCardinality", 1);
		selector = new BinaryRelevanceSelector(numberOfLabels);

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

	@Override
	public AbstractLearningClassifierSystem createNew() {
		try {
			return new TransformationUCS();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String[] getEvaluationNames() {
		String[] names = { "Accuracy(pcut)", "Recall(pcut)",
				"HammingLoss(pcut)", "ExactMatch(pcut)", "Accuracy(ival)",
				"Recall(ival)", "HammingLoss(ival)", "ExactMatch(ival)",
				"Accuracy(best)", "Recall(best)", "HammingLoss(best)",
				"ExactMatch(best)" };
		return names;
	}

	@Override
	public double[] getEvaluations(Instances testSet) {
		double[] results = new double[12];
		Arrays.fill(results, 0);

		VotingClassificationStrategy str = rep.new VotingClassificationStrategy(
				(float) SettingsLoader.getNumericSetting(
						"datasetLabelCardinality", 1));
		rep.setClassificationStrategy(str);

		str.proportionalCutCalibration(this.instances, rulePopulation);

		final AccuracyRecallEvaluator accEval = new AccuracyRecallEvaluator(
				testSet, false, this, AccuracyRecallEvaluator.TYPE_ACCURACY);
		results[0] = accEval.evaluateSet(rulePopulation);

		final AccuracyRecallEvaluator recEval = new AccuracyRecallEvaluator(
				testSet, false, this, AccuracyRecallEvaluator.TYPE_RECALL);
		results[1] = recEval.evaluateSet(rulePopulation);

		final HammingLossEvaluator hamEval = new HammingLossEvaluator(testSet,
				false, numberOfLabels, this);
		results[2] = hamEval.evaluateSet(rulePopulation);

		final ExactMatchEvalutor testEval = new ExactMatchEvalutor(testSet,
				false, this);
		results[3] = testEval.evaluateSet(rulePopulation);

		final AccuracyRecallEvaluator selfAcc = new AccuracyRecallEvaluator(
				instances, false, this, AccuracyRecallEvaluator.TYPE_ACCURACY);
		final InternalValidation ival = new InternalValidation(rulePopulation,
				str, selfAcc);
		ival.calibrate(15);

		results[4] = accEval.evaluateSet(rulePopulation);
		results[5] = recEval.evaluateSet(rulePopulation);
		results[6] = hamEval.evaluateSet(rulePopulation);
		results[7] = testEval.evaluateSet(rulePopulation);

		rep.setClassificationStrategy(rep.new BestFitnessClassificationStrategy());

		results[8] = accEval.evaluateSet(rulePopulation);
		results[9] = recEval.evaluateSet(rulePopulation);
		results[10] = hamEval.evaluateSet(rulePopulation);
		results[11] = testEval.evaluateSet(rulePopulation);

		return results;
	}

	/**
	 * Runs the Direct-ML-UCS.
	 * 
	 */
	@Override
	public void train() {

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
			trainSet(iterations, brpopulation);
			updatePopulation(
					(int) (iterations * UPDATE_ONLY_ITERATION_PERCENTAGE),
					brpopulation);

			rep.reinforceDeactivatedLabels(brpopulation);
			rulePopulation.merge(brpopulation);

		} while (selector.next());
		rep.activateAllLabels();

	}

}
