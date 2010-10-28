package gr.auth.ee.lcs.data;

import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.classifiers.Classifier;

public class SSLCSUpdateAlgorithm extends AbstractSLCSUpdateAlgorithm {

	private double R,p;
	
	public SSLCSUpdateAlgorithm(double reward, double penaltyPercent){
		R=reward;
		penaltyPercent=p;
	}
	
  public void updateFitness(Classifier aClassifier, ClassifierSet correctSet) {
	  GenericSLCSClassifierData data=((GenericSLCSClassifierData)aClassifier.updateData);
	  if (correctSet.getClassifierNumerosity(aClassifier)>0){ //aClassifier belongs to correctSet
		  data.tp++;
		  data.str+=R/correctSet.getTotalNumerosity();
	  }else{
		  data.fp++;
		  data.str-=p*R/data.ns;
	  }
	  
	  aClassifier.fitness=(data.str/data.msa);
	  
  }


}