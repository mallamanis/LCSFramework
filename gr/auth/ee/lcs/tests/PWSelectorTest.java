/**
 * 
 */
package gr.auth.ee.lcs.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gr.auth.ee.lcs.utilities.PairwiseLabelSelector;

import org.junit.Before;
import org.junit.Test;

/**
 * Test for the PW Selector.
 * 
 * @author Miltos Allamanis
 * 
 */
public class PWSelectorTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Test method for
	 * {@link gr.auth.ee.lcs.utilities.PairwiseLabelSelector#next()}.
	 */
	@Test
	public void testNext() {
		PairwiseLabelSelector selector = new PairwiseLabelSelector(3);
		assertTrue(selector.getStatus(0));
		assertTrue(selector.getStatus(1));
		assertFalse(selector.getStatus(2));
		assertTrue(selector.hasNext());

		selector.next();
		assertTrue(selector.getStatus(0));
		assertTrue(selector.getStatus(2));
		assertFalse(selector.getStatus(1));
		assertTrue(selector.hasNext());

		selector.next();
		assertTrue(selector.getStatus(1));
		assertTrue(selector.getStatus(2));
		assertFalse(selector.getStatus(0));
		assertFalse(selector.hasNext());
	}

	/**
	 * Test method for
	 * {@link gr.auth.ee.lcs.utilities.PairwiseLabelSelector#reset()}.
	 */
	@Test
	public void testReset() {
		PairwiseLabelSelector selector = new PairwiseLabelSelector(3);
		assertTrue(selector.getStatus(0));
		assertTrue(selector.getStatus(1));
		assertFalse(selector.getStatus(2));
		assertTrue(selector.hasNext());

		selector.next();
		assertTrue(selector.getStatus(0));
		assertTrue(selector.getStatus(2));
		assertFalse(selector.getStatus(1));
		assertTrue(selector.hasNext());

		selector.reset();
		assertTrue(selector.getStatus(0));
		assertTrue(selector.getStatus(1));
		assertFalse(selector.getStatus(2));
		assertTrue(selector.hasNext());

		selector.next();
		assertTrue(selector.getStatus(0));
		assertTrue(selector.getStatus(2));
		assertFalse(selector.getStatus(1));
		assertTrue(selector.hasNext());
	}

	/**
	 * Test method for
	 * {@link gr.auth.ee.lcs.utilities.PairwiseLabelSelector#hasNext()}.
	 */
	@Test
	public void testHasNext() {
		PairwiseLabelSelector selector = new PairwiseLabelSelector(10);

		for (int i = 0; i < 44; i++) { // 44 = N(N-1)/2 -1
			assertTrue(selector.hasNext());
			selector.next();
		}

		assertFalse(selector.hasNext());
	}

}
