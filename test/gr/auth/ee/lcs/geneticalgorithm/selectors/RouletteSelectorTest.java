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
package gr.auth.ee.lcs.geneticalgorithm.selectors;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gr.auth.ee.lcs.MockLCS;
import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.classifiers.Macroclassifier;
import gr.auth.ee.lcs.data.AbstractUpdateStrategy;
import gr.auth.ee.lcs.data.representations.SimpleBooleanRepresentation;
import gr.auth.ee.lcs.data.updateAlgorithms.ASLCSUpdateAlgorithm;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

/**
 * A test for the Roulette Wheel Selector.
 * 
 * @author Miltiadis Allamanis
 * 
 */
public class RouletteSelectorTest {

	/**
	 * The mock lcs.
	 */
	private MockLCS lcs;

	/**
	 * A population.
	 */
	private ClassifierSet population;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() {
		lcs = new MockLCS();
		final SimpleBooleanRepresentation rep = new SimpleBooleanRepresentation(
				.33, 2, lcs);
		final ASLCSUpdateAlgorithm update = new ASLCSUpdateAlgorithm(5, .99,
				50, 0.01, null, lcs);
		lcs.setElements(rep, update);

		population = new ClassifierSet(null);
		for (int i = 0; i < 3; i++) {
			Classifier aClassifier = lcs.getNewClassifier();
			aClassifier.setComparisonValue(
					AbstractUpdateStrategy.COMPARISON_MODE_EXPLOITATION, i + 1);
			aClassifier.setActionAdvocated(i);
			aClassifier.experience = 100;
			population.addClassifier(new Macroclassifier(aClassifier, i + 1),
					false);
		}
		// We now should have fitnesses {1,2,2,3,3,3}

	}

	/**
	 * Test method for
	 * {@link gr.auth.ee.lcs.geneticalgorithm.selectors.RouletteWheelSelector#select(int, gr.auth.ee.lcs.classifiers.ClassifierSet, gr.auth.ee.lcs.classifiers.ClassifierSet)}
	 * .
	 */
	@Test
	public void testSelect() {
		final RouletteWheelSelector selector = new RouletteWheelSelector(
				AbstractUpdateStrategy.COMPARISON_MODE_EXPLORATION, true);

		final boolean[] atLeastOnce = new boolean[3];
		Arrays.fill(atLeastOnce, false);

		for (int i = 0; i < 500; i++) {
			ClassifierSet set = new ClassifierSet(null);
			selector.select(1, population, set);
			if (set.getClassifier(0).equals(population.getClassifier(0)))
				atLeastOnce[0] = true;
			if (set.getClassifier(0).equals(population.getClassifier(1)))
				atLeastOnce[1] = true;
			if (set.getClassifier(0).equals(population.getClassifier(2)))
				atLeastOnce[2] = true;
		}

		for (int i = 0; i < atLeastOnce.length; i++) {
			assertTrue("Might fail with probability 1 -(13/14)^500",
					atLeastOnce[i]);
		}

	}

	/**
	 * Test method for
	 * {@link gr.auth.ee.lcs.geneticalgorithm.selectors.RouletteWheelSelector#select(int, gr.auth.ee.lcs.classifiers.ClassifierSet, gr.auth.ee.lcs.classifiers.ClassifierSet)}
	 * .
	 */
	@Test
	public void testSelectMin() {
		final RouletteWheelSelector selector = new RouletteWheelSelector(
				AbstractUpdateStrategy.COMPARISON_MODE_EXPLORATION, false);

		final boolean[] atLeastOnce = new boolean[3];
		Arrays.fill(atLeastOnce, false);

		for (int i = 0; i < 500; i++) {
			ClassifierSet set = new ClassifierSet(null);
			selector.select(1, population, set);
			if (set.getClassifier(0).equals(population.getClassifier(0)))
				atLeastOnce[0] = true;
			if (set.getClassifier(0).equals(population.getClassifier(1)))
				atLeastOnce[1] = true;
			if (set.getClassifier(0).equals(population.getClassifier(2)))
				atLeastOnce[2] = true;
		}

		for (int i = 0; i < atLeastOnce.length; i++) {
			assertTrue("Might fail with probability 1 -(13/14)^500",
					atLeastOnce[i]);
		}

	}

	/**
	 * Test method for
	 * {@link gr.auth.ee.lcs.geneticalgorithm.selectors.RouletteWheelSelector#select(int, gr.auth.ee.lcs.classifiers.ClassifierSet, gr.auth.ee.lcs.classifiers.ClassifierSet)}
	 * .
	 */
	@Test
	public void testZeroSelection() {
		final ClassifierSet population = new ClassifierSet(null);
		for (int i = 0; i < 3; i++) {
			Classifier aClassifier = lcs.getNewClassifier();
			aClassifier.setComparisonValue(
					AbstractUpdateStrategy.COMPARISON_MODE_EXPLOITATION, i);
			aClassifier.setActionAdvocated(i);
			aClassifier.experience = 100;
			population.addClassifier(new Macroclassifier(aClassifier, i + 1),
					false);
		}

		final RouletteWheelSelector selector = new RouletteWheelSelector(
				AbstractUpdateStrategy.COMPARISON_MODE_EXPLORATION, true);

		final boolean[] atLeastOnce = new boolean[3];
		Arrays.fill(atLeastOnce, false);

		for (int i = 0; i < 500; i++) {
			ClassifierSet set = new ClassifierSet(null);
			selector.select(1, population, set);
			if (set.getClassifier(0).equals(population.getClassifier(0)))
				atLeastOnce[0] = true;
			if (set.getClassifier(0).equals(population.getClassifier(1)))
				atLeastOnce[1] = true;
			if (set.getClassifier(0).equals(population.getClassifier(2)))
				atLeastOnce[2] = true;
		}

		assertFalse(atLeastOnce[0]);

	}

	/**
	 * Test method for
	 * {@link gr.auth.ee.lcs.geneticalgorithm.selectors.RouletteWheelSelector#select(int, gr.auth.ee.lcs.classifiers.ClassifierSet, gr.auth.ee.lcs.classifiers.ClassifierSet)}
	 * .
	 */
	@Test
	public void testZeroSelectionMin() {
		final ClassifierSet population = new ClassifierSet(null);
		for (int i = 0; i < 3; i++) {
			Classifier aClassifier = lcs.getNewClassifier();
			aClassifier.setComparisonValue(
					AbstractUpdateStrategy.COMPARISON_MODE_EXPLOITATION, i + 1);
			aClassifier.setActionAdvocated(i);
			aClassifier.experience = 100;
			population.addClassifier(new Macroclassifier(aClassifier, i + 1),
					false);
		}

		final RouletteWheelSelector selector = new RouletteWheelSelector(
				AbstractUpdateStrategy.COMPARISON_MODE_EXPLORATION, false);

		final boolean[] atLeastOnce = new boolean[3];
		Arrays.fill(atLeastOnce, false);

		for (int i = 0; i < 500; i++) {
			ClassifierSet set = new ClassifierSet(null);
			selector.select(1, population, set);
			if (set.getClassifier(0).equals(population.getClassifier(0)))
				atLeastOnce[0] = true;
			if (set.getClassifier(0).equals(population.getClassifier(1)))
				atLeastOnce[1] = true;
			if (set.getClassifier(0).equals(population.getClassifier(2)))
				atLeastOnce[2] = true;
		}

		for (int i = 0; i < atLeastOnce.length; i++) {
			assertTrue("Might fail with probability 1 -(9/14)^500",
					atLeastOnce[i]);
		}

	}

	/**
	 * Test method for
	 * {@link gr.auth.ee.lcs.geneticalgorithm.selectors.RouletteWheelSelector#select(int, gr.auth.ee.lcs.classifiers.ClassifierSet, gr.auth.ee.lcs.classifiers.ClassifierSet)}
	 * .
	 */
	@Test
	public void testZeroSelectionMin2() {
		final ClassifierSet population = new ClassifierSet(null);
		for (int i = 0; i < 3; i++) {
			Classifier aClassifier = lcs.getNewClassifier();
			aClassifier.setComparisonValue(
					AbstractUpdateStrategy.COMPARISON_MODE_EXPLOITATION, 0);
			aClassifier.setActionAdvocated(i);
			aClassifier.experience = 100;
			population.addClassifier(new Macroclassifier(aClassifier, i + 1),
					false);
		}

		final RouletteWheelSelector selector = new RouletteWheelSelector(
				AbstractUpdateStrategy.COMPARISON_MODE_EXPLORATION, false);

		final boolean[] atLeastOnce = new boolean[3];
		Arrays.fill(atLeastOnce, false);

		for (int i = 0; i < 500; i++) {
			ClassifierSet set = new ClassifierSet(null);
			selector.select(1, population, set);
			if (set.getClassifier(0).equals(population.getClassifier(0)))
				atLeastOnce[0] = true;
			if (set.getClassifier(0).equals(population.getClassifier(1)))
				atLeastOnce[1] = true;
			if (set.getClassifier(0).equals(population.getClassifier(2)))
				atLeastOnce[2] = true;
		}

		for (int i = 0; i < atLeastOnce.length; i++) {
			assertTrue("Might fail with probability 1 -(9/14)^500",
					atLeastOnce[i]);
		}

	}
}
