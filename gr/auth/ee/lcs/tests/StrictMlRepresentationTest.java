/**
 * 
 */
package gr.auth.ee.lcs.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ExtendedBitSet;
import gr.auth.ee.lcs.data.ClassifierTransformBridge;
import gr.auth.ee.lcs.data.representations.ComplexRepresentation.Attribute;
import gr.auth.ee.lcs.data.representations.GenericMultiLabelRepresentation;
import gr.auth.ee.lcs.data.representations.StrictMultiLabelRepresentation;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Miltos Allamanis
 * 
 */
public class StrictMlRepresentationTest {

	StrictMultiLabelRepresentation rep;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		GenericMultiLabelRepresentation.Attribute list[] = new Attribute[5];
		String[] names = { "Good", "Mediocre", "Bad" };
		rep = new StrictMultiLabelRepresentation(list, names, 3,
				StrictMultiLabelRepresentation.EXACT_MATCH);
		ClassifierTransformBridge.setInstance(rep);
		String[] attribute = { "A", "B", "A+" };
		list[0] = rep.new NominalAttribute(rep.getChromosomeSize(), "nom",
				attribute, 0);
		list[1] = rep.new NominalAttribute(rep.getChromosomeSize(), "nom2",
				attribute, 0);
		list[2] = rep.new Label(rep.getChromosomeSize(), "Good");
		list[3] = rep.new Label(rep.getChromosomeSize(), "Mediocre");
		list[4] = rep.new Label(rep.getChromosomeSize(), "Bad");

		rep.setClassificationStrategy(rep.new VotingClassificationStrategy());
		ClassifierTransformBridge.setInstance(rep);
	}

	@Test
	public void testClassifyAbilityAll() {
		ExtendedBitSet set1 = new ExtendedBitSet("10100111011");
		Classifier ex1 = new Classifier(set1);
		double[][] instances = { { 2, 0, 1, 0, 1 } };
		ClassifierTransformBridge.instances = instances;

		assertTrue(rep.classifyAbilityAll(ex1, 0) == 1);

		set1 = new ExtendedBitSet("11100111011");
		ex1 = new Classifier(set1);
		assertTrue(rep.classifyAbilityAll(ex1, 0) == 0);
	}

	/**
	 * Test method for
	 * {@link gr.auth.ee.lcs.data.representations.GenericMultiLabelRepresentation#classifyAbilityLabel(gr.auth.ee.lcs.classifiers.Classifier, int, int)}
	 * .
	 */
	@Test
	public void testClassifyAbilityLabel() {
		ExtendedBitSet set1 = new ExtendedBitSet("10100111011");
		Classifier ex1 = new Classifier(set1);
		double[][] instances = { { 2, 0, 1, 0, 1 } };
		ClassifierTransformBridge.instances = instances;
		assertTrue(rep.classifyAbilityLabel(ex1, 0, 0) == 1);
		assertTrue(rep.classifyAbilityLabel(ex1, 0, 1) == 1);
		assertTrue(rep.classifyAbilityLabel(ex1, 0, 2) == 1);

		set1 = new ExtendedBitSet("11100111011");
		ex1 = new Classifier(set1);
		assertTrue(rep.classifyAbilityLabel(ex1, 0, 0) == 1);
		assertTrue(rep.classifyAbilityLabel(ex1, 0, 1) == -1);
		assertTrue(rep.classifyAbilityLabel(ex1, 0, 2) == 1);

		set1 = new ExtendedBitSet("01100111011");
		ex1 = new Classifier(set1);
		assertTrue(rep.classifyAbilityLabel(ex1, 0, 0) == 1);
		assertTrue(rep.classifyAbilityLabel(ex1, 0, 1) == -1);
		assertTrue(rep.classifyAbilityLabel(ex1, 0, 2) == -1);

		set1 = new ExtendedBitSet("01000111011");
		ex1 = new Classifier(set1);
		assertTrue(rep.classifyAbilityLabel(ex1, 0, 0) == -1);
		assertTrue(rep.classifyAbilityLabel(ex1, 0, 1) == -1);
		assertTrue(rep.classifyAbilityLabel(ex1, 0, 2) == -1);
	}

	/**
	 * Test method for
	 * {@link gr.auth.ee.lcs.data.representations.GenericMultiLabelRepresentation#getClassification(gr.auth.ee.lcs.classifiers.Classifier)}
	 * .
	 */
	@Test
	public void testGetClassification() {
		ExtendedBitSet set1 = new ExtendedBitSet("10100111011");
		Classifier ex1 = new Classifier(set1);
		int[] instanceLabels = rep.getClassification(ex1);
		int[] expected = { 0, 2 };
		assertTrue(Arrays.equals(instanceLabels, expected));

		set1 = new ExtendedBitSet("00100111011");
		ex1 = new Classifier(set1);
		instanceLabels = rep.getClassification(ex1);
		int[] expected2 = { 0 };
		assertTrue(Arrays.equals(instanceLabels, expected2));

		set1 = new ExtendedBitSet("11100111011");
		ex1 = new Classifier(set1);
		instanceLabels = rep.getClassification(ex1);
		int[] expected3 = { 0, 1, 2 };
		assertTrue(Arrays.equals(instanceLabels, expected3));

		set1 = new ExtendedBitSet("0000111011");
		ex1 = new Classifier(set1);
		instanceLabels = rep.getClassification(ex1);
		int[] expected4 = {};
		assertTrue(Arrays.equals(instanceLabels, expected4));
	}

	/**
	 * Test method for
	 * {@link gr.auth.ee.lcs.data.representations.GenericMultiLabelRepresentation#getDataInstanceLabels(double[])}
	 * .
	 */
	@Test
	public void testGetDataInstanceLabels() {
		double[][] instances = { { 2, 0, 1, 0, 1 } };
		int[] instanceLabels = rep.getDataInstanceLabels(instances[0]);
		int[] expected = { 0, 2 };
		assertTrue(Arrays.equals(instanceLabels, expected));

		double[][] instances2 = { { 1, 1, 0, 0, 1 } };
		int[] instanceLabels2 = rep.getDataInstanceLabels(instances2[0]);
		int[] expected2 = { 2 };
		assertTrue(Arrays.equals(instanceLabels2, expected2));

		double[][] instances3 = { { 1, 1, 0, 0, 0 } };
		int[] instanceLabels3 = rep.getDataInstanceLabels(instances3[0]);
		int[] expected3 = {};
		assertTrue(Arrays.equals(instanceLabels3, expected3));

		double[][] instances4 = { { 1, 1, 1, 1, 1 } };
		int[] instanceLabels4 = rep.getDataInstanceLabels(instances4[0]);
		int[] expected4 = { 0, 1, 2 };
		assertTrue(Arrays.equals(instanceLabels4, expected4));
	}

	@Test
	public void testMoreGeneral() {
		ExtendedBitSet set1 = new ExtendedBitSet("10100111011");
		Classifier ex1 = new Classifier(set1);

		ExtendedBitSet set2 = new ExtendedBitSet("10100111011");
		Classifier ex2 = new Classifier(set2);

		assertTrue(rep.isMoreGeneral(ex1, ex2));
		assertTrue(rep.isMoreGeneral(ex2, ex1));
		assertTrue(rep.isMoreGeneral(ex1, ex1));

		set2 = new ExtendedBitSet("10100111010");
		ex2 = new Classifier(set1);
		assertTrue(rep.isMoreGeneral(ex2, ex1));

		set1 = new ExtendedBitSet("10100111011");
		ex1 = new Classifier(set1);
		set2 = new ExtendedBitSet("10100111010");
		ex2 = new Classifier(set2);
		assertFalse(rep.isMoreGeneral(ex1, ex2));
		assertTrue(rep.isMoreGeneral(ex2, ex1));

		set2 = new ExtendedBitSet("10100111011");
		ex2 = new Classifier(set2);
		assertTrue(rep.isMoreGeneral(ex1, ex2));
		assertTrue(rep.isMoreGeneral(ex2, ex1));

		set1 = new ExtendedBitSet("10100101010");
		ex1 = new Classifier(set1);
		set2 = new ExtendedBitSet("10100111011");
		ex2 = new Classifier(set2);
		assertTrue(rep.isMoreGeneral(ex1, ex2));
		assertFalse(rep.isMoreGeneral(ex2, ex1));
	}

	@Test
	public void classificationMethods() {
		double[][] instances = { { 1, 1, 0, 0, 1 } };
		ClassifierTransformBridge.instances = instances;

		ExtendedBitSet set = new ExtendedBitSet("10001010101");
		Classifier ex = new Classifier(set);

		assertTrue(rep.classifyExact(ex, 0) == 1);
		assertTrue(rep.classifyHamming(ex, 0) == 1);
		assertTrue(rep.classifyAccuracy(ex, 0) == 1);

		set = new ExtendedBitSet("10001010101");
		ex = new Classifier(set);
		assertTrue(rep.classifyExact(ex, 0) == 1);
		assertTrue(rep.classifyHamming(ex, 0) == 1);
		assertTrue(rep.classifyAccuracy(ex, 0) == 1);

		set = new ExtendedBitSet("11001010101");
		ex = new Classifier(set);
		assertTrue(rep.classifyExact(ex, 0) == 0);
		assertTrue(Math.abs(rep.classifyHamming(ex, 0) - (2. / 3.)) < 0.0001);
		assertTrue(rep.classifyAccuracy(ex, 0) == .5);

		set = new ExtendedBitSet("00101010101");
		ex = new Classifier(set);
		assertTrue(rep.classifyExact(ex, 0) == 0);
		assertTrue(Math.abs(rep.classifyHamming(ex, 0) - (1. / 3.)) < 0.0001);
		assertTrue(rep.classifyAccuracy(ex, 0) == 0);
	}

}
