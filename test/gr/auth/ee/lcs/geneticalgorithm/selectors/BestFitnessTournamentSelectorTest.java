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
package gr.auth.ee.lcs.geneticalgorithm.selectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import gr.auth.ee.lcs.MockLCS;
import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.classifiers.Macroclassifier;
import gr.auth.ee.lcs.data.AbstractUpdateStrategy;
import gr.auth.ee.lcs.data.representations.SimpleBooleanRepresentation;
import gr.auth.ee.lcs.data.updateAlgorithms.ASLCSUpdateAlgorithm;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests for Best Fitness Tournament Selector.
 * 
 * @author Miltos Allamanis
 * 
 */
public final class BestFitnessTournamentSelectorTest {

	/**
	 * A selector.
	 */
	TournamentSelector mySelector;

	/**
	 * A population.
	 */
	ClassifierSet population;

	/**
	 * The mock lcs.
	 */
	MockLCS lcs;

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

	@Test
	public void testTournament1() {
		final int participants[] = { 0, 0, 0 };
		mySelector = new TournamentSelector(3, true,
				AbstractUpdateStrategy.COMPARISON_MODE_EXPLORATION);

		assertTrue(mySelector.tournament(population, participants) == 0);
	}

	@Test
	public void testTournament2() {
		final int participants[] = { 0, 1, 0 };
		mySelector = new TournamentSelector(3, true,
				AbstractUpdateStrategy.COMPARISON_MODE_EXPLORATION);

		assertEquals(mySelector.tournament(population, participants), 1);
	}

	@Test
	public void testTournament3() {
		final int participants[] = { 2, 1, 0 };
		mySelector = new TournamentSelector(3, true,
				AbstractUpdateStrategy.COMPARISON_MODE_EXPLORATION);

		assertTrue(mySelector.tournament(population, participants) == 1);
	}

	@Test
	public void testTournament4() {
		final int participants[] = { 5, 1, 0 };
		mySelector = new TournamentSelector(3, true,
				AbstractUpdateStrategy.COMPARISON_MODE_EXPLORATION);

		System.out.println(mySelector.tournament(population, participants));
		assertTrue(mySelector.tournament(population, participants) == 2);
	}

	@Test
	public void testTournament5() {
		final int participants[] = { 5, 5, 5 };
		mySelector = new TournamentSelector(3, true,
				AbstractUpdateStrategy.COMPARISON_MODE_EXPLORATION);

		assertEquals(mySelector.tournament(population, participants), 2);
	}

	@Test
	public void testTournament6() {
		final int participants[] = { 5, 1, 0 };
		mySelector = new TournamentSelector(3, false,
				AbstractUpdateStrategy.COMPARISON_MODE_EXPLORATION);

		assertTrue(mySelector.tournament(population, participants) == 0);
	}

	@Test
	public void testTournament7() {
		final int participants[] = { 3, 5, 0 };
		mySelector = new TournamentSelector(3, false,
				AbstractUpdateStrategy.COMPARISON_MODE_EXPLORATION);

		assertTrue(mySelector.tournament(population, participants) == 0);
	}

}
