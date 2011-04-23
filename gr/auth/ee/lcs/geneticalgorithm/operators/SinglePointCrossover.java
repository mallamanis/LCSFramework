package gr.auth.ee.lcs.geneticalgorithm.operators;

import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy;
import gr.auth.ee.lcs.geneticalgorithm.IBinaryGeneticOperator;
import gr.auth.ee.lcs.utilities.ExtendedBitSet;

/**
 * A binary genetic operator that performs gene crossover at a single point.
 * 
 * @author Miltos Allamanis
 */
public class SinglePointCrossover implements IBinaryGeneticOperator {

	/**
	 * The implementation of the abstract method.
	 * 
	 * @see gr.auth.ee.lcs.geneticalgorithm.IBinaryGeneticOperator
	 */
	@Override
	public final Classifier operate(final Classifier classifierA,
			final Classifier classifierB) {
		final int chromosomeSize = classifierB.size();
		Classifier child;
		/*
		 * The point at which the crossover will occur
		 */
		final int mutationPoint = (int) Math.round(Math.random()
				* chromosomeSize - 1);
		child = new Classifier(performCrossover(classifierA, classifierB,
				mutationPoint));
		double newFitness = classifierA
				.getComparisonValue(UpdateAlgorithmFactoryAndStrategy.COMPARISON_MODE_EXPLOITATION)
				+ classifierB
						.getComparisonValue(UpdateAlgorithmFactoryAndStrategy.COMPARISON_MODE_EXPLOITATION);
		newFitness /= 2;
		child.setComparisonValue(
				UpdateAlgorithmFactoryAndStrategy.COMPARISON_MODE_EXPLOITATION,
				newFitness);
		// TODO: Set specific update data
		return child;
	}

	/**
	 * A protected function that performs a single point crossover.
	 * 
	 * @param chromosomeA
	 *            the first chromosome to crossover
	 * @param chromosomeB
	 *            the second chromosome to crossover
	 * @param position
	 *            the position (bit) to perform the crossover
	 * @return the new cross-overed (child) chromosome
	 */
	protected final ExtendedBitSet performCrossover(
			final ExtendedBitSet chromosomeA, final ExtendedBitSet chromosomeB,
			final int position) {
		final ExtendedBitSet child = (ExtendedBitSet) chromosomeA.clone();
		child.setSubSet(position,
				chromosomeB.getSubSet(position, chromosomeB.size() - position));

		return child;
	}

}