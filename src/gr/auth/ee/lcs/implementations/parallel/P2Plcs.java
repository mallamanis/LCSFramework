/**
 * 
 */
package gr.auth.ee.lcs.implementations.parallel;

import gr.auth.ee.lcs.AbstractLearningClassifierSystem;
import gr.auth.ee.lcs.data.AbstractUpdateStrategy;
import gr.auth.ee.lcs.distributed.distributers.SimpleRuleDistributer;
import gr.auth.ee.lcs.distributed.routers.All2AllRouter;
import gr.auth.ee.lcs.distributed.sockets.LocalHubbedRuleSocket;
import gr.auth.ee.lcs.geneticalgorithm.selectors.RouletteWheelSelector;
import gr.auth.ee.lcs.geneticalgorithm.selectors.TournamentSelector;
import gr.auth.ee.lcs.utilities.SettingsLoader;
import weka.core.Instances;

/**
 * A all-to-all multi-lcs implementation.
 * 
 * @author Miltiadis Allamanis
 * 
 */
public class P2Plcs extends AbstractLearningClassifierSystem {

	/**
	 * The nodes of the lcs network.
	 */
	private final AbstractLearningClassifierSystem[] nodes;

	/**
	 * The common socket of the network
	 */
	private final LocalHubbedRuleSocket socket;

	/**
	 * The class name of the lcs instances
	 */
	private final String lcsClassName;

	public P2Plcs() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		nodes = new AbstractLearningClassifierSystem[(int) SettingsLoader.getNumericSetting("parallelNodes",
				3)];
		lcsClassName = SettingsLoader.getStringSetting("lcsNodeType", "");
		socket = new LocalHubbedRuleSocket();
		initNodes();
	}
	
	/**
	 * Constructor
	 * 
	 * @param numberOfNodes
	 *            the number of nodes to use
	 * @param lcsClass
	 *            the lcs class name
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	public P2Plcs(int numberOfNodes, String lcsClass)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		nodes = new AbstractLearningClassifierSystem[numberOfNodes];
		lcsClassName = lcsClass;
		socket = new LocalHubbedRuleSocket();
		initNodes();
	}

	/**
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	private void initNodes() throws InstantiationException,
			IllegalAccessException, ClassNotFoundException {
				
		for (int i = 0; i < nodes.length; i++) {
			final AbstractLearningClassifierSystem lcs = (AbstractLearningClassifierSystem) Class
					.forName(lcsClassName).newInstance();
			final SimpleRuleDistributer distributer = new SimpleRuleDistributer(
					null, lcs, new TournamentSelector(15,true,AbstractUpdateStrategy.COMPARISON_MODE_EXPLORATION)); //TODO: pass parameters
			final All2AllRouter router = new All2AllRouter(distributer, socket);
			distributer.setRouter(router);
			socket.setRuleRouter(router, "lcs"+i);
			
			//Set all peers
			for (int j = 0 ; j < nodes.length; j++)
				if (j!=i)
					router.getPeers().add("lcs"+j);
			lcs.registerHook(distributer);

			nodes[i] = lcs;

		}
	}

	@Override
	public int[] classifyInstance(double[] instance) {
		return nodes[0].classifyInstance(instance);
	}

	@Override
	public AbstractLearningClassifierSystem createNew() {

		try {
			return new P2Plcs(nodes.length, lcsClassName);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String[] getEvaluationNames() {
		return nodes[0].getEvaluationNames();
	}

	@Override
	public double[] getEvaluations(Instances testSet) {
		return nodes[0].getEvaluations(testSet);
	}

	@Override
	public void train() {
		Thread[] threads = new Thread[nodes.length];
		for (int i = 0; i < nodes.length; i++) {
			final int num = i;
			nodes[num].instances = this.instances; //TODO: Alternative training method with subsampling?
			threads[i] = new Thread() {
				@Override
				public void run() {
					nodes[num].train();
				}
			};
			threads[i].start();
		}

		for (int i = 0; i < threads.length; i++)
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

	}

}
