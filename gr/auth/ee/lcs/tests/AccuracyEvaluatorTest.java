package gr.auth.ee.lcs.tests;

import static org.junit.Assert.assertTrue;
import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.classifiers.Macroclassifier;
import gr.auth.ee.lcs.data.representations.ComplexRepresentation.AbstractAttribute;
import gr.auth.ee.lcs.data.representations.GenericMultiLabelRepresentation;
import gr.auth.ee.lcs.data.updateAlgorithms.ASLCSUpdateAlgorithm;
import gr.auth.ee.lcs.evaluators.AccuracyEvaluator;
import gr.auth.ee.lcs.tests.mocks.MockLCS;
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
	GenericMultiLabelRepresentation rep;

	MockLCS lcs;

	@Before
	public void setUp() throws Exception {
		lcs = new MockLCS();

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
		final AccuracyEvaluator a = new AccuracyEvaluator(instances, false, lcs);
		final double evalResult = a.evaluateSet(set);
		assertTrue(Math.abs(evalResult - 5 / 6.) < .0001);
		
		//final ExtendedBitSet set2 = new ExtendedBitSet("10011101111011");
		//final Classifier ex2 = lcs.getNewClassifier(set2);
		//set.deleteClassifier(0);
		//ex2.setComparisonValue(0, 1);
		//ex2.experience = 100;
		//set.addClassifier(new Macroclassifier(ex2, 1), false);
		//final double evalResult2 = a.evaluateSet(set);
		//assertTrue(Math.abs(evalResult2 - 3 / 6.) < .0001); //TODO: fix
		
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
		final AccuracyEvaluator a = new AccuracyEvaluator(instances, false, lcs);
		assertTrue(a.evaluateSet(set) == 1);
	}

}
