/**
 * 
 */
package gr.auth.ee.lcs.tests;

import static org.junit.Assert.assertEquals;
import gr.auth.ee.lcs.classifiers.ExtendedBitSet;
import gr.auth.ee.lcs.geneticalgorithm.operators.SinglePointCrossover;

import org.junit.Test;

/**
 * @author Miltos Allamanis
 * 
 */
public class SinglePointCrossoverTest extends SinglePointCrossover {

	/**
	 * Test method for
	 * {@link gr.auth.ee.lcs.geneticalgorithm.operators.SinglePointCrossover#performCrossover(gr.auth.ee.lcs.classifiers.ExtendedBitSet, gr.auth.ee.lcs.classifiers.ExtendedBitSet, int)}
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

}
