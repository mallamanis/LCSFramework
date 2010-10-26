package gr.auth.ee.lcs.data;

import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.classifiers.Classifier;

public class ASLCSUpdateAlgorithm extends AbstractSLCSUpdateAlgorithm {

	public double n;
	
  public void updateFitness(Classifier aClassifier, ClassifierSet correctSet) {
	  GenericSLCSClassifierData data=((GenericSLCSClassifierData)aClassifier.updateData);
	  if (correctSet.getClassifierNumerosity(aClassifier)>0) //aClassifier belongs to correctSet
		  data.tp++;
	  else
		  data.fp++;
	  
	  aClassifier.fitness=Math.pow(data.tp/data.msa,n);
	  
  }

}