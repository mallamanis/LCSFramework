package gr.auth.ee.lcs.data.updateAlgorithms;

import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.classifiers.Macroclassifier;
import gr.auth.ee.lcs.data.ClassifierTransformBridge;
import gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy;
import gr.auth.ee.lcs.geneticalgorithm.IGeneticAlgorithmStrategy;

import java.io.Serializable;

/**
 * The XCS update algorithm.
 * 
 * @author Miltos Allamanis
 */
public class XCSUpdateAlgorithm extends UpdateAlgorithmFactoryAndStrategy {

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
	 * A double indicating the probability that the GA will run on the matchSet
	 * (and not on the correct set).
	 */
	private double matchSetRunProbability;

	/**
	 * Genetic Algorithm.
	 */
	public IGeneticAlgorithmStrategy ga;

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
			final double fitnessThreshold, final int experienceThreshold,
			double gaMatchSetRunProbability,
			IGeneticAlgorithmStrategy geneticAlgorithm) {
		this.subsumptionFitnessThreshold = fitnessThreshold;
		this.subsumptionExperienceThreshold = experienceThreshold;
		this.beta = beta;
		this.payoff = P;
		this.e0 = e0;
		this.alpha = alpha;
		this.n = n;
		this.ga = geneticAlgorithm;
		this.matchSetRunProbability = gaMatchSetRunProbability;
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
		aClassifier
				.setSubsumptionAbility((aClassifier
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
	public final double getComparisonValue(final Classifier aClassifier,
			final int mode) {
		final XCSClassifierData data = ((XCSClassifierData) aClassifier
				.getUpdateDataObject());
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy#setComparisonValue
	 * (gr.auth.ee.lcs.classifiers.Classifier, int, double)
	 */
	@Override
	public final void setComparisonValue(final Classifier aClassifier,
			final int mode, final double comparisonValue) {
		XCSClassifierData data = ((XCSClassifierData) aClassifier
				.getUpdateDataObject());
		data.fitness = comparisonValue; // TODO: Mode changes?

	}

	/**
	 * Calls covering operator.
	 * @param population the population to cover
	 * @param instanceIndex
	 *            the index of the current sample
	 */
	private void cover(final ClassifierSet population,
			final int instanceIndex) {
		Classifier coveringClassifier = ClassifierTransformBridge.getInstance()
				.createRandomCoveringClassifier(
						ClassifierTransformBridge.instances[instanceIndex]);
		population.addClassifier(new Macroclassifier(coveringClassifier, 1),
				false);
	}

	/**
	 * Generates the correct set.
	 * 
	 * @param matchSet
	 *            the match set
	 * @param instanceIndex
	 *            the global instance index
	 * @return the correct set
	 */
	private ClassifierSet generateCorrectSet(final ClassifierSet matchSet,
			final int instanceIndex) {
		ClassifierSet correctSet = new ClassifierSet(null);
		final int matchSetSize = matchSet.getNumberOfMacroclassifiers();
		for (int i = 0; i < matchSetSize; i++) {
			Macroclassifier cl = matchSet.getMacroclassifier(i);
			if (cl.myClassifier.classifyCorrectly(instanceIndex) == 1)
				correctSet.addClassifier(cl, false);
		}
		return correctSet;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy#updateSet(gr.auth
	 * .ee.lcs.classifiers.ClassifierSet,
	 * gr.auth.ee.lcs.classifiers.ClassifierSet, int)
	 */
	@Override
	protected final void updateSet(final ClassifierSet population,
			final ClassifierSet matchSet, final int instanceIndex) {
		/*
		 * Generate correct set
		 */
		ClassifierSet correctSet = generateCorrectSet(matchSet, instanceIndex);

		/*
		 * Cover if necessary
		 */
		if (correctSet.getNumberOfMacroclassifiers() == 0) {
			cover(population, instanceIndex);
			return;
		}

		/*
		 * Update
		 */
		performUpdate(matchSet, correctSet);

		/*
		 * Run GA
		 */
		if (Math.random() < matchSetRunProbability)
			ga.evolveSet(matchSet, population);
		else
			ga.evolveSet(correctSet, population);

	}

	/**
	 * Perform Update
	 * 
	 * @param actionSet
	 *            the action set
	 * @param correctSet
	 *            the correct set
	 */
	public final void performUpdate(final ClassifierSet actionSet,
			final ClassifierSet correctSet) {
		double accuracySum = 0;

		for (int i = 0; i < actionSet.getNumberOfMacroclassifiers(); i++) {
			Classifier cl = actionSet.getClassifier(i);

			// Get update data object
			XCSClassifierData data = ((XCSClassifierData) cl
					.getUpdateDataObject());
			cl.experience++; // Increase Experience

			double payOff; // the classifier's payoff
			if (correctSet.getClassifierNumerosity(cl) > 0)
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
				data.actionSet += (actionSet.getTotalNumerosity() - data.actionSet)
						/ cl.experience;
			else
				data.actionSet += beta
						* (actionSet.getTotalNumerosity() - data.actionSet);

			// Fitness Update Step 1
			if (data.predictionError < e0)
				data.k = 1;
			else
				data.k = alpha * Math.pow(data.predictionError / e0, -n);
			accuracySum += data.k * actionSet.getClassifierNumerosity(i);
		}

		// Update Fitness Step 2
		for (int i = 0; i < actionSet.getNumberOfMacroclassifiers(); i++) {
			Classifier cl = actionSet.getClassifier(i);

			// Get update data object
			XCSClassifierData data = ((XCSClassifierData) cl
					.getUpdateDataObject());

			// per micro-classifier
			data.fitness += beta * (data.k / accuracySum - data.fitness);
		}

	}

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy#getData(gr.auth
	 * .ee.lcs.classifiers.Classifier)
	 */
	@Override
	public final String getData(final Classifier aClassifier) {
		String response;
		XCSClassifierData data = ((XCSClassifierData) aClassifier
				.getUpdateDataObject());
		response = "predictionError:" + data.predictionError
				+ ", predictedPayOff:" + data.predictedPayOff;
		return response;
	}
}