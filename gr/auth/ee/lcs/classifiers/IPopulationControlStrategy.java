package gr.auth.ee.lcs.classifiers;

/**
 * An interface for a strategy on controlling classifiers from the set.
 * 
 * @stereotype Strategy
 * 
 * @author Miltos Allamanis
 */
public interface IPopulationControlStrategy {

	/**
	 * The abstract function that is used to control the population.
	 * 
	 * @param aSet
	 *            the set to control
	 */
	void controlPopulation(ClassifierSet aSet);

}