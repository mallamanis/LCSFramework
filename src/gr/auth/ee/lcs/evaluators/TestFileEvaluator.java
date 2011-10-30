/**
 * 
 */
package gr.auth.ee.lcs.evaluators;

import gr.auth.ee.lcs.AbstractLearningClassifierSystem;
import gr.auth.ee.lcs.data.ILCSMetric;

/**
 * TestFileEvaluator classifies the data in a test files and output the
 * classification.
 * 
 * @author Miltos Allamanis
 * 
 */
public class TestFileEvaluator implements ILCSMetric {

	@Override
	public double getMetric(AbstractLearningClassifierSystem lcs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getMetricName() {
		return "Test File Classification";
	}

}
