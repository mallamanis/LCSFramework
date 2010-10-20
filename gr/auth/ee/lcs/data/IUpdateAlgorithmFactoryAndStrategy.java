package gr.auth.ee.lcs.data;

import gr.auth.ee.lcs.classifiers.ClassifierSet;

/** 
 *  An interface for representing different update strategies, depending on the LCS algorithm
 */
public interface IUpdateAlgorithmFactoryAndStrategy {

  /** 
   *  Creates a data object for a classifier
   */
  public Object createStateClassifierObject();

  /** 
   *  updates classifiers of a setA taking into consideration setB
   */
  public void updateSet(ClassifierSet setA, ClassifierSet setB);

}