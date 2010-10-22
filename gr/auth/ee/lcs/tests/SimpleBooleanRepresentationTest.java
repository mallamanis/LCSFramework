/**
 * 
 */
package gr.auth.ee.lcs.tests;

import static org.junit.Assert.*;
import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ExtendedBitSet;
import gr.auth.ee.lcs.data.ClassifierTransformBridge;
import gr.auth.ee.lcs.data.SimpleBooleanRepresentation;

import org.junit.Test;

/**
 * A simple boolean representation for input data
 * @author Miltos Allamanis
 *
 */
public class SimpleBooleanRepresentationTest {

	/**
	 * Test method for {@link gr.auth.ee.lcs.data.SimpleBooleanRepresentation#isMatch(double[], gr.auth.ee.lcs.classifiers.ExtendedBitSet)}.
	 */
	@Test
	public void testIsMatch() {
		SimpleBooleanRepresentation test= new SimpleBooleanRepresentation(0,4);
		ClassifierTransformBridge.setInstance(test);
		Classifier testClassifier= new Classifier();
		double visionVector[]=new double[4];
		
		
		//Test Mask 1##0
		testClassifier.chromosome=new ExtendedBitSet("11100001"); 
		
		visionVector[0]=0; visionVector[1]=0; visionVector[2]=1;visionVector[3]=1;	//1100
		assertTrue(test.isMatch(visionVector, testClassifier.chromosome));
		
		visionVector[0]=0; visionVector[1]=0; visionVector[2]=0;visionVector[3]=1;	//1000	
		assertTrue(test.isMatch(visionVector, testClassifier.chromosome));
		
		visionVector[0]=1; visionVector[1]=0; visionVector[2]=0;visionVector[3]=1;	//1001
		assertFalse(test.isMatch(visionVector, testClassifier.chromosome));
		
		//Test Mask ####
		testClassifier.chromosome=new ExtendedBitSet("00000000");
		
		visionVector[0]=0; visionVector[1]=0; visionVector[2]=0;visionVector[3]=1;	//1000	
		assertTrue(test.isMatch(visionVector, testClassifier.chromosome));
		
		visionVector[0]=1; visionVector[1]=1; visionVector[2]=1;visionVector[3]=1;	//1111	
		assertTrue(test.isMatch(visionVector, testClassifier.chromosome));
		
		visionVector[0]=0; visionVector[1]=0; visionVector[2]=0;visionVector[3]=0;	//0000	
		assertTrue(test.isMatch(visionVector, testClassifier.chromosome));
		
		//Test Mask 0000
		testClassifier.chromosome=new ExtendedBitSet("01010101"); 
		
		visionVector[0]=0; visionVector[1]=0; visionVector[2]=0;visionVector[3]=0;	//0000	
		assertTrue(test.isMatch(visionVector, testClassifier.chromosome));
		
		visionVector[0]=1; visionVector[1]=0; visionVector[2]=1;visionVector[3]=0;	//1010	
		assertFalse(test.isMatch(visionVector, testClassifier.chromosome));
		
		visionVector[0]=1; visionVector[1]=1; visionVector[2]=1;visionVector[3]=1;	//1111	
		assertFalse(test.isMatch(visionVector, testClassifier.chromosome));
		
		//Test Mask 1111
		testClassifier.chromosome=new ExtendedBitSet("11111111"); 
		
		visionVector[0]=1; visionVector[1]=1; visionVector[2]=1;visionVector[3]=1;	//1111	
		assertTrue(test.isMatch(visionVector, testClassifier.chromosome));
		
		visionVector[0]=1; visionVector[1]=0; visionVector[2]=1;visionVector[3]=0;	//1010	
		assertFalse(test.isMatch(visionVector, testClassifier.chromosome));
		
		visionVector[0]=0; visionVector[1]=0; visionVector[2]=0;visionVector[3]=1;	//1000	
		assertFalse(test.isMatch(visionVector, testClassifier.chromosome));
	}

	/**
	 * Test method for {@link gr.auth.ee.lcs.data.SimpleBooleanRepresentation#toNaturalLanguageString(gr.auth.ee.lcs.classifiers.Classifier)}.
	 */
	@Test
	public void testToNaturalLanguageString() {
		SimpleBooleanRepresentation test= new SimpleBooleanRepresentation(0,4);
		ClassifierTransformBridge.setInstance(test);
		Classifier testClassifier= new Classifier();
		
		testClassifier.chromosome=new ExtendedBitSet("01110110");
		testClassifier.actionAdvocated=0;
		assertEquals(test.toNaturalLanguageString(testClassifier),"010#=>0");
		
		testClassifier.chromosome=new ExtendedBitSet("10011111");
		testClassifier.actionAdvocated=1;
		assertEquals(test.toNaturalLanguageString(testClassifier),"#011=>1");
		
		testClassifier.chromosome=new ExtendedBitSet("00000000");
		testClassifier.actionAdvocated=1;
		assertEquals(test.toNaturalLanguageString(testClassifier),"####=>1");
	}

	/**
	 * Test method for {@link gr.auth.ee.lcs.data.SimpleBooleanRepresentation#createRandomCoveringClassifier(double[])}.
	 */
	@Test
	public void testCreateRandomCoveringClassifier() {
		SimpleBooleanRepresentation test= new SimpleBooleanRepresentation(0.5,4);
		ClassifierTransformBridge.setInstance(test);
		double visionVector[]=new double[4];
		Classifier testClassifier;
		
		visionVector[0]=0; visionVector[1]=0; visionVector[2]=1;visionVector[3]=1;	//1100
		for (int i=0;i<10;i++){ //Generate 10 random			
			testClassifier=test.createRandomCoveringClassifier(visionVector);
			assertTrue(test.isMatch(visionVector, testClassifier.getChromosome()));
		}
		
		visionVector[0]=1; visionVector[1]=0; visionVector[2]=1;visionVector[3]=0;	//1010
		for (int i=0;i<10;i++){ //Generate 10 random			
			testClassifier=test.createRandomCoveringClassifier(visionVector);			
			assertTrue(test.isMatch(visionVector, testClassifier.getChromosome()));
		}
	}

	/**
	 * Test method for {@link gr.auth.ee.lcs.data.SimpleBooleanRepresentation#isMoreGeneral(gr.auth.ee.lcs.classifiers.Classifier, gr.auth.ee.lcs.classifiers.Classifier)}.
	 */
	@Test
	public void testIsMoreGeneral() {
		SimpleBooleanRepresentation testRep= new SimpleBooleanRepresentation(0.5,4);
		ClassifierTransformBridge.setInstance(testRep);
		Classifier base=new Classifier();
		Classifier test=new Classifier();
		
		base.actionAdvocated=1; test.actionAdvocated=1;
		base.chromosome=new ExtendedBitSet("10110010"); //#1##
		test.chromosome=new ExtendedBitSet("11110110"); //110#
		assertTrue(testRep.isMoreGeneral(base, test));
				
		base.actionAdvocated=1; test.actionAdvocated=0; 
		base.chromosome=new ExtendedBitSet("11110010"); //11##
		test.chromosome=new ExtendedBitSet("11110110"); //110#
		assertFalse(testRep.isMoreGeneral(base, test));
				
		base.actionAdvocated=0; test.actionAdvocated=0;
		base.chromosome=new ExtendedBitSet("11110010"); //11##
		test.chromosome=new ExtendedBitSet("10110110"); //#10#
		assertFalse(testRep.isMoreGeneral(base, test));
					
		test.chromosome=new ExtendedBitSet("11110010"); //11##
		assertTrue(testRep.isMoreGeneral(base, test));
		
		test.chromosome=new ExtendedBitSet("10110010"); //#1##
		assertFalse(testRep.isMoreGeneral(base, test));
		
		
	}

	/**
	 * Test method for {@link gr.auth.ee.lcs.data.SimpleBooleanRepresentation#getChromosomeSize()}.
	 */
	@Test
	public void testGetChromosomeSize() {
		SimpleBooleanRepresentation test= new SimpleBooleanRepresentation(0.5,4);
		assertEquals(test.getChromosomeSize(),8);
		test= new SimpleBooleanRepresentation(0.5,5);
		assertEquals(test.getChromosomeSize(),10);
	}

	/**
	 * Test method for {@link gr.auth.ee.lcs.data.SimpleBooleanRepresentation#setVisionSize(int)}.
	 */
	@Test
	public void testSetVisionSize() {
		SimpleBooleanRepresentation test= new SimpleBooleanRepresentation(0.5,4);
		test.setVisionSize(10);
		assertEquals(test.getChromosomeSize(),20);
	}

}
