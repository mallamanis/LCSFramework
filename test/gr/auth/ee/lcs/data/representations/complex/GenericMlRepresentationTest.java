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
package gr.auth.ee.lcs.data.representations.complex;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gr.auth.ee.lcs.MockLCS;
import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.data.representations.complex.ComplexRepresentation.AbstractAttribute;
import gr.auth.ee.lcs.utilities.ExtendedBitSet;
import gr.auth.ee.lcs.utilities.ILabelSelector;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

/**
 * A Generic -ML repersentation test.
 * 
 * @author Miltos Allamanis
 * 
 */
public class GenericMlRepresentationTest {

	/**
	 * The reperesentation.
	 */
	GenericMultiLabelRepresentation rep;

	MockLCS lcs;

	@Test
	public void classificationMethods() {
		final double[][] instances = { { 1, 1, 0, 0, 1 } };
		lcs.instances = instances;
		final int[] instanceLabels1 = rep.getDataInstanceLabels(instances[0]);

		assertEquals(instanceLabels1.length, 1);
		assertEquals(instanceLabels1[0], 2);

		ExtendedBitSet set = new ExtendedBitSet("11010101010101");
		Classifier ex = lcs.getNewClassifier(set);

		assertTrue(rep.classifyAbsolute(ex, 0) == 1);
		assertTrue(rep.classifyHamming(ex, 0) == 1);
		assertTrue(rep.classifyAccuracy(ex, 0) == 1);

		set = new ExtendedBitSet("11000001010101");
		ex = lcs.getNewClassifier(set);
		assertTrue(rep.classifyAbsolute(ex, 0) == 1);
		assertTrue(rep.classifyHamming(ex, 0) == 1);
		assertTrue(rep.classifyAccuracy(ex, 0) == 1);

		set = new ExtendedBitSet("10000001010101");
		ex = lcs.getNewClassifier(set);
		assertTrue(rep.classifyAbsolute(ex, 0) == 0);
		assertTrue(rep.classifyHamming(ex, 0) == 0);
		assertTrue(rep.classifyAccuracy(ex, 0) == 1);

		set = new ExtendedBitSet("11110001010101");
		ex = lcs.getNewClassifier(set);
		assertTrue(rep.classifyAbsolute(ex, 0) == 0);
		assertTrue(Math.abs(rep.classifyHamming(ex, 0) - (1. / 2.)) < 0.0001);
		assertTrue(rep.classifyAccuracy(ex, 0) == .5);

		set = new ExtendedBitSet("11110101010101");
		ex = lcs.getNewClassifier(set);
		assertTrue(rep.classifyAbsolute(ex, 0) == 0);
		assertTrue(Math.abs(rep.classifyHamming(ex, 0) - (2. / 3.)) < 0.0001);
		assertTrue(rep.classifyAccuracy(ex, 0) == .5);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() {
		lcs = new MockLCS();

		final GenericMultiLabelRepresentation.AbstractAttribute list[] = new AbstractAttribute[5];
		final String[] names = { "Good", "Mediocre", "Bad" };
		rep = new GenericMultiLabelRepresentation(list, names, 3,
				GenericMultiLabelRepresentation.EXACT_MATCH, .33, .7, lcs);

		final String[] attribute = { "A", "B", "A+" };
		list[0] = rep.new NominalAttribute(rep.getChromosomeSize(), "nom",
				attribute, 0);
		list[1] = rep.new NominalAttribute(rep.getChromosomeSize(), "nom2",
				attribute, 0);
		list[2] = rep.new GenericLabel(rep.getChromosomeSize(), "Good", .33);
		list[3] = rep.new GenericLabel(rep.getChromosomeSize(), "Mediocre", .33);
		list[4] = rep.new GenericLabel(rep.getChromosomeSize(), "Bad", .33);

		rep.setClassificationStrategy(rep.new VotingClassificationStrategy(0));

		lcs.setElements(rep, null);
	}

	@Test
	public void testActivations() {

		// Activate only label 0
		ILabelSelector selector = new ILabelSelector() {

			@Override
			public int[] activeIndexes() {
				return null;
			}

			@Override
			public boolean getStatus(int labelIndex) {
				return labelIndex == 0;
			}

			@Override
			public boolean hasNext() {
				return false;
			}

			@Override
			public boolean next() {
				return false;
			}

			@Override
			public void reset() {
			}

		};

		rep.activateLabel(selector);

		ExtendedBitSet set1 = new ExtendedBitSet("11101100111011");
		Classifier ex1 = lcs.getNewClassifier(set1);

		ExtendedBitSet set2 = new ExtendedBitSet("11011100111011");
		Classifier ex2 = lcs.getNewClassifier(set2);

		assertTrue(ex1.equals(ex2));
		assertTrue(ex2.equals(ex1));
		assertTrue(ex1.isMoreGeneral(ex2));
		assertTrue(ex2.isMoreGeneral(ex1));
		rep.activateAllLabels();
		assertFalse(ex1.equals(ex2));
		assertFalse(ex2.equals(ex1));
		assertFalse(ex1.isMoreGeneral(ex2));
		assertTrue(ex2.isMoreGeneral(ex1));

		rep.activateLabel(selector);

		set1 = new ExtendedBitSet("11001100111011");
		ex1 = lcs.getNewClassifier(set1);

		set2 = new ExtendedBitSet("01011100111100");
		ex2 = lcs.getNewClassifier(set2);
		assertFalse(ex1.equals(ex2));
		assertFalse(ex2.equals(ex1));
		assertFalse(ex1.isMoreGeneral(ex2));
		assertTrue(ex2.isMoreGeneral(ex1));
		assertFalse(ex1.equals(ex2));

		// Activate only label 1
		ILabelSelector selector2 = new ILabelSelector() {

			@Override
			public int[] activeIndexes() {
				return null;
			}

			@Override
			public boolean getStatus(int labelIndex) {
				return labelIndex == 1;
			}

			@Override
			public boolean hasNext() {
				return false;
			}

			@Override
			public boolean next() {
				return false;
			}

			@Override
			public void reset() {
			}

		};

		rep.activateLabel(selector2);

		set1 = new ExtendedBitSet("11001100111011");
		ex1 = lcs.getNewClassifier(set1);

		set2 = new ExtendedBitSet("01010100111100");
		ex2 = lcs.getNewClassifier(set2);

		assertTrue(ex2.isMoreGeneral(ex1));
		assertFalse(ex1.isMoreGeneral(ex2));

		double[] vision = { 2, 0, 1, 0, 1 };
		assertTrue(ex2.isMatch(vision));
		assertTrue(ex1.isMatch(vision));

		rep.activateAllLabels();
		assertTrue(ex2.isMatch(vision));
		assertTrue(ex1.isMatch(vision));

		rep.activateLabel(selector2);
		double[][] sample1 = { { 2, 0, 1, 0, 1 } };
		lcs.instances = sample1;
		assertTrue(rep.classifyAbilityLabel(ex2, 0, 0) == 0);
		assertTrue(rep.classifyAbilityLabel(ex2, 0, 2) == 0);
		assertTrue(rep.classifyAbilityLabel(ex2, 0, 1) == 1);
		double[][] sample2 = { { 2, 0, 1, 1, 1 } };
		lcs.instances = sample2;
		assertTrue(rep.classifyAbilityLabel(ex2, 0, 0) == 0);
		assertTrue(rep.classifyAbilityLabel(ex2, 0, 2) == 0);
		assertTrue(rep.classifyAbilityLabel(ex2, 0, 1) == -1);

		rep.activateAllLabels(); // leave test to prior state

	}

	/**
	 * Test method for
	 * {@link gr.auth.ee.lcs.data.representations.complex.GenericMultiLabelRepresentation#classifyAbilityAll(gr.auth.ee.lcs.classifiers.Classifier, int)}
	 * .
	 */
	@Test
	public void testClassifyAbilityAll() {
		ExtendedBitSet set1 = new ExtendedBitSet("11011100111011");
		Classifier ex1 = lcs.getNewClassifier(set1);
		final double[][] instances = { { 2, 0, 1, 0, 1 } };
		lcs.instances = instances;

		assertTrue(rep.classifyAbilityAll(ex1, 0) == 1);

		set1 = new ExtendedBitSet("11111100111011");
		ex1 = lcs.getNewClassifier(set1);
		assertTrue(rep.classifyAbilityAll(ex1, 0) == 0);

		set1 = new ExtendedBitSet("11101000111011");
		ex1 = lcs.getNewClassifier(set1);
		assertTrue(rep.classifyAbilityAll(ex1, 0) == 1);

	}

	/**
	 * Test method for
	 * {@link gr.auth.ee.lcs.data.representations.complex.GenericMultiLabelRepresentation#classifyAbilityLabel(gr.auth.ee.lcs.classifiers.Classifier, int, int)}
	 * .
	 */
	@Test
	public void testClassifyAbilityLabel() {
		ExtendedBitSet set1 = new ExtendedBitSet("11011100111011");
		Classifier ex1 = lcs.getNewClassifier(set1);
		final double[][] instances = { { 2, 0, 1, 0, 1 } };
		lcs.instances = instances;
		assertTrue(rep.classifyAbilityLabel(ex1, 0, 0) == 1);
		assertTrue(rep.classifyAbilityLabel(ex1, 0, 1) == 1);
		assertTrue(rep.classifyAbilityLabel(ex1, 0, 2) == 1);

		set1 = new ExtendedBitSet("11111100111011");
		ex1 = lcs.getNewClassifier(set1);
		assertTrue(rep.classifyAbilityLabel(ex1, 0, 0) == 1);
		assertTrue(rep.classifyAbilityLabel(ex1, 0, 1) == -1);
		assertTrue(rep.classifyAbilityLabel(ex1, 0, 2) == 1);

		set1 = new ExtendedBitSet("10111100111011");
		ex1 = lcs.getNewClassifier(set1);
		assertTrue(rep.classifyAbilityLabel(ex1, 0, 0) == 1);
		assertTrue(rep.classifyAbilityLabel(ex1, 0, 1) == -1);
		assertTrue(rep.classifyAbilityLabel(ex1, 0, 2) == 0);

		set1 = new ExtendedBitSet("10101000111011");
		ex1 = lcs.getNewClassifier(set1);
		assertTrue(rep.classifyAbilityLabel(ex1, 0, 0) == 0);
		assertTrue(rep.classifyAbilityLabel(ex1, 0, 1) == 0);
		assertTrue(rep.classifyAbilityLabel(ex1, 0, 2) == 0);

		set1 = new ExtendedBitSet("01110100111011");
		ex1 = lcs.getNewClassifier(set1);
		assertTrue(rep.classifyAbilityLabel(ex1, 0, 0) == -1);
		assertTrue(rep.classifyAbilityLabel(ex1, 0, 1) == -1);
		assertTrue(rep.classifyAbilityLabel(ex1, 0, 2) == -1);
	}

	@Test
	public void testEquals() {
		ExtendedBitSet set1 = new ExtendedBitSet("11011100111011");
		Classifier ex1 = lcs.getNewClassifier(set1);

		ExtendedBitSet set2 = new ExtendedBitSet("11011100111011");
		Classifier ex2 = lcs.getNewClassifier(set2);

		assertTrue(ex1.equals(ex2));
		assertTrue(ex2.equals(ex1));
		assertTrue(ex1.equals(ex1));
		assertTrue(ex2.equals(ex2));

		set2 = new ExtendedBitSet("11011100111010");
		ex2 = lcs.getNewClassifier(set2);
		assertFalse(ex2.equals(ex1));
		assertFalse(ex1.equals(ex2));

		set1 = new ExtendedBitSet("11011100111000");
		ex1 = lcs.getNewClassifier(set1);
		assertTrue(ex1.equals(ex2));
		assertTrue(ex2.equals(ex1));

	}

	@Test
	public void testEquals2() {
		ExtendedBitSet set1 = new ExtendedBitSet("11001100111011");
		Classifier ex1 = lcs.getNewClassifier(set1);

		ExtendedBitSet set2 = new ExtendedBitSet("11001100111011");
		Classifier ex2 = lcs.getNewClassifier(set2);

		assertTrue(ex1.equals(ex2));
		assertTrue(ex2.equals(ex1));

		set2 = new ExtendedBitSet("11101100111011");
		ex2 = lcs.getNewClassifier(set2);

		assertTrue(ex1.equals(ex2));
		assertTrue(ex2.equals(ex1));

		set2 = new ExtendedBitSet("01101100111011");
		ex2 = lcs.getNewClassifier(set2);
		assertFalse(ex2.equals(ex1));
		assertFalse(ex1.equals(ex2));

		set2 = new ExtendedBitSet("00101100111011");
		ex2 = lcs.getNewClassifier(set2);
		assertFalse(ex2.equals(ex1));
		assertFalse(ex1.equals(ex2));

	}

	/**
	 * Test method for
	 * {@link gr.auth.ee.lcs.data.representations.complex.GenericMultiLabelRepresentation#getClassification(gr.auth.ee.lcs.classifiers.Classifier)}
	 * .
	 */
	@Test
	public void testGetClassification() {
		ExtendedBitSet set1 = new ExtendedBitSet("11011100111011");
		Classifier ex1 = lcs.getNewClassifier(set1);
		int[] instanceLabels = rep.getClassification(ex1);
		final int[] expected = { 0, 2 };
		assertTrue(Arrays.equals(instanceLabels, expected));

		set1 = new ExtendedBitSet("11001100111011");
		ex1 = lcs.getNewClassifier(set1);
		instanceLabels = rep.getClassification(ex1);
		assertTrue(Arrays.equals(instanceLabels, expected));

		set1 = new ExtendedBitSet("10001100111011");
		ex1 = lcs.getNewClassifier(set1);
		instanceLabels = rep.getClassification(ex1);
		final int[] expected2 = { 0 };
		assertTrue(Arrays.equals(instanceLabels, expected2));

		set1 = new ExtendedBitSet("00001100111011");
		ex1 = lcs.getNewClassifier(set1);
		instanceLabels = rep.getClassification(ex1);
		assertTrue(Arrays.equals(instanceLabels, expected2));

		set1 = new ExtendedBitSet("1000100111011");
		ex1 = lcs.getNewClassifier(set1);
		instanceLabels = rep.getClassification(ex1);
		final int[] expected3 = {};
		assertTrue(Arrays.equals(instanceLabels, expected3));
	}

	/**
	 * Test method for
	 * {@link gr.auth.ee.lcs.data.representations.complex.GenericMultiLabelRepresentation#getDataInstanceLabels(double[])}
	 * .
	 */
	@Test
	public void testGetDataInstanceLabels() {
		final double[][] instances = { { 2, 0, 1, 0, 1 } };
		final int[] instanceLabels = rep.getDataInstanceLabels(instances[0]);
		final int[] expected = { 0, 2 };
		assertTrue(Arrays.equals(instanceLabels, expected));

		final double[][] instances2 = { { 1, 1, 0, 0, 1 } };
		final int[] instanceLabels2 = rep.getDataInstanceLabels(instances2[0]);
		final int[] expected2 = { 2 };
		assertTrue(Arrays.equals(instanceLabels2, expected2));

		final double[][] instances3 = { { 1, 1, 0, 0, 0 } };
		final int[] instanceLabels3 = rep.getDataInstanceLabels(instances3[0]);
		final int[] expected3 = {};
		assertTrue(Arrays.equals(instanceLabels3, expected3));

		final double[][] instances4 = { { 1, 1, 1, 1, 1 } };
		final int[] instanceLabels4 = rep.getDataInstanceLabels(instances4[0]);
		final int[] expected4 = { 0, 1, 2 };
		assertTrue(Arrays.equals(instanceLabels4, expected4));
	}

	@Test
	public void testMoreGeneral() {
		ExtendedBitSet set1 = new ExtendedBitSet("11011100111011");
		Classifier ex1 = lcs.getNewClassifier(set1);

		ExtendedBitSet set2 = new ExtendedBitSet("11011100111011");
		Classifier ex2 = lcs.getNewClassifier(set2);

		assertTrue(rep.isMoreGeneral(ex1, ex2));
		assertTrue(rep.isMoreGeneral(ex1, ex1));
		assertTrue(rep.isMoreGeneral(ex2, ex2));

		set2 = new ExtendedBitSet("11011100111010");
		ex2 = lcs.getNewClassifier(set1);
		assertTrue(rep.isMoreGeneral(ex2, ex1));

		set1 = new ExtendedBitSet("11011100111011");
		ex1 = lcs.getNewClassifier(set1);
		set2 = new ExtendedBitSet("11011100111010");
		ex2 = lcs.getNewClassifier(set2);
		assertFalse(rep.isMoreGeneral(ex1, ex2));
		assertTrue(rep.isMoreGeneral(ex2, ex1));

		set2 = new ExtendedBitSet("11001100111011");
		ex2 = lcs.getNewClassifier(set2);
		assertTrue(rep.isMoreGeneral(ex1, ex2));
		assertFalse(rep.isMoreGeneral(ex2, ex1));

		set1 = new ExtendedBitSet("11011100101010");
		ex1 = lcs.getNewClassifier(set1);
		set2 = new ExtendedBitSet("11001100111011");
		ex2 = lcs.getNewClassifier(set2);
		assertTrue(rep.isMoreGeneral(ex1, ex2));
		assertFalse(rep.isMoreGeneral(ex2, ex1));

		set1 = new ExtendedBitSet("11011100101010");
		ex1 = lcs.getNewClassifier(set1);
		set2 = new ExtendedBitSet("11101100111011");
		ex2 = lcs.getNewClassifier(set2);
		assertTrue(rep.isMoreGeneral(ex1, ex2));
		assertTrue(ex1.isMoreGeneral(ex2));
		assertFalse(rep.isMoreGeneral(ex2, ex1));
		assertFalse(ex2.isMoreGeneral(ex1));
		assertFalse(ex2.equals(ex1));
		assertFalse(ex1.equals(ex2));

		set1 = new ExtendedBitSet("11011100101010");
		ex1 = lcs.getNewClassifier(set1);
		set2 = new ExtendedBitSet("01101100111011");
		ex2 = lcs.getNewClassifier(set2);
		assertFalse(rep.isMoreGeneral(ex2, ex1));
		assertFalse(ex2.isMoreGeneral(ex1));
		assertFalse(rep.isMoreGeneral(ex1, ex2));
		assertFalse(ex1.isMoreGeneral(ex2));
		assertFalse(ex2.equals(ex1));
		assertFalse(ex1.equals(ex2));

		set1 = new ExtendedBitSet("10011000101010");
		ex1 = lcs.getNewClassifier(set1);
		set2 = new ExtendedBitSet("11011100101010");
		ex2 = lcs.getNewClassifier(set2);
		assertTrue(ex2.isMoreGeneral(ex1));
		assertFalse(ex1.isMoreGeneral(ex2));

	}

	@Test
	public void testVoting() {
		final ExtendedBitSet set1 = new ExtendedBitSet("11011100111011");
		final Classifier ex1 = lcs.getNewClassifier(set1);
		// TODO
	}

}
