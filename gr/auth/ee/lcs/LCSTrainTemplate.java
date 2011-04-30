package gr.auth.ee.lcs;

import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.classifiers.populationcontrol.InadequeteClassifierDeletionStrategy;
import gr.auth.ee.lcs.data.AbstractUpdateAlgorithmStrategy;
import gr.auth.ee.lcs.data.ClassifierTransformBridge;
import gr.auth.ee.lcs.data.IEvaluator;

import java.util.Vector;

/**
 * This is a template algorithm for training LCSs.
 * 
 * @author Miltos Allamanis
 * 
 */
public class LCSTrainTemplate {

	/**
	 * A vector of all evaluator hooks.
	 */
	private final Vector<IEvaluator> hooks;

	/**
	 * Frequency of the hook callback execution.
	 */
	private final int hookCallbackRate;

	/**
	 * Constructor.
	 * 
	 * @param callbackFrequency
	 *            the frequency at which to call the callbacks
	 */
	public LCSTrainTemplate(final int callbackFrequency) {
		hooks = new Vector<IEvaluator>();
		hookCallbackRate = callbackFrequency;
	}

	/**
	 * Register an evaluator to be called during training.
	 * 
	 * @param evaluator
	 *            the evaluator to register
	 * @return true if the evaluator has been registered successfully
	 */
	public final boolean registerHook(final IEvaluator evaluator) {
		return hooks.add(evaluator);
	}

	/**
	 * Train a classifier set with all train instances.
	 * 
	 * @param iterations
	 *            the number of full iterations (one iteration the LCS is
	 *            trained with all instances) to train the LCS
	 * @param population
	 *            the population of the classifiers to train.
	 */
	public final void train(final int iterations, final ClassifierSet population) {

		final int numInstances = ClassifierTransformBridge.instances.length;
		final InadequeteClassifierDeletionStrategy del = new InadequeteClassifierDeletionStrategy(
				0);

		int repetition = 0;
		while (repetition < iterations) {
			for (int j = 0; j < hookCallbackRate; j++) {
				System.out.print(".");

				for (int i = 0; i < numInstances; i++) {
					trainWithInstance(population, i);
				}
				repetition++;
				del.controlPopulation(population);
			}
			executeCallbacks(population);

		}

	}

	/**
	 * Train with instance main template. Trains the classifier set with a
	 * single instance.
	 * 
	 * @param population
	 *            the classifier's popoulation
	 * @param dataInstanceIndex
	 *            the index of the training data instance
	 */
	public final void trainWithInstance(final ClassifierSet population,
			final int dataInstanceIndex) {

		final ClassifierSet matchSet = population
				.generateMatchSet(dataInstanceIndex);

		AbstractUpdateAlgorithmStrategy.updateData(population, matchSet,
				dataInstanceIndex, true);

	}

	/**
	 * Unregister an evaluator.
	 * 
	 * @param evaluator
	 *            the evaluator to register
	 * @return true if the evaluator has been unregisterd successfully
	 */
	public final boolean unregisterEvaluator(final IEvaluator evaluator) {
		return hooks.remove(evaluator);
	}

	/**
	 * Execute hooks.
	 * 
	 * @param aSet
	 *            the set on which to run the callbacks
	 */
	private void executeCallbacks(final ClassifierSet aSet) {
		for (int i = 0; i < hooks.size(); i++) {
			hooks.elementAt(i).evaluateSet(aSet);
		}
	}

}