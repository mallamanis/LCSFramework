/**
 * 
 */
package gr.auth.ee.lcs.distributed;

import gr.auth.ee.lcs.classifiers.ClassifierSet;

/**
 * A interface of a rule socket. The interface aims to abstract tranportation
 * layer (TCP, Serialization, MPI)
 * 
 * @author Miltiadis Allamanis
 * 
 */
public interface IRuleSocket {

	/**
	 * Set the local router of the socket.
	 * 
	 * @param router
	 * @param address
	 *            the address of the router
	 */
	void setRuleRouter(IRuleRouter router, String address);

	/**
	 * Send rules to a specific address.
	 * 
	 * @param rules
	 *            the rules to send
	 * @param address
	 *            the address to send the rules to
	 */
	void sendRules(ClassifierSet rules, String address);
}
