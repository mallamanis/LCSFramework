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
package gr.auth.ee.lcs.data.representations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.easymock.EasyMock.*;
import gr.auth.ee.lcs.AbstractLearningClassifierSystem;
import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.utilities.ExtendedBitSet;

import org.junit.Before;
import org.junit.Test;

/**
 * A test for @see gr.auth.ee.data.SimpleBooleanRepresentation .
 * 
 * @author Miltos Allamanis
 * 
 */
public class SimpleBooleanRepresentationTest {

	private AbstractLearningClassifierSystem lcs;

	@Before
	public void setUp() {
		lcs = createMock(AbstractLearningClassifierSystem.class);
	}

	/**
	 * Test method for
	 * {@link gr.auth.ee.lcs.data.representations.SimpleBooleanRepresentation#createRandomCoveringClassifier(double[],int)}
	 * .
	 */
	@Test
	public void testCreateRandomCoveringClassifier() {
		final SimpleBooleanRepresentation test = new SimpleBooleanRepresentation(
				0.5, 4, lcs);
		lcs.setElements(test, null);

		final double visionVector[] = new double[4];
		Classifier testClassifier;

		visionVector[0] = 0;
		visionVector[1] = 0;
		visionVector[2] = 1;
		visionVector[3] = 1; // 1100
		// visionVector[4] = 0; // 01100
		for (int i = 0; i < 10; i++) { // Generate 10 random
			testClassifier = test.createRandomCoveringClassifier(visionVector);
			assertTrue(test.isMatch(visionVector, testClassifier));
		}

		visionVector[0] = 1;
		visionVector[1] = 0;
		visionVector[2] = 1;
		visionVector[3] = 0; // 1010
		// visionVector[4] = 0; // 01010
		for (int i = 0; i < 10; i++) { // Generate 10 random
			testClassifier = test.createRandomCoveringClassifier(visionVector);
			assertTrue(test.isMatch(visionVector, testClassifier));
		}
	}

	/**
	 * Test method for
	 * {@link gr.auth.ee.lcs.data.representations.SimpleBooleanRepresentation#getChromosomeSize()}
	 * .
	 */
	@Test
	public void testGetChromosomeSize() {
		SimpleBooleanRepresentation test = new SimpleBooleanRepresentation(0.5,
				4, lcs);
		assertEquals(test.getChromosomeSize(), 8);
		test = new SimpleBooleanRepresentation(0.5, 5, lcs);
		assertEquals(test.getChromosomeSize(), 10);
	}

	/**
	 * Test method for
	 * {@link gr.auth.ee.lcs.data.representations.SimpleBooleanRepresentation#isMatch(double[], gr.auth.ee.lcs.utilities.ExtendedBitSet)}
	 * .
	 */
	@Test
	public void testIsMatch() {
		final SimpleBooleanRepresentation test = new SimpleBooleanRepresentation(
				0, 4, lcs);
		lcs.setElements(test, null);
		// Test Mask 1##0
		Classifier testClassifier = lcs.getNewClassifier(new ExtendedBitSet(
				"11100001"));
		final double visionVector[] = new double[4];

		visionVector[0] = 0;
		visionVector[1] = 0;
		visionVector[2] = 1;
		visionVector[3] = 1; // 1100
		assertTrue(test.isMatch(visionVector, testClassifier));

		visionVector[0] = 0;
		visionVector[1] = 0;
		visionVector[2] = 0;
		visionVector[3] = 1; // 1000
		assertTrue(test.isMatch(visionVector, testClassifier));

		visionVector[0] = 1;
		visionVector[1] = 0;
		visionVector[2] = 0;
		visionVector[3] = 1; // 1001
		assertFalse(test.isMatch(visionVector, testClassifier));

		// Test Mask ####
		testClassifier = lcs.getNewClassifier(new ExtendedBitSet("00000000"));

		visionVector[0] = 0;
		visionVector[1] = 0;
		visionVector[2] = 0;
		visionVector[3] = 1; // 1000
		assertTrue(test.isMatch(visionVector, testClassifier));

		visionVector[0] = 1;
		visionVector[1] = 1;
		visionVector[2] = 1;
		visionVector[3] = 1; // 1111
		assertTrue(test.isMatch(visionVector, testClassifier));

		visionVector[0] = 0;
		visionVector[1] = 0;
		visionVector[2] = 0;
		visionVector[3] = 0; // 0000
		assertTrue(test.isMatch(visionVector, testClassifier));

		// Test Mask 0000
		testClassifier = lcs.getNewClassifier(new ExtendedBitSet("01010101"));

		visionVector[0] = 0;
		visionVector[1] = 0;
		visionVector[2] = 0;
		visionVector[3] = 0; // 0000
		assertTrue(test.isMatch(visionVector, testClassifier));

		visionVector[0] = 1;
		visionVector[1] = 0;
		visionVector[2] = 1;
		visionVector[3] = 0; // 1010
		assertFalse(test.isMatch(visionVector, testClassifier));

		visionVector[0] = 1;
		visionVector[1] = 1;
		visionVector[2] = 1;
		visionVector[3] = 1; // 1111
		assertFalse(test.isMatch(visionVector, testClassifier));

		// Test Mask 1111
		testClassifier = lcs.getNewClassifier(new ExtendedBitSet("11111111"));

		visionVector[0] = 1;
		visionVector[1] = 1;
		visionVector[2] = 1;
		visionVector[3] = 1; // 1111
		assertTrue(test.isMatch(visionVector, testClassifier));

		visionVector[0] = 1;
		visionVector[1] = 0;
		visionVector[2] = 1;
		visionVector[3] = 0; // 1010
		assertFalse(test.isMatch(visionVector, testClassifier));

		visionVector[0] = 0;
		visionVector[1] = 0;
		visionVector[2] = 0;
		visionVector[3] = 1; // 1000
		assertFalse(test.isMatch(visionVector, testClassifier));
	}

	/**
	 * Test method for
	 * {@link gr.auth.ee.lcs.data.representations.SimpleBooleanRepresentation#isMoreGeneral(gr.auth.ee.lcs.classifiers.Classifier, gr.auth.ee.lcs.classifiers.Classifier)}
	 * .
	 */
	@Test
	public void testIsMoreGeneral() {
		final SimpleBooleanRepresentation testRep = new SimpleBooleanRepresentation(
				0.5, 4, lcs);
		lcs.setElements(testRep, null);

		Classifier base = lcs.getNewClassifier();
		Classifier test = lcs.getNewClassifier();

		base = lcs.getNewClassifier(new ExtendedBitSet("10110010")); // #1##
		test = lcs.getNewClassifier(new ExtendedBitSet("11110110")); // 110#
		base.setActionAdvocated(1);
		test.setActionAdvocated(1);
		assertTrue(testRep.isMoreGeneral(base, test));

		base = lcs.getNewClassifier(new ExtendedBitSet("11110010")); // 11##
		test = lcs.getNewClassifier(new ExtendedBitSet("11110110")); // 110#
		base.setActionAdvocated(1);
		test.setActionAdvocated(0);
		assertFalse(testRep.isMoreGeneral(base, test));

		base = lcs.getNewClassifier(new ExtendedBitSet("11110010")); // 11##
		test = lcs.getNewClassifier(new ExtendedBitSet("10110110")); // #10#
		base.setActionAdvocated(0);
		test.setActionAdvocated(0);
		assertFalse(testRep.isMoreGeneral(base, test));

		test = lcs.getNewClassifier(new ExtendedBitSet("11110010")); // 11##
		assertTrue(testRep.isMoreGeneral(base, test));

		test = lcs.getNewClassifier(new ExtendedBitSet("10110010")); // #1##
		assertFalse(testRep.isMoreGeneral(base, test));

	}
	
	@Test
	public void testIsSpecific() {
		final SimpleBooleanRepresentation testRep = new SimpleBooleanRepresentation(
				0.5, 4, lcs);
		lcs.setElements(testRep, null);

		Classifier test1 = lcs.getNewClassifier(new ExtendedBitSet("10110010"));
		
		assertFalse(testRep.isAttributeSpecific(test1, 3));
		assertTrue(testRep.isAttributeSpecific(test1, 2));
		assertFalse(testRep.isAttributeSpecific(test1, 1));
		assertFalse(testRep.isAttributeSpecific(test1, 0));
		
		Classifier test2 = lcs.getNewClassifier(new ExtendedBitSet("11110110"));
		assertFalse(testRep.isAttributeSpecific(test2, 0));
		assertTrue(testRep.isAttributeSpecific(test2, 1));
		assertTrue(testRep.isAttributeSpecific(test2, 2));
		assertTrue(testRep.isAttributeSpecific(test2, 3));
	}

	/**
	 * Test method for
	 * {@link gr.auth.ee.lcs.data.representations.SimpleBooleanRepresentation#setVisionSize(int)}
	 * .
	 */
	@Test
	public void testSetVisionSize() {
		final SimpleBooleanRepresentation test = new SimpleBooleanRepresentation(
				0.5, 4, lcs);
		test.setVisionSize(10);
		assertEquals(test.getChromosomeSize(), 20);
	}

	/**
	 * Test method for
	 * {@link gr.auth.ee.lcs.data.representations.SimpleBooleanRepresentation#toNaturalLanguageString(gr.auth.ee.lcs.classifiers.Classifier)}
	 * .
	 */
	@Test
	public void testToNaturalLanguageString() {
		final SimpleBooleanRepresentation test = new SimpleBooleanRepresentation(
				0, 4, lcs);
		lcs.setElements(test, null);
		Classifier testClassifier;

		testClassifier = lcs.getNewClassifier(new ExtendedBitSet("01110110"));
		testClassifier.setActionAdvocated(0);
		assertEquals(test.toNaturalLanguageString(testClassifier), "010#=>0");

		testClassifier = lcs.getNewClassifier(new ExtendedBitSet("10011111"));
		testClassifier.setActionAdvocated(1);
		assertEquals(test.toNaturalLanguageString(testClassifier), "#011=>1");

		testClassifier = lcs.getNewClassifier(new ExtendedBitSet("00000000"));
		testClassifier.setActionAdvocated(1);
		assertEquals(test.toNaturalLanguageString(testClassifier), "####=>1");
	}

}
