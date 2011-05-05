/**
 * 
 */
package gr.auth.ee.lcs.tests;

import gr.auth.ee.lcs.LCSTrainTemplate;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.classifiers.populationcontrol.FixedSizeSetWorstFitnessDeletion;
import gr.auth.ee.lcs.data.AbstractUpdateAlgorithmStrategy;
import gr.auth.ee.lcs.data.ClassifierTransformBridge;
import gr.auth.ee.lcs.data.representations.SimpleBooleanRepresentation;
import gr.auth.ee.lcs.data.updateAlgorithms.ASLCSUpdateAlgorithm;
import gr.auth.ee.lcs.geneticalgorithm.IGeneticAlgorithmStrategy;
import gr.auth.ee.lcs.geneticalgorithm.algorithms.SteadyStateGeneticAlgorithm;
import gr.auth.ee.lcs.geneticalgorithm.operators.SinglePointCrossover;
import gr.auth.ee.lcs.geneticalgorithm.operators.UniformBitMutation;
import gr.auth.ee.lcs.geneticalgorithm.selectors.TournamentSelector;

/**
 * @author Miltos Allamanis
 * @deprecated
 */
public class SimpleBoolTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LCSTrainTemplate myExample = new LCSTrainTemplate(10);
		IGeneticAlgorithmStrategy ga = new SteadyStateGeneticAlgorithm(
				new TournamentSelector(
						10,
						true,
						AbstractUpdateAlgorithmStrategy.COMPARISON_MODE_EXPLORATION),
				new SinglePointCrossover(), 1, new UniformBitMutation(.04), 15);

		ClassifierTransformBridge.setInstance(new SimpleBooleanRepresentation(
				.33, 3));
		AbstractUpdateAlgorithmStrategy.currentStrategy = new ASLCSUpdateAlgorithm(
				5, .99, 50, .01, ga);
		// UpdateAlgorithmFactoryAndStrategy.currentStrategy=new
		// XCSUpdateAlgorithm(.2,10,.01,.1,3);

		ClassifierSet rulePopulation = new ClassifierSet(
				new FixedSizeSetWorstFitnessDeletion(
						300,
						new TournamentSelector(
								50,
								false,
								AbstractUpdateAlgorithmStrategy.COMPARISON_MODE_DELETION)));

		ClassifierTransformBridge.instances = new double[8][4];
		fillInstance();

		for (int rep = 0; rep < 8000; rep++) {// Iterate
			for (int a = 0; a < 2; a++)
				for (int b = 0; b < 2; b++)
					for (int c = 0; c < 2; c++) {

						myExample.trainWithInstance(rulePopulation, 4 * a + 2
								* b + c);

						System.out.println("Iteration " + rep + "Trained:" + a
								+ b + c);
						for (int i = 0; i < rulePopulation
								.getNumberOfMacroclassifiers(); i++) {
							System.out
									.println(rulePopulation.getClassifier(i)
											.toString()
											+ " fit:"
											+ rulePopulation
													.getClassifier(i)
													.getComparisonValue(
															AbstractUpdateAlgorithmStrategy.COMPARISON_MODE_EXPLOITATION)
											+ " exp:"
											+ rulePopulation.getClassifier(i).experience
											+ " num:"
											+ rulePopulation
													.getClassifierNumerosity(i));
							// System.out.println("Predicted Payoff: "+((XCSClassifierData)(rulePopulation.getClassifier(i).updateData)).predictedPayOff);
						}
					}
		}

		ClassifierSet.saveClassifierSet(rulePopulation, "set");

	}

	private static void fillInstance() {
		ClassifierTransformBridge.instances[0][0] = 0;
		ClassifierTransformBridge.instances[0][1] = 0;
		ClassifierTransformBridge.instances[0][2] = 0;
		ClassifierTransformBridge.instances[0][3] = 0;

		ClassifierTransformBridge.instances[1][0] = 1;
		ClassifierTransformBridge.instances[1][1] = 0;
		ClassifierTransformBridge.instances[1][2] = 0;
		ClassifierTransformBridge.instances[1][3] = 1;

		ClassifierTransformBridge.instances[2][0] = 0;
		ClassifierTransformBridge.instances[2][1] = 1;
		ClassifierTransformBridge.instances[2][2] = 0;
		ClassifierTransformBridge.instances[2][3] = 2;

		ClassifierTransformBridge.instances[3][0] = 1;
		ClassifierTransformBridge.instances[3][1] = 1;
		ClassifierTransformBridge.instances[3][2] = 0;
		ClassifierTransformBridge.instances[3][3] = 2;

		ClassifierTransformBridge.instances[4][0] = 0;
		ClassifierTransformBridge.instances[4][1] = 0;
		ClassifierTransformBridge.instances[4][2] = 1;
		ClassifierTransformBridge.instances[4][3] = 3;

		ClassifierTransformBridge.instances[5][0] = 1;
		ClassifierTransformBridge.instances[5][1] = 0;
		ClassifierTransformBridge.instances[5][2] = 1;
		ClassifierTransformBridge.instances[5][3] = 3;

		ClassifierTransformBridge.instances[6][0] = 0;
		ClassifierTransformBridge.instances[6][1] = 1;
		ClassifierTransformBridge.instances[6][2] = 1;
		ClassifierTransformBridge.instances[6][3] = 3;

		ClassifierTransformBridge.instances[7][0] = 1;
		ClassifierTransformBridge.instances[7][1] = 1;
		ClassifierTransformBridge.instances[7][2] = 1;
		ClassifierTransformBridge.instances[7][3] = 3;

	}

}
