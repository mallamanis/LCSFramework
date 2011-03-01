package gr.auth.ee.lcs.data.updateAlgorithms.data;

import java.io.Serializable;

/**
 * An object representing the classifier data for the XCS update algorithm.
 * 
 * @author Miltos Allamanis
 */
public class XCSClassifierData implements Serializable {

	/**
	 * Serialization Id.
	 */
	private static final long serialVersionUID = -4348877142305226957L;

	public double predictionError = 0;

	public double actionSet = 1;

	public double predictedPayOff = 5;

	public double k;

	public double fitness = .5;

}