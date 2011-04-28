/**
 * 
 */
package gr.auth.ee.lcs.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import gr.auth.ee.lcs.utilities.ProportionalCut;

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
		float[][] confValues = { { (float) .4, (float) .6 },
				{ (float) .8, (float) .2 }, { (float) .1, (float) .9 } };
		ProportionalCut a = new ProportionalCut();

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
		float[][] confValues = { { (float) .4, (float) .6 },
				{ (float) .8, (float) .2 }, { (float) .1, (float) .9 } };
		ProportionalCut a = new ProportionalCut();
		final float threshold = a.calibrate((float) 1.33, confValues);
		assertTrue(threshold < .4);
		assertTrue(threshold > .2);
	}

	/**
	 * 
	 */
	@Test
	public void testPcut2() {
		float[][] confValues = {
				{ (float) (1. / 3.), (float) (1. / 3.), (float) (1. / 3.) },
				{ (float) .5, (float) .2, (float) .3 },
				{ (float) .1, (float) .2, (float) .7 } };
		ProportionalCut a = new ProportionalCut();
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
		float[][] confValues = {
				{ (float) (1. / 5.), (float) (1. / 5.), (float) (1. / 5.),
						(float) (1. / 5.), (float) (1. / 5.) },
				{ (float) .5, (float) .15, (float) .15, (float) .15,
						(float) .15 },
				{ (float) 0.1, (float) 0.05, (float) 0.05, (float) .24,
						(float) .24 } };
		ProportionalCut a = new ProportionalCut();
		float threshold = a.calibrate(5, confValues);
		assertTrue(threshold < .05);

		threshold = a.calibrate((float) (8. / 3.), confValues);
		assertTrue(threshold < .2);
	}
}
