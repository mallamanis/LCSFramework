package gr.auth.ee.lcs.tests;

import static org.junit.Assert.*;
import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.classifiers.DummySizeControlStrategy;
import gr.auth.ee.lcs.data.ClassifierTransformBridge;
import gr.auth.ee.lcs.data.SimpleBooleanRepresentation;
import gr.auth.ee.lcs.geneticalgorithm.BestFitnessTournamentSelector;

import org.junit.Before;
import org.junit.Test;

public class BestFitnessTournamentSelectorTest {

	BestFitnessTournamentSelector mySelector;
	ClassifierSet population;
	
	@Before
	public void setUp() throws Exception {
		ClassifierTransformBridge.instance=new SimpleBooleanRepresentation(.33,2);
		population=new ClassifierSet(new DummySizeControlStrategy());
		for (int i=0;i<3;i++){
			Classifier aClassifier=new Classifier();
			aClassifier.fitness=i+1;
			aClassifier.actionAdvocated=i;
			population.addClassifier(aClassifier, i+1);
		}
		//We now should have fitnesses {1,2,2,3,3,3}
		
	}

	@Test
	public void testTournament1() {
		int participants[]={0,0,0};
		mySelector=new BestFitnessTournamentSelector(3,true);
		ClassifierSet results=new ClassifierSet(new DummySizeControlStrategy());
		
		mySelector.tournament(population, participants, results);
		assertTrue(results.getClassifier(0).fitness==1);
	}
	
	@Test
	public void testTournament2() {
		int participants[]={0,1,0};
		mySelector=new BestFitnessTournamentSelector(3,true);
		ClassifierSet results=new ClassifierSet(new DummySizeControlStrategy());
		
		mySelector.tournament(population, participants, results);
		assertTrue(results.getClassifier(0).fitness==2);
	}
	
	@Test
	public void testTournament3() {
		int participants[]={2,1,0};
		mySelector=new BestFitnessTournamentSelector(3,true);
		ClassifierSet results=new ClassifierSet(new DummySizeControlStrategy());
		
		mySelector.tournament(population, participants, results);
		assertTrue(results.getClassifier(0).fitness==2);
	}
	
	@Test
	public void testTournament4() {
		int participants[]={5,1,0};
		mySelector=new BestFitnessTournamentSelector(3,true);
		ClassifierSet results=new ClassifierSet(new DummySizeControlStrategy());
		
		mySelector.tournament(population, participants, results);
		assertTrue(results.getClassifier(0).fitness==3);
	}
	
	@Test
	public void testTournament5() {
		int participants[]={5,5,5};
		mySelector=new BestFitnessTournamentSelector(3,true);
		ClassifierSet results=new ClassifierSet(new DummySizeControlStrategy());
		
		mySelector.tournament(population, participants, results);
		assertTrue(results.getClassifier(0).fitness==3);
	}
	
	@Test
	public void testTournament6() {
		int participants[]={5,1,0};
		mySelector=new BestFitnessTournamentSelector(3,false);
		ClassifierSet results=new ClassifierSet(new DummySizeControlStrategy());
		
		mySelector.tournament(population, participants, results);
		assertTrue(results.getClassifier(0).fitness==1);
	}
	
	@Test
	public void testTournament7() {
		int participants[]={3,5,0};
		mySelector=new BestFitnessTournamentSelector(3,false);
		ClassifierSet results=new ClassifierSet(new DummySizeControlStrategy());
		
		mySelector.tournament(population, participants, results);
		assertTrue(results.getClassifier(0).fitness==1);
	}

}
