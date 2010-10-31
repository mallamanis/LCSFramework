/**
 * 
 */
package gr.auth.ee.lcs.tests;

import gr.auth.ee.lcs.LCSTrainTemplate;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.classifiers.DummySizeControlStrategy;
import gr.auth.ee.lcs.classifiers.FixedSizeSetWorstFitnessTournamentDeletion;
import gr.auth.ee.lcs.classifiers.OverpopulationInverseFitnessProportionateDeletion;
import gr.auth.ee.lcs.data.ClassifierTransformBridge;
import gr.auth.ee.lcs.data.ASLCSUpdateAlgorithm;
import gr.auth.ee.lcs.data.SSLCSUpdateAlgorithm;
import gr.auth.ee.lcs.data.SimpleBooleanRepresentation;
import gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy;
import gr.auth.ee.lcs.geneticalgorithm.BestClassifierSelector;
import gr.auth.ee.lcs.geneticalgorithm.BestFitnessTournamentSelector;
import gr.auth.ee.lcs.geneticalgorithm.NichedSteadyStateFitnessProportionalGeneticAlgorithm;
import gr.auth.ee.lcs.geneticalgorithm.SinglePointCrossover;
import gr.auth.ee.lcs.geneticalgorithm.UniformBitMutation;
import gr.auth.ee.lcs.geneticalgorithm.WeightedRouletteSelector;

/**
 * @author Miltos Allamanis
 *
 */
public class SimpleBoolTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LCSTrainTemplate myExample=new LCSTrainTemplate();
		myExample.ga=new NichedSteadyStateFitnessProportionalGeneticAlgorithm(
				new BestFitnessTournamentSelector(10,true),
				new SinglePointCrossover(),
				new UniformBitMutation(.04),15);
		
		ClassifierTransformBridge.instance=new SimpleBooleanRepresentation(.33,3);
		UpdateAlgorithmFactoryAndStrategy.currentStrategy=new ASLCSUpdateAlgorithm(5);
		
		ClassifierSet rulePopulation=new ClassifierSet(new FixedSizeSetWorstFitnessTournamentDeletion(27,new BestClassifierSelector(false)));
		
		int trainSet[][][]=new int[2][2][2];
		fillSet(trainSet);
		for (int rep=0;rep<1000;rep++){//Repeat 100times
			for (int a=0;a<2;a++)
				for (int b=0;b<2;b++)
					for (int c=0;c<2;c++){
						double visionVector[]=new double[3];
						visionVector[0]=c;
						visionVector[1]=b;
						visionVector[2]=a;
						myExample.trainWithInstance(rulePopulation, visionVector, trainSet[a][b][c]);
					
			
						System.out.println("Iteration "+rep + "Trained:"+a+b+c);
						for (int i=0;i<rulePopulation.getNumberOfMacroclassifiers();i++){
							System.out.println(rulePopulation.getClassifier(i).toString()+" fit:"+rulePopulation.getClassifier(i).fitness+" exp:"+rulePopulation.getClassifier(i).experience+" num:"+rulePopulation.getClassifierNumerosity(i));
						}
					}
		}
		
		
		
	}
	
	private static void fillSet(int trainSet[][][]){
		//Fill with (aANDb)OR(aXORc)
		trainSet[0][0][0]=0;
		trainSet[0][0][1]=1;
		trainSet[0][1][0]=0;
		trainSet[0][1][1]=1;
		trainSet[1][0][0]=1;
		trainSet[1][0][1]=0;
		trainSet[1][1][0]=1;
		trainSet[1][1][1]=1;
	}

}
