package gr.auth.ee.lcs.data;

import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.classifiers.Classifier;

/**
 * The update algorithm for the AS-LCS
 * @author Miltos Allamanis
 *
 */
public class ASLCSUpdateAlgorithm extends AbstractSLCSUpdateAlgorithm {

	/**
	 * The strictness factor for updating
	 */
	private double n;
	
	/**
	 * Object's Constuctor
	 * @param n the strictness factor ν used in updating
	 */
	public ASLCSUpdateAlgorithm(double n){
		this.n=n;
	}
	
	/**
	 * implements @see gr.auth.ee.cs.data.AbstractSLCSUpdateAlgorithm
	 */
  public void updateFitness(Classifier aClassifier, int numerosity, ClassifierSet correctSet) {
	  GenericSLCSClassifierData data=((GenericSLCSClassifierData)aClassifier.updateData);
	  if (correctSet.getClassifierNumerosity(aClassifier)>0) //aClassifier belongs to correctSet
		  data.tp+=1;
	  else
		  data.fp+=1;
	  
	  //Niche set sharing heuristic...
	  aClassifier.fitness=Math.pow(((double)(data.tp))/(double)(data.msa),n)/Math.log(data.ns+1)*Math.log(data.msa);
	  
	  
  }

}