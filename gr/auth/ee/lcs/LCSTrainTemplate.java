package gr.auth.ee.lcs;

import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.classifiers.DummySizeControlStrategy;
import gr.auth.ee.lcs.data.ClassifierTransformBridge;
import gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy;
import gr.auth.ee.lcs.geneticalgorithm.IGeneticAlgorithmStrategy;

/**
 * This is a template algorithm for training LCSs
 * @author Miltos Allamanis
 *
 */
public class LCSTrainTemplate {

	public IGeneticAlgorithmStrategy ga;
	
	  public void trainWithDataSet(double[][] dataSet) {
	  }

	  /**
	   * A double indicating the probability that the GA will run on the matchSet 
	   * (and not on the correct set)
	   */
	  private double matchSetRunProbability;

	  /**
	   * Constructor
	   * @param gaMatchSetRunProbability
	   */
	  public LCSTrainTemplate(double gaMatchSetRunProbability){
		  matchSetRunProbability=gaMatchSetRunProbability;
	  }
	  
	  public void trainWithInstance(ClassifierSet population, int dataInstanceIndex,int expectedAction) {
		  //Generate MatchSet
		  ClassifierSet matchSet=new ClassifierSet(new DummySizeControlStrategy());
		  for (int i=0;i<population.getNumberOfMacroclassifiers();i++){
			  if ( population.getClassifier(i).isMatch(dataInstanceIndex)){
				  matchSet.addClassifier(population.getClassifier(i), population.getClassifierNumerosity(i));
			  }
		  }
		  
		  //Generate Correct Set
		  ClassifierSet correctSet=new ClassifierSet(new DummySizeControlStrategy());
		  for (int i=0;i<matchSet.getNumberOfMacroclassifiers();i++){
			  if (matchSet.getClassifier(i).actionAdvocated==expectedAction)
				  correctSet.addClassifier(matchSet.getClassifier(i), matchSet.getClassifierNumerosity(i));		  
		  }
		  
		  if (correctSet.getNumberOfMacroclassifiers()==0){ //Cover
			  Classifier coveringClassifier= ClassifierTransformBridge.instance.createRandomCoveringClassifier(
					  ClassifierTransformBridge.instances[dataInstanceIndex],expectedAction);
			  
			  population.addClassifier(coveringClassifier, 1);
			  UpdateAlgorithmFactoryAndStrategy.updateData(matchSet,correctSet);
			  return;
		  }
		  
		  UpdateAlgorithmFactoryAndStrategy.updateData(matchSet,correctSet);
		  if (Math.random()<matchSetRunProbability)
		  	ga.evolveSet(matchSet, population);
		  else
			ga.evolveSet(correctSet, population);
		  
		  
	  }

}