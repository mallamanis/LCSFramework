/**
 * 
 */
package gr.auth.ee.lcs.classifiers.statistics;

import gr.auth.ee.lcs.AbstractLearningClassifierSystem;
import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.data.ClassifierTransformBridge;
import gr.auth.ee.lcs.data.ILCSMetric;

/**
 * Calculates the mean label specificity of all rules in LCS population.
 * @author Miltiadis Allamanis
 *
 */
public class MeanLabelSpecificity implements ILCSMetric {
	
	/**
	 * The number of labels in the problem.
	 */
	private final int numOfLabels;
	
	public MeanLabelSpecificity(final int numberOfLabels) {
		numOfLabels = numberOfLabels;
	}

	/* (non-Javadoc)
	 * @see gr.auth.ee.lcs.data.ILCSMetric#getMetric(gr.auth.ee.lcs.AbstractLearningClassifierSystem)
	 */
	@Override
	public double getMetric(AbstractLearningClassifierSystem lcs) {
		final ClassifierSet set = lcs.getRulePopulation();
		final ClassifierTransformBridge bridge = lcs.getClassifierTransformBridge();
		
		final int numberOfAttributes = bridge.getNumberOfAttributes();
		
		final int numberOfMacroclassifiers = set.getNumberOfMacroclassifiers();
		
		int specificLabels = 0;
		
		for (int i = 0; i < numberOfMacroclassifiers; i++) {
			final Classifier cl = set.getClassifier(i);
			final int numerosity = set.getClassifierNumerosity(i);
			for (int l = numberOfAttributes; l < numberOfAttributes + numOfLabels; l++) {
				if (bridge.isAttributeSpecific(cl, l)){
					specificLabels += numerosity;
				}
			}
		}
		
		
		return ((double) specificLabels) / ((double) set.getTotalNumerosity() * numOfLabels);
	}

	/* (non-Javadoc)
	 * @see gr.auth.ee.lcs.data.ILCSMetric#getMetricName()
	 */
	@Override
	public String getMetricName() {
		return "Mean Label Specificity";
	}

}
