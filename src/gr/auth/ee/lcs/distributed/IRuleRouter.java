/**
 * 
 */
package gr.auth.ee.lcs.distributed;

import gr.auth.ee.lcs.classifiers.ClassifierSet;

/**
 * A rule router interface. Receives rules from the LCS and sends them
 * accordingly.
 * 
 * @author Miltiadis Allamanis
 * 
 */
public interface IRuleRouter {

	/**
	 * Send rules according to the implementation routing plan.
	 * 
	 * @param sendRules
	 *            the rules to send
	 */
	public void sendRules(ClassifierSet sendRules);

	/**
	 * Set the router's socket
	 * 
	 * @param socket
	 */
	public void setRouterSocket(IRuleSocket socket);

	/**
	 * Receive rules (from local socket).
	 * 
	 * @param rules
	 *            the rules just received
	 * @param metadata
	 *            any meta-data
	 */
	public void receiveRules(ClassifierSet rules, Object metadata);
}
