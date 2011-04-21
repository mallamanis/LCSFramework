/**
 * 
 */
package gr.auth.ee.lcs.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ExtendedBitSet;
import gr.auth.ee.lcs.data.ClassifierTransformBridge;
import gr.auth.ee.lcs.data.representations.ComplexRepresentation.AbstractAttribute;
import gr.auth.ee.lcs.data.representations.GenericMultiLabelRepresentation;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Miltos Allamanis
 * 
 */
public class GenericMlRepresentationTest {

	GenericMultiLabelRepresentation rep;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		GenericMultiLabelRepresentation.AbstractAttribute list[] = new AbstractAttribute[5];
		String[] names = { "Good", "Mediocre", "Bad" };
		rep = new GenericMultiLabelRepresentation(list, names, 3,
				GenericMultiLabelRepresentation.EXACT_MATCH, .33);
		ClassifierTransformBridge.setInstance(rep);
		String[] attribute = { "A", "B", "A+" };
		list[0] = rep.new NominalAttribute(rep.getChromosomeSize(), "nom",
				attribute, 0);
		list[1] = rep.new NominalAttribute(rep.getChromosomeSize(), "nom2",
				attribute, 0);
		list[2] = rep.new GenericLabel(rep.getChromosomeSize(), "Good", .33);
		list[3] = rep.new GenericLabel(rep.getChromosomeSize(), "Mediocre", .33);
		list[4] = rep.new GenericLabel(rep.getChromosomeSize(), "Bad", .33);

		rep.setClassificationStrategy(rep.new VotingClassificationStrategy(0));
		ClassifierTransformBridge.setInstance(rep);
	}

	/**
	 * Test method for
	 * {@link gr.auth.ee.lcs.data.representations.GenericMultiLabelRepresentation#classifyAbilityAll(gr.auth.ee.lcs.classifiers.Classifier, int)}
	 * .
	 */
	@Test
	public void testClassifyAbilityAll() {
		ExtendedBitSet set1 = new ExtendedBitSet("11011100111011");
		Classifier ex1 = new Classifier(set1);
		double[][] instances = { { 2, 0, 1, 0, 1 } };
		ClassifierTransformBridge.instances = instances;

		assertTrue(rep.classifyAbilityAll(ex1, 0) == 1);

		set1 = new ExtendedBitSet("11111100111011");
		ex1 = new Classifier(set1);
		assertTrue(rep.classifyAbilityAll(ex1, 0) == 0);

		set1 = new ExtendedBitSet("11101000111011");
		ex1 = new Classifier(set1);
		assertTrue(rep.classifyAbilityAll(ex1, 0) == 1);

	}

	/**
	 * Test method for
	 * {@link gr.auth.ee.lcs.data.representations.GenericMultiLabelRepresentation#classifyAbilityLabel(gr.auth.ee.lcs.classifiers.Classifier, int, int)}
	 * .
	 */
	@Test
	public void testClassifyAbilityLabel() {
		ExtendedBitSet set1 = new ExtendedBitSet("11011100111011");
		Classifier ex1 = new Classifier(set1);
		double[][] instances = { { 2, 0, 1, 0, 1 } };
		ClassifierTransformBridge.instances = instances;
		assertTrue(rep.classifyAbilityLabel(ex1, 0, 0) == 1);
		assertTrue(rep.classifyAbilityLabel(ex1, 0, 1) == 1);
		assertTrue(rep.classifyAbilityLabel(ex1, 0, 2) == 1);

		set1 = new ExtendedBitSet("11111100111011");
		ex1 = new Classifier(set1);
		assertTrue(rep.classifyAbilityLabel(ex1, 0, 0) == 1);
		assertTrue(rep.classifyAbilityLabel(ex1, 0, 1) == -1);
		assertTrue(rep.classifyAbilityLabel(ex1, 0, 2) == 1);

		set1 = new ExtendedBitSet("10111100111011");
		ex1 = new Classifier(set1);
		assertTrue(rep.classifyAbilityLabel(ex1, 0, 0) == 1);
		assertTrue(rep.classifyAbilityLabel(ex1, 0, 1) == -1);
		assertTrue(rep.classifyAbilityLabel(ex1, 0, 2) == 0);

		set1 = new ExtendedBitSet("10101000111011");
		ex1 = new Classifier(set1);
		assertTrue(rep.classifyAbilityLabel(ex1, 0, 0) == 0);
		assertTrue(rep.classifyAbilityLabel(ex1, 0, 1) == 0);
		assertTrue(rep.classifyAbilityLabel(ex1, 0, 2) == 0);

		set1 = new ExtendedBitSet("01110100111011");
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
		ExtendedBitSet set1 = new ExtendedBitSet("11011100111011");
		Classifier ex1 = new Classifier(set1);
		int[] instanceLabels = rep.getClassification(ex1);
		int[] expected = { 0, 2 };
		assertTrue(Arrays.equals(instanceLabels, expected));

		set1 = new ExtendedBitSet("11001100111011");
		ex1 = new Classifier(set1);
		instanceLabels = rep.getClassification(ex1);
		assertTrue(Arrays.equals(instanceLabels, expected));

		set1 = new ExtendedBitSet("10001100111011");
		ex1 = new Classifier(set1);
		instanceLabels = rep.getClassification(ex1);
		int[] expected2 = { 0 };
		assertTrue(Arrays.equals(instanceLabels, expected2));

		set1 = new ExtendedBitSet("00001100111011");
		ex1 = new Classifier(set1);
		instanceLabels = rep.getClassification(ex1);
		assertTrue(Arrays.equals(instanceLabels, expected2));

		set1 = new ExtendedBitSet("1000100111011");
		ex1 = new Classifier(set1);
		instanceLabels = rep.getClassification(ex1);
		int[] expected3 = {};
		assertTrue(Arrays.equals(instanceLabels, expected3));
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
		ExtendedBitSet set1 = new ExtendedBitSet("11011100111011");
		Classifier ex1 = new Classifier(set1);

		ExtendedBitSet set2 = new ExtendedBitSet("11011100111011");
		Classifier ex2 = new Classifier(set2);

		assertTrue(rep.isMoreGeneral(ex1, ex2));
		assertTrue(rep.isMoreGeneral(ex1, ex1));

		set2 = new ExtendedBitSet("11011100111010");
		ex2 = new Classifier(set1);
		assertTrue(rep.isMoreGeneral(ex2, ex1));

		set1 = new ExtendedBitSet("11011100111011");
		ex1 = new Classifier(set1);
		set2 = new ExtendedBitSet("11011100111010");
		ex2 = new Classifier(set2);
		assertFalse(rep.isMoreGeneral(ex1, ex2));
		assertTrue(rep.isMoreGeneral(ex2, ex1));

		set2 = new ExtendedBitSet("11001100111011");
		ex2 = new Classifier(set2);
		assertTrue(rep.isMoreGeneral(ex1, ex2));
		assertFalse(rep.isMoreGeneral(ex2, ex1));

		set1 = new ExtendedBitSet("11011100101010");
		ex1 = new Classifier(set1);
		set2 = new ExtendedBitSet("11001100111011");
		ex2 = new Classifier(set2);
		assertTrue(rep.isMoreGeneral(ex1, ex2));
		assertFalse(rep.isMoreGeneral(ex2, ex1));
	}

	@Test
	public void classificationMethods() {
		double[][] instances = { { 1, 1, 0, 0, 1 } };
		ClassifierTransformBridge.instances = instances;
		int[] instanceLabels1 = rep.getDataInstanceLabels(instances[0]);
		System.out.print(Arrays.toString(instanceLabels1));
		ExtendedBitSet set = new ExtendedBitSet("11010101010101");
		Classifier ex = new Classifier(set);

		assertTrue(rep.classifyAbsolute(ex, 0) == 1);
		assertTrue(rep.classifyHamming(ex, 0) == 1);
		assertTrue(rep.classifyAccuracy(ex, 0) == 1);

		set = new ExtendedBitSet("11000001010101");
		ex = new Classifier(set);
		assertTrue(rep.classifyAbsolute(ex, 0) == 1);
		assertTrue(rep.classifyHamming(ex, 0) == 1);
		assertTrue(rep.classifyAccuracy(ex, 0) == 1);

		set = new ExtendedBitSet("10000001010101");
		ex = new Classifier(set);
		assertTrue(rep.classifyAbsolute(ex, 0) == 0);
		assertTrue(rep.classifyHamming(ex, 0) == 0);
		assertTrue(rep.classifyAccuracy(ex, 0) == 1);

		set = new ExtendedBitSet("11110001010101");
		ex = new Classifier(set);
		assertTrue(rep.classifyAbsolute(ex, 0) == 0);
		assertTrue(Math.abs(rep.classifyHamming(ex, 0) - (1. / 2.)) < 0.0001);
		assertTrue(rep.classifyAccuracy(ex, 0) == .5);

		set = new ExtendedBitSet("11110101010101");
		ex = new Classifier(set);
		assertTrue(rep.classifyAbsolute(ex, 0) == 0);
		assertTrue(Math.abs(rep.classifyHamming(ex, 0) - (2. / 3.)) < 0.0001);
		assertTrue(rep.classifyAccuracy(ex, 0) == .5);
	}

}
