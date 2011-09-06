package gr.auth.ee.lcs.evaluators;


import gr.auth.ee.lcs.AbstractLearningClassifierSystem;
import gr.auth.ee.lcs.data.ClassifierTransformBridge;

import static org.junit.Assert.*;
import org.easymock.EasyMockSupport;
import static org.easymock.EasyMock.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class HammingLossEvaluatorTest extends EasyMockSupport{

	HammingLossEvaluator test;
	AbstractLearningClassifierSystem mockLcs;
	ClassifierTransformBridge mockBridge;
	double[][] testSet;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		mockLcs = createMock(AbstractLearningClassifierSystem.class);
		mockBridge = createMock(ClassifierTransformBridge.class);
		
		double[][] set = {{0,0,0,1,0},{1,0,0,1,0},{0,0,1,0,0},{1,1,1,1,1}};
		testSet = set;
		test = new HammingLossEvaluator(testSet,false,3,mockLcs);		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link gr.auth.ee.lcs.evaluators.ExactMatchEvalutor#evaluateLCS(gr.auth.ee.lcs.AbstractLearningClassifierSystem)}.
	 */
	@Test
	public void testEvaluateLCS() {
		resetAll();
		mockLcs.setElements(mockBridge, null);
		
		int[] resp0 = {1};
		expect(mockLcs.classifyInstance(testSet[0])).andReturn(resp0).anyTimes();
		expect(mockBridge.getDataInstanceLabels(testSet[0])).andReturn(resp0).anyTimes();
		
		int[] resp1 = {1};
		expect(mockLcs.classifyInstance(testSet[1])).andReturn(resp1).anyTimes();
		expect(mockBridge.getDataInstanceLabels(testSet[1])).andReturn(resp1).anyTimes();
		
		int[] resp2 = {0};
		expect(mockLcs.classifyInstance(testSet[2])).andReturn(resp2).anyTimes();
		expect(mockBridge.getDataInstanceLabels(testSet[2])).andReturn(resp2).anyTimes();
		
		int[] resp3 = {0,1,2};
		expect(mockLcs.classifyInstance(testSet[3])).andReturn(resp3).anyTimes();
		expect(mockBridge.getDataInstanceLabels(testSet[3])).andReturn(resp3).anyTimes();
		
		replayAll();
		assertEquals(Double.compare(test.evaluateLCS(mockLcs),0),0);
		verifyAll();
		
	}
	
	/**
	 * Test method for {@link gr.auth.ee.lcs.evaluators.ExactMatchEvalutor#evaluateLCS(gr.auth.ee.lcs.AbstractLearningClassifierSystem)}.
	 */
	@Test
	public void testEvaluateLCS2() {
		resetAll();
		mockLcs.setElements(mockBridge, null);
		
		int[] resp0 = {1};
		expect(mockLcs.classifyInstance(testSet[0])).andReturn(resp0).anyTimes();
		expect(mockBridge.getDataInstanceLabels(testSet[0])).andReturn(resp0).anyTimes();
		
		int[] resp1 = {1};
		expect(mockLcs.classifyInstance(testSet[1])).andReturn(resp1).anyTimes();
		expect(mockBridge.getDataInstanceLabels(testSet[1])).andReturn(resp1).anyTimes();
		
		int[] resp2 = {0};
		int[] resp2b = {1};
		expect(mockLcs.classifyInstance(testSet[2])).andReturn(resp2b).anyTimes();
		expect(mockBridge.getDataInstanceLabels(testSet[2])).andReturn(resp2).anyTimes();
		
		int[] resp3 = {0,1,2};
		expect(mockLcs.classifyInstance(testSet[3])).andReturn(resp3).anyTimes();
		expect(mockBridge.getDataInstanceLabels(testSet[3])).andReturn(resp3).anyTimes();
		
		replayAll();
		assertEquals(Double.compare(test.evaluateLCS(mockLcs),2./12.),0);
		verifyAll();
		
	}
	
	/**
	 * Test method for {@link gr.auth.ee.lcs.evaluators.ExactMatchEvalutor#evaluateLCS(gr.auth.ee.lcs.AbstractLearningClassifierSystem)}.
	 */
	@Test
	public void testEvaluateLCS3() {
		resetAll();
		mockLcs.setElements(mockBridge, null);
		
		int[] resp0 = {1};
		int[] resp0b = {};
		expect(mockLcs.classifyInstance(testSet[0])).andReturn(resp0b).anyTimes();
		expect(mockBridge.getDataInstanceLabels(testSet[0])).andReturn(resp0).anyTimes();
		
		int[] resp1 = {1};
		int[] resp1b = {2};
		expect(mockLcs.classifyInstance(testSet[1])).andReturn(resp1b).anyTimes();
		expect(mockBridge.getDataInstanceLabels(testSet[1])).andReturn(resp1).anyTimes();
		
		int[] resp2 = {0};
		int[] resp2b = {1};
		expect(mockLcs.classifyInstance(testSet[2])).andReturn(resp2b).anyTimes();
		expect(mockBridge.getDataInstanceLabels(testSet[2])).andReturn(resp2).anyTimes();
		
		int[] resp3 = {0,1,2};
		int[] resp3b = {0,1};
		expect(mockLcs.classifyInstance(testSet[3])).andReturn(resp3b).anyTimes();
		expect(mockBridge.getDataInstanceLabels(testSet[3])).andReturn(resp3).anyTimes();
		
		replayAll();
		assertEquals(Double.compare(test.evaluateLCS(mockLcs),0.5),0);
		verifyAll();
		
	}


}
