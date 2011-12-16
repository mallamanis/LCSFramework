/**
 * 
 */
package gr.auth.ee.lcs.distributed.sockets;

import java.util.HashMap;

import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.distributed.IRuleRouter;
import gr.auth.ee.lcs.distributed.IRuleSocket;

/**
 * A common socket used to allow communications between threaded LCSs
 * 
 * @author Miltiadis Allamanis
 * 
 */
public class LocalHubbedRuleSocket implements IRuleSocket {

	/**
	 * A hash map containing the address->Router
	 */
	private HashMap<String, IRuleRouter> mAddressList = new HashMap<String, IRuleRouter>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see gr.auth.ee.lcs.distributed.IRuleSocket#setRuleRouter(gr.auth.ee.lcs.
	 * distributed.IRuleRouter)
	 */
	@Override
	public void setRuleRouter(IRuleRouter router, String address) {
		// Adds router to the common net
		if (!mAddressList.containsValue(router)) {
			mAddressList.put(address, router);

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.distributed.IRuleSocket#sendRules(gr.auth.ee.lcs.classifiers
	 * .ClassifierSet, java.lang.String)
	 */
	@Override
	public void sendRules(ClassifierSet rules, String address) {
		mAddressList.get(address).receiveRules(rules, null);
	}

}
