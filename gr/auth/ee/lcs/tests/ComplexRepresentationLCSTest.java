/**
 * 
 */
package gr.auth.ee.lcs.tests;

import gr.auth.ee.lcs.ArffLoader;
import gr.auth.ee.lcs.LCSTrainTemplate;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.classifiers.populationcontrol.FixedSizeSetWorstFitnessDeletion;
import gr.auth.ee.lcs.classifiers.populationcontrol.PostProcessPopulationControl;
import gr.auth.ee.lcs.classifiers.populationcontrol.SortPopulationControl;
import gr.auth.ee.lcs.data.ClassifierTransformBridge;
import gr.auth.ee.lcs.data.IEvaluator;
import gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy;
import gr.auth.ee.lcs.data.representations.GenericMultiLabelRepresentation;
import gr.auth.ee.lcs.data.updateAlgorithms.SequentialMlUpdateAlgorithm;
import gr.auth.ee.lcs.data.updateAlgorithms.UCSUpdateAlgorithm;
import gr.auth.ee.lcs.evaluators.ExactMatchEvalutor;
import gr.auth.ee.lcs.evaluators.ExactMatchSelfEvaluator;
import gr.auth.ee.lcs.evaluators.FileLogger;
import gr.auth.ee.lcs.geneticalgorithm.IGeneticAlgorithmStrategy;
import gr.auth.ee.lcs.geneticalgorithm.algorithms.SteadyStateGeneticAlgorithm;
import gr.auth.ee.lcs.geneticalgorithm.operators.SinglePointCrossover;
import gr.auth.ee.lcs.geneticalgorithm.operators.UniformBitMutation;
import gr.auth.ee.lcs.geneticalgorithm.selectors.RouletteWheelSelector;

import java.io.IOException;

/**
 * @author Miltos Allamanis
 * 
 */
public class ComplexRepresentationLCSTest {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		LCSTrainTemplate myExample = new LCSTrainTemplate(10);
		IGeneticAlgorithmStrategy ga = new SteadyStateGeneticAlgorithm(

		/*
		 * new TournamentSelector(50, true,
		 * UpdateAlgorithmFactoryAndStrategy.COMPARISON_MODE_EXPLORATION),
		 */

		new RouletteWheelSelector(
				UpdateAlgorithmFactoryAndStrategy.COMPARISON_MODE_EXPLORATION,
				true), new SinglePointCrossover(), (float) .8,
				new UniformBitMutation(.04), 100);

		String filename = "/home/miltiadis/Desktop/datasets/mlTestbeds/mlidentity7.arff";
		final int numOfLabels = 7;
		// StrictMultiLabelRepresentation rep = new
		// StrictMultiLabelRepresentation(
		// filename, 4, 7, StrictMultiLabelRepresentation.EXACT_MATCH);
		GenericMultiLabelRepresentation rep = new GenericMultiLabelRepresentation(
				filename, 6, numOfLabels,
				GenericMultiLabelRepresentation.EXACT_MATCH, .33);
		rep.setClassificationStrategy(rep.new VotingClassificationStrategy());
		ClassifierTransformBridge.setInstance(rep);

		// UpdateAlgorithmFactoryAndStrategy.currentStrategy = new
		// ASLCSUpdateAlgorithm(
		// 10, .99, 10, .01, ga);
		// UpdateAlgorithmFactoryAndStrategy.currentStrategy = new
		// UCSUpdateAlgorithm(
		// .1, 10, .99, .1, 50, 0.01, ga, 100,.9);
		UCSUpdateAlgorithm updateObj = new UCSUpdateAlgorithm(.1, 10, .99, .1,
				50, 0.01, ga, 100, 1);
		UpdateAlgorithmFactoryAndStrategy.currentStrategy = new SequentialMlUpdateAlgorithm(
				updateObj, ga, 7);
		// UpdateAlgorithmFactoryAndStrategy.currentStrategy = new
		// GenericUCSUpdateAlgorithm(
		// ga, .1);
		// UpdateAlgorithmFactoryAndStrategy.currentStrategy=new
		// XCSUpdateAlgorithm(.2,10,.01,.1,3);

		ClassifierSet rulePopulation = new ClassifierSet(
				new FixedSizeSetWorstFitnessDeletion(
						1000,

						/*
						 * new TournamentSelector( 40, true,
						 * UpdateAlgorithmFactoryAndStrategy
						 * .COMPARISON_MODE_DELETION)
						 */

						new RouletteWheelSelector(
								UpdateAlgorithmFactoryAndStrategy.COMPARISON_MODE_DELETION,
								true)));
		// ClassifierSet rulePopulation=new ClassifierSet(new
		// FixedSizeSetWorstFitnessDeletion(
		// 1000,new
		// BestClassifierSelector(false,UpdateAlgorithmFactoryAndStrategy.COMPARISON_MODE_DELETION)));

		// ClassifierSet rulePopulation = ClassifierSet.openClassifierSet("set",
		// new FixedSizeSetWorstFitnessDeletion(
		// 600,new
		// TournamentSelector(50,false,UpdateAlgorithmFactoryAndStrategy.COMPARISON_MODE_DELETION)));
		ArffLoader trainer = new ArffLoader();
		trainer.loadInstances(filename, false);
		final IEvaluator eval = new ExactMatchSelfEvaluator(true, true);
		myExample.registerHook(new FileLogger("test3.txt", eval));
		myExample.train(500, rulePopulation);

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
			// System.out.println("Predicted Payoff: "+((XCSClassifierData)(rulePopulation.getClassifier(i).updateData)).predictedPayOff);
		}
		System.out.println("Post process...");
		PostProcessPopulationControl postProcess = new PostProcessPopulationControl(
				0, 0, 0.01,
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
			// System.out.println("Predicted Payoff: "+((XCSClassifierData)(rulePopulation.getClassifier(i).updateData)).predictedPayOff);
			System.out
					.println(UpdateAlgorithmFactoryAndStrategy.currentStrategy
							.getData((rulePopulation.getClassifier(i))));
		}
		// ClassifierSet.saveClassifierSet(rulePopulation, "set");

		// eval.evaluateSet(rulePopulation);
		// ConfusionMatrixEvaluator conf = new ConfusionMatrixEvaluator(
		// rep.getLabelNames(), ClassifierTransformBridge.instances);
		// conf.evaluateSet(rulePopulation);

		System.out.println("Evaluating on test set");
		ExactMatchEvalutor testEval = new ExactMatchEvalutor(trainer.testSet,
				true);
		testEval.evaluateSet(rulePopulation);

	}
}
