/**
 * 
 */
package gr.auth.ee.lcs.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.geneticalgorithm.operators.SinglePointCrossover;
import gr.auth.ee.lcs.tests.mocks.MockLCS;
import gr.auth.ee.lcs.utilities.ExtendedBitSet;

import org.junit.Test;

/**
 * Test the single point crossover.
 * 
 * @author Miltos Allamanis
 * 
 */
public class SinglePointCrossoverTest extends SinglePointCrossover {

	private static MockLCS lcs = new MockLCS();

	public SinglePointCrossoverTest() {
		super(lcs);
	}

	/**
	 * Test method for
	 * {@link gr.auth.ee.lcs.geneticalgorithm.operators.SinglePointCrossover#performCrossover(gr.auth.ee.lcs.utilities.ExtendedBitSet, gr.auth.ee.lcs.utilities.ExtendedBitSet, int)}
	 * .
	 */
	@Test
	public void testPerformCrossover() {
		ExtendedBitSet chromosome1 = new ExtendedBitSet("0000000");
		ExtendedBitSet chromosome2 = new ExtendedBitSet("1111111");

		assertEquals("Chromosomes do not crossover correctly at position 3",
				performCrossover(chromosome1, chromosome2, 3).toString(),
				"1111000");
		assertEquals("Chromosomes do not crossover correctly at position 0",
				performCrossover(chromosome1, chromosome2, 0).toString(),
				"1111111");
		assertEquals("Chromosomes do not crossover correctly at position 7",
				performCrossover(chromosome1, chromosome2, 7).toString(),
				"0000000");

		chromosome1 = new ExtendedBitSet("1001001");
		chromosome2 = new ExtendedBitSet("0011001");
		assertEquals("Chromosomes do not crossover correctly at position 1",
				performCrossover(chromosome1, chromosome2, 1).toString(),
				"0011001");
		assertEquals("Chromosomes do not crossover correctly at position 5",
				performCrossover(chromosome1, chromosome2, 5).toString(),
				"0001001");
	}

	/**
	 * Test method for
	 * {@link gr.auth.ee.lcs.geneticalgorithm.operators.SinglePointCrossover#performCrossover(gr.auth.ee.lcs.utilities.ExtendedBitSet, gr.auth.ee.lcs.utilities.ExtendedBitSet, int)}
	 * .
	 */
	@Test
	public void testOperateCrossover() {
		ExtendedBitSet chromosome1 = new ExtendedBitSet("0000000");
		ExtendedBitSet chromosome2 = new ExtendedBitSet("1111111");

		Classifier cl1 = Classifier.createNewClassifier(lcs, chromosome1);
		Classifier cl2 = Classifier.createNewClassifier(lcs, chromosome2);

		boolean atLeastOnce[] = new boolean[8];
		Arrays.fill(atLeastOnce, false);

		for (int i = 0; i < 500; i++) {
			Classifier child = operate(cl1, cl2);
			final String result = child.getSubSet(0, 7).toString();

			boolean isRight = false;

			isRight |= (result.equals("0000000"));
			isRight |= (result.equals("1000000"));
			isRight |= (result.equals("1100000"));
			isRight |= (result.equals("1110000"));
			isRight |= (result.equals("1111000"));
			isRight |= (result.equals("1111100"));
			isRight |= (result.equals("1111110"));
			isRight |= (result.equals("1111111"));

			assertTrue(isRight);

			if (result.equals("0000000"))
				atLeastOnce[0] = true;
			if (result.equals("1000000"))
				atLeastOnce[1] = true;
			if (result.equals("1100000"))
				atLeastOnce[2] = true;
			if (result.equals("1110000"))
				atLeastOnce[3] = true;
			if (result.equals("1111000"))
				atLeastOnce[4] = true;
			if (result.equals("1111100"))
				atLeastOnce[5] = true;
			if (result.equals("1111110"))
				atLeastOnce[6] = true;
			if (result.equals("1111111"))
				atLeastOnce[7] = true;
		}

		// Check if at least once all possible crossovers have been made
		for (int i = 0; i < atLeastOnce.length; i++)
			assertTrue("This test might fail with probabilty (.125)^500",
					atLeastOnce[i]);

	}

}
