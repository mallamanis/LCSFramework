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
		final BinaryRelevanceSelector br = new BinaryRelevanceSelector(3);
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
		final BinaryRelevanceSelector br = new BinaryRelevanceSelector(4);
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
