package gr.auth.ee.lcs;

import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.classifiers.DummySizeControlStrategy;
import gr.auth.ee.lcs.classifiers.Macroclassifier;
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
		  
		  /*
		   * Generate match and correct set
		   */
		  ClassifierSet matchSet=new ClassifierSet(null);
		  ClassifierSet correctSet=new ClassifierSet(null);
		  
		  //TODO: Parallelize for performance increase
		  for (int i=0;i<population.getNumberOfMacroclassifiers();i++){
			  if ( population.getClassifier(i).isMatch(dataInstanceIndex)){
				  Macroclassifier cl = population.getMacroclassifier(i);
				  int numerosity = cl.numerosity;
				  //Generate MatchSet
				  matchSet.addClassifier(cl ,false);
				  
				  //Generate Correct Set
				  if (cl.myClassifier.getActionAdvocated()==expectedAction)
					  correctSet.addClassifier(cl,false);		  
			  }
		  }
		  
 
		  /**
		   * Cover if necessary
		   */
		  if (correctSet.getNumberOfMacroclassifiers()==0){
			  Classifier coveringClassifier= ClassifierTransformBridge.instance.createRandomCoveringClassifier(
					  ClassifierTransformBridge.instances[dataInstanceIndex],expectedAction);
			  
			  population.addClassifier(new Macroclassifier(coveringClassifier, 1),false);
			  UpdateAlgorithmFactoryAndStrategy.updateData(matchSet,correctSet);
			  return;
		  }
		  
		  /*
		   * Evolve Population
		   */
		  UpdateAlgorithmFactoryAndStrategy.updateData(matchSet,correctSet);
		  if (Math.random()<matchSetRunProbability)
		  	ga.evolveSet(matchSet, population);
		  else
			ga.evolveSet(correctSet, population);
		  
		  
	  }

}