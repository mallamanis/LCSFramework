/**
 * 
 */
package gr.auth.ee.lcs.tests;

import static org.junit.Assert.*;

import java.util.Arrays;

import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.geneticalgorithm.operators.UniformBitMutation;
import gr.auth.ee.lcs.tests.mocks.MockLCS;
import gr.auth.ee.lcs.utilities.ExtendedBitSet;

import org.junit.Before;
import org.junit.Test;

/**
 * @author miltiadis
 * 
 */
public class UniformMutationTest {

	private static MockLCS lcs = new MockLCS();

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Test method for
	 * {@link gr.auth.ee.lcs.geneticalgorithm.operators.UniformBitMutation#operate(gr.auth.ee.lcs.classifiers.Classifier)}
	 * .
	 */
	@Test
	public void testOperate() {
		ExtendedBitSet chromosome1 = new ExtendedBitSet("0000000");
		Classifier cl = Classifier.createNewClassifier(lcs, chromosome1);
		UniformBitMutation mut = new UniformBitMutation(1);
		final String result = mut.operate(cl).getSubSet(0, 7).toString();
		assertEquals(result, "1111111");

		chromosome1 = new ExtendedBitSet("0000000");
		cl = Classifier.createNewClassifier(lcs, chromosome1);
		mut = new UniformBitMutation(0);
		final String result2 = mut.operate(cl).getSubSet(0, 7).toString();
		assertEquals(result2, "0000000");

		mut = new UniformBitMutation(.5);
		final boolean[] atLeastOnce = new boolean[4];
		Arrays.fill(atLeastOnce, false);

		for (int i = 0; i < 500; i++) {
			chromosome1 = new ExtendedBitSet("00");
			Classifier classifer = Classifier.createNewClassifier(lcs,
					chromosome1);
			mut.operate(classifer);
			final String res = mut.operate(cl).getSubSet(0, 2).toString();

			if (res.equals("00"))
				atLeastOnce[0] = true;
			else if (res.equals("01"))
				atLeastOnce[1] = true;
			else if (res.equals("10"))
				atLeastOnce[2] = true;
			else if (res.equals("11"))
				atLeastOnce[3] = true;
		}

		for (int i = 0; i < atLeastOnce.length; i++)
			assertTrue("This test fails with probabilty .25^500",
					atLeastOnce[i]);
	}

}
