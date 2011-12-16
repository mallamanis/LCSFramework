/**
 * 
 */
package gr.auth.ee.lcs.distributed;

import gr.auth.ee.lcs.AbstractLearningClassifierSystem;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.data.ILCSMetric;
import gr.auth.ee.lcs.geneticalgorithm.IRuleSelector;

/**
 * An abstract class of an LCS callback that can be used for distributing rules in a distributed LCS environment.
 * Essentially this is a strategy for adding received rules.
 * @author Miltiadis Allamanis
 *
 */
public abstract class AbstractRuleDistributer implements ILCSMetric {
	
	/**
	 * The LCS used by this distributer.
	 */
	AbstractLearningClassifierSystem mLCS;
	
	/**
	 * A rule selector, to select the rules to be added into the LCS population.
	 */
	IRuleSelector receiveSelector;
	
	/**
	 * A rule selector, to select the rules to be send from the LCS.
	 */
	IRuleSelector sendSelector;
	
	/**
	 * The local router interface.
	 */
	IRuleRouter localRouter;
	
	/**
	 * Receive rules from other LCSs
	 * @param ruleSet the a set of rules being received
	 * @param metaData any metadata on these rules (e.g. sender)
	 */
	void receiveRules(ClassifierSet ruleSet){
		receiveSelector.select(-1, ruleSet, mLCS.getRulePopulation());
	}
	
	@Override
	public double getMetric(AbstractLearningClassifierSystem lcs){
		sendRules();
		return 0;
	}
	
	/**
	 * Send rules to the local router.
	 */
	public void sendRules(){
		ClassifierSet outRules = new ClassifierSet(null);
		sendSelector.select(-1, mLCS.getRulePopulation(), outRules);
		localRouter.sendRules(outRules);
	}
		

}
