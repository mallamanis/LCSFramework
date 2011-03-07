package gr.auth.ee.lcs;

import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.classifiers.Macroclassifier;
import gr.auth.ee.lcs.data.ClassifierTransformBridge;
import gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy;
import gr.auth.ee.lcs.geneticalgorithm.IGeneticAlgorithmStrategy;

/**
 * This is a template algorithm for training LCSs
 * 
 * @author Miltos Allamanis
 * 
 */
public class LCSTrainTemplate {

	public void trainWithDataSet(double[][] dataSet) {
	}

	public void trainWithInstance(ClassifierSet population,
			int dataInstanceIndex) {

		/*
		 * Generate match set
		 */
		ClassifierSet matchSet = new ClassifierSet(null);

		// TODO: Parallelize for performance increase
		final int populationSize = population.getNumberOfMacroclassifiers();
		for (int i = 0; i < populationSize; i++) {
			if (population.getClassifier(i).isMatch(dataInstanceIndex)) {
				Macroclassifier cl = population.getMacroclassifier(i);
				// Generate MatchSet
				matchSet.addClassifier(cl, false);
			}
		}

		UpdateAlgorithmFactoryAndStrategy.updateData(population, matchSet,
				dataInstanceIndex);

	}

}