/**
 * 
 */
package gr.auth.ee.lcs.tests.mocks;

import java.io.IOException;

import gr.auth.ee.lcs.AbstractLearningClassifierSystem;
import gr.auth.ee.lcs.utilities.SettingsLoader;
import weka.core.Instances;

/**
 * A simple mock LCS used for testing
 * 
 * @author Miltos Allamanis
 * 
 */
public class MockLCS extends AbstractLearningClassifierSystem {

	public MockLCS() {
		try {
			SettingsLoader.loadSettings();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.setElements(new MockTransformBridge(), new MockUpdateStrategy());
	}

	@Override
	public AbstractLearningClassifierSystem createNew() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getEvaluationNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double[] getEvaluations(Instances testSet) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gr.auth.ee.lcs.AbstractLearningClassifierSystem#train()
	 */
	@Override
	public void train() {
		// TODO Auto-generated method stub

	}

}
