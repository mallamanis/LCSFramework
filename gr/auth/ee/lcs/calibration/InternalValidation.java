/**
 * 
 */
package gr.auth.ee.lcs.calibration;

import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.data.IClassificationStrategy;
import gr.auth.ee.lcs.data.IEvaluator;

/**
 * Internal Validation for thresholds.
 * 
 * @author Miltos Allamanis
 * 
 */
public class InternalValidation {

	/**
	 * The classification strategy used.
	 */
	private final IClassificationStrategy strategy;

	/**
	 * The optimization metric used.
	 */
	private final IEvaluator optimizationMetric;

	/**
	 * The rules on which to perform Internal Validation.
	 */
	private final ClassifierSet ruleSet;

	/**
	 * Constructor.
	 * 
	 * @param rules
	 *            the rules to perform validation on.
	 * @param classificationStrategy
	 *            the classification strategy used.
	 * @param metric
	 *            the metric to optimize on
	 */
	public InternalValidation(ClassifierSet rules,
			IClassificationStrategy classificationStrategy, IEvaluator metric) {
		ruleSet = rules;
		strategy = classificationStrategy;
		optimizationMetric = metric;
	}

	/**
	 * Calibrate with Internal Validation.
	 * 
	 * @param iterations
	 *            the number of iterations.
	 */
	public void calibrate(int iterations) {

		double step = .25;
		double center = .25;
		for (int i = 0; i < iterations; i++) {
			center = getNextCenter(center, step, strategy);
			step /= 2.;
		}
		strategy.setThreshold(center);
		System.out.println("Threshold set to " + center);
	}

	/**
	 * Calculates the next center.
	 * 
	 * @param center
	 *            the center
	 * @param step
	 *            the step
	 * @param classificationStrategy
	 *            the classification used
	 * @return the next center for the next iteration
	 */
	private double getNextCenter(final double center, final double step,
			IClassificationStrategy classificationStrategy) {
		double downLimit = center - step;
		if (downLimit < 0)
			downLimit = 0;
		double upLimit = center + step;
		if (upLimit > .5)
			upLimit = (float) .5;
		double bestEvaluation = Double.MIN_VALUE;
		double bestThreshold = center;
		for (double th = downLimit; th <= upLimit; th += (step / 2)) {
			classificationStrategy.setThreshold(th);
			final double eval = optimizationMetric.evaluateSet(ruleSet);
			if (eval > bestEvaluation) {
				bestEvaluation = eval;
				bestThreshold = th;
			}
		}

		return bestThreshold;
	}
}
