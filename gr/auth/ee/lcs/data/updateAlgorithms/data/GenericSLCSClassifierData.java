package gr.auth.ee.lcs.data.updateAlgorithms.data;

import java.io.Serializable;

/**
 * An data object for the *S-LCS update algorithm.
 * 
 * @author Miltos Allamanis
 */
public class GenericSLCSClassifierData implements Serializable {

	/**
	 * serial for versions.
	 */
	private static final long serialVersionUID = -20798032843413916L;

	/**
	 * niche set size estimation.
	 */
	public double ns = 1;

	/**
	 * Match Set Appearances.
	 */
	public int msa = 1;

	/**
	 * true positives.
	 */
	public int tp = 1;

	/**
	 * false positives.
	 */
	public int fp = 0;

	/**
	 * Strength.
	 */
	public double str = 0;

}