/**
 * 
 */
package gr.auth.ee.lcs.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.classifiers.ExtendedBitSet;
import gr.auth.ee.lcs.classifiers.Macroclassifier;
import gr.auth.ee.lcs.data.ClassifierTransformBridge;
import gr.auth.ee.lcs.data.SimpleBooleanRepresentation;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Miltos Allamanis
 * 
 */
public class ClassifierSetTest {

	SimpleBooleanRepresentation test;

	/**
	 * Setup test
	 */
	@Before
	public void setup() {
		test = new SimpleBooleanRepresentation(0.5, 4);
		ClassifierTransformBridge.setInstance(test);

	}

	/**
	 * Test method for
	 * {@link gr.auth.ee.lcs.classifiers.ClassifierSet#addClassifier(gr.auth.ee.lcs.classifiers.Classifier, int)}
	 * .
	 */
	@Test
	public void testAddClassifier() {
		ClassifierSet testSet = new ClassifierSet(null);
		Classifier testClassifier = new Classifier();
		testClassifier.setActionAdvocated(1);
		testClassifier.chromosome = new ExtendedBitSet("10010111");

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
		Classifier testClassifier2 = new Classifier();
		testClassifier2.setActionAdvocated(0);
		testClassifier2.chromosome = new ExtendedBitSet("10010111");
		testSet.addClassifier(new Macroclassifier(testClassifier2, 3), true);
		assertEquals(testSet.getNumberOfMacroclassifiers(), 2);
		testSet.addClassifier(new Macroclassifier(testClassifier2, 10), true);
		assertEquals(testSet.getNumberOfMacroclassifiers(), 2);
		assertEquals(testSet.getClassifier(1), testClassifier2);
		assertEquals(testSet.getClassifierNumerosity(testClassifier2), 13);
		assertEquals(testSet.getTotalNumerosity(), 13 + 5);

		Classifier testClassifier3 = new Classifier();
		testClassifier3.setActionAdvocated(1);
		testClassifier2.chromosome = new ExtendedBitSet("10001100");
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
		ClassifierSet testSet = new ClassifierSet(null);
		Classifier testClassifier = new Classifier();
		testClassifier.setActionAdvocated(1);
		testClassifier.chromosome = new ExtendedBitSet("10010111");

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

	/**
	 * Test method for
	 * {@link gr.auth.ee.lcs.classifiers.ClassifierSet#addFromClassifierSet(gr.auth.ee.lcs.classifiers.ClassifierSet, int)}
	 * .
	 */
	@Test
	public void testAddFromClassifierSet() {

	}

}
