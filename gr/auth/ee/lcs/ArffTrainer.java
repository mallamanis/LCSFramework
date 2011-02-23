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
	Instances testSet;
	
	public void loadInstances(String filename) throws IOException{
		//Open .arff
		FileReader reader = new FileReader(filename);
		Instances set = new Instances(reader);
		if (set.classIndex()<0)
			set.setClassIndex(set.numAttributes() - 1);
		
		set.stratify(10);
		
		Instances trainSet = set;//set.trainCV(10, 9);
		//testSet = set.testCV(10, 9);
		
		ClassifierTransformBridge.instances=new double[trainSet.numInstances()][trainSet.numAttributes()-1];
		advocatedActions=new int[trainSet.numInstances()];
		
		//Load instances
		for (int i=0;i<trainSet.numInstances();i++){
			for (int j=0;j<trainSet.numAttributes()-1;j++){
				ClassifierTransformBridge.instances[i][j]=trainSet.instance(i).value(j);
			}
			advocatedActions[i]=(int)trainSet.instance(i).value(trainSet.classIndex());
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
		//int confusionMatrix[][]=new int[][];
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
	
	public void evaluateOnTest(ClassifierSet population){
		LCSExploitTemplate eval=new LCSExploitTemplate();
		int tp=0,fp=0;
		
		for (int i=0;i<testSet.numInstances();i++){
			double[] instance =  new double[testSet.numAttributes()-1];
			int advocatedAction = (int)testSet.instance(i).classValue();
			for (int j=0;j<testSet.numAttributes()-1;j++){
				instance[j]=testSet.instance(i).value(j);
			}
			if (eval.classifiy(instance, population)==advocatedActions[i])
				tp++;
			else if (eval.classifiy(instance, population)!=-1)
				fp++;
			
		}
		
		double errorRate=((double)fp)/((double)(fp+tp));
		System.out.println("tp:"+tp+" fp:"+fp+" errorRate:"+errorRate+" total instances:"+testSet.numInstances());
		
		
	}
	
	


}
