package gr.auth.ee.lcs.classifiers;

/** 
 *  A dummy strategy that does not delete any classifiers whatsoever
 *  @author Miltos Allamanis
 */
public class DummySizeControlStrategy implements ISizeControlStrategy {

	/**
	 * Do nothing
	 */
  public void controlSize(ClassifierSet aSet) {
  }

}