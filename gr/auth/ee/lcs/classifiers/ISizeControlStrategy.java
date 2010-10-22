package gr.auth.ee.lcs.classifiers;

/** 
 *  An interface for a strategy on deleting classifiers from the set
 *  @author Miltos Allamanis
 */
public interface ISizeControlStrategy {

  /** 
   *  the abstract function that is used to control the size of the population
   */
  public void controlSize(ClassifierSet aSet);

}