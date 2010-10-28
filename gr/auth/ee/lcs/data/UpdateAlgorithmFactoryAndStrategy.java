package gr.auth.ee.lcs.data;

import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ClassifierSet;

/** 
 *  An interface for representing different update strategies, depending on the LCS algorithm
 *  @author Miltos Allamanis
 */
public abstract class  UpdateAlgorithmFactoryAndStrategy {

  /** 
   *  Creates a data object for a classifier
   */
  protected abstract Object createStateClassifierObject();

  /** 
   *  updates classifiers of a setA taking into consideration setB
   *  @param setA The first set to take into consideration during update
   *  @param setB The second set to take into consideration during update
   */
  protected abstract void updateSet(ClassifierSet setA, ClassifierSet setB);
  
  public static UpdateAlgorithmFactoryAndStrategy currentStrategy ;
  
  public static Object createDefaultDataObject(){
	  if( currentStrategy!=null)
		  return currentStrategy.createStateClassifierObject();
	  else
		  return null;
  }
  
  public static void updateData(ClassifierSet setA, ClassifierSet setB){
	  if( currentStrategy!=null){
		  currentStrategy.updateSet(setA, setB);
		 
	  }
  }
  
  public double subsumptionFitnessThreshold=1;
  public int subsumptionExperienceThreshold=100;
  
  protected void updateSubsumption(Classifier aClassifier){
	 aClassifier.canSubsume=aClassifier.fitness>subsumptionFitnessThreshold &&
	 							aClassifier.experience> subsumptionExperienceThreshold;
  }

}