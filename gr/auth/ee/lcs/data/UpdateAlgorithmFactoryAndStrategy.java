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
  
  /**
   * The static strategy used throughout the algorithms. Works like a singleton
   */
  public static UpdateAlgorithmFactoryAndStrategy currentStrategy ;
  
  /**
   * Bridge to selected strategy
   * @return returns the data object as specified by the implementation
   */
  public static Object createDefaultDataObject(){
	  if( currentStrategy!=null)
		  return currentStrategy.createStateClassifierObject();
	  else
		  return null;
  }
  
  /**
   * A bridge between update data and the actual implementation
   * @param setA
   * @param setB
   */
  public static void updateData(ClassifierSet setA, ClassifierSet setB){
	  if( currentStrategy!=null){
		  currentStrategy.updateSet(setA, setB);
		 
	  }
  }
  
  
  public double subsumptionFitnessThreshold=.9;
  public int subsumptionExperienceThreshold=100;
  
  /**
   * Concrete implementation of the subsumption strength
   * @param aClassifier the classifier, whose subsumption ability is to be updated
   */
  protected void updateSubsumption(Classifier aClassifier){
	 aClassifier.canSubsume=aClassifier.fitness>subsumptionFitnessThreshold &&
	 							aClassifier.experience> subsumptionExperienceThreshold;
  }

}