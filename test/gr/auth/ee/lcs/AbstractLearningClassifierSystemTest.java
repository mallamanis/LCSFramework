/**
 * 
 */
package gr.auth.ee.lcs;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.data.AbstractUpdateStrategy;
import gr.auth.ee.lcs.data.ClassifierTransformBridge;
import gr.auth.ee.lcs.data.IEvaluator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Miltiadis Allamanis
 *
 */
public class AbstractLearningClassifierSystemTest {

	AbstractLearningClassifierSystem lcs;
	
	ClassifierTransformBridge mockBridge;
	
	AbstractUpdateStrategy mockUpdate;
	
	ClassifierSet population;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		lcs = createMockBuilder(AbstractLearningClassifierSystem.class).withConstructor().createMock();
		mockUpdate = createMock(AbstractUpdateStrategy.class);
		mockBridge = createMock(ClassifierTransformBridge.class);
		lcs.setElements(mockBridge, mockUpdate);
		
		population = new ClassifierSet(null);
		lcs.setRulePopulation(population);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link gr.auth.ee.lcs.AbstractLearningClassifierSystem#getClassifierTransformBridge()}.
	 */
	@Test
	public void testGetClassifierTransformBridge() {
		assertEquals(lcs.getClassifierTransformBridge(),mockBridge);
	}

	/**
	 * Test method for {@link gr.auth.ee.lcs.AbstractLearningClassifierSystem#getNewClassifier()}.
	 */
	@Test
	public void testGetNewClassifier() {
		reset(mockBridge);
		reset(mockUpdate);
		
		expect(mockBridge.getChromosomeSize()).andReturn(10).anyTimes();
		mockBridge.setRepresentationSpecificClassifierData(anyObject(Classifier.class));
		expect(mockUpdate.createStateClassifierObject()).andReturn(null).anyTimes();
		
		replay(mockBridge);
		Classifier test = lcs.getNewClassifier();
		assertEquals(test.size(),10);
		verify(mockBridge);
		
	}

	/**
	 * Test method for {@link gr.auth.ee.lcs.AbstractLearningClassifierSystem#getNewClassifier(gr.auth.ee.lcs.utilities.ExtendedBitSet)}.
	 */
	@Test
	public void testGetNewClassifierExtendedBitSet() {
		
	}

	/**
	 * Test method for {@link gr.auth.ee.lcs.AbstractLearningClassifierSystem#getRulePopulation()}.
	 */
	@Test
	public void testGetRulePopulation() {
		assertEquals(lcs.getRulePopulation(),population);
	}

	/**
	 * Test method for {@link gr.auth.ee.lcs.AbstractLearningClassifierSystem#getUpdateStrategy()}.
	 */
	@Test
	public void testGetUpdateStrategy() {
		assertEquals(lcs.getUpdateStrategy(),mockUpdate);
	}

	/**
	 * Test method for {@link gr.auth.ee.lcs.AbstractLearningClassifierSystem#registerHook(gr.auth.ee.lcs.data.IEvaluator)}.
	 */
	@Test
	public void testRegisterHook() {
		reset(mockUpdate);
		IEvaluator mockEvaluator = createMock(IEvaluator.class);
		lcs.instances = new double[1][10];
		
		lcs.registerHook(mockEvaluator);
		lcs.setHookCallbackRate(10);
		expect(mockEvaluator.evaluateLCS(lcs)).andReturn(0.).times(3);
		mockUpdate.updateSet(eq(population), anyObject(ClassifierSet.class), eq(0), eq(true));
		expectLastCall().times(60);
		
		replay(mockUpdate);
		replay(mockEvaluator);
		lcs.trainSet(30, population, true);
		
		lcs.unregisterEvaluator(mockEvaluator);
		lcs.trainSet(30, population, true);
		
		verify(mockUpdate);
		verify(mockEvaluator);
		
	}

	/**
	 * Test method for {@link gr.auth.ee.lcs.AbstractLearningClassifierSystem#setElements(gr.auth.ee.lcs.data.ClassifierTransformBridge, gr.auth.ee.lcs.data.AbstractUpdateStrategy)}.
	 */
	@Test
	public void testSetElements() {
		lcs.setElements(null, null);
		assertEquals(lcs.getClassifierTransformBridge(),null);
		assertEquals(lcs.getUpdateStrategy(),null);
		
		lcs.setElements(mockBridge, mockUpdate);
		assertEquals(lcs.getClassifierTransformBridge(),mockBridge);
		assertEquals(lcs.getUpdateStrategy(),mockUpdate);
	}

	/**
	 * Test method for {@link gr.auth.ee.lcs.AbstractLearningClassifierSystem#setRulePopulation(gr.auth.ee.lcs.classifiers.ClassifierSet)}.
	 */
	@Test
	public void testSetRulePopulation() {
		lcs.setRulePopulation(null);
		assertEquals(lcs.getRulePopulation(),null);
		
		lcs.setRulePopulation(population);
		assertEquals(lcs.getRulePopulation(),population);
	}

	/**
	 * Test method for {@link gr.auth.ee.lcs.AbstractLearningClassifierSystem#trainSet(int, gr.auth.ee.lcs.classifiers.ClassifierSet, boolean)}.
	 */
	@Test
	public void testTrainSetIntClassifierSetBoolean() {
		reset(mockUpdate);
		lcs.instances = new double[1][10];
		
		mockUpdate.updateSet(eq(population), anyObject(ClassifierSet.class), eq(0), eq(true));
		expectLastCall().times(10);
		mockUpdate.updateSet(eq(population), anyObject(ClassifierSet.class), eq(0), eq(false));
		expectLastCall().times(10);
		
		replay(mockUpdate);		
		lcs.trainSet(10, population, true);
		lcs.trainSet(10, population, false);
		verify(mockUpdate);
	}

	/**
	 * Test method for {@link gr.auth.ee.lcs.AbstractLearningClassifierSystem#trainWithInstance(gr.auth.ee.lcs.classifiers.ClassifierSet, int)}.
	 */
	@Test
	public void testTrainWithInstance() {
		reset(mockUpdate);
		lcs.instances = new double[1][10];
		
		mockUpdate.updateSet(eq(population), anyObject(ClassifierSet.class), eq(0), eq(true));
		
		replay(mockUpdate);		
		lcs.trainWithInstance(population, 0,true);
		verify(mockUpdate);
	}

	/**
	 * Test method for {@link gr.auth.ee.lcs.AbstractLearningClassifierSystem#updatePopulation(int, gr.auth.ee.lcs.classifiers.ClassifierSet)}.
	 */
	@Test
	public void testUpdatePopulation() {
		reset(mockUpdate);
		lcs.instances = new double[1][10];
		
		mockUpdate.updateSet(eq(population), anyObject(ClassifierSet.class), eq(0), eq(false));
		
		replay(mockUpdate);		
		lcs.updatePopulation(1, population);
		verify(mockUpdate);
	}

	/**
	 * Test method for {@link gr.auth.ee.lcs.AbstractLearningClassifierSystem#trainSet(int, gr.auth.ee.lcs.classifiers.ClassifierSet)}.
	 */
	@Test
	public void testTrainSetIntClassifierSet() {
		reset(mockUpdate);
		lcs.instances = new double[1][10];
		mockUpdate.updateSet(eq(population), anyObject(ClassifierSet.class), eq(0), eq(true));
		expectLastCall().times(10);
		
		replay(mockUpdate);	
		lcs.trainSet(10, population);
		verify(mockUpdate);
	}

}
