/**
 * 
 */
package gr.auth.ee.lcs.utilities;

/**
 * A threshold calibration method.
 * 
 * @author Miltos Allamanis
 * 
 */
public class ProportionalCut {
	/**
	 * The threshold.
	 */
	private float threshold;

	/**
	 * The number of refinement steps to be taken.
	 */
	private static final int STEPS = 15;

	/**
	 * Calibrates the threshold.
	 * 
	 * @param targetLC
	 *            the target Label Cardinality we are trying to achieve
	 * @param confidenceValues
	 *            the normalized confidence value's array per instance per label
	 * @return a float representing the normalized threshold (between 0 and .5)
	 */
	public float calibrate(final float targetLC,
			final float[][] confidenceValues) {
		threshold = (float) .25;
		float pCutStep = (float) .25;
		for (int i = 0; i < STEPS; i++) {
			calibrateThreshold(confidenceValues, targetLC, pCutStep);
			pCutStep /= 2;
		}
		return threshold;
	}

	/**
	 * Calibrate threshold with a given step for threshold values.
	 * 
	 * @param confidenceValues
	 *            the confidence value array (NxL)
	 * @param targetLc
	 *            the target LC (Label Cardinality) we are trying to achieve
	 * @param pCutStep
	 *            the step used for the pCut
	 */
	private void calibrateThreshold(final float[][] confidenceValues,
			final float targetLc, final float pCutStep) {
		float downLimit = this.threshold - pCutStep;
		if (downLimit < 0)
			downLimit = 0;
		float upLimit = this.threshold + pCutStep;
		if (upLimit > .5)
			upLimit = (float) .5;
		float bestDif = Float.MAX_VALUE;
		for (float th = downLimit; th <= upLimit; th += (pCutStep / 2)) {
			final float diff = getPcutDiff(confidenceValues, targetLc, th);
			if (diff < bestDif) {
				bestDif = diff;
				this.threshold = th;
			}
		}
	}

	/**
	 * Returns the number of active labels for a given threshold.
	 * 
	 * @param lblProbs
	 *            the normalized confidence array of all labels.
	 * @param threshold
	 *            the threshold
	 * @return an integer indicating the number of labels that are active
	 */
	public int getNumberOfActiveLabels(final float[] lblProbs,
			final float threshold) {
		// Classify
		int activeLabels = 0;
		for (int i = 0; i < lblProbs.length; i++) {
			if (lblProbs[i] >= threshold)
				activeLabels++;
		}
		return activeLabels;
	}

	/**
	 * Get the proportional cut difference with a given target label
	 * cardinality, threshold and instance confidence valies.
	 * 
	 * @param confidenceValues
	 *            the normalized array of confidence values for all instances
	 *            and labels
	 * @param targetLc
	 *            the target LC (Label Cardinality) that we are trying to
	 *            achieve.
	 * @param threshold
	 *            the threshold
	 * @return a float indicating the absolute difference
	 */
	private float getPcutDiff(final float[][] confidenceValues,
			final float targetLc, final float threshold) {
		int sumOfActive = 0;
		for (int i = 0; i < confidenceValues.length; i++) {
			sumOfActive += getNumberOfActiveLabels(confidenceValues[i],
					threshold);
		}

		// Compare diff
		final double diff = Math.abs(((double) sumOfActive)
				/ ((double) confidenceValues.length) - targetLc);
		return (float) diff;
	}
}
