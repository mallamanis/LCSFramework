/**
 * 
 */
package gr.auth.ee.lcs.classifiers;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gr.auth.ee.lcs.AbstractLearningClassifierSystem;
import gr.auth.ee.lcs.data.AbstractUpdateStrategy;
import gr.auth.ee.lcs.data.ClassifierTransformBridge;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Miltos Allamanis
 * 
 */
public class ClassifierTest extends EasyMockSupport {

	AbstractLearningClassifierSystem mockLCS;

	ClassifierTransformBridge mockBridge;

	AbstractUpdateStrategy mockUpdate;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		mockLCS = createMock(AbstractLearningClassifierSystem.class);
		double[][] instances = { { 1, 2, 3 }, { 3, 4, 5 }, { 6, 7, 8 },
				{ 9, 10, 11 }, { 12, 13, 14 } };
		mockLCS.instances = instances;

		mockBridge = createMock(ClassifierTransformBridge.class);
		mockUpdate = createMock(AbstractUpdateStrategy.class);

		mockLCS.setElements(mockBridge, mockUpdate);
	}

	@Test
	public void testMatchCache() {
		resetAll();
		expect(mockBridge.getChromosomeSize()).andReturn(10).once();
		expect(mockBridge.toNaturalLanguageString(anyObject(Classifier.class)))
				.andReturn("").anyTimes();
		mockBridge
				.setRepresentationSpecificClassifierData(anyObject(Classifier.class));
		expect(mockUpdate.createStateClassifierObject()).andReturn(null)
				.anyTimes();

		replayAll();
		Classifier cl = mockLCS.getNewClassifier();
		verifyAll();

		resetAll();
		expect(mockBridge.isMatch(mockLCS.instances[0], cl)).andReturn(true)
				.times(1);
		expect(mockBridge.isMatch(mockLCS.instances[1], cl)).andReturn(false)
				.times(1);
		expect(mockBridge.isMatch(mockLCS.instances[2], cl)).andReturn(false)
				.times(1);
		expect(mockBridge.isMatch(mockLCS.instances[3], cl)).andReturn(true)
				.times(1);
		expect(mockBridge.isMatch(mockLCS.instances[4], cl)).andReturn(true)
				.times(1);

		replayAll();

		assertEquals(cl.getCheckedInstances(), 0);

		assertTrue(cl.isMatch(0));
		assertEquals(cl.getCheckedInstances(), 1);

		assertTrue(cl.isMatch(3));
		assertEquals(cl.getCheckedInstances(), 2);

		assertTrue(cl.isMatch(4));
		assertEquals(cl.getCheckedInstances(), 3);

		assertFalse(cl.isMatch(1));
		assertEquals(cl.getCheckedInstances(), 4);

		assertFalse(cl.isMatch(2));
		assertEquals(cl.getCheckedInstances(), 5);

		for (int i = 0; i < 100; i++) {
			assertTrue(cl.isMatch(0));
			assertTrue(cl.isMatch(3));
			assertTrue(cl.isMatch(4));

			assertFalse(cl.isMatch(1));
			assertFalse(cl.isMatch(2));
			assertEquals(cl.getCheckedInstances(), 5);
		}
		verifyAll();
	}
}
