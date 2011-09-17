/**
 * 
 */
package gr.auth.ee.lcs.evaluators;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import gr.auth.ee.lcs.AbstractLearningClassifierSystem;
import gr.auth.ee.lcs.data.ClassifierTransformBridge;

import org.easymock.EasyMockSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Miltos Allamanis
 * 
 */
public class RecallEvaluatorTest extends EasyMockSupport {

	AccuracyRecallEvaluator test;
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

		double[][] set = { { 0, 0, 0, 1, 0 }, { 1, 0, 0, 1, 0 },
				{ 0, 0, 1, 0, 0 }, { 1, 1, 1, 1, 1 } };
		testSet = set;
		test = new AccuracyRecallEvaluator(testSet, false, mockLcs,
				AccuracyRecallEvaluator.TYPE_RECALL);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for
	 * {@link gr.auth.ee.lcs.evaluators.ExactMatchEvalutor#getMetric(gr.auth.ee.lcs.AbstractLearningClassifierSystem)}
	 * .
	 */
	@Test
	public void testEvaluateLCS() {
		resetAll();
		mockLcs.setElements(mockBridge, null);

		int[] resp0 = { 1 };
		expect(mockLcs.classifyInstance(testSet[0])).andReturn(resp0)
				.anyTimes();
		expect(mockBridge.getDataInstanceLabels(testSet[0])).andReturn(resp0)
				.anyTimes();

		int[] resp1 = { 1 };
		expect(mockLcs.classifyInstance(testSet[1])).andReturn(resp1)
				.anyTimes();
		expect(mockBridge.getDataInstanceLabels(testSet[1])).andReturn(resp1)
				.anyTimes();

		int[] resp2 = { 0 };
		expect(mockLcs.classifyInstance(testSet[2])).andReturn(resp2)
				.anyTimes();
		expect(mockBridge.getDataInstanceLabels(testSet[2])).andReturn(resp2)
				.anyTimes();

		int[] resp3 = { 0, 1, 2 };
		expect(mockLcs.classifyInstance(testSet[3])).andReturn(resp3)
				.anyTimes();
		expect(mockBridge.getDataInstanceLabels(testSet[3])).andReturn(resp3)
				.anyTimes();

		replayAll();
		assertEquals(Double.compare(test.getMetric(mockLcs), 1), 0);
		verifyAll();

	}

	/**
	 * Test method for
	 * {@link gr.auth.ee.lcs.evaluators.ExactMatchEvalutor#getMetric(gr.auth.ee.lcs.AbstractLearningClassifierSystem)}
	 * .
	 */
	@Test
	public void testEvaluateLCS2() {
		resetAll();
		mockLcs.setElements(mockBridge, null);

		int[] resp0 = { 1 };
		expect(mockLcs.classifyInstance(testSet[0])).andReturn(resp0)
				.anyTimes();
		expect(mockBridge.getDataInstanceLabels(testSet[0])).andReturn(resp0)
				.anyTimes();

		int[] resp1 = { 1 };
		expect(mockLcs.classifyInstance(testSet[1])).andReturn(resp1)
				.anyTimes();
		expect(mockBridge.getDataInstanceLabels(testSet[1])).andReturn(resp1)
				.anyTimes();

		int[] resp2 = { 0 };
		int[] resp2b = { 1 };
		expect(mockLcs.classifyInstance(testSet[2])).andReturn(resp2b)
				.anyTimes();
		expect(mockBridge.getDataInstanceLabels(testSet[2])).andReturn(resp2)
				.anyTimes();

		int[] resp3 = { 0, 1, 2 };
		expect(mockLcs.classifyInstance(testSet[3])).andReturn(resp3)
				.anyTimes();
		expect(mockBridge.getDataInstanceLabels(testSet[3])).andReturn(resp3)
				.anyTimes();

		replayAll();
		assertEquals(Double.compare(test.getMetric(mockLcs), .75), 0);
		verifyAll();

	}

	/**
	 * Test method for
	 * {@link gr.auth.ee.lcs.evaluators.ExactMatchEvalutor#getMetric(gr.auth.ee.lcs.AbstractLearningClassifierSystem)}
	 * .
	 */
	@Test
	public void testEvaluateLCS3() {
		resetAll();
		mockLcs.setElements(mockBridge, null);

		int[] resp0 = { 1 };
		int[] resp0b = {};
		expect(mockLcs.classifyInstance(testSet[0])).andReturn(resp0b)
				.anyTimes();
		expect(mockBridge.getDataInstanceLabels(testSet[0])).andReturn(resp0)
				.anyTimes();

		int[] resp1 = { 1 };
		int[] resp1b = { 2 };
		expect(mockLcs.classifyInstance(testSet[1])).andReturn(resp1b)
				.anyTimes();
		expect(mockBridge.getDataInstanceLabels(testSet[1])).andReturn(resp1)
				.anyTimes();

		int[] resp2 = { 0 };
		int[] resp2b = { 0, 1 };
		expect(mockLcs.classifyInstance(testSet[2])).andReturn(resp2b)
				.anyTimes();
		expect(mockBridge.getDataInstanceLabels(testSet[2])).andReturn(resp2)
				.anyTimes();

		int[] resp3 = { 0, 1, 2 };
		int[] resp3b = { 0, 1 };
		expect(mockLcs.classifyInstance(testSet[3])).andReturn(resp3b)
				.anyTimes();
		expect(mockBridge.getDataInstanceLabels(testSet[3])).andReturn(resp3)
				.anyTimes();

		replayAll();
		assertTrue(Math.abs(test.getMetric(mockLcs) - (5. / 12.)) < 0.0001);
		verifyAll();

	}

}
