/**
 * 
 */
package gr.auth.ee.lcs.data;

import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ClassifierSet;

/**
 * @author miltiadis
 *
 */
public class XSLCSUpdateAlgorithm extends AbstractSLCSUpdateAlgorithm {

	private double beta=0.5;
	/* (non-Javadoc)
	 * @see gr.auth.ee.lcs.data.AbstractSLCSUpdateAlgorithm#updateFitness(gr.auth.ee.lcs.classifiers.Classifier, int, gr.auth.ee.lcs.classifiers.ClassifierSet)
	 */
	@Override
	public void updateFitness(Classifier aClassifier, int numerosity,
			ClassifierSet correctSet) {
		GenericSLCSClassifierData data=((GenericSLCSClassifierData)aClassifier.updateData);
		  if (correctSet.getClassifierNumerosity(aClassifier)>0){ //aClassifier belongs to correctSet
			  data.tp++;			  
		  }else{
			  data.fp++;			 
		  }
		  float acc=((float)(data.tp))/((float)data.msa);
		  data.str+=beta*(acc-data.str);
		  		  
		  aClassifier.fitness=1/(1/aClassifier.fitness+beta*(Math.abs(acc-data.str)- aClassifier.fitness))/Math.log(Math.E+data.ns);

	}

}
