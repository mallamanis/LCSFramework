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
import gr.auth.ee.lcs.data.updateAlgorithms.SSLCSUpdateAlgorithm;
import gr.auth.ee.lcs.data.updateAlgorithms.SequentialMlUpdateAlgorithm;
import gr.auth.ee.lcs.evaluators.AccuracyRecallEvaluator;
import gr.auth.ee.lcs.evaluators.ExactMatchEvalutor;
import gr.auth.ee.lcs.evaluators.HammingLossEvaluator;
import gr.auth.ee.lcs.geneticalgorithm.IGeneticAlgorithmStrategy;
import gr.auth.ee.lcs.geneticalgorithm.algorithms.SteadyStateGeneticAlgorithm;
import gr.auth.ee.lcs.geneticalgorithm.operators.SinglePointCrossover;
import gr.auth.ee.lcs.geneticalgorithm.operators.UniformBitMutation;
import gr.auth.ee.lcs.geneticalgorithm.selectors.TournamentSelector;
import gr.auth.ee.lcs.utilities.SettingsLoader;

import java.io.IOException;
import java.util.Arrays;

import weka.core.Instances;

/**
 * A Sequential Generic Multi-label SS-LCS.
 * 
 * @author Miltos Allamanis
 * 
 */
public class SequentialGSSLCS extends AbstractLearningClassifierSystem {
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		SettingsLoader.loadSettings();

		final String file = SettingsLoader.getStringSetting("filename", "");

		final SequentialGSSLCS gsslcs = new SequentialGSSLCS();
		FoldEvaluator loader = new FoldEvaluator(10, gsslcs, file);
		loader.evaluate();

	}

	/**
	 * The input file used (.arff).
	 */
	private final String inputFile;

	/**
	 * The target LC used at for classification.
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
	 * Voting classification method.
	 */
	private final VotingClassificationStrategy str;

	/**
	 * Constructor.
	 * 
	 * @throws IOException
	 */
	public SequentialGSSLCS() throws IOException {
		inputFile = SettingsLoader.getStringSetting("filename", "");
		numberOfLabels = (int) SettingsLoader.getNumericSetting(
				"numberOfLabels", 1);
		iterations = (int) SettingsLoader.getNumericSetting("trainIterations",
				1000);
		populationSize = (int) SettingsLoader.getNumericSetting(
				"populationSize", 1500);
		targetLC = (float) SettingsLoader.getNumericSetting(
				"datasetLabelCardinality", 1);

		labelGeneralizationRate = SettingsLoader.getNumericSetting(
				"LabelGeneralizationRate", 0.33);

		final IGeneticAlgorithmStrategy ga = new SteadyStateGeneticAlgorithm(
				new TournamentSelector(50, true,
						AbstractUpdateStrategy.COMPARISON_MODE_EXPLORATION),
				new SinglePointCrossover(this), CROSSOVER_RATE,
				new UniformBitMutation(MUTATION_RATE), THETA_GA, this);

		rep = new GenericMultiLabelRepresentation(inputFile, PRECISION_BITS,
				numberOfLabels, GenericMultiLabelRepresentation.HAMMING_LOSS,
				labelGeneralizationRate, ATTRIBUTE_GENERALIZATION_RATE, this);
		str = rep.new VotingClassificationStrategy(targetLC);
		rep.setClassificationStrategy(str);

		final SSLCSUpdateAlgorithm updateObj = new SSLCSUpdateAlgorithm(
				SSLCS_REWARD, SSLCS_PENALTY, SSLCS_FITNESS_THRESHOLD,
				SSLCS_EXPERIENCE_THRESHOLD, MATCHSET_GA_RUN_PROBABILITY, ga,
				this);
		final SequentialMlUpdateAlgorithm update = new SequentialMlUpdateAlgorithm(
				updateObj, ga, numberOfLabels);
		this.setElements(rep, update);

		rulePopulation = new ClassifierSet(
				new FixedSizeSetWorstFitnessDeletion(
						populationSize,
						new TournamentSelector(40, false,
								AbstractUpdateStrategy.COMPARISON_MODE_DELETION)));
	}

	@Override
	public AbstractLearningClassifierSystem createNew() {
		try {
			return new SequentialGSSLCS();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String[] getEvaluationNames() {
		String[] names = { "Accuracy(pcut)", "Recall(pcut)",
				"HammingLoss(pcut)", "ExactMatch(pcut)", "Accuracy(ival)",
				"Recall(ival)", "HammingLoss(ival)", "ExactMatch(ival)", "Accuracy(ival)",
				"Recall(best)", "HammingLoss(best)", "ExactMatch(best)" };
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
	 * Run the SGmlUCS.
	 * 
	 */
	@Override
	public void train() {

		trainSet(iterations, rulePopulation);
		updatePopulation((int) (iterations * UPDATE_ONLY_ITERATION_PERCENTAGE),
				rulePopulation);

	}

}
