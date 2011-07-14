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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import gr.auth.ee.lcs.MockLCS;
import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.classifiers.Macroclassifier;
import gr.auth.ee.lcs.data.representations.SimpleBooleanRepresentation;
import gr.auth.ee.lcs.utilities.ExtendedBitSet;

import org.junit.Before;
import org.junit.Test;

/**
 * A test for a ClassifierSet.
 * 
 * @author Miltos Allamanis
 * 
 */
public final class ClassifierSetTest {

	SimpleBooleanRepresentation test;

	MockLCS lcs;

	/**
	 * Setup test.
	 */
	@Before
	public void setup() {
		lcs = new MockLCS();
		test = new SimpleBooleanRepresentation(0.5, 4, lcs);
		lcs.setElements(test, null);

	}

	/**
	 * Test method for
	 * {@link gr.auth.ee.lcs.classifiers.ClassifierSet#addClassifier(gr.auth.ee.lcs.classifiers.Classifier, int)}
	 * .
	 */
	@Test
	public void testAddClassifier() {
		final ClassifierSet testSet = new ClassifierSet(null);
		final Classifier testClassifier = lcs
				.getNewClassifier(new ExtendedBitSet("10010111"));
		testClassifier.setActionAdvocated(1);

		assertEquals(testSet.getNumberOfMacroclassifiers(), 0);
		testSet.addClassifier(new Macroclassifier(testClassifier, 3), true);
		assertEquals(testSet.getNumberOfMacroclassifiers(), 1);
		testSet.addClassifier(new Macroclassifier(testClassifier, 2), true);
		assertEquals(testSet.getNumberOfMacroclassifiers(), 1);

		assertEquals(testSet.getClassifier(0), testClassifier);
		assertEquals(testSet.getClassifierNumerosity(testClassifier), 5);
		assertEquals(testSet.getTotalNumerosity(), 5);
		// Create a classifier with the same chromosome but different advocated
		// action
		final Classifier testClassifier2 = lcs
				.getNewClassifier(new ExtendedBitSet("10010111"));
		testClassifier2.setActionAdvocated(0);
		testSet.addClassifier(new Macroclassifier(testClassifier2, 3), true);
		assertEquals(testSet.getNumberOfMacroclassifiers(), 2);
		testSet.addClassifier(new Macroclassifier(testClassifier2, 10), true);
		assertEquals(testSet.getNumberOfMacroclassifiers(), 2);
		assertEquals(testSet.getClassifier(1), testClassifier2);
		assertEquals(testSet.getClassifierNumerosity(testClassifier2), 13);
		assertEquals(testSet.getTotalNumerosity(), 13 + 5);

		final Classifier testClassifier3 = lcs
				.getNewClassifier(new ExtendedBitSet("10001100"));
		testClassifier3.setActionAdvocated(1);

		testSet.addClassifier(new Macroclassifier(testClassifier3, 1), true);
		assertEquals(testSet.getNumberOfMacroclassifiers(), 3);
		testSet.addClassifier(new Macroclassifier(testClassifier3, 1), true);
		assertEquals(testSet.getNumberOfMacroclassifiers(), 3);
		assertEquals(testSet.getClassifier(2), testClassifier3);
		assertEquals(testSet.getClassifierNumerosity(testClassifier3), 2);
		assertEquals(testSet.getTotalNumerosity(), 13 + 5 + 2);
	}

	/**
	 * Test method for
	 * {@link gr.auth.ee.lcs.classifiers.ClassifierSet#deleteClassifier(gr.auth.ee.lcs.classifiers.Classifier)}
	 * .
	 */
	@Test
	public void testDeleteClassifier() {
		final ClassifierSet testSet = new ClassifierSet(null);
		final Classifier testClassifier = lcs
				.getNewClassifier(new ExtendedBitSet("10010111"));
		testClassifier.setActionAdvocated(1);

		assertEquals(testSet.getNumberOfMacroclassifiers(), 0);
		testSet.addClassifier(new Macroclassifier(testClassifier, 3), false);
		assertEquals(testSet.getNumberOfMacroclassifiers(), 1);
		assertEquals(testSet.getTotalNumerosity(), 3);

		testSet.deleteClassifier(testClassifier);
		assertEquals(testSet.getNumberOfMacroclassifiers(), 1);
		assertEquals(testSet.getClassifierNumerosity(testClassifier), 2);
		assertEquals(testSet.getTotalNumerosity(), 2);

		testSet.deleteClassifier(testClassifier);
		assertEquals(testSet.getNumberOfMacroclassifiers(), 1);
		assertEquals(testSet.getClassifierNumerosity(testClassifier), 1);
		assertEquals(testSet.getTotalNumerosity(), 1);

		testSet.deleteClassifier(testClassifier);
		assertEquals(testSet.getNumberOfMacroclassifiers(), 0);
		assertEquals(testSet.getClassifierNumerosity(testClassifier), 0);
		assertEquals(testSet.getTotalNumerosity(), 0);
		assertTrue(testSet.isEmpty());
	}

	@Test
	public void testRemoveAll() {
		final Classifier testClassifier = lcs
				.getNewClassifier(new ExtendedBitSet("10010111"));
		final ClassifierSet testSet = new ClassifierSet(null);
		for (int i = 0; i < 10; i++)
			testSet.addClassifier(new Macroclassifier(testClassifier, 1), true);
		assertEquals(testSet.getClassifierNumerosity(testClassifier), 10);
		assertEquals(testSet.getTotalNumerosity(), 10);
		assertEquals(testSet.getNumberOfMacroclassifiers(), 1);

		testSet.removeAllMacroclassifiers();

		assertEquals(testSet.getClassifierNumerosity(testClassifier), 0);
		assertEquals(testSet.getTotalNumerosity(), 0);
		assertEquals(testSet.getNumberOfMacroclassifiers(), 0);

	}

	/**
	 * Test method for
	 * {@link gr.auth.ee.lcs.classifiers.ClassifierSet#addFromClassifierSet(gr.auth.ee.lcs.classifiers.ClassifierSet, int)}
	 * .
	 */
	@Test
	public void testSubsumptionClassifierSet() {
		final ClassifierSet testSet = new ClassifierSet(null);
		final Classifier subsumableClassifier = lcs
				.getNewClassifier(new ExtendedBitSet("10010110"));
		subsumableClassifier.setSubsumptionAbility(true);
		testSet.addClassifier(new Macroclassifier(subsumableClassifier, 1),
				false);
		assertEquals(testSet.getNumberOfMacroclassifiers(), 1);
		assertEquals(testSet.getTotalNumerosity(), 1);
		assertEquals(testSet.getClassifierNumerosity(subsumableClassifier), 1);

		final Classifier testClassifier = lcs
				.getNewClassifier(new ExtendedBitSet("10010111"));
		assertTrue(subsumableClassifier.isMoreGeneral(testClassifier));

		testSet.addClassifier(new Macroclassifier(testClassifier, 1), true);

		assertEquals(testSet.getNumberOfMacroclassifiers(), 1);
		assertEquals(testSet.getClassifierNumerosity(0), 2);

		assertEquals(testSet.getClassifierNumerosity(testClassifier), 0);
		assertEquals(testSet.getClassifierNumerosity(subsumableClassifier), 2);

	}

}
