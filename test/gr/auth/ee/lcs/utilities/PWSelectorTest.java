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
package gr.auth.ee.lcs.utilities;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
	public void setUp() {
	}

	/**
	 * Test method for
	 * {@link gr.auth.ee.lcs.utilities.PairwiseLabelSelector#hasNext()}.
	 */
	@Test
	public void testHasNext() {
		final PairwiseLabelSelector selector = new PairwiseLabelSelector(10);

		for (int i = 0; i < 44; i++) { // 44 = N(N-1)/2 -1
			assertTrue(selector.hasNext());
			selector.next();
		}

		assertFalse(selector.hasNext());
	}

	/**
	 * Test method for
	 * {@link gr.auth.ee.lcs.utilities.PairwiseLabelSelector#next()}.
	 */
	@Test
	public void testNext() {
		final PairwiseLabelSelector selector = new PairwiseLabelSelector(3);
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
		final PairwiseLabelSelector selector = new PairwiseLabelSelector(3);
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

}
