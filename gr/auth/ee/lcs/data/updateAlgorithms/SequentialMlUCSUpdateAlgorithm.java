/**
 * 
 */
package gr.auth.ee.lcs.data.updateAlgorithms;

import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.classifiers.Macroclassifier;
import gr.auth.ee.lcs.data.ClassifierTransformBridge;
import gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy;
import gr.auth.ee.lcs.data.updateAlgorithms.UCSUpdateAlgorithm.UCSClassifierData;
import gr.auth.ee.lcs.geneticalgorithm.IGeneticAlgorithmStrategy;

import java.io.Serializable;
import java.util.Random;

/**
 * @author Miltos Allamanis
 *
 */
public class SequentialMlUCSUpdateAlgorithm extends UpdateAlgorithmFactoryAndStrategy {

	/**
	 * A data object for the UCS update algorithm.
	 * 
	 * @author Miltos Allamanis
	 * 
	 */
	class UCSClassifierData implements Serializable {

		/**
		 * Serial code for serialization.
		 */
		private static final long serialVersionUID = 3098073593334379507L;

		/**
		 *
		 */
		private double fitness = .5;

		/**
		 * niche set size estimation.
		 */
		private double cs = 1;

		/**
		 * 
		 */
		private double msAvgFitness;

		/**
		 * Match Set Appearances.
		 */
		private int msa = 0;

		/**
		 * true positives.
		 */
		private int tp = 0;

		/**
		 * false positives.
		 */
		private int fp = 0;

		/**
		 * Strength.
		 */
		private double fitness0 = 0;

	}

	/**
	 * Genetic Algorithm.
	 */
	public IGeneticAlgorithmStrategy ga;

	private final int deleteAge;

	/**
	 * Private variables: the UCS parameter sharing. accuracy0 is considered the
	 * subsumption fitness threshold
	 */
	private final double a, accuracy0, n, b;

	/**
	 * The experience threshold for subsumption.
	 */
	private final int subsumptionExperienceThreshold;
	
	/**
	 * Number of labels used
	 */
	private final int numberOfLabels;

	/**
	 * Default constructor.
	 * 
	 * @param alpha
	 *            used in fitness sharing
	 * @param nParameter
	 *            used in fitness sharing
	 * @param acc0
	 *            used in fitness sharing: the minimum "good" accuracy
	 * @param learningRate
	 *            the beta of UCS
	 * @param experienceThreshold
	 *            the experience threshold for subsumption
	 * @param geneticAlgorithm
	 *            the genetic algorithm to be used for evolving
	 * @param thetaDel
	 *            the theta del UCS parameter (deletion age)
	 * @param correctSet Threshold
	 *            the threshold used to set a rule in the correct set
	 */
	public SequentialMlUCSUpdateAlgorithm(final double alpha, final double nParameter,
			final double acc0, final double learningRate,
			final int experienceThreshold, 
			IGeneticAlgorithmStrategy geneticAlgorithm, int thetaDel, int numberOfLabels) {
		this.a = alpha;
		this.n = nParameter;
		this.accuracy0 = acc0;
		this.b = learningRate;
		subsumptionExperienceThreshold = experienceThreshold;		
		this.ga = geneticAlgorithm;
		deleteAge = thetaDel;
		this.numberOfLabels = numberOfLabels;
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
		UCSClassifierData data = (UCSClassifierData) aClassifier
				.getUpdateDataObject();

		switch (mode) {
		case COMPARISON_MODE_EXPLORATION:
			final double value = data.fitness
					* (aClassifier.experience < deleteAge ? 0 : 1);
			return Double.isNaN(value) ? 0 : value;
		case COMPARISON_MODE_DELETION:

			if (aClassifier.experience < deleteAge) {
				final double result = data.cs / data.fitness;
				return Double.isNaN(result) ? 1 : result;
			}

			return data.cs;

		case COMPARISON_MODE_EXPLOITATION:
			final double acc = (((double) (data.tp)) / (double) (data.msa));
			final double exploitValue = acc
					* (aClassifier.experience < deleteAge ? 0 : 1);
			return Double.isNaN(exploitValue) ? 0 : exploitValue;
		}
		return 0;
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
		UCSClassifierData data = ((UCSClassifierData) aClassifier
				.getUpdateDataObject());
		return "tp:" + data.tp;
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
		UCSClassifierData data = ((UCSClassifierData) aClassifier
				.getUpdateDataObject());
		data.fitness = comparisonValue;
	}

	/**
	 * Calls covering operator.
	 * 
	 * @param instanceIndex
	 *            the index of the current sample
	 */
	private void cover(final ClassifierSet population, final int instanceIndex) {
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
	 * @param labelIndex
	 * 			  the label index
	 * @return the correct set
	 */
	private ClassifierSet generateCorrectSet(final ClassifierSet matchSet,
			final int instanceIndex, final int labelIndex) {
		ClassifierSet correctSet = new ClassifierSet(null);
		final int matchSetSize = matchSet.getNumberOfMacroclassifiers();
		for (int i = 0; i < matchSetSize; i++) {
			Macroclassifier cl = matchSet.getMacroclassifier(i);
			if (cl.myClassifier.classifyCorrectly(instanceIndex) > 0)
				correctSet.addClassifier(cl, false);
		}
		return correctSet;
	}
	
	/**
	 * Generates the set of classifier that advocate clearly against or for 
	 * the label at the given index.
	 * @param matchSet the matchSet
	 * @param instanceIndex the instance we are trying to match
	 * @param labelIndex the label index we are trying to match
	 * @return
	 */
	private ClassifierSet generateLabelMatchSet(final ClassifierSet matchSet,
			final int instanceIndex, final int labelIndex) {
		ClassifierSet labelMatchSet = new ClassifierSet(null);
		final int matchSetSize = matchSet.getNumberOfMacroclassifiers();
		for (int i = 0; i < matchSetSize; i++) {
			Macroclassifier cl = matchSet.getMacroclassifier(i);
			if (cl.myClassifier.classifyLabelCorrectly(instanceIndex, labelIndex) != 0)
				labelMatchSet.addClassifier(cl, false);
		}
		return labelMatchSet;
	}

	/**
	 * Perform an update to the set.
	 * 
	 * @param matchSet
	 * @param correctSet
	 */
	private void performUpdate(final ClassifierSet matchSet,
			final ClassifierSet correctSet) {
		double strengthSum = 0;
		final int matchSetMacroclassifiers = matchSet
				.getNumberOfMacroclassifiers();
		final int correctSetSize = correctSet.getTotalNumerosity();
		for (int i = 0; i < matchSetMacroclassifiers; i++) {
			final Classifier cl = matchSet.getClassifier(i);
			UCSClassifierData data = ((UCSClassifierData) cl
					.getUpdateDataObject());
			cl.experience++;
			data.msa += 1;
			data.cs = data.cs + 0.1 * (correctSetSize - data.cs);
			if (correctSet.getClassifierNumerosity(cl) > 0) {
				data.tp += 1;
				final double accuracy = ((double) data.tp)
						/ ((double) data.msa);
				if (accuracy > accuracy0) {
					data.fitness0 = 1;

					// Check subsumption
					if (cl.experience >= this.subsumptionExperienceThreshold)
						cl.setSubsumptionAbility(true);

				} else {
					data.fitness0 = a * Math.pow(accuracy / accuracy0, n);
					cl.setSubsumptionAbility(false);
				}

				strengthSum += data.fitness0
						* matchSet.getClassifierNumerosity(i);
			} else {
				data.fp += 1;
				data.fitness0 = 0;
			}

		}

		// Fix for avoiding problems...
		if (strengthSum == 0)
			strengthSum = 1;

		// double fitnessSum = 0;
		final int msSize = matchSet.getNumberOfMacroclassifiers();
		for (int i = 0; i < msSize; i++) {
			Classifier cl = matchSet.getClassifier(i);
			UCSClassifierData data = ((UCSClassifierData) cl
					.getUpdateDataObject());
			data.fitness += b * (data.fitness0 / strengthSum - data.fitness);// TODO:
																				// Something
																				// else?
			
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy#
	 * createStateClassifierObject()
	 */
	@Override
	protected final Serializable createStateClassifierObject() {
		return new UCSClassifierData();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy#updateSet(gr.auth
	 * .ee.lcs.classifiers.ClassifierSet,
	 * gr.auth.ee.lcs.classifiers.ClassifierSet) Updates the set setA is the
	 * match set setB is the correct set
	 */
	@Override
	protected final void updateSet(final ClassifierSet population,
			final ClassifierSet matchSet, final int instanceIndex) {
		// Generate random labels
		final int[] labelSequence = new int[numberOfLabels];
		for (int i = 0; i < numberOfLabels; i++){
			labelSequence[i] = i;
		}
		
		//Shuffle
		final Random rgen = new Random();
		for (int i = 0; i < numberOfLabels; i++) {
			final int randomPosition = rgen.nextInt(numberOfLabels);
			final int temp = labelSequence[i];
			labelSequence[i] = labelSequence[randomPosition];
			labelSequence[randomPosition] = temp;
		}
		
		//for each label loop
		for (int label = 0; label < this.numberOfLabels; label++) {
			/*
			 * Generate label set
			 */
			final ClassifierSet labelSet = generateLabelMatchSet(matchSet, instanceIndex,label);
			
			/*
			 * Generate correct set
			 */
			final ClassifierSet correctSet = generateCorrectSet(labelSet, instanceIndex,label);
	
			/*
			 * Cover if necessary
			 */
			if (correctSet.getNumberOfMacroclassifiers() == 0) {
				cover(population, instanceIndex);
				continue;
			}
	
			/*
			 * Update
			 */
			performUpdate(labelSet, correctSet);
		}

		/*
		 * Run GA
		 */
		if (matchSet.getNumberOfMacroclassifiers()>0)
			ga.evolveSet(matchSet, population);
		

	}

	
}
