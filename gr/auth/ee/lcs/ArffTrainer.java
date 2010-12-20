/**
 * 
 */
package gr.auth.ee.lcs;

import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.data.ClassifierTransformBridge;

import java.io.FileReader;
import java.io.IOException;

import weka.core.Instances;

/**
 * @author Miltos Allamanis
 *
 */
public class ArffTrainer {

	
	private int[] advocatedActions;
	
	public void loadInstances(String filename) throws IOException{
		//Open .arff
		FileReader reader = new FileReader(filename);
		Instances instances = new Instances(reader);
		if (instances.classIndex()<0)
			instances.setClassIndex(instances.numAttributes() - 1);
		
		ClassifierTransformBridge.instances=new double[instances.numInstances()][instances.numAttributes()-1];
		advocatedActions=new int[instances.numInstances()];
		
		//Load instances
		for (int i=0;i<instances.numInstances();i++){
			for (int j=0;j<instances.numAttributes()-1;j++){
				ClassifierTransformBridge.instances[i][j]=instances.get(i).value(j);
			}
			advocatedActions[i]=(int)instances.get(i).value(instances.classIndex());
		}
		
	}
	
	public void train(LCSTrainTemplate lcs,int iterations,ClassifierSet population){
		int numInstances=advocatedActions.length;
		for (int repetition=0;repetition<iterations;repetition++){//Iterate
			System.out.println("Iteration "+repetition);
			for (int i=0;i<numInstances;i++)
					lcs.trainWithInstance(population, i, advocatedActions[i]);
			//if (repetition % 10==0) population.selfSubsume();
		}	
		//population.selfSubsume();
	}
	
	public void selfEvaluate(ClassifierSet population){
		LCSExploitTemplate eval=new LCSExploitTemplate();
		int tp=0,fp=0;
		for (int i=0;i<ClassifierTransformBridge.instances.length;i++){ //for each instance
			if (eval.classifiy(ClassifierTransformBridge.instances[i], population)==advocatedActions[i])
				tp++;
			else if (eval.classifiy(ClassifierTransformBridge.instances[i], population)!=-1)
				fp++;
		}
		
		double errorRate=((double)fp)/((double)(fp+tp));
		System.out.println("tp:"+tp+" fp:"+fp+" errorRate:"+errorRate+" total instances:"+ClassifierTransformBridge.instances.length);
		
	}
	
	


}
