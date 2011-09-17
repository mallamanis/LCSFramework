/**
 * 
 */
package gr.auth.ee.lcs.data.updateAlgorithms;

import static org.junit.Assert.*;

import java.io.Serializable;

import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.data.AbstractUpdateStrategy;
import gr.auth.ee.lcs.geneticalgorithm.IGeneticAlgorithmStrategy;
import static org.easymock.EasyMock.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Miltiadis Allamanis
 * 
 */
public class SequentialMlUpdateAlgorithmTest {

	public SequentialMlUpdateAlgorithm update;
	private AbstractUpdateStrategy mockStrategy;
	private IGeneticAlgorithmStrategy mockGa;

	public static int NUM_OF_LABELS = 5;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		mockStrategy = createMock(AbstractUpdateStrategy.class);
		mockGa = createMock(IGeneticAlgorithmStrategy.class);
		update = new SequentialMlUpdateAlgorithm(mockStrategy, mockGa,
				NUM_OF_LABELS);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for
	 * {@link gr.auth.ee.lcs.data.updateAlgorithms.SequentialMlUpdateAlgorithm#cover(gr.auth.ee.lcs.classifiers.ClassifierSet, int)}
	 * .
	 */
	@Test
	public void testCover() {
		reset(mockStrategy);
		ClassifierSet population = new ClassifierSet(null);
		mockStrategy.cover(population, 0);

		replay(mockStrategy);
		update.cover(population, 0);
		verify(mockStrategy);
	}

	/**
	 * Test method for
	 * {@link gr.auth.ee.lcs.data.updateAlgorithms.SequentialMlUpdateAlgorithm#createStateClassifierObject()}
	 * .
	 */
	@Test
	public void testCreateStateClassifierObject() {

		reset(mockStrategy);
		Serializable obj = createMock(Serializable.class);
		expect(mockStrategy.createStateClassifierObject()).andReturn(obj)
				.times(10);
		replay(mockStrategy);
		for (int i = 0; i < 10; i++)
			assertEquals(update.createStateClassifierObject(), obj);
		verify(mockStrategy);
	}

	/**
	 * Test method for
	 * {@link gr.auth.ee.lcs.data.updateAlgorithms.SequentialMlUpdateAlgorithm#getComparisonValue(gr.auth.ee.lcs.classifiers.Classifier, int)}
	 * .
	 */
	@Test
	public void testGetComparisonValue() {
		reset(mockStrategy);
		expect(mockStrategy.getComparisonValue(null, 0)).andReturn(1.).times(1);
		replay(mockStrategy);
		assertEquals(Double.compare(update.getComparisonValue(null, 0), 1), 0);
		verify(mockStrategy);
	}

	/**
	 * Test method for
	 * {@link gr.auth.ee.lcs.data.updateAlgorithms.SequentialMlUpdateAlgorithm#getData(gr.auth.ee.lcs.classifiers.Classifier)}
	 * .
	 */
	@Test
	public void testGetData() {
		reset(mockStrategy);
		expect(mockStrategy.getData(null)).andReturn("Hi").times(1);
		replay(mockStrategy);
		assertTrue(update.getData(null).equals("Hi"));
		verify(mockStrategy);
	}

	/**
	 * Test method for
	 * {@link gr.auth.ee.lcs.data.updateAlgorithms.SequentialMlUpdateAlgorithm#performUpdate(gr.auth.ee.lcs.classifiers.ClassifierSet, gr.auth.ee.lcs.classifiers.ClassifierSet)}
	 * .
	 */
	@Test
	public void testPerformUpdate() {
		reset(mockStrategy);

		ClassifierSet matchSet = new ClassifierSet(null);
		ClassifierSet correctSet = new ClassifierSet(null);
		mockStrategy.performUpdate(matchSet, correctSet);
		replay(mockStrategy);
		update.performUpdate(matchSet, correctSet);
		verify(mockStrategy);
	}

	/**
	 * Test method for
	 * {@link gr.auth.ee.lcs.data.updateAlgorithms.SequentialMlUpdateAlgorithm#updateSet(gr.auth.ee.lcs.classifiers.ClassifierSet, gr.auth.ee.lcs.classifiers.ClassifierSet, int, boolean)}
	 * .
	 */
	@Test
	public void testUpdateSet() {
		reset(mockStrategy);
		reset(mockGa);
		ClassifierSet population = new ClassifierSet(null);
		ClassifierSet matchSet = new ClassifierSet(null);

		replay(mockStrategy);
		replay(mockGa);
		update.updateSet(population, matchSet, 0, false);
		verify(mockStrategy);
		verify(mockGa);

		// TODO: More testing needed
	}

}
