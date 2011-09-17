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
package gr.auth.ee.lcs.classifiers.populationcontrol;

import static org.easymock.EasyMock.createMock;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import gr.auth.ee.lcs.AbstractLearningClassifierSystem;
import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.classifiers.Macroclassifier;
import gr.auth.ee.lcs.data.AbstractUpdateStrategy;
import gr.auth.ee.lcs.data.representations.SimpleBooleanRepresentation;
import gr.auth.ee.lcs.data.updateAlgorithms.ASLCSUpdateAlgorithm;
import gr.auth.ee.lcs.geneticalgorithm.selectors.RouletteWheelSelector;
import gr.auth.ee.lcs.utilities.SettingsLoader;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

/**
 * A test for the fixed size population control.
 * 
 * @author Miltiadis Allamanis
 * 
 */
public class FixedSizeDeletionTest {

	/**
	 * The mock lcs.
	 */
	private AbstractLearningClassifierSystem lcs;

	/**
	 * A population.
	 */
	private ClassifierSet population;

	/**
	 * @throws IOException
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws IOException {
		SettingsLoader.loadSettings();
		lcs = createMock(AbstractLearningClassifierSystem.class);

		final SimpleBooleanRepresentation rep = new SimpleBooleanRepresentation(
				.33, 2, lcs);
		final ASLCSUpdateAlgorithm update = new ASLCSUpdateAlgorithm(5, .99,
				50, 0.01, null, lcs);
		lcs.setElements(rep, update);
		lcs.instances = new double[100][];
		population = new ClassifierSet(new FixedSizeSetWorstFitnessDeletion(
				lcs, 3, new RouletteWheelSelector(
						AbstractUpdateStrategy.COMPARISON_MODE_DELETION, true)));
	}

	/**
	 * Test method for
	 * {@link gr.auth.ee.lcs.classifiers.populationcontrol.FixedSizeSetWorstFitnessDeletion#controlPopulation(gr.auth.ee.lcs.classifiers.ClassifierSet)}
	 * .
	 */
	@Test
	public void testControlPopulation() {
		for (int i = 0; i < 200; i++) {
			Classifier aClassifier = lcs.getNewClassifier();
			aClassifier.setComparisonValue(
					AbstractUpdateStrategy.COMPARISON_MODE_EXPLOITATION, i + 1);
			aClassifier.setActionAdvocated(i);
			aClassifier.experience = 100;
			population.addClassifier(new Macroclassifier(aClassifier, i + 1),
					false);
			if (i > 2)
				assertEquals(population.getTotalNumerosity(), 3);
			else
				assertTrue(population.getTotalNumerosity() <= 3);
		}

		for (int i = 0; i < 200; i++) {
			Classifier aClassifier = lcs.getNewClassifier();
			aClassifier.setComparisonValue(
					AbstractUpdateStrategy.COMPARISON_MODE_EXPLOITATION, 0);
			aClassifier.setActionAdvocated(i);

			population.addClassifier(new Macroclassifier(aClassifier, i + 1),
					false);

			assertEquals(population.getTotalNumerosity(), 3);

		}
	}

}
