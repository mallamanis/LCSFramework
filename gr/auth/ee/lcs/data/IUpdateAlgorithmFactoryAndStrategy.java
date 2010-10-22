package gr.auth.ee.lcs.data;

import gr.auth.ee.lcs.classifiers.ClassifierSet;

/** 
 *  An interface for representing different update strategies, depending on the LCS algorithm
 *  @author Miltos Allamanis
 */
public interface IUpdateAlgorithmFactoryAndStrategy {

  /** 
   *  Creates a data object for a classifier
   */
  public Object createStateClassifierObject();

  /** 
   *  updates classifiers of a setA taking into consideration setB
   *  @param setA The first set to take into consideration during update
   *  @param setB The second set to take into consideration during update
   */
  public void updateSet(ClassifierSet setA, ClassifierSet setB);

}