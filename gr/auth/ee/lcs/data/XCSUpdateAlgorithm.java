package gr.auth.ee.lcs.data;

import java.io.Serializable;

import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ClassifierSet;

/** 
 *  The XCS update algorithm.
 *  @author Miltos Allamanis
 */
public class XCSUpdateAlgorithm extends UpdateAlgorithmFactoryAndStrategy {

  /** 
   *  the initial parameters of the data object.
   */
  public static XCSClassifierData initialParameters;

  /**
   * XCS learning rate.
   */
  private double beta;
  
  /**
   * Correct classification payoff.
   */
    private double payoff;
    
   /**
    * Accepted Error e0 (accuracy function parameter).
    */
    private double e0;
  
  /**
   * alpha rate (accuracy function parameter).
   */
    private double alpha;
    
    /**
     * n factor.
     */
    private double n;
  
  /**
   * Constructor.
   * @param beta the learning rate of the XCS update algorithm
   */
  public XCSUpdateAlgorithm(double beta,
		  double P, double e0, double alpha, double n) {
	  this.beta = beta;
	  this.payoff = P;
	  this.e0 = e0;
	  this.alpha = alpha;
	  this.n = n;
  }
  
  public Serializable createStateClassifierObject() {
	  //TODO: Initial Parameters
	return (Serializable) new XCSClassifierData();
  }

  /**
   * Implementing abstract method.
   * @see gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy 
   * @param setA the action set
   * @param setB the correct set
   */
  public void updateSet(ClassifierSet setA, ClassifierSet setB) {
	  double accuracySum = 0;
	  
	  for (int i = 0; i < setA.getNumberOfMacroclassifiers(); i++){
		  Classifier cl = setA.getClassifier(i);
		  
		  //Get update data object
		  XCSClassifierData data 
		  		= ((XCSClassifierData) cl.updateData); 
		  cl.experience++; //Increase Experience
		  
		  double payOff; //the classifier's payoff
		  if (setB.getClassifierNumerosity(cl) > 0)
			  payOff = payoff;
		  else
			  payOff = 0;
		  
		  //Update Predicted Payoff
		  if (cl.experience < 1 / beta)
			  data.predictedPayOff += (payOff - data.predictedPayOff) / cl.experience;
		  else
			  data.predictedPayOff += beta * (payOff - data.predictedPayOff);
		  
		  //Update Prediction Error
		  if (cl.experience < 1 / beta)
			  data.predictionError += (Math.abs(payOff - data.predictedPayOff) 
					  - data.predictionError) / cl.experience;
		  else
			  data.predictionError += beta * (Math.abs(payOff - data.predictedPayOff) - data.predictionError);
		   
		  //Update Action Set Estimate
		  if (cl.experience < 1 / beta)
			  data.actionSet += (setA.getTotalNumerosity() - data.actionSet) / cl.experience;
		  else
			  data.actionSet += beta * (setA.getTotalNumerosity() - data.actionSet);
			 
		  //Fitness Update Step 1
		  if (data.predictionError < e0)
			  data.k = 1;
		  else
			  data.k = alpha * Math.pow(data.predictionError / e0, -n);
		  accuracySum += data.k * setA.getClassifierNumerosity(i);		  
	  }
	  
	  //Update Fitness Step 2
	  for (int i = 0; i < setA.getNumberOfMacroclassifiers(); i++){
		  Classifier cl = setA.getClassifier(i);
		  
		  //Get update data object
		  XCSClassifierData data = ((XCSClassifierData) cl.updateData); 
		  
		  //per micro-classifier
		  cl.fitness += beta * (data.k / accuracySum - cl.fitness);
	  }
	  	
	  
  }

@Override
public double getComparisonValue(Classifier aClassifier, int mode) {
	XCSClassifierData data;
	switch (mode){
		case COMPARISON_MODE_EXPLORATION:
			return aClassifier.fitness;
		case COMPARISON_MODE_DELETION:
			data = ((XCSClassifierData) aClassifier.updateData);
			return data.k; //TODO: Something else?		
		case COMPARISON_MODE_EXPLOITATION:
			data = ((XCSClassifierData) aClassifier.updateData);
			return data.predictedPayOff; 				
	}
	return 0;
}
}