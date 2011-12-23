/**
 * 
 */
package gr.auth.ee.lcs.distributed.routers;

import java.util.Vector;

import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.distributed.AbstractRuleDistributer;
import gr.auth.ee.lcs.distributed.IRuleRouter;
import gr.auth.ee.lcs.distributed.IRuleSocket;

/**
 * @author Miltiadis Allamanis
 * 
 */
public class All2AllRouter implements IRuleRouter {

	/**
	 * The router's socket.
	 */
	private IRuleSocket mSocket;

	/**
	 * The rule's distributer being used
	 */
	final private AbstractRuleDistributer mDistributer;

	/**
	 * A vector containing the addresses of all lcs peers.
	 */
	private Vector<String> mPeers = new Vector<String>();

	/**
	 * Constructor.
	 * 
	 * @param socket
	 */
	public All2AllRouter(AbstractRuleDistributer distributer, IRuleSocket socket) {
		mSocket = socket;
		mDistributer = distributer;
	}

	public Vector<String> getPeers() {
		return mPeers;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.distributed.IRuleRouter#sendRules(gr.auth.ee.lcs.classifiers
	 * .ClassifierSet)
	 */
	@Override
	public void sendRules(ClassifierSet sendRules) {
		for (String peer : mPeers)
			mSocket.sendRules(sendRules, peer);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.distributed.IRuleRouter#setRouterSocket(gr.auth.ee.lcs
	 * .distributed.IRuleSocket)
	 */
	@Override
	public void setRouterSocket(IRuleSocket socket) {
		mSocket = socket;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gr.auth.ee.lcs.distributed.IRuleRouter#receiveRules(gr.auth.ee.lcs.
	 * classifiers.ClassifierSet, java.lang.Object)
	 */
	@Override
	public void receiveRules(ClassifierSet rules, Object metadata) {
		mDistributer.receiveRules(rules);
	}

}
