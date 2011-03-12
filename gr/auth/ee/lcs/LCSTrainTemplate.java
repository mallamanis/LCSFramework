package gr.auth.ee.lcs;

import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.classifiers.Macroclassifier;
import gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy;

/**
 * This is a template algorithm for training LCSs.
 * 
 * @author Miltos Allamanis
 * 
 */
public class LCSTrainTemplate {

	public void trainWithDataSet(double[][] dataSet) {
	}

	public void trainWithInstance(ClassifierSet population,
			int dataInstanceIndex) {

		ClassifierSet matchSet = population.generateMatchSet(dataInstanceIndex);
		
		UpdateAlgorithmFactoryAndStrategy.updateData(population, matchSet,
				dataInstanceIndex);

	}

}