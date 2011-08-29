/**
 * 
 */
package gr.auth.ee.lcs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

/**
 * A test case for FoldEvaluator.
 * 
 * @author Miltos Allamanis
 * 
 */
public class FoldEvaluatorTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() {
	}

	/**
	 * Test method for {@link gr.auth.ee.lcs.FoldEvaluator#calcMean(double[][])}
	 * .
	 */
	@Test
	public void testCalcMean() {
		final double[][] results = new double[3][4];

		final FoldEvaluator tested = new FoldEvaluator(3, 3, new MockLCS(),
				null);

		Arrays.fill(results[0], 0);
		Arrays.fill(results[1], 1);
		Arrays.fill(results[2], 2);
		assertTrue(Double.compare(tested.calcMean(results)[0], 1) == 0);
		assertTrue(Double.compare(tested.calcMean(results)[1], 1) == 0);
		assertTrue(Double.compare(tested.calcMean(results)[2], 1) == 0);

		Arrays.fill(results[0], 0);
		Arrays.fill(results[1], 0);
		Arrays.fill(results[2], 0);
		assertTrue(Double.compare(tested.calcMean(results)[0], 0) == 0);
		assertTrue(Double.compare(tested.calcMean(results)[1], 0) == 0);
		assertTrue(Double.compare(tested.calcMean(results)[2], 0) == 0);

	}

	/**
	 * Test method for
	 * {@link gr.auth.ee.lcs.FoldEvaluator#gatherResults(double[], int)}.
	 */
	@Test
	public void testGatherResults() {
		final FoldEvaluator tested = new FoldEvaluator(3, 2, new MockLCS(),
				null);

		double[] inputResult = new double[3];

		Arrays.fill(inputResult, 0);
		final double[][] results = tested.gatherResults(inputResult, 0);
		assertTrue(Arrays.equals(results[0], inputResult));
		assertEquals(results.length, 2);

		inputResult = new double[3];
		Arrays.fill(inputResult, 1);
		tested.gatherResults(inputResult, 1);
		assertFalse(Arrays.equals(results[0], inputResult));
		assertTrue(Arrays.equals(results[1], inputResult));

		inputResult = new double[3];
		Arrays.fill(inputResult, 2);
		try {
			tested.gatherResults(inputResult, 2);
			fail("Out of Bounds exception failed");
		} catch (Exception ex) {
			// Exception thrown! That's what we expected.
		}

	}

}
