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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gr.auth.ee.lcs.MockLCS;
import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.data.representations.complex.ComplexRepresentation.AbstractAttribute;
import gr.auth.ee.lcs.utilities.ExtendedBitSet;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

/**
 * Test the strict- ml representation.
 * 
 * @author Miltos Allamanis
 * 
 */
public class StrictMlRepresentationTest {

	/**
	 * A global test representation instance.
	 */
	StrictMultiLabelRepresentation rep;

	MockLCS lcs;

	@Test
	public void classificationMethods() {
		final double[][] instances = { { 1, 1, 0, 0, 1 } };
		lcs.instances = instances;

		ExtendedBitSet set = new ExtendedBitSet("10001010101");
		Classifier ex = lcs.getNewClassifier(set);

		assertTrue(rep.classifyExact(ex, 0) == 1);
		assertTrue(rep.classifyHamming(ex, 0) == 1);
		assertTrue(rep.classifyAccuracy(ex, 0) == 1);

		set = new ExtendedBitSet("10001010101");
		ex = lcs.getNewClassifier(set);
		assertTrue(rep.classifyExact(ex, 0) == 1);
		assertTrue(rep.classifyHamming(ex, 0) == 1);
		assertTrue(rep.classifyAccuracy(ex, 0) == 1);

		set = new ExtendedBitSet("11001010101");
		ex = lcs.getNewClassifier(set);
		assertTrue(rep.classifyExact(ex, 0) == 0);
		assertTrue(Math.abs(rep.classifyHamming(ex, 0) - (2. / 3.)) < 0.0001);
		assertTrue(rep.classifyAccuracy(ex, 0) == .5);

		set = new ExtendedBitSet("00101010101");
		ex = lcs.getNewClassifier(set);
		assertTrue(rep.classifyExact(ex, 0) == 0);
		assertTrue(Math.abs(rep.classifyHamming(ex, 0) - (1. / 3.)) < 0.0001);
		assertTrue(rep.classifyAccuracy(ex, 0) == 0);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() {
		lcs = new MockLCS();
		final GenericMultiLabelRepresentation.AbstractAttribute list[] = new AbstractAttribute[5];
		final String[] names = { "Good", "Mediocre", "Bad" };
		rep = new StrictMultiLabelRepresentation(list, names, 3,
				StrictMultiLabelRepresentation.EXACT_MATCH, .7, lcs);

		final String[] attribute = { "A", "B", "A+" };
		list[0] = rep.new NominalAttribute(rep.getChromosomeSize(), "nom",
				attribute, 0);
		list[1] = rep.new NominalAttribute(rep.getChromosomeSize(), "nom2",
				attribute, 0);
		list[2] = rep.new Label(rep.getChromosomeSize(), "Good");
		list[3] = rep.new Label(rep.getChromosomeSize(), "Mediocre");
		list[4] = rep.new Label(rep.getChromosomeSize(), "Bad");

		rep.setClassificationStrategy(rep.new VotingClassificationStrategy(1));
		lcs.setElements(rep, null);
	}

	@Test
	public void testClassifyAbilityAll() {
		ExtendedBitSet set1 = new ExtendedBitSet("10100111011");
		Classifier ex1 = lcs.getNewClassifier(set1);
		final double[][] instances = { { 2, 0, 1, 0, 1 } };
		lcs.instances = instances;

		assertTrue(rep.classifyAbilityAll(ex1, 0) == 1);

		set1 = new ExtendedBitSet("11100111011");
		ex1 = lcs.getNewClassifier(set1);
		assertTrue(rep.classifyAbilityAll(ex1, 0) == 0);
	}

	/**
	 * Test method for
	 * {@link gr.auth.ee.lcs.data.representations.complex.GenericMultiLabelRepresentation#classifyAbilityLabel(gr.auth.ee.lcs.classifiers.Classifier, int, int)}
	 * .
	 */
	@Test
	public void testClassifyAbilityLabel() {
		ExtendedBitSet set1 = new ExtendedBitSet("10100111011");
		Classifier ex1 = lcs.getNewClassifier(set1);
		final double[][] instances = { { 2, 0, 1, 0, 1 } };
		lcs.instances = instances;
		assertTrue(rep.classifyAbilityLabel(ex1, 0, 0) == 1);
		assertTrue(rep.classifyAbilityLabel(ex1, 0, 1) == 1);
		assertTrue(rep.classifyAbilityLabel(ex1, 0, 2) == 1);

		set1 = new ExtendedBitSet("11100111011");
		ex1 = lcs.getNewClassifier(set1);
		assertTrue(rep.classifyAbilityLabel(ex1, 0, 0) == 1);
		assertTrue(rep.classifyAbilityLabel(ex1, 0, 1) == -1);
		assertTrue(rep.classifyAbilityLabel(ex1, 0, 2) == 1);

		set1 = new ExtendedBitSet("01100111011");
		ex1 = lcs.getNewClassifier(set1);
		assertTrue(rep.classifyAbilityLabel(ex1, 0, 0) == 1);
		assertTrue(rep.classifyAbilityLabel(ex1, 0, 1) == -1);
		assertTrue(rep.classifyAbilityLabel(ex1, 0, 2) == -1);

		set1 = new ExtendedBitSet("01000111011");
		ex1 = lcs.getNewClassifier(set1);
		assertTrue(rep.classifyAbilityLabel(ex1, 0, 0) == -1);
		assertTrue(rep.classifyAbilityLabel(ex1, 0, 1) == -1);
		assertTrue(rep.classifyAbilityLabel(ex1, 0, 2) == -1);
	}

	/**
	 * Test method for
	 * {@link gr.auth.ee.lcs.data.representations.complex.GenericMultiLabelRepresentation#getClassification(gr.auth.ee.lcs.classifiers.Classifier)}
	 * .
	 */
	@Test
	public void testGetClassification() {
		ExtendedBitSet set1 = new ExtendedBitSet("10100111011");
		Classifier ex1 = lcs.getNewClassifier(set1);
		int[] instanceLabels = rep.getClassification(ex1);
		final int[] expected = { 0, 2 };
		assertTrue(Arrays.equals(instanceLabels, expected));

		set1 = new ExtendedBitSet("00100111011");
		ex1 = lcs.getNewClassifier(set1);
		instanceLabels = rep.getClassification(ex1);
		final int[] expected2 = { 0 };
		assertTrue(Arrays.equals(instanceLabels, expected2));

		set1 = new ExtendedBitSet("11100111011");
		ex1 = lcs.getNewClassifier(set1);
		instanceLabels = rep.getClassification(ex1);
		final int[] expected3 = { 0, 1, 2 };
		assertTrue(Arrays.equals(instanceLabels, expected3));

		set1 = new ExtendedBitSet("0000111011");
		ex1 = lcs.getNewClassifier(set1);
		instanceLabels = rep.getClassification(ex1);
		final int[] expected4 = {};
		assertTrue(Arrays.equals(instanceLabels, expected4));
	}

	/**
	 * Test method for
	 * {@link gr.auth.ee.lcs.data.representations.complex.GenericMultiLabelRepresentation#getDataInstanceLabels(double[])}
	 * .
	 */
	@Test
	public void testGetDataInstanceLabels() {
		double[][] instances = { { 2, 0, 1, 0, 1 } };
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

		double[][] instances4 = { { 1, 1, 1, 1, 1 } };
		final int[] instanceLabels4 = rep.getDataInstanceLabels(instances4[0]);
		final int[] expected4 = { 0, 1, 2 };
		assertTrue(Arrays.equals(instanceLabels4, expected4));
	}

	@Test
	public void testMoreGeneral() {
		ExtendedBitSet set1 = new ExtendedBitSet("10100111011");
		Classifier ex1 = lcs.getNewClassifier(set1);

		ExtendedBitSet set2 = new ExtendedBitSet("10100111011");
		Classifier ex2 = lcs.getNewClassifier(set2);

		assertTrue(rep.isMoreGeneral(ex1, ex2));
		assertTrue(rep.isMoreGeneral(ex2, ex1));
		assertTrue(rep.isMoreGeneral(ex1, ex1));

		set2 = new ExtendedBitSet("10100111010");
		ex2 = lcs.getNewClassifier(set1);
		assertTrue(rep.isMoreGeneral(ex2, ex1));

		set1 = new ExtendedBitSet("10100111011");
		ex1 = lcs.getNewClassifier(set1);
		set2 = new ExtendedBitSet("10100111010");
		ex2 = lcs.getNewClassifier(set2);
		assertFalse(rep.isMoreGeneral(ex1, ex2));
		assertTrue(rep.isMoreGeneral(ex2, ex1));

		set2 = new ExtendedBitSet("10100111011");
		ex2 = lcs.getNewClassifier(set2);
		assertTrue(rep.isMoreGeneral(ex1, ex2));
		assertTrue(rep.isMoreGeneral(ex2, ex1));

		set1 = new ExtendedBitSet("10100101010");
		ex1 = lcs.getNewClassifier(set1);
		set2 = new ExtendedBitSet("10100111011");
		ex2 = lcs.getNewClassifier(set2);
		assertTrue(rep.isMoreGeneral(ex1, ex2));
		assertFalse(rep.isMoreGeneral(ex2, ex1));
	}

}
