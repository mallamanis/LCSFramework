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
package gr.auth.ee.lcs.calibration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import gr.auth.ee.lcs.calibration.ProportionalCut;

import org.junit.Before;
import org.junit.Test;

/**
 * A proportional cut test.
 * 
 * @author Miltos Allamanis
 * 
 */
public class ProportionalCutTest {

	/**
	 * 
	 */
	@Test
	public void activeLabels() {
		final float[][] confValues = { { (float) .4, (float) .6 },
				{ (float) .8, (float) .2 }, { (float) .1, (float) .9 } };
		final ProportionalCut a = new ProportionalCut();

		assertEquals(a.getNumberOfActiveLabels(confValues[0], (float) .8), 0);
		assertEquals(a.getNumberOfActiveLabels(confValues[0], (float) .6), 1);
		assertEquals(a.getNumberOfActiveLabels(confValues[0], (float) .5), 1);
		assertEquals(a.getNumberOfActiveLabels(confValues[0], (float) .4), 2);
		assertEquals(a.getNumberOfActiveLabels(confValues[0], (float) .1), 2);

		assertEquals(a.getNumberOfActiveLabels(confValues[1], (float) .8), 1);
		assertEquals(a.getNumberOfActiveLabels(confValues[1], (float) .801), 0);
		assertEquals(a.getNumberOfActiveLabels(confValues[1], (float) .5), 1);
		assertEquals(a.getNumberOfActiveLabels(confValues[1], (float) .2), 2);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * 
	 */
	@Test
	public void testPcut1() {
		final float[][] confValues = { { (float) .4, (float) .6 },
				{ (float) .8, (float) .2 }, { (float) .1, (float) .9 } };
		final ProportionalCut a = new ProportionalCut();
		final float threshold = a.calibrate((float) 1.33, confValues);
		assertTrue(threshold < .4);
		assertTrue(threshold > .2);
	}

	/**
	 * 
	 */
	@Test
	public void testPcut2() {
		final float[][] confValues = {
				{ (float) (1. / 3.), (float) (1. / 3.), (float) (1. / 3.) },
				{ (float) .5, (float) .2, (float) .3 },
				{ (float) .1, (float) .2, (float) .7 } };
		final ProportionalCut a = new ProportionalCut();
		float threshold = a.calibrate(3, confValues);
		assertTrue(threshold < .1);

		threshold = a.calibrate((float) (5. / 3.), confValues);
		assertTrue(threshold < (1. / 3.));
		assertTrue(threshold > .2);
	}

	/**
	 * 
	 */
	@Test
	public void testPcut3() {
		final float[][] confValues = {
				{ (float) (1. / 5.), (float) (1. / 5.), (float) (1. / 5.),
						(float) (1. / 5.), (float) (1. / 5.) },
				{ (float) .5, (float) .15, (float) .15, (float) .15,
						(float) .15 },
				{ (float) 0.1, (float) 0.05, (float) 0.05, (float) .24,
						(float) .24 } };
		final ProportionalCut a = new ProportionalCut();
		float threshold = a.calibrate(5, confValues);
		assertTrue(threshold < .05);

		threshold = a.calibrate((float) (8. / 3.), confValues);
		assertTrue(threshold < .2);
	}
}
