package gr.auth.ee.lcs.classifiers;

public interface ISizeControlStrategy {

  /** 
   *  An interface for a strategy on deleting classifiers from the set
   */

  /** 
   *  the abstract function that is used to control the size of the population
   */
  public void controlSize(ClassifierSet aSet);

}