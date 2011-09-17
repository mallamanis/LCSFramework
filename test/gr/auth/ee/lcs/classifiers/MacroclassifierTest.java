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
package gr.auth.ee.lcs.classifiers;

import static org.easymock.EasyMock.createMock;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import gr.auth.ee.lcs.AbstractLearningClassifierSystem;
import gr.auth.ee.lcs.data.AbstractUpdateStrategy;
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
	private SimpleBooleanRepresentation test;

	private AbstractLearningClassifierSystem lcs;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() {
		lcs = createMock(AbstractLearningClassifierSystem.class);
		test = new SimpleBooleanRepresentation(0.5, 4, lcs);

		final UCSUpdateAlgorithm update = new UCSUpdateAlgorithm(.1, 10, .99,
				.1, 50, 0,
				new SteadyStateGeneticAlgorithm(new TournamentSelector(10,
						true,
						AbstractUpdateStrategy.COMPARISON_MODE_EXPLORATION),
						new SinglePointCrossover(lcs), (float) .8,
						new UniformBitMutation(.04), 50, lcs), 100, 1, lcs);
		lcs.setElements(test, update);
	}

	@Test
	public final void testClone() {
		final Classifier parentClassifier = lcs
				.getNewClassifier(new ExtendedBitSet("10110001"));
		final Classifier clone = (Classifier) parentClassifier.clone();
		assertTrue(clone.equals(parentClassifier));
		assertTrue(parentClassifier.equals(clone));
		assertEquals(clone.experience, 0);
		assertTrue(clone
				.getComparisonValue(AbstractUpdateStrategy.COMPARISON_MODE_EXPLOITATION) == parentClassifier
				.getComparisonValue(AbstractUpdateStrategy.COMPARISON_MODE_EXPLOITATION));

		final ExtendedBitSet a = parentClassifier;
		final ExtendedBitSet b = clone;

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
		final Classifier testClassifier = lcs
				.getNewClassifier(new ExtendedBitSet("10110001"));
		testClassifier.setActionAdvocated(0);
		final Macroclassifier testMacro1 = new Macroclassifier(testClassifier,
				1);
		final Macroclassifier testMacro2 = new Macroclassifier(testClassifier,
				0);
		assertTrue(testMacro1.equals(testMacro2));
		assertTrue(testMacro2.equals(testMacro1));
		assertTrue(testMacro1.equals(testClassifier));
		assertTrue(testMacro2.equals(testClassifier));

		final Classifier testClassifier2 = lcs
				.getNewClassifier(new ExtendedBitSet("10110001"));
		testClassifier2.setActionAdvocated(0);
		assertTrue(testMacro1.equals(testClassifier2));
	}

}
