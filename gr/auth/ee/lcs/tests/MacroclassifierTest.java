/**
 * 
 */
package gr.auth.ee.lcs.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.Macroclassifier;
import gr.auth.ee.lcs.data.AbstractUpdateStrategy;
import gr.auth.ee.lcs.data.ClassifierTransformBridge;
import gr.auth.ee.lcs.data.representations.SimpleBooleanRepresentation;
import gr.auth.ee.lcs.data.updateAlgorithms.UCSUpdateAlgorithm;
import gr.auth.ee.lcs.geneticalgorithm.algorithms.SteadyStateGeneticAlgorithm;
import gr.auth.ee.lcs.geneticalgorithm.operators.SinglePointCrossover;
import gr.auth.ee.lcs.geneticalgorithm.operators.UniformBitMutation;
import gr.auth.ee.lcs.geneticalgorithm.selectors.TournamentSelector;
import gr.auth.ee.lcs.utilities.ExtendedBitSet;

import org.junit.Before;
import org.junit.Test;

/**
 * A macroclassifier test.
 * 
 * @author Miltos Allamanis
 * 
 */
public class MacroclassifierTest {

	/**
	 * Set up a simple representation.
	 */
	SimpleBooleanRepresentation test;
	
	MockLCS lcs;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		lcs = new MockLCS();
		test = new SimpleBooleanRepresentation(0.5, 4, lcs);
		
		UCSUpdateAlgorithm update = new UCSUpdateAlgorithm(
				.1,
				10,
				.99,
				.1,
				50,
				0,
				new SteadyStateGeneticAlgorithm(
						new TournamentSelector(
								10,
								true,
								AbstractUpdateStrategy.COMPARISON_MODE_EXPLORATION),
						new SinglePointCrossover(lcs), (float) .8,
						new UniformBitMutation(.04), 50, lcs), 100, 1, lcs);
		lcs.setElements(test, update);
	}

	@Test
	public final void testClone() {
		Classifier parentClassifier = lcs.getNewClassifier(new ExtendedBitSet(
				"10110001"));
		Classifier clone = (Classifier) parentClassifier.clone();
		assertTrue(clone.equals(parentClassifier));
		assertTrue(parentClassifier.equals(clone));
		assertEquals(clone.experience, 0);
		assertTrue(clone
				.getComparisonValue(AbstractUpdateStrategy.COMPARISON_MODE_EXPLOITATION) == parentClassifier
				.getComparisonValue(AbstractUpdateStrategy.COMPARISON_MODE_EXPLOITATION));

		ExtendedBitSet a = parentClassifier;
		ExtendedBitSet b = clone;

		assertEquals(a, b);
		assertTrue(parentClassifier.getSerial() != clone.getSerial());

	}

	/**
	 * Test method for
	 * {@link gr.auth.ee.lcs.classifiers.Macroclassifier#equals(gr.auth.ee.lcs.classifiers.Classifier)}
	 * .
	 */
	@Test
	public void testEquals() {
		Classifier testClassifier = lcs.getNewClassifier(new ExtendedBitSet(
				"10110001"));
		testClassifier.setActionAdvocated(0);
		Macroclassifier testMacro1 = new Macroclassifier(testClassifier, 1);
		Macroclassifier testMacro2 = new Macroclassifier(testClassifier, 0);
		assertTrue(testMacro1.equals(testMacro2));
		assertTrue(testMacro2.equals(testMacro1));
		assertTrue(testMacro1.equals(testClassifier));
		assertTrue(testMacro2.equals(testClassifier));

		Classifier testClassifier2 = lcs.getNewClassifier(new ExtendedBitSet(
				"10110001"));
		testClassifier2.setActionAdvocated(0);
		assertTrue(testMacro1.equals(testClassifier2));
	}

}
