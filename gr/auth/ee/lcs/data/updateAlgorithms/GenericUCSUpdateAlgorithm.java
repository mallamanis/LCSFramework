/**
 * 
 */
package gr.auth.ee.lcs.data.updateAlgorithms;

import java.io.Serializable;

import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.classifiers.Macroclassifier;
import gr.auth.ee.lcs.data.ClassifierTransformBridge;
import gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy;
import gr.auth.ee.lcs.data.updateAlgorithms.UCSUpdateAlgorithm.UCSClassifierData;
import gr.auth.ee.lcs.geneticalgorithm.IGeneticAlgorithmStrategy;

/**
 * @author Miltos Allamanis
 *
 */
public class GenericUCSUpdateAlgorithm extends
		UpdateAlgorithmFactoryAndStrategy {

	private final int deleteAge = 20;
	
	/* (non-Javadoc)
	 * @see gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy#getComparisonValue(gr.auth.ee.lcs.classifiers.Classifier, int)
	 */
	@Override
	public double getComparisonValue(Classifier aClassifier, int mode) {
		GenericUCSClassifierData data = (GenericUCSClassifierData) aClassifier
		.getUpdateDataObject();

		switch (mode) {
			case COMPARISON_MODE_EXPLORATION:
					final double value = data.fitness
						* (aClassifier.experience < deleteAge ? 0 : 1);
					return Double.isNaN(value) ? 0 : value;
			case COMPARISON_MODE_DELETION:

				if (aClassifier.experience < deleteAge) {
						final double result = 1 / data.fitness; //TODO:Correct?
						return Double.isNaN(result) ? 1 : result;
				}

				//return data.ms;
				final double result = 1 / data.fitness ;// (aClassifier.getCoverage()/.05); //TODO:Correct?
				return Double.isNaN(result) ? 1 : result;

			case COMPARISON_MODE_EXPLOITATION:
					final double acc = (((double) data.metricSum) / (double) (aClassifier.experience));
					final double exploitValue = acc
						* (aClassifier.experience < deleteAge ? 0 : 1);
					return Double.isNaN(exploitValue) ? 0 : exploitValue;
		}
		return 0;
	}

	/* (non-Javadoc)
	 * @see gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy#setComparisonValue(gr.auth.ee.lcs.classifiers.Classifier, int, double)
	 */
	@Override
	public void setComparisonValue(Classifier aClassifier, int mode,
			double comparisonValue) {
		// TODO Auto-generated method stub

	}
	
	/**
	 * The learning rate.
	 */
	final double b;
	
	public GenericUCSUpdateAlgorithm(IGeneticAlgorithmStrategy geneticAlgorithm, double learningRate) {
		this.ga = geneticAlgorithm;
		this.b = learningRate;
	}
	
	/**
	 * Genetic Algorithm.
	 */
	public IGeneticAlgorithmStrategy ga;

	/* (non-Javadoc)
	 * @see gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy#createStateClassifierObject()
	 */
	@Override
	protected Serializable createStateClassifierObject() {
		return new GenericUCSClassifierData();
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

	/* (non-Javadoc)
	 * @see gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy#updateSet(gr.auth.ee.lcs.classifiers.ClassifierSet, gr.auth.ee.lcs.classifiers.ClassifierSet, int)
	 */
	@Override
	protected void updateSet(ClassifierSet population, ClassifierSet matchSet,
			int instanceIndex) {
		final int matchSetSize = matchSet.getNumberOfMacroclassifiers();
		
		if (matchSetSize == 0) {
			cover(population, instanceIndex);
			return;
		}
		
		double sumOfFitness = 0;
		for (int i = 0; i < matchSetSize; i++) {
			final Classifier cl = matchSet.getClassifier(i);
			GenericUCSClassifierData data = ((GenericUCSClassifierData) cl
					.getUpdateDataObject());
			cl.experience++;
			data.ms = data.ms + 0.1 * (matchSetSize - data.ms);
			final double metric = cl.classifyCorrectly(instanceIndex);
			data.metricSum += metric;
			data.fitness0 = Math.pow(data.metricSum / cl.experience, 10);
			sumOfFitness += data.fitness0 * matchSet.getClassifierNumerosity(i);
		}
		
		for (int i = 0; i < matchSetSize; i++) {
			final Classifier cl = matchSet.getClassifier(i);
			GenericUCSClassifierData data = ((GenericUCSClassifierData) cl
					.getUpdateDataObject());
			final double k = data.fitness0 / sumOfFitness;
			data.fitness += b * (k - data.fitness);
			
		}
		
		ga.evolveSet(matchSet, population);

	}

	/* (non-Javadoc)
	 * @see gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy#getData(gr.auth.ee.lcs.classifiers.Classifier)
	 */
	@Override
	public String getData(Classifier aClassifier) {
		GenericUCSClassifierData data = ((GenericUCSClassifierData) aClassifier
				.getUpdateDataObject());
		return "Fitness: "+ data.fitness;
	}
	
	/**
	 * The data used at each classifier.
	 * @author Miltos Allamanis
	 *
	 */
	class GenericUCSClassifierData implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -6248732998420320548L;

		/**
		 * The total sum of the metric used.
		 */
		private double metricSum=0;
		
		/**
		 * Strength.
		 */
		private double fitness0 = 0;
		
		private double ms = 1;
		
		/**
		 *
		 */
		private double fitness = .5;
	}

}
