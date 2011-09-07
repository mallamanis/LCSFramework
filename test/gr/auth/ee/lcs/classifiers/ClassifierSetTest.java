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
import gr.auth.ee.lcs.AbstractLearningClassifierSystem;
import gr.auth.ee.lcs.data.ClassifierTransformBridge;
import gr.auth.ee.lcs.data.representations.SimpleBooleanRepresentation;
import gr.auth.ee.lcs.utilities.ExtendedBitSet;
import static org.easymock.EasyMock.*;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

/**
 * A test for a ClassifierSet.
 * 
 * @author Miltos Allamanis
 * 
 */
public final class ClassifierSetTest extends EasyMockSupport{

	private SimpleBooleanRepresentation test;

	private AbstractLearningClassifierSystem lcs;
	
	private ClassifierTransformBridge mockBridge;

	/**
	 * Setup test.
	 */
	@Before
	public void setup() {
		lcs = createMock(AbstractLearningClassifierSystem.class);
		mockBridge = createMock(ClassifierTransformBridge.class);
		test = new SimpleBooleanRepresentation(0.5, 4, lcs);
		lcs.setElements(mockBridge, null);

	}

	/**
	 * Test method for
	 * {@link gr.auth.ee.lcs.classifiers.ClassifierSet#addClassifier(gr.auth.ee.lcs.classifiers.Classifier, int)}
	 * .
	 */
	@Test
	public void testAddClassifier() {
		resetAll();
		IPopulationControlStrategy mockControlStrategy = createMock(IPopulationControlStrategy.class);
				
		final ClassifierSet testSet = new ClassifierSet(mockControlStrategy);
		final Classifier testClassifier = lcs
				.getNewClassifier(new ExtendedBitSet("10010111"));
		testClassifier.setSubsumptionAbility(false);
		final Classifier testClassifier2 = lcs
			.getNewClassifier(new ExtendedBitSet("10100111"));
		testClassifier2.setSubsumptionAbility(false);
		final Classifier testClassifier3 = lcs
			.getNewClassifier(new ExtendedBitSet("10001100"));
		testClassifier3.setSubsumptionAbility(true);
		
		expect(mockBridge.isMoreGeneral(testClassifier, testClassifier)).andReturn(true).anyTimes();
		expect(mockBridge.areEqual(testClassifier, testClassifier)).andReturn(true).anyTimes();
				
		expect(mockBridge.isMoreGeneral(testClassifier2, testClassifier2)).andReturn(true).anyTimes();
		expect(mockBridge.areEqual(testClassifier2, testClassifier2)).andReturn(true).anyTimes();
		
		expect(mockBridge.isMoreGeneral(testClassifier3, testClassifier3)).andReturn(true).anyTimes();
		expect(mockBridge.areEqual(testClassifier3, testClassifier3)).andReturn(true).anyTimes();
		
		/*
		expect(mockBridge.areEqual(testClassifier, testClassifier2)).andReturn(false).anyTimes();
		expect(mockBridge.areEqual(testClassifier2, testClassifier)).andReturn(false).anyTimes();
		expect(mockBridge.areEqual(testClassifier, testClassifier3)).andReturn(false).anyTimes();
		expect(mockBridge.areEqual(testClassifier3, testClassifier)).andReturn(false).anyTimes();
		expect(mockBridge.areEqual(testClassifier3, testClassifier2)).andReturn(false).anyTimes();
		expect(mockBridge.areEqual(testClassifier2, testClassifier3)).andReturn(false).anyTimes();
		
		expect(mockBridge.isMoreGeneral(testClassifier, testClassifier2)).andReturn(false).anyTimes();
		expect(mockBridge.isMoreGeneral(testClassifier2, testClassifier)).andReturn(false).anyTimes();
		expect(mockBridge.isMoreGeneral(testClassifier, testClassifier3)).andReturn(false).anyTimes();
		expect(mockBridge.isMoreGeneral(testClassifier3, testClassifier)).andReturn(false).anyTimes();
		expect(mockBridge.isMoreGeneral(testClassifier3, testClassifier2)).andReturn(false).anyTimes();
		expect(mockBridge.isMoreGeneral(testClassifier2, testClassifier3)).andReturn(false).anyTimes();
		*/
		
		expect(mockBridge.areEqual(anyObject(Classifier.class),anyObject(Classifier.class))).andReturn(false).anyTimes();
		
		mockControlStrategy.controlPopulation(testSet);
		expectLastCall().times(6);
		
		replayAll();
		
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
		
		testSet.addClassifier(new Macroclassifier(testClassifier2, 3), true);
		assertEquals(testSet.getNumberOfMacroclassifiers(), 2);
		testSet.addClassifier(new Macroclassifier(testClassifier2, 10), true);
		assertEquals(testSet.getNumberOfMacroclassifiers(), 2);
		assertEquals(testSet.getClassifier(1), testClassifier2);
		assertEquals(testSet.getClassifierNumerosity(testClassifier2), 13);
		assertEquals(testSet.getTotalNumerosity(), 13 + 5);

		
		

		testSet.addClassifier(new Macroclassifier(testClassifier3, 1), true);
		assertEquals(testSet.getNumberOfMacroclassifiers(), 3);
		testSet.addClassifier(new Macroclassifier(testClassifier3, 1), true);
		assertEquals(testSet.getNumberOfMacroclassifiers(), 3);
		assertEquals(testSet.getClassifier(2), testClassifier3);
		assertEquals(testSet.getClassifierNumerosity(testClassifier3), 2);
		assertEquals(testSet.getTotalNumerosity(), 13 + 5 + 2);
		resetAll();
	}

	/**
	 * Test method for
	 * {@link gr.auth.ee.lcs.classifiers.ClassifierSet#deleteClassifier(gr.auth.ee.lcs.classifiers.Classifier)}
	 * .
	 */
	@Test
	public void testDeleteClassifier() {
		IPopulationControlStrategy mockControlStrategy = createMock(IPopulationControlStrategy.class);
		
		final ClassifierSet testSet = new ClassifierSet(mockControlStrategy);
		final Classifier testClassifier = lcs
				.getNewClassifier(new ExtendedBitSet("10010111"));
		testClassifier.setActionAdvocated(1);
		
		mockControlStrategy.controlPopulation(testSet);
		
		replay(mockControlStrategy);
		
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
		verify(mockControlStrategy);
	}

	@Test
	public void testRemoveAll() {
		resetAll();
		
		mockBridge.setRepresentationSpecificClassifierData(anyObject(Classifier.class));
		expectLastCall().anyTimes();
		
		replayAll();
		final Classifier testClassifier = lcs
			.getNewClassifier(new ExtendedBitSet("10010111"));
		
		verifyAll();
		resetAll();
		IPopulationControlStrategy mockControlStrategy = createMock(IPopulationControlStrategy.class);
		final ClassifierSet testSet = new ClassifierSet(mockControlStrategy);

		mockControlStrategy.controlPopulation(testSet);
		expectLastCall().times(10);
		
		expect(mockBridge.areEqual(testClassifier, testClassifier)).andReturn(true).anyTimes();
		expect(mockBridge.isMoreGeneral(testClassifier, testClassifier)).andReturn(true).anyTimes();
		expect(mockBridge.toNaturalLanguageString(anyObject(Classifier.class))).andReturn("").anyTimes();
		
		
		replayAll();
		
		for (int i = 0; i < 10; i++)
			testSet.addClassifier(new Macroclassifier(testClassifier, 1), true);
		assertEquals(testSet.getClassifierNumerosity(testClassifier), 10);
		assertEquals(testSet.getTotalNumerosity(), 10);
		assertEquals(testSet.getNumberOfMacroclassifiers(), 1);

		testSet.removeAllMacroclassifiers();

		assertEquals(testSet.getClassifierNumerosity(testClassifier), 0);
		assertEquals(testSet.getTotalNumerosity(), 0);
		assertEquals(testSet.getNumberOfMacroclassifiers(), 0);
		verifyAll();

	}

	/**
	 * Test method for
	 * {@link gr.auth.ee.lcs.classifiers.ClassifierSet#addFromClassifierSet(gr.auth.ee.lcs.classifiers.ClassifierSet, int)}
	 * .
	 */
	@Test
	public void testSubsumptionClassifierSet() {
		resetAll();
		
		IPopulationControlStrategy mockControlStrategy = createMock(IPopulationControlStrategy.class);
		
		mockBridge.setRepresentationSpecificClassifierData(anyObject(Classifier.class));
		expectLastCall().anyTimes();
		
		replayAll();
		final Classifier subsumableClassifier = lcs
			.getNewClassifier(new ExtendedBitSet("10010110"));
		final Classifier testClassifier = lcs
			.getNewClassifier(new ExtendedBitSet("10010111"));
		
		verifyAll();
		resetAll();

		final ClassifierSet testSet = new ClassifierSet(mockControlStrategy);
		mockControlStrategy.controlPopulation(testSet);
		expectLastCall().times(2);
		
		expect (mockBridge.isMoreGeneral(subsumableClassifier, testClassifier)).andReturn(true).atLeastOnce();
		expect (mockBridge.isMoreGeneral(testClassifier, subsumableClassifier)).andReturn(false).anyTimes();
		expect(mockBridge.toNaturalLanguageString(anyObject(Classifier.class))).andReturn("").anyTimes();
		
		replayAll();
		
		subsumableClassifier.setSubsumptionAbility(true);
		
		testSet.addClassifier(new Macroclassifier(subsumableClassifier, 1),
				false);
		assertEquals(testSet.getNumberOfMacroclassifiers(), 1);
		assertEquals(testSet.getTotalNumerosity(), 1);
		assertEquals(testSet.getClassifierNumerosity(subsumableClassifier), 1);

		
		assertTrue(subsumableClassifier.isMoreGeneral(testClassifier));
		
		
		testSet.addClassifier(new Macroclassifier(testClassifier, 1), true);

		assertEquals(testSet.getNumberOfMacroclassifiers(), 1);
		assertEquals(testSet.getClassifierNumerosity(0), 2);

		assertEquals(testSet.getClassifierNumerosity(testClassifier), 0);
		assertEquals(testSet.getClassifierNumerosity(subsumableClassifier), 2);
		
		verifyAll();
	}

	@Test
	public void testGenerateMatchSet() {
		IPopulationControlStrategy mockControlStrategy = createMock(IPopulationControlStrategy.class);
		ClassifierSet population = new ClassifierSet(mockControlStrategy);
		double instance[] = {0,1,2,3,4};
		
		mockBridge.setRepresentationSpecificClassifierData(anyObject(Classifier.class));
		expectLastCall().anyTimes();
		
		replayAll();
		final Classifier mockClassifier1 = lcs
			.getNewClassifier(new ExtendedBitSet("10010110"));
		final Classifier mockClassifier2 = lcs
			.getNewClassifier(new ExtendedBitSet("11010111"));
		
		verifyAll();
		resetAll();
		
		mockControlStrategy.controlPopulation(population);
		expectLastCall().times(2);

		expect(mockBridge.toNaturalLanguageString(anyObject(Classifier.class))).andReturn("").anyTimes();
		expect(mockBridge.areEqual(anyObject(Classifier.class),anyObject(Classifier.class))).andReturn(false);
		expect(mockClassifier1.isMatch(instance)).andReturn(false);
		expect(mockClassifier2.isMatch(instance)).andReturn(true);
		
		replayAll();
		
		population.addClassifier(new Macroclassifier(mockClassifier1,10), true);
		population.addClassifier(new Macroclassifier(mockClassifier2,8), true);
				
		ClassifierSet matchSet = population.generateMatchSet(instance);
		
		assertEquals(matchSet.getTotalNumerosity(),8);
		assertEquals(matchSet.getNumberOfMacroclassifiers(),1);
		assertEquals(matchSet.getClassifier(0),mockClassifier2);
		
		verifyAll();
		
	}
	
}
