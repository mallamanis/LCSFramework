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
package gr.auth.ee.lcs.evaluators;

import static org.junit.Assert.assertTrue;
import static org.easymock.EasyMock.*;
import gr.auth.ee.lcs.AbstractLearningClassifierSystem;
import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.classifiers.Macroclassifier;
import gr.auth.ee.lcs.data.representations.complex.ComplexRepresentation.AbstractAttribute;
import gr.auth.ee.lcs.data.representations.complex.GenericMultiLabelRepresentation;
import gr.auth.ee.lcs.data.updateAlgorithms.ASLCSUpdateAlgorithm;
import gr.auth.ee.lcs.utilities.ExtendedBitSet;

import org.junit.Before;
import org.junit.Test;

/**
 * Test the AccuracyEvaluator class.
 * 
 * @author Miltos Allamanis
 * 
 */
public final class AccuracyEvaluatorTest {

	/**
	 * A generic ml representation.
	 */
	private GenericMultiLabelRepresentation rep;

	private AbstractLearningClassifierSystem lcs;

	@Before
	public void setUp() {
		lcs = createMock(AbstractLearningClassifierSystem.class);

		// Create instances
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

		rep.setClassificationStrategy(rep.new BestFitnessClassificationStrategy());

		final ASLCSUpdateAlgorithm update = new ASLCSUpdateAlgorithm(5, .99,
				50, 0.01, null, lcs);
		lcs.setElements(rep, update);
	}

	@Test
	public void test2EvaluateSet() {
		final double[][] instances = { { 0, 1, 1, 1, 1 }, { 0, 1, 1, 0, 1 } };
		final ExtendedBitSet set1 = new ExtendedBitSet("11011101111011");
		final Classifier ex1 = lcs.getNewClassifier(set1);

		final ClassifierSet set = new ClassifierSet(null);
		ex1.setComparisonValue(0, 1);
		ex1.experience = 100;
		set.addClassifier(new Macroclassifier(ex1, 1), false);
		final AccuracyRecallEvaluator a = new AccuracyRecallEvaluator(
				instances, false, lcs, AccuracyRecallEvaluator.TYPE_ACCURACY);
		lcs.setRulePopulation(set);

		final double evalResult = a.evaluateLCS(lcs);
		assertTrue(Math.abs(evalResult - (5 / 6.)) < .0001);

		final ExtendedBitSet set2 = new ExtendedBitSet("10011101111011");
		final Classifier ex2 = lcs.getNewClassifier(set2);
		set.deleteClassifier(0);
		ex2.setComparisonValue(0, 1);
		ex2.experience = 100;
		set.addClassifier(new Macroclassifier(ex2, 1), false);
		lcs.setRulePopulation(set);
		final double evalResult2 = a.evaluateLCS(lcs);
		assertTrue(Math.abs(evalResult2 - (5 / 12.)) < .0001);

	}

	@Test
	public void testEvaluateSet() {
		final double[][] instances = { { 0, 1, 1, 0, 1 }, { 0, 1, 1, 0, 1 } };
		final ExtendedBitSet set1 = new ExtendedBitSet("11011101111011");
		final Classifier ex1 = lcs.getNewClassifier(set1);
		ex1.experience = 100;
		final ClassifierSet set = new ClassifierSet(null);
		ex1.setComparisonValue(0, 1);
		set.addClassifier(new Macroclassifier(ex1, 1), false);
		final AccuracyRecallEvaluator a = new AccuracyRecallEvaluator(
				instances, false, lcs, AccuracyRecallEvaluator.TYPE_ACCURACY);
		lcs.setRulePopulation(set);
		assertTrue(a.evaluateLCS(lcs) == 1);
	}

}
