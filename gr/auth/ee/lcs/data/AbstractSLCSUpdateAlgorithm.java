package gr.auth.ee.lcs.data;

import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.classifiers.Classifier;

/** 
 *  An abstract *S-LCS update algorithm as described in Tzima-Mitkas paper
 */
public abstract class AbstractSLCSUpdateAlgorithm implements IUpdateAlgorithmFactoryAndStrategy {

  /** 
   *  The abstract function used to calculate the fitness of a classifier
   */
  public abstract void updateFitness(Classifier aClassifier, ClassifierSet correctSet);

  @Override
  public Object createStateClassifierObject() {
	return new GenericSLCSClassifierData();
  }

  @Override
  public void updateSet(ClassifierSet setA, ClassifierSet setB) {
	// TODO Auto-generated method stub
	
  }

}