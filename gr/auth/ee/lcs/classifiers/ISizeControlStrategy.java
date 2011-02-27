package gr.auth.ee.lcs.classifiers;

/** 
 *  An interface for a strategy on deleting classifiers from the set.
 *  @author Miltos Allamanis
 */
public interface ISizeControlStrategy {

  /** 
   *  The abstract function that is used
   *  to control the size of the population.
   *  @param aSet the set to control
   */
  void controlSize(ClassifierSet aSet);

}