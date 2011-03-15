package gr.auth.ee.lcs;

import java.util.Vector;

import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.data.ClassifierTransformBridge;
import gr.auth.ee.lcs.data.IEvaluator;
import gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy;

/**
 * This is a template algorithm for training LCSs.
 * 
 * @author Miltos Allamanis
 * 
 */
public class LCSTrainTemplate {

	private Vector<IEvaluator> hooks;
	
	private final int hookCallbackFrequency; 
	
	public LCSTrainTemplate(int callbackFrequency){
		hooks = new Vector<IEvaluator>();
		hookCallbackFrequency = callbackFrequency;
	}
	
	public void registerHook(IEvaluator evaluator) {
		hooks.add(evaluator);
	}
	
	public boolean unregisterEvaluator(IEvaluator evaluator) {
		return hooks.remove(evaluator);
	}
	
	public void executeCallbacks(){
		//TODO: Implement
	}
	
	public void trainWithInstance(ClassifierSet population,
			int dataInstanceIndex) {

		ClassifierSet matchSet = population.generateMatchSet(dataInstanceIndex);

		UpdateAlgorithmFactoryAndStrategy.updateData(population, matchSet,
				dataInstanceIndex);

	}
	
	public void train(int iterations,
			ClassifierSet population) {
		int numInstances = ClassifierTransformBridge.instances.length;
		for (int repetition = 0; repetition < iterations; repetition++) {
			for (int j=0; j < hookCallbackFrequency ; j++){
				System.out.println("Iteration " + repetition);
				for (int i = 0; i < numInstances; i++)
					trainWithInstance(population, i);
				repetition++;
			}			
			executeCallbacks();
		}
		
	}

}