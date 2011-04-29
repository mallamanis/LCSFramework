package gr.auth.ee.lcs.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.data.ClassifierTransformBridge;
import gr.auth.ee.lcs.data.representations.ComplexRepresentation.AbstractAttribute;
import gr.auth.ee.lcs.data.representations.SingleClassRepresentation;
import gr.auth.ee.lcs.utilities.ExtendedBitSet;

import org.junit.Before;
import org.junit.Test;

public class ComplexRepresentationTest {

	SingleClassRepresentation rep;

	@Test
	public void advocatedActionTest() {
		ExtendedBitSet set1 = new ExtendedBitSet(
				"0111111111111110111111111111010");
		Classifier ex1 = new Classifier(set1);

		assertEquals(ex1.getActionAdvocated()[0], 1);

		ex1.setActionAdvocated(2);
		assertEquals(ex1.getActionAdvocated()[0], 2);

		ex1.setActionAdvocated(3);
		assertEquals(ex1.getActionAdvocated()[0], 3);

		ex1.setActionAdvocated(10);
		assertEquals(ex1.getActionAdvocated()[0], 2);
		assertEquals(ex1.size(), rep.getChromosomeSize());
	}

	@Test
	public void checkSize() {
		assertTrue(rep.getChromosomeSize() == 31);
	}

	@Test
	public void checkStringOutput() {
		ExtendedBitSet set = new ExtendedBitSet("11101111111111000000000011011");
		Classifier ex = new Classifier(set);

		// TODO: Fix and test

	}

	@Test
	public void coverageMatches() {
		double visionVector[] = { 0, 1.1256, 2, 0 };
		for (int i = 0; i < 1000; i++) { // Random check 1000 of these instances
			Classifier cover = rep.createRandomCoveringClassifier(visionVector);
			assertTrue(rep.isMatch(visionVector, cover));
		}
	}
	
	@Test
	public void coverageRandom() {
		double visionVector[] = { 0, 1.1256, 2, 0 };
		for (int i = 0; i < 1000; i++) { // Random check 1000 of these instances
			visionVector[1] = (float) (Math.random() * (5.785 + 2.3) - 2.3);
			visionVector[0] = (int) Math.floor(Math.random() * 3);
			visionVector[2] = (int) Math.floor(Math.random() * 3);
			Classifier cover = rep.createRandomCoveringClassifier(visionVector);
			assertTrue(rep.isMatch(visionVector, cover));
		}
	}

	@Test
	public void fixChromosomeTest() {
		ExtendedBitSet set1 = new ExtendedBitSet(
				"0011111111111110111111111111010");
		Classifier ex1 = new Classifier(set1);

		// Does fix work correctly?
		ExtendedBitSet fixed = new ExtendedBitSet(
				"0011101111111111111111111011010");
		rep.fixChromosome(set1);
		assertTrue(set1.equals(fixed));

		// Does fix work correctly on correct chromosomes?
		rep.fixChromosome(fixed);
		assertTrue(set1.equals(fixed));

	}

	@Test
	public void fixClassTest() {
		ExtendedBitSet set1 = new ExtendedBitSet(
				"1111111111111110111111111111010");

		// Does fix work correctly?
		rep.fixChromosome(set1);
		assertTrue(set1.getByteAt(30, 2) < 3);
	}

	@Test
	public void moreGeneralTest1() {
		ExtendedBitSet set1 = new ExtendedBitSet(
				"11101111111111000000000011011");
		Classifier ex1 = new Classifier(set1);

		ExtendedBitSet set2 = new ExtendedBitSet(
				"11111111111111000000000011011");
		Classifier ex2 = new Classifier(set2);

		assertFalse(rep.isMoreGeneral(ex2, ex1));
		assertTrue(rep.isMoreGeneral(ex1, ex2));

	}

	@Test
	public void moreGeneralTest2() {
		ExtendedBitSet set1 = new ExtendedBitSet(
				"11101111111111000000000001010");
		Classifier ex1 = new Classifier(set1);

		ExtendedBitSet set2 = new ExtendedBitSet(
				"11111111111111000000000011011");
		Classifier ex2 = new Classifier(set2);

		assertFalse(rep.isMoreGeneral(ex2, ex1));
		assertTrue(rep.isMoreGeneral(ex1, ex2));
	}

	@Test
	public void moreGeneralTest3() {
		ExtendedBitSet set1 = new ExtendedBitSet(
				"11101111111111000000000011010");
		Classifier ex1 = new Classifier(set1);

		ExtendedBitSet set2 = new ExtendedBitSet(
				"00000000010001000000001010010");
		Classifier ex2 = new Classifier(set2);

		assertFalse(rep.isMoreGeneral(ex2, ex1));
		assertTrue(rep.isMoreGeneral(ex1, ex2));
	}

	@Test
	public void realValuesTest() {
		ExtendedBitSet set1 = new ExtendedBitSet(
				"11101111111111000000000011010");
		Classifier ex1 = new Classifier(set1);
		double[] st = { 1, .1, 2 };
		assertTrue(rep.isMatch(st, ex1));

		set1 = new ExtendedBitSet("11101111111111001000000011010");
		ex1 = new Classifier(set1);
		assertTrue(rep.isMatch(st, ex1));

		set1 = new ExtendedBitSet("11100111111111001000000011010");
		ex1 = new Classifier(set1);
		assertTrue(rep.isMatch(st, ex1));
	}

	@Before
	public void setUp() throws Exception {
		SingleClassRepresentation.AbstractAttribute list[] = new AbstractAttribute[4];
		String[] names = { "Good", "Mediocre", "Bad" };
		rep = new SingleClassRepresentation(list, names);
		ClassifierTransformBridge.setInstance(rep);
		String[] attribute = { "A", "B", "A+" };
		list[0] = rep.new NominalAttribute(rep.getChromosomeSize(), "nom",
				attribute, 0);
		list[1] = rep.new IntervalAttribute(rep.getChromosomeSize(), "int",
				(float) -2.3, (float) 5.785, 10, 0);
		list[2] = rep.new NominalAttribute(rep.getChromosomeSize(), "nom2",
				attribute, 0);
		list[3] = rep.new UniLabel(rep.getChromosomeSize(), "class", names);

	}

}
