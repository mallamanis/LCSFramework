/**
 * 
 */
package gr.auth.ee.lcs;

import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.data.AbstractUpdateStrategy;
import gr.auth.ee.lcs.data.ClassifierTransformBridge;
import gr.auth.ee.lcs.utilities.ExtendedBitSet;

/**
 * An abstract LCS class to be implemented by all LCSs.
 * 
 * @author Miltiadis Allamanis
 *
 */
public abstract class AbstractLearningClassifierSystem {
	
	/**
	 * Constructor.
	 * @param bridge the classifier transform bridge
	 * @param update the update strategy
	 */
	public void setElements(final ClassifierTransformBridge bridge, final AbstractUpdateStrategy update) {
		transformBridge = bridge;
		updateStrategy = update;
	}
	
	/**
	 * The train set.
	 */
	public double[][] trainSet;
	
	/**
	 * The LCS instance transform bridge.
	 */
	private ClassifierTransformBridge transformBridge;
	
	/**
	 * The Abstract Update Algorithm Strategy of the LCS.
	 */
	private AbstractUpdateStrategy updateStrategy;
	
	/**
	 * Run the LCS and train it.
	 */
	public abstract void train();
	
	public ClassifierTransformBridge getClassifierTransformBridge() {
		return transformBridge;
	}
	
	public AbstractUpdateStrategy getUpdateStrategy() {
		return updateStrategy;
	}
	
	/**
	 * Create a new classifier for the specific LCS.
	 * @return the new classifier.
	 */
	public Classifier getNewClassifier(){
		return Classifier.createNewClassifier(transformBridge, updateStrategy);
	}; 
	
	public Classifier getNewClassifier(ExtendedBitSet chromosome) {
		return Classifier.createNewClassifier(transformBridge, updateStrategy, chromosome);
	}
}
