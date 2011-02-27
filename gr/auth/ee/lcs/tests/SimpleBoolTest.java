/**
 * 
 */
package gr.auth.ee.lcs.tests;

import gr.auth.ee.lcs.LCSTrainTemplate;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.classifiers.FixedSizeSetWorstFitnessDeletion;
import gr.auth.ee.lcs.data.ClassifierTransformBridge;
import gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy;
import gr.auth.ee.lcs.data.representations.SimpleBooleanRepresentation;
import gr.auth.ee.lcs.data.updateAlgorithms.ASLCSUpdateAlgorithm;
import gr.auth.ee.lcs.geneticalgorithm.algorithms.SteadyStateGeneticAlgorithm;
import gr.auth.ee.lcs.geneticalgorithm.operators.SinglePointCrossover;
import gr.auth.ee.lcs.geneticalgorithm.operators.UniformBitMutation;
import gr.auth.ee.lcs.geneticalgorithm.selectors.TournamentSelector;

/**
 * @author Miltos Allamanis
 * 
 */
public class SimpleBoolTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LCSTrainTemplate myExample = new LCSTrainTemplate(0);
		myExample.ga = new SteadyStateGeneticAlgorithm(new TournamentSelector(
				10, true,
				UpdateAlgorithmFactoryAndStrategy.COMPARISON_MODE_EXPLORATION),
				new SinglePointCrossover(), 1, new UniformBitMutation(.04), 15);

		ClassifierTransformBridge.instance = new SimpleBooleanRepresentation(
				.33, 3);
		UpdateAlgorithmFactoryAndStrategy.currentStrategy = new ASLCSUpdateAlgorithm(
				5);
		// UpdateAlgorithmFactoryAndStrategy.currentStrategy=new
		// XCSUpdateAlgorithm(.2,10,.01,.1,3);

		ClassifierSet rulePopulation = new ClassifierSet(
				new FixedSizeSetWorstFitnessDeletion(
						300,
						new TournamentSelector(
								50,
								false,
								UpdateAlgorithmFactoryAndStrategy.COMPARISON_MODE_DELETION)));

		int trainSet[][][] = new int[2][2][2];
		fillSet(trainSet);
		ClassifierTransformBridge.instances = new double[8][3];
		fillInstance();

		for (int rep = 0; rep < 8000; rep++) {// Iterate
			for (int a = 0; a < 2; a++)
				for (int b = 0; b < 2; b++)
					for (int c = 0; c < 2; c++) {

						myExample.trainWithInstance(rulePopulation, 4 * a + 2
								* b + c, trainSet[a][b][c]);

						System.out.println("Iteration " + rep + "Trained:" + a
								+ b + c);
						for (int i = 0; i < rulePopulation
								.getNumberOfMacroclassifiers(); i++) {
							System.out
									.println(rulePopulation.getClassifier(i)
											.toString()
											+ " fit:"
											+ rulePopulation.getClassifier(i).fitness
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

	private static void fillSet(int trainSet[][][]) {
		// Fill with (aANDb)OR(aXORc)
		trainSet[0][0][0] = 0;
		trainSet[0][0][1] = 1;
		trainSet[0][1][0] = 2;
		trainSet[0][1][1] = 2;
		trainSet[1][0][0] = 3;
		trainSet[1][0][1] = 3;
		trainSet[1][1][0] = 3;
		trainSet[1][1][1] = 3;
	}

	private static void fillInstance() {
		ClassifierTransformBridge.instances[0][0] = 0;
		ClassifierTransformBridge.instances[0][1] = 0;
		ClassifierTransformBridge.instances[0][2] = 0;

		ClassifierTransformBridge.instances[1][0] = 1;
		ClassifierTransformBridge.instances[1][1] = 0;
		ClassifierTransformBridge.instances[1][2] = 0;

		ClassifierTransformBridge.instances[2][0] = 0;
		ClassifierTransformBridge.instances[2][1] = 1;
		ClassifierTransformBridge.instances[2][2] = 0;

		ClassifierTransformBridge.instances[3][0] = 1;
		ClassifierTransformBridge.instances[3][1] = 1;
		ClassifierTransformBridge.instances[3][2] = 0;

		ClassifierTransformBridge.instances[4][0] = 0;
		ClassifierTransformBridge.instances[4][1] = 0;
		ClassifierTransformBridge.instances[4][2] = 1;

		ClassifierTransformBridge.instances[5][0] = 1;
		ClassifierTransformBridge.instances[5][1] = 0;
		ClassifierTransformBridge.instances[5][2] = 1;

		ClassifierTransformBridge.instances[6][0] = 0;
		ClassifierTransformBridge.instances[6][1] = 1;
		ClassifierTransformBridge.instances[6][2] = 1;

		ClassifierTransformBridge.instances[7][0] = 1;
		ClassifierTransformBridge.instances[7][1] = 1;
		ClassifierTransformBridge.instances[7][2] = 1;

	}

}
