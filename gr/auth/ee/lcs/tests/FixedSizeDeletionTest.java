/**
 * 
 */
package gr.auth.ee.lcs.tests;

import static org.junit.Assert.*;
import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.classifiers.Macroclassifier;
import gr.auth.ee.lcs.classifiers.populationcontrol.FixedSizeSetWorstFitnessDeletion;
import gr.auth.ee.lcs.data.AbstractUpdateStrategy;
import gr.auth.ee.lcs.data.representations.SimpleBooleanRepresentation;
import gr.auth.ee.lcs.data.updateAlgorithms.ASLCSUpdateAlgorithm;
import gr.auth.ee.lcs.geneticalgorithm.selectors.RouletteWheelSelector;
import gr.auth.ee.lcs.tests.mocks.MockLCS;

import org.junit.Before;
import org.junit.Test;

/**
 * A test for the fixed size population control.
 * @author Miltiadis Allamanis
 *
 */
public class FixedSizeDeletionTest {


	/**
	 * The mock lcs.
	 */
	MockLCS lcs;
	

	/**
	 * A population.
	 */
	ClassifierSet population;
	
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		lcs = new MockLCS();
		SimpleBooleanRepresentation rep = new SimpleBooleanRepresentation(.33,
				2, lcs);
		ASLCSUpdateAlgorithm update = new ASLCSUpdateAlgorithm(5, .99, 50,
				0.01, null, lcs);
		lcs.setElements(rep, update);

		population =  new ClassifierSet(
				new FixedSizeSetWorstFitnessDeletion(
						3,
						new RouletteWheelSelector(
								AbstractUpdateStrategy.COMPARISON_MODE_DELETION,
								true)));		
	}

	/**
	 * Test method for {@link gr.auth.ee.lcs.classifiers.populationcontrol.FixedSizeSetWorstFitnessDeletion#controlPopulation(gr.auth.ee.lcs.classifiers.ClassifierSet)}.
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
			if (i >2)
				assertEquals(population.getTotalNumerosity(), 3);
			else
				assertTrue(population.getTotalNumerosity() <=3);
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
