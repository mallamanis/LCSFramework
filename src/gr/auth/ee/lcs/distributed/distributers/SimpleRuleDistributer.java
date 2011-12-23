/**
 * 
 */
package gr.auth.ee.lcs.distributed.distributers;

import gr.auth.ee.lcs.AbstractLearningClassifierSystem;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.distributed.AbstractRuleDistributer;
import gr.auth.ee.lcs.distributed.IRuleRouter;
import gr.auth.ee.lcs.geneticalgorithm.IRuleSelector;

/**
 * A simple distributer, adding and sending rules with no filtering
 * 
 * @author Miltiadis Allamanis
 * 
 */
public class SimpleRuleDistributer extends AbstractRuleDistributer {

	/**
	 * A simple selector that selects all the rules
	 */
	static IRuleSelector selector = new IRuleSelector() {
		@Override
		public void select(int howManyToSelect, ClassifierSet fromPopulation,
				ClassifierSet toPopulation) {
			toPopulation.merge(fromPopulation);
		}

	};

	/**
	 * Constructor.
	 * 
	 * @param router
	 *            the router
	 * @param lcs
	 *            the LCS
	 */
	public SimpleRuleDistributer(IRuleRouter router,
			AbstractLearningClassifierSystem lcs) {
		super(router, lcs, selector, selector);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gr.auth.ee.lcs.data.ILCSMetric#getMetricName()
	 */
	@Override
	public String getMetricName() {
		return "SimpleRuleDistributor";
	}

}
