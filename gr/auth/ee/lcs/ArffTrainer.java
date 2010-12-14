/**
 * 
 */
package gr.auth.ee.lcs;

import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.data.XCSClassifierData;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import weka.core.Instances;

/**
 * @author Miltos Allamanis
 *
 */
public class ArffTrainer {

	private double[][] instancesValues;
	private int[] advocatedActions;
	
	public void loadInstances(String filename) throws IOException{
		//Open .arff
		FileReader reader = new FileReader(filename);
		Instances instances = new Instances(reader);
		if (instances.classIndex()<0)
			instances.setClassIndex(instances.numAttributes() - 1);
		
		instancesValues=new double[instances.numInstances()][instances.numAttributes()-1];
		advocatedActions=new int[instances.numInstances()];
		
		//Load instances
		for (int i=0;i<instances.numInstances();i++){
			for (int j=0;j<instances.numAttributes()-1;j++){
				instancesValues[i][j]=instances.get(i).value(j);
			}
			advocatedActions[i]=(int)instances.get(i).value(instances.classIndex());
		}
		
	}
	
	public void train(LCSTrainTemplate lcs,int iterations,ClassifierSet population){
		int numInstances=advocatedActions.length;
		for (int repetition=0;repetition<iterations;repetition++){//Iterate
			int i=repetition % numInstances;
			lcs.trainWithInstance(population, instancesValues[i], advocatedActions[i]);
			
	
				/*System.out.println("Iteration "+repetition + "Trained:"+instancesValues[i].toString());
				for (int l=0;l<population.getNumberOfMacroclassifiers();l++){
					System.out.println(population.getClassifier(l).toString()+" fit:"+population.getClassifier(l).fitness+" exp:"+population.getClassifier(l).experience+" num:"+population.getClassifierNumerosity(l));
					System.out.println("Predicted Payoff: "+((XCSClassifierData)(population.getClassifier(l).updateData)).predictedPayOff);
				}*/
		}	

	
	}


}
