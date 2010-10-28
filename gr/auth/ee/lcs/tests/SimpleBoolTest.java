/**
 * 
 */
package gr.auth.ee.lcs.tests;

import gr.auth.ee.lcs.LCSTrainTemplate;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.classifiers.OverpopulationInverseFitnessProportionateDeletion;
import gr.auth.ee.lcs.data.ASLCSUpdateAlgorithm;
import gr.auth.ee.lcs.data.ClassifierTransformBridge;
import gr.auth.ee.lcs.data.SSLCSUpdateAlgorithm;
import gr.auth.ee.lcs.data.SimpleBooleanRepresentation;
import gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy;
import gr.auth.ee.lcs.geneticalgorithm.NichedSteadyStateFitnessProportionalGeneticAlgorithm;
import gr.auth.ee.lcs.geneticalgorithm.SinglePointCrossover;
import gr.auth.ee.lcs.geneticalgorithm.UniformBitMutation;
import gr.auth.ee.lcs.geneticalgorithm.WeightedRouletteSelector;

/**
 * @author miltiadis
 *
 */
public class SimpleBoolTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LCSTrainTemplate myExample=new LCSTrainTemplate();
		myExample.ga=new NichedSteadyStateFitnessProportionalGeneticAlgorithm(
				new WeightedRouletteSelector(),
				new SinglePointCrossover(),
				new UniformBitMutation(.1));
		
		ClassifierTransformBridge.instance=new SimpleBooleanRepresentation(.9,3);
		UpdateAlgorithmFactoryAndStrategy.currentStrategy=new SSLCSUpdateAlgorithm(5,.05);
		
		ClassifierSet rulePopulation=new ClassifierSet(new OverpopulationInverseFitnessProportionateDeletion(30));
		
		int trainSet[][][]=new int[2][2][2];
		fillSet(trainSet);
		for (int rep=0;rep<100000;rep++)//Repeat 1000times
			for (int a=0;a<2;a++)
				for (int b=0;b<2;b++)
					for (int c=0;c<2;c++){
						double visionVector[]=new double[3];
						visionVector[0]=c;
						visionVector[1]=b;
						visionVector[2]=a;
						myExample.trainWithInstance(rulePopulation, visionVector, trainSet[a][b][c]);
					}
		
		for (int i=0;i<rulePopulation.getNumberOfMacroclassifiers();i++){
			System.out.println(rulePopulation.getClassifier(i).toString()+" "+rulePopulation.getClassifier(i).fitness+" "+rulePopulation.getClassifier(i).experience);
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
