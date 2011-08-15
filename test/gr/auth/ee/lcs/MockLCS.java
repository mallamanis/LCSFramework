/*
 *	Copyright (C) 2011 by Allamanis Miltiadis
 *
 *	Permission is hereby granted, free of charge, to any person obtaining a copy
 *	of this software and associated documentation files (the "Software"), to deal
 *	in the Software without restriction, including without limitation the rights
 *	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *	copies of the Software, and to permit persons to whom the Software is
 *	furnished to do so, subject to the following conditions:
 *
 *	The above copyright notice and this permission notice shall be included in
 *	all copies or substantial portions of the Software.
 *
 *	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *	THE SOFTWARE.
 */
/**
 * 
 */
package gr.auth.ee.lcs;

import gr.auth.ee.lcs.data.MockTransformBridge;
import gr.auth.ee.lcs.data.MockUpdateStrategy;
import gr.auth.ee.lcs.utilities.SettingsLoader;

import java.io.IOException;

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
	public int[] classifyInstance(double[] instance) {
		return getClassifierTransformBridge().classify(
				this.getRulePopulation(), instance);
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
