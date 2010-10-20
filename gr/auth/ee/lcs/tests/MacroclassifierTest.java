/**
 * 
 */
package gr.auth.ee.lcs.tests;

import static org.junit.Assert.*;
import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.classifiers.ExtendedBitSet;
import gr.auth.ee.lcs.classifiers.Macroclassifier;
import gr.auth.ee.lcs.data.ClassifierTransformBridge;
import gr.auth.ee.lcs.data.SimpleBooleanRepresentation;

import org.junit.Before;
import org.junit.Test;

/**
 * @author miltiadis
 *
 */
public class MacroclassifierTest {
	
	SimpleBooleanRepresentation test;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		test= new SimpleBooleanRepresentation(0.5,4);
		ClassifierTransformBridge.setInstance(test);
	}

	/**
	 * Test method for {@link gr.auth.ee.lcs.classifiers.Macroclassifier#equals(gr.auth.ee.lcs.classifiers.Classifier)}.
	 */
	@Test
	public void testEquals() {
		Classifier testClassifier=new Classifier();
		testClassifier.actionAdvocated=0;
		testClassifier.chromosome=new ExtendedBitSet("10110001");
		Macroclassifier testMacro1=new Macroclassifier(testClassifier,1);
		Macroclassifier testMacro2=new Macroclassifier(testClassifier,0);
		assertTrue(testMacro1.equals(testMacro2));
		assertTrue(testMacro2.equals(testMacro1));
		assertTrue(testMacro1.equals(testClassifier));
		assertTrue(testMacro2.equals(testClassifier));
		
		Classifier testClassifier2=new Classifier();
		testClassifier2.actionAdvocated=0;
		testClassifier2.chromosome=new ExtendedBitSet("10110001");
		assertTrue(testMacro1.equals(testClassifier2));
	}
	

}
