/**
 * 
 */
package gr.auth.ee.lcs.tests.mocks;

import gr.auth.ee.lcs.AbstractLearningClassifierSystem;

/**
 * A simple mock LCS used for testing
 * 
 * @author Miltos Allamanis
 * 
 */
public class MockLCS extends AbstractLearningClassifierSystem {

	public MockLCS() {
		this.setElements(new MockTransformBridge(), new MockUpdateStrategy());
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
