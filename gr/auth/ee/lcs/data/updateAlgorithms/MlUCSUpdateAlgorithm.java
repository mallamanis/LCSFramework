/**
 * 
 */
package gr.auth.ee.lcs.data.updateAlgorithms;

import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.classifiers.Macroclassifier;
import gr.auth.ee.lcs.data.ClassifierTransformBridge;
import gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy;
import gr.auth.ee.lcs.geneticalgorithm.IGeneticAlgorithmStrategy;

import java.io.Serializable;
import java.util.Arrays;

/**
 * A generalized UCS Update Algorithm. This UCS tries to estimate a given metric
 * (not just accuracy)
 * 
 * @author Miltos Allamanis
 * 
 */
public class MlUCSUpdateAlgorithm extends
		UpdateAlgorithmFactoryAndStrategy {

	/**
	 * The data used at each classifier.
	 * 
	 * @author Miltos Allamanis
	 * 
	 */
	class MlUCSClassifierData implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -6248732998420320548L;

		public MlUCSClassifierData(int numberOfLabels) {
			tp = new int[numberOfLabels];
			fp = new int[numberOfLabels];
			cs = new float[numberOfLabels];
			active = new boolean[numberOfLabels];
			labelFitness = new float[numberOfLabels];
			fitness0 = new float[numberOfLabels];
		}
		
		/**
		 * The total sum of the metric used.
		 */
		private int tp[];

		private int fp[];
		
		private float fitness0[];
		
		private float labelFitness[];
		
		private float cs[];
		
		private boolean active[];
		
		/**
		 * Strength.
		 */
		private int activeLabels = -1;

		/**
		 * Fitness.
		 */
		private double fitness = .5;
		
		private double globalCs = 0.1;
		
		private double acc;
	}

	/**
	 * The theta_DEL parameter of UCS.
	 */
	private final int deleteAge;

	/**
	 * The learning rate.
	 */
	private final double b;
	
	/**
	 * Acc0
	 */
	private final double acc0 = .99;

	/**
	 * Genetic Algorithm.
	 */
	private final IGeneticAlgorithmStrategy ga;
	
	private final int numberOfLabels;

	/**
	 * The constructor.
	 * 
	 * @param geneticAlgorithm
	 *            the GA to be used
	 * @param learningRate
	 *            the learning rate to be applied at each iteration
	 */
	public MlUCSUpdateAlgorithm(
			final IGeneticAlgorithmStrategy geneticAlgorithm,
			final double learningRate, final int ageThreshold, final int numberOfLabels) {
		this.ga = geneticAlgorithm;
		this.b = learningRate;
		deleteAge = ageThreshold;
		this.numberOfLabels =  numberOfLabels;
	}

	/**
	 * Calls covering operator.
	 * 
	 * @param population
	 *            the population where the new covering classifier will be added
	 * @param instanceIndex
	 *            the index of the current sample
	 */
	@Override
	public void cover(final ClassifierSet population, final int instanceIndex) {
		Classifier coveringClassifier = ClassifierTransformBridge.getInstance()
				.createRandomCoveringClassifier(
						ClassifierTransformBridge.instances[instanceIndex]);
		population.addClassifier(new Macroclassifier(coveringClassifier, 1),
				false);
	}

	@Override
	public final Serializable createStateClassifierObject() {
		return new MlUCSClassifierData(numberOfLabels);
	}

	@Override
	public final double getComparisonValue(final Classifier aClassifier,
			final int mode) {
		MlUCSClassifierData data = (MlUCSClassifierData) aClassifier
				.getUpdateDataObject();

		switch (mode) {
		case COMPARISON_MODE_EXPLORATION:
			final double value = data.fitness
					* (aClassifier.experience < deleteAge ? 0.1 : 1);
			return Double.isNaN(value) ? 0 : value;
		case COMPARISON_MODE_DELETION:

			if (aClassifier.experience < deleteAge) {
				final double result = data.globalCs / data.fitness;
				return Double.isNaN(result) ? 1 : result;
			}

			return data.globalCs;

		case COMPARISON_MODE_EXPLOITATION:
					
			final double exploitValue = data.acc
					* (aClassifier.experience < deleteAge ? 0 : 1);
			return Double.isNaN(exploitValue) ? 0 : exploitValue;
		default:
			return 0;
		}

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
		MlUCSClassifierData data = ((MlUCSClassifierData) aClassifier
				.getUpdateDataObject());
		return "Fitness: " + data.fitness + "activeLbl:" + data.activeLabels + "tp:" + Arrays.toString(data.tp) 
			+"globalCs:"+data.globalCs+"cs:"+Arrays.toString(data.cs); // TODO
																			// more
	}

	@Override
	public void performUpdate(ClassifierSet matchSet, ClassifierSet correctSet) {
		return;
	}

	@Override
	public final void setComparisonValue(final Classifier aClassifier,
			final int mode, final double comparisonValue) {
		// TODO Auto-generated method stub

	}

	private void buildActive(final Classifier cl) {
		int active = 0;
		final MlUCSClassifierData data = ((MlUCSClassifierData) cl
				.getUpdateDataObject());
		for (int i = 0; i < numberOfLabels; i++) {
			if (cl.classifyLabelCorrectly(0, i) != 0 ) {
				active ++;
				data.active[i] = true;
			} else {
				data.active[i] = false;
			}
		}
		
		data.activeLabels = active;
	}
	
	/**
	 * Generates the correct set.
	 * 
	 * @param matchSet
	 *            the match set
	 * @param instanceIndex
	 *            the global instance index
	 * @param labelIndex
	 *            the label index
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
	 * Generates the set of classifier that advocate clearly against or for the
	 * label at the given index.
	 * 
	 * @param matchSet
	 *            the matchSet
	 * @param instanceIndex
	 *            the instance we are trying to match
	 * @param labelIndex
	 *            the label index we are trying to match
	 * @return
	 */
	private ClassifierSet generateLabelMatchSet(final ClassifierSet matchSet,
			final int instanceIndex, final int labelIndex) {
		ClassifierSet labelMatchSet = new ClassifierSet(null);
		final int matchSetSize = matchSet.getNumberOfMacroclassifiers();
		for (int i = 0; i < matchSetSize; i++) {
			Macroclassifier cl = matchSet.getMacroclassifier(i);
			if (cl.myClassifier.classifyLabelCorrectly(instanceIndex,
					labelIndex) != 0)
				labelMatchSet.addClassifier(cl, false);
		}
		return labelMatchSet;
	}
	
	
	private final int n =10;
	
	private void updatePerLabel(final ClassifierSet population,
			final ClassifierSet matchSet, final int instanceIndex) {
		for (int label = 0; label < numberOfLabels; label++) {
			ClassifierSet labelMatchSet = generateLabelMatchSet(matchSet, instanceIndex, label);
			ClassifierSet labelCorrectSet = generateCorrectSet(labelMatchSet, instanceIndex, label);
			final int matchSetSize = labelMatchSet.getNumberOfMacroclassifiers();
			final int correctSetSize = labelCorrectSet.getTotalNumerosity();
			
			if (correctSetSize == 0) {
				cover(population, instanceIndex);
				continue;
			} 
			
			float fitnessSum = 0;
			for (int i = 0; i < matchSetSize; i++) {
				final Classifier cl = labelMatchSet.getClassifier(i);
				final MlUCSClassifierData data = ((MlUCSClassifierData) cl
						.getUpdateDataObject());
				if (cl.classifyLabelCorrectly(instanceIndex, label) > 0) {
					data.tp[label] += 1;
					if (Double.isInfinite(data.cs[label]))
						System.out.println("in " + data.cs[label]);
					data.cs[label] += b * (correctSetSize - data.cs[label]);
					if (Double.isInfinite(data.cs[label]))
						System.out.println("out " + data.cs[label]);
					final float acc = ((float) (data.tp[label])) / ((float) (data.tp[label] + data.fp[label]));
					if (acc > acc0) {
						data.fitness0[label] = 1;						
					} else {
						data.fitness0[label] = (float) Math.pow(acc/acc0, n);
					}
					fitnessSum += data.fitness0[label];
				} else {
					data.fp[label] += 1;
					data.fitness0[label] = 0;
				}
			}
			
			for (int i = 0; i < matchSetSize; i++) {
				final Classifier cl = labelMatchSet.getClassifier(i);
				final MlUCSClassifierData data = ((MlUCSClassifierData) cl
						.getUpdateDataObject());
				data.labelFitness[label] += b * (data.fitness0[label] / fitnessSum  - data.labelFitness[label]);
			}
		}
	}
	
	private void gatherResults(ClassifierSet matchSet) {
		final int matchSetSize = matchSet.getNumberOfMacroclassifiers();
		for (int i = 0; i < matchSetSize; i++) {
			final Classifier cl = matchSet.getClassifier(i);
			final MlUCSClassifierData data = ((MlUCSClassifierData) cl
					.getUpdateDataObject());
			if (data.activeLabels == 0) {
				data.fitness = 0;
				continue;
			}
			double fitnessHMean = 0;
			double csMin = Double.MIN_VALUE;
			for (int label = 0; label < numberOfLabels; label++) {
				if (!data.active[label]) continue;
				fitnessHMean += 1 / data.labelFitness[label];
				if (csMin < data.cs[label])
					csMin = data.cs[label];
			}
			
			int totalTp = 0;
			int msa = 0;
			for (int j = 0; j < numberOfLabels; j++) {
				totalTp += data.tp[j];
				msa += data.fp[j] + data.tp[j];
			}
			data.acc = (((double) totalTp) / ((double) msa));
			if (data.acc > acc0)
				cl.setSubsumptionAbility(true);
			else
				cl.setSubsumptionAbility(false);
			final double accC = data.acc>acc0?Math.pow(data.acc,n):(.1*Math.pow(data.acc/acc0, n));
			final double hmean = ((double) data.activeLabels) / fitnessHMean;
			data.fitness = accC * hmean;
			data.globalCs += b * (csMin - data.globalCs);
			
		}
	}
	
	@Override
	protected final void updateSet(final ClassifierSet population,
			final ClassifierSet matchSet, final int instanceIndex) {
		final int matchSetSize = matchSet.getNumberOfMacroclassifiers();

		
		for (int i = 0; i < matchSetSize; i++) {
			final Classifier cl = matchSet.getClassifier(i);
			final MlUCSClassifierData data = ((MlUCSClassifierData) cl
					.getUpdateDataObject());
			cl.experience++;
			
			if (data.activeLabels < 0)
				buildActive(cl);		
			
		}

		

		updatePerLabel(population,matchSet,instanceIndex);
		
		gatherResults(matchSet);
				
		if (matchSetSize > 0)
			ga.evolveSet(matchSet, population);

	
	}

}
