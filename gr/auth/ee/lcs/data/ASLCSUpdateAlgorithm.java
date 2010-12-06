package gr.auth.ee.lcs.data;

import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.classifiers.Classifier;

public class ASLCSUpdateAlgorithm extends AbstractSLCSUpdateAlgorithm {

	private double n;
	
	public ASLCSUpdateAlgorithm(double n){
		this.n=n;
	}
	
  public void updateFitness(Classifier aClassifier, int numerosity, ClassifierSet correctSet) {
	  GenericSLCSClassifierData data=((GenericSLCSClassifierData)aClassifier.updateData);
	  if (correctSet.getClassifierNumerosity(aClassifier)>0) //aClassifier belongs to correctSet
		  data.tp+=1;
	  else
		  data.fp+=1;
	  
	  //Niche set sharing heuristic...
	  aClassifier.fitness=Math.pow(((double)(data.tp))/(double)(data.msa),n)/Math.log(data.ns+1);
	  
	  
  }

}