/**
 * 
 */
package gr.auth.ee.lcs.tests;

import static org.junit.Assert.*;
import gr.auth.ee.lcs.classifiers.ExtendedBitSet;
import gr.auth.ee.lcs.geneticalgorithm.SinglePointCrossover;

import org.junit.Test;

/**
 * @author Miltos Allamanis
 *
 */
public class SinglePointCrossoverTest extends SinglePointCrossover {

	/**
	 * Test method for {@link gr.auth.ee.lcs.geneticalgorithm.SinglePointCrossover#performCrossover(gr.auth.ee.lcs.classifiers.ExtendedBitSet, gr.auth.ee.lcs.classifiers.ExtendedBitSet, int)}.
	 */
	@Test
	public void testPerformCrossover() {
		ExtendedBitSet a=new ExtendedBitSet("0000000");
		ExtendedBitSet b=new ExtendedBitSet("1111111");
		
		assertEquals(performCrossover(a, b, 3).toString(),"1111000");
		assertEquals(performCrossover(a, b, 0).toString(),"1111111");
		assertEquals(performCrossover(a, b, 7).toString(),"0000000");
		
		a=new ExtendedBitSet("1001001");
		b=new ExtendedBitSet("0011001");
		assertEquals(performCrossover(a, b, 1).toString(),"0011001");
		assertEquals(performCrossover(a, b, 5).toString(),"0001001");
	}

}
