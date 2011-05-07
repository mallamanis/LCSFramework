/**
 * 
 */
package gr.auth.ee.lcs.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gr.auth.ee.lcs.utilities.BinaryRelevanceSelector;

import org.junit.Before;
import org.junit.Test;

/**
 * Test the BR selector.
 * 
 * @author Miltiadis Allamanis
 * 
 */
public final class BRSelectorTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

	}

	@Test
	public void testNext() {
		BinaryRelevanceSelector br = new BinaryRelevanceSelector(3);
		assertTrue(br.getStatus(0));
		assertFalse(br.getStatus(1));
		assertFalse(br.getStatus(2));
		assertFalse(br.getStatus(3));
		assertFalse(br.getStatus(4));
		assertFalse(br.getStatus(5));
		assertTrue(br.hasNext());

		br.next();
		assertTrue(br.getStatus(1));
		assertFalse(br.getStatus(0));
		assertFalse(br.getStatus(2));
		assertFalse(br.getStatus(3));
		assertFalse(br.getStatus(4));
		assertFalse(br.getStatus(5));
		assertTrue(br.hasNext());

		br.next();
		assertTrue(br.getStatus(2));
		assertFalse(br.getStatus(0));
		assertFalse(br.getStatus(1));
		assertFalse(br.getStatus(3));
		assertFalse(br.getStatus(4));
		assertFalse(br.getStatus(5));
		assertFalse(br.hasNext());

		br.next();
		assertTrue(br.getStatus(2));
		assertFalse(br.getStatus(0));
		assertFalse(br.getStatus(1));
		assertFalse(br.getStatus(3));
		assertFalse(br.getStatus(4));
		assertFalse(br.getStatus(5));
		assertFalse(br.hasNext());
	}

	@Test
	public void testReset() {
		BinaryRelevanceSelector br = new BinaryRelevanceSelector(4);
		assertTrue(br.getStatus(0));
		assertFalse(br.getStatus(1));
		assertFalse(br.getStatus(2));
		assertFalse(br.getStatus(3));
		assertFalse(br.getStatus(4));
		assertFalse(br.getStatus(5));
		assertTrue(br.hasNext());

		br.next();
		assertTrue(br.getStatus(1));
		assertFalse(br.getStatus(0));
		assertFalse(br.getStatus(2));
		assertFalse(br.getStatus(3));
		assertFalse(br.getStatus(4));
		assertFalse(br.getStatus(5));
		assertTrue(br.hasNext());

		br.reset();
		assertTrue(br.getStatus(0));
		assertFalse(br.getStatus(1));
		assertFalse(br.getStatus(2));
		assertFalse(br.getStatus(3));
		assertFalse(br.getStatus(4));
		assertFalse(br.getStatus(5));
		assertTrue(br.hasNext());

		br.next();
		assertTrue(br.getStatus(1));
		assertFalse(br.getStatus(0));
		assertFalse(br.getStatus(2));
		assertFalse(br.getStatus(3));
		assertFalse(br.getStatus(4));
		assertFalse(br.getStatus(5));
		assertTrue(br.hasNext());
	}

}
