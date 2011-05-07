package gr.auth.ee.lcs.data.updateAlgorithms;

import gr.auth.ee.lcs.AbstractLearningClassifierSystem;
import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.geneticalgorithm.IGeneticAlgorithmStrategy;

/**
 * The update algorithm for the AS-LCS.
 * 
 * @author Miltos Allamanis
 * 
 */
public class ASLCSUpdateAlgorithm extends AbstractSLCSUpdateAlgorithm {

	/**
	 * The strictness factor for updating.
	 */
	private final double n;
	

	/**
	 * Object's Constructor.
	 * 
	 * @param nParameter
	 *            the strictness factor ν used in updating
	 * @param fitnessThreshold
	 *            the fitness threshold for subsumption
	 * @param experienceThreshold
	 *            the experience threshold for subsumption
	 * @param gaMatchSetRunProbability
	 *            the probability of running the GA on the match set
	 * @param geneticAlgorithm
	 *            the GA
	 */
	public ASLCSUpdateAlgorithm(final double nParameter,
			final double fitnessThreshold, final int experienceThreshold,
			double gaMatchSetRunProbability,
			IGeneticAlgorithmStrategy geneticAlgorithm, AbstractLearningClassifierSystem lcs) {
		super(fitnessThreshold, experienceThreshold, gaMatchSetRunProbability,
				geneticAlgorithm, lcs);
		this.n = nParameter;
		
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
		SLCSClassifierData data = (SLCSClassifierData) aClassifier
				.getUpdateDataObject();
		switch (mode) {
		case COMPARISON_MODE_EXPLORATION:
			return data.fitness * (aClassifier.experience < 8 ? 0 : 1);
		case COMPARISON_MODE_DELETION:
			return 1 / (data.fitness
					* ((aClassifier.experience < 20) ? 100. : Math.exp(-(Double
							.isNaN(data.ns) ? 1 : data.ns) + 1)) * (((aClassifier
					.getCoverage() == 0) && (aClassifier.experience == 1)) ? 0.
					: 1));
			// TODO: Something else?
		case COMPARISON_MODE_EXPLOITATION:
			return data.fitness * (aClassifier.experience < 8 ? 0 : 1);
			/*
			 * final double exploitationFitness = (((double) (data.tp)) /
			 * (double) (data.msa)); return Double.isNaN(exploitationFitness) ?
			 * .000001 : exploitationFitness;
			 */
		}
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gr.auth.ee.lcs.data.updateAlgorithms.AbstractSLCSUpdateAlgorithm#
	 * updateFitness(gr.auth.ee.lcs.classifiers.Classifier, int,
	 * gr.auth.ee.lcs.classifiers.ClassifierSet)
	 */
	@Override
	public void updateFitness(final Classifier aClassifier,
			final int numerosity, final ClassifierSet correctSet) {
		SLCSClassifierData data = ((SLCSClassifierData) aClassifier
				.getUpdateDataObject());
		if (correctSet.getClassifierNumerosity(aClassifier) > 0)
			data.tp += 1; // aClassifier at the correctSet
		else
			data.fp += 1;

		// Niche set sharing heuristic...
		data.fitness = Math.pow(((double) (data.tp)) / (double) (data.msa), n);

	}

}