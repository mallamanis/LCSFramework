/**
 * 
 */
package gr.auth.ee.lcs.tests;

import gr.auth.ee.lcs.ArffTrainer;
import gr.auth.ee.lcs.LCSTrainTemplate;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.classifiers.FixedSizeSetWorstFitnessDeletion;
import gr.auth.ee.lcs.data.ClassifierTransformBridge;
import gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy;
import gr.auth.ee.lcs.data.representations.ComplexRepresentation;
import gr.auth.ee.lcs.data.representations.UnilabelRepresentation;
import gr.auth.ee.lcs.data.updateAlgorithms.ASLCSUpdateAlgorithm;
import gr.auth.ee.lcs.data.updateAlgorithms.UCSUpdateAlgorithm;
import gr.auth.ee.lcs.geneticalgorithm.IGeneticAlgorithmStrategy;
import gr.auth.ee.lcs.geneticalgorithm.algorithms.SteadyStateGeneticAlgorithm;
import gr.auth.ee.lcs.geneticalgorithm.operators.SinglePointCrossover;
import gr.auth.ee.lcs.geneticalgorithm.operators.UniformBitMutation;
import gr.auth.ee.lcs.geneticalgorithm.selectors.TournamentSelector;

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
		LCSTrainTemplate myExample = new LCSTrainTemplate();
		IGeneticAlgorithmStrategy ga = new SteadyStateGeneticAlgorithm(
				new TournamentSelector(
						10,
						true,
						UpdateAlgorithmFactoryAndStrategy.COMPARISON_MODE_EXPLORATION),
				new SinglePointCrossover(), (float) .8, new UniformBitMutation(
						.04), 50);

		String filename = "/home/miltiadis/Desktop/position9.arff";
		ComplexRepresentation rep = new UnilabelRepresentation(filename, 7);
		ClassifierTransformBridge.setInstance(rep);

		UpdateAlgorithmFactoryAndStrategy.currentStrategy = new ASLCSUpdateAlgorithm(
				10, .99, 10, .01, ga);
		// UpdateAlgorithmFactoryAndStrategy.currentStrategy = new
		// UCSUpdateAlgorithm(
		// .1, 10, .99, .1, 50, 0.01, ga);
		// UpdateAlgorithmFactoryAndStrategy.currentStrategy=new
		// XCSUpdateAlgorithm(.2,10,.01,.1,3);

		ClassifierSet rulePopulation = new ClassifierSet(
				new FixedSizeSetWorstFitnessDeletion(
						1000,
						new TournamentSelector(
								40,
								false,
								UpdateAlgorithmFactoryAndStrategy.COMPARISON_MODE_DELETION)));
		// ClassifierSet rulePopulation=new ClassifierSet(new
		// FixedSizeSetWorstFitnessDeletion(
		// 1000,new
		// BestClassifierSelector(false,UpdateAlgorithmFactoryAndStrategy.COMPARISON_MODE_DELETION)));

		// ClassifierSet rulePopulation = ClassifierSet.openClassifierSet("set",
		// new FixedSizeSetWorstFitnessDeletion(
		// 600,new
		// TournamentSelector(50,false,UpdateAlgorithmFactoryAndStrategy.COMPARISON_MODE_DELETION)));
		ArffTrainer trainer = new ArffTrainer();
		trainer.loadInstances(filename);
		trainer.train(myExample, 1000, rulePopulation);

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
		// rulePopulation.postProcessThreshold(4, (float)0);
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

		trainer.selfEvaluate(rulePopulation);
		// trainer.evaluateOnTest(rulePopulation);

	}

}
