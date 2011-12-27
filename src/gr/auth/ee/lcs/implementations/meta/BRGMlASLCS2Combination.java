/**
 * 
 */
package gr.auth.ee.lcs.implementations.meta;

import java.io.IOException;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import weka.core.Instances;
import gr.auth.ee.lcs.AbstractLearningClassifierSystem;
import gr.auth.ee.lcs.FoldEvaluator;
import gr.auth.ee.lcs.calibration.InternalValidation;
import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.classifiers.populationcontrol.FixedSizeSetWorstFitnessDeletion;
import gr.auth.ee.lcs.data.AbstractUpdateStrategy;
import gr.auth.ee.lcs.data.ILCSMetric;
import gr.auth.ee.lcs.data.representations.complex.GenericMultiLabelRepresentation;
import gr.auth.ee.lcs.data.representations.complex.GenericMultiLabelRepresentation.BestFitnessClassificationStrategy;
import gr.auth.ee.lcs.data.representations.complex.GenericMultiLabelRepresentation.VotingClassificationStrategy;
import gr.auth.ee.lcs.data.updateAlgorithms.MlASLCS2UpdateAlgorithm;
import gr.auth.ee.lcs.data.updateAlgorithms.SequentialMlUpdateAlgorithm;
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

/**
 * @author Miltiadis Allamanis
 * 
 */
public class BRGMlASLCS2Combination extends AbstractLearningClassifierSystem {

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

		final BRGMlASLCS2Combination trucs = new BRGMlASLCS2Combination();
		final FoldEvaluator loader = new FoldEvaluator(10, trucs, file);
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
	SteadyStateGeneticAlgorithm ga;

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
	public BRGMlASLCS2Combination() {

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

	}

	@Override
	public int[] classifyInstance(double[] instance) {
		return getClassifierTransformBridge().classify(
				this.getRulePopulation(), instance);
	}

	@Override
	public AbstractLearningClassifierSystem createNew() {

		return new BRSGUCSCombination();

	}

	@Override
	public String[] getEvaluationNames() {
		final String[] names = { "Accuracy(pcut)", "Recall(pcut)",
				"HammingLoss(pcut)", "ExactMatch(pcut)", "Accuracy(ival)",
				"Recall(ival)", "HammingLoss(ival)", "ExactMatch(ival)",
				"Accuracy(best)", "Recall(best)", "HammingLoss(best)",
				"ExactMatch(best)" };
		return names;
	}

	@Override
	public double[] getEvaluations(Instances testSet) {
		final double[] results = new double[12];
		Arrays.fill(results, 0);

		final AccuracyRecallEvaluator selfAcc = new AccuracyRecallEvaluator(
				instances, false, this, AccuracyRecallEvaluator.TYPE_ACCURACY);

		final VotingClassificationStrategy str = proportionalCutCalibration();

		final AccuracyRecallEvaluator accEval = new AccuracyRecallEvaluator(
				testSet, false, this, AccuracyRecallEvaluator.TYPE_ACCURACY);
		results[0] = accEval.getMetric(this);

		final AccuracyRecallEvaluator recEval = new AccuracyRecallEvaluator(
				testSet, false, this, AccuracyRecallEvaluator.TYPE_RECALL);
		results[1] = recEval.getMetric(this);

		final HammingLossEvaluator hamEval = new HammingLossEvaluator(testSet,
				false, numberOfLabels, this);
		results[2] = hamEval.getMetric(this);

		final ExactMatchEvalutor testEval = new ExactMatchEvalutor(testSet,
				false, this);
		results[3] = testEval.getMetric(this);

		internalValidationCalibration(selfAcc);

		results[4] = accEval.getMetric(this);
		results[5] = recEval.getMetric(this);
		results[6] = hamEval.getMetric(this);
		results[7] = testEval.getMetric(this);

		useBestClassificationMode();

		results[8] = accEval.getMetric(this);
		results[9] = recEval.getMetric(this);
		results[10] = hamEval.getMetric(this);
		results[11] = testEval.getMetric(this);

		return results;
	}

	public void internalValidationCalibration(ILCSMetric selfAcc) {
		final VotingClassificationStrategy str = rep.new VotingClassificationStrategy(
				(float) SettingsLoader.getNumericSetting(
						"datasetLabelCardinality", 1));
		rep.setClassificationStrategy(str);
		final InternalValidation ival = new InternalValidation(this, str,
				selfAcc);
		ival.calibrate(15);
	}

	public VotingClassificationStrategy proportionalCutCalibration() {
		final VotingClassificationStrategy str = rep.new VotingClassificationStrategy(
				(float) SettingsLoader.getNumericSetting(
						"datasetLabelCardinality", 1));
		rep.setClassificationStrategy(str);

		str.proportionalCutCalibration(this.instances, rulePopulation);
		return str;
	}

	/**
	 * Runs the Direct-ML-UCS.
	 * 
	 */
	@Override
	public void train() {

		// Set BR- variables
		ga = new SteadyStateGeneticAlgorithm(new RouletteWheelSelector(
				AbstractUpdateStrategy.COMPARISON_MODE_EXPLORATION, true),
				new SinglePointCrossover(this), CROSSOVER_RATE,
				new UniformBitMutation(MUTATION_RATE), 0, this);

		try {
			rep = new GenericMultiLabelRepresentation(inputFile,
					PRECISION_BITS, numberOfLabels,
					GenericMultiLabelRepresentation.EXACT_MATCH, 0,
					ATTRIBUTE_GENERALIZATION_RATE, this);
		} catch (IOException e) {
			e.printStackTrace();
		}
		vs = rep.new VotingClassificationStrategy(targetLC);
		rep.setClassificationStrategy(vs);

		final UCSUpdateAlgorithm ucsStrategy = new UCSUpdateAlgorithm(
				UCS_ALPHA, UCS_N, UCS_ACC0, UCS_LEARNING_RATE,
				UCS_EXPERIENCE_THRESHOLD, MATCHSET_GA_RUN_PROBABILITY, ga,
				THETA_GA, 1, this);

		this.setElements(rep, ucsStrategy);

		rulePopulation = new ClassifierSet(
				new FixedSizeSetWorstFitnessDeletion(this, numberOfLabels
						* populationSize, new RouletteWheelSelector(
						AbstractUpdateStrategy.COMPARISON_MODE_DELETION, true)));

		// Train BR
		do {
			System.out.println("Training Classifier Set");
			rep.activateLabel(selector);
			TreeMap<String, Integer> fr = LabelFrequencyCalculator
					.createCombinationMap(selector.activeIndexes(),
							numberOfLabels, instances);
			final double imbalance = LabelFrequencyCalculator.imbalanceRate(fr);
			ga.setThetaGA((int) (imbalance * THETA_GA_IMBALANCE_MULTIPLIER));
			ClassifierSet brpopulation = new ClassifierSet(
					new FixedSizeSetWorstFitnessDeletion(
							this,
							populationSize,
							new RouletteWheelSelector(
									AbstractUpdateStrategy.COMPARISON_MODE_DELETION,
									true)));
			trainSet(iterations, brpopulation);

			rep.reinforceDeactivatedLabels(brpopulation);
			rulePopulation.merge(brpopulation);

		} while (selector.next());
		rep.activateAllLabels();

		useBestClassificationMode();

		final MlASLCS2UpdateAlgorithm strategy = new MlASLCS2UpdateAlgorithm(
				ASLCS_N, ASLCS_ACC0, ASLCS_EXPERIENCE_THRESHOLD, ga,
				numberOfLabels, this);

		this.setElements(rep, strategy);

		// Convert classifier update objects
		final int numOfMacro = rulePopulation.getNumberOfMacroclassifiers();
		for (int i = 0; i < numOfMacro; i++) {
			final Classifier cl = rulePopulation.getClassifier(i);
			cl.setUpdateStrategy(strategy);
			cl.setUpdateObject(strategy.createStateClassifierObject());
		}

		updatePopulation((int) (iterations * UPDATE_ONLY_ITERATION_PERCENTAGE),
				rulePopulation);
		trainSet(iterations, rulePopulation);
		updatePopulation((int) (iterations * UPDATE_ONLY_ITERATION_PERCENTAGE),
				rulePopulation);

	}

	public void useBestClassificationMode() {
		rep.setClassificationStrategy(rep.new BestFitnessClassificationStrategy());
	}

}
