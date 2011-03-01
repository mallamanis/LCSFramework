package gr.auth.ee.lcs.data.updateAlgorithms;

import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy;
import gr.auth.ee.lcs.data.updateAlgorithms.data.XCSClassifierData;

import java.io.Serializable;

/**
 * The XCS update algorithm.
 * 
 * @author Miltos Allamanis
 */
public class XCSUpdateAlgorithm extends UpdateAlgorithmFactoryAndStrategy {

	/**
	 * the initial parameters of the data object.
	 */
	public static XCSClassifierData initialParameters;

	/**
	 * XCS learning rate.
	 */
	private final double beta;

	/**
	 * Correct classification payoff.
	 */
	private final double payoff;

	/**
	 * Accepted Error e0 (accuracy function parameter).
	 */
	private final double e0;

	/**
	 * alpha rate (accuracy function parameter).
	 */
	private final double alpha;

	/**
	 * The fitness threshold for subsumption.
	 */
	private final double subsumptionFitnessThreshold;
	/**
	 * The experience threshold for subsumption.
	 */
	private final int subsumptionExperienceThreshold;
	
	/**
	 * n factor.
	 */
	private final double n;

	/**
	 * Constructor.
	 * 
	 * @param beta
	 *            the learning rate of the XCS update algorithm
	 * @param P
	 *            the penalty of the XCS update algorithm
	 * @param e0
	 *            the maximum acceptable error for fitness sharing
	 * @param alpha
	 *            used for fitness sharing
	 * @param n
	 *            used for fitness sharing
	 * @param fitnessThreshold
	 *            the fitness threshold for subsumption
	 * @param experienceThreshold
	 *            the experience threshold for subsumption
	 */
	public XCSUpdateAlgorithm(final double beta, final double P,
			final double e0, final double alpha, final double n,
			final double fitnessThreshold, final int experienceThreshold) {
		this.subsumptionFitnessThreshold = fitnessThreshold;
		this.subsumptionExperienceThreshold = experienceThreshold;
		this.beta = beta;
		this.payoff = P;
		this.e0 = e0;
		this.alpha = alpha;
		this.n = n;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy#
	 * createStateClassifierObject()
	 */
	@Override
	public Serializable createStateClassifierObject() {
		// TODO: Initial Parameters
		return new XCSClassifierData();
	}
	
	/**
	 * Implementation of the subsumption strength.
	 * 
	 * @param aClassifier
	 *            the classifier, whose subsumption ability is to be updated
	 */
	protected final void updateSubsumption(final Classifier aClassifier) {
		aClassifier.setSubsumptionAbility( (aClassifier
				.getComparisonValue(COMPARISON_MODE_EXPLOITATION) > subsumptionFitnessThreshold)
				&& (aClassifier.experience > subsumptionExperienceThreshold));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy#getComparisonValue
	 * (gr.auth.ee.lcs.classifiers.Classifier, int)
	 */
	@Override
	public double getComparisonValue(final Classifier aClassifier,
			final int mode) {
		final XCSClassifierData data = ((XCSClassifierData) aClassifier.updateData);
		switch (mode) {
		case COMPARISON_MODE_EXPLORATION:
			return data.fitness;
		case COMPARISON_MODE_DELETION:

			return data.k; // TODO: Something else?
		case COMPARISON_MODE_EXPLOITATION:

			return data.predictedPayOff;
		default:
			return 0;
		}
	}

	@Override
	public void setComparisonValue(Classifier aClassifier, int mode,
			double comparisonValue) {
		XCSClassifierData data = ((XCSClassifierData) aClassifier.updateData);
		data.fitness = comparisonValue; // TODO: Mode changes?

	}

	/**
	 * Implementing abstract method.
	 * 
	 * @see gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy
	 * @param setA
	 *            the action set
	 * @param setB
	 *            the correct set
	 */
	@Override
	public void updateSet(final ClassifierSet setA, final ClassifierSet setB) {
		double accuracySum = 0;

		for (int i = 0; i < setA.getNumberOfMacroclassifiers(); i++) {
			Classifier cl = setA.getClassifier(i);

			// Get update data object
			XCSClassifierData data = ((XCSClassifierData) cl.updateData);
			cl.experience++; // Increase Experience

			double payOff; // the classifier's payoff
			if (setB.getClassifierNumerosity(cl) > 0)
				payOff = payoff;
			else
				payOff = 0;

			// Update Predicted Payoff
			if (cl.experience < 1 / beta)
				data.predictedPayOff += (payOff - data.predictedPayOff)
						/ cl.experience;
			else
				data.predictedPayOff += beta * (payOff - data.predictedPayOff);

			// Update Prediction Error
			if (cl.experience < 1 / beta)
				data.predictionError += (Math
						.abs(payOff - data.predictedPayOff) - data.predictionError)
						/ cl.experience;
			else
				data.predictionError += beta
						* (Math.abs(payOff - data.predictedPayOff) - data.predictionError);

			// Update Action Set Estimate
			if (cl.experience < 1 / beta)
				data.actionSet += (setA.getTotalNumerosity() - data.actionSet)
						/ cl.experience;
			else
				data.actionSet += beta
						* (setA.getTotalNumerosity() - data.actionSet);

			// Fitness Update Step 1
			if (data.predictionError < e0)
				data.k = 1;
			else
				data.k = alpha * Math.pow(data.predictionError / e0, -n);
			accuracySum += data.k * setA.getClassifierNumerosity(i);
		}

		// Update Fitness Step 2
		for (int i = 0; i < setA.getNumberOfMacroclassifiers(); i++) {
			Classifier cl = setA.getClassifier(i);

			// Get update data object
			XCSClassifierData data = ((XCSClassifierData) cl.updateData);

			// per micro-classifier
			data.fitness += beta * (data.k / accuracySum - data.fitness);
		}

	}
}