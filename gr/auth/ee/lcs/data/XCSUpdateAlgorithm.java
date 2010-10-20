package gr.auth.ee.lcs.data;

import gr.auth.ee.lcs.classifiers.ClassifierSet;

/** 
 *  The XCS update algorithm
 */
public class XCSUpdateAlgorithm implements IUpdateAlgorithmFactoryAndStrategy {

  /** 
   *  the initial parameters of the data object
   */
  public static XCSClassifierData initialParameters;

  @Override
  public Object createStateClassifierObject() {
	return (Object)new XCSClassifierData();
  }

  @Override
  public void updateSet(ClassifierSet setA, ClassifierSet setB) {
	// TODO Auto-generated method stub
	
  }

}