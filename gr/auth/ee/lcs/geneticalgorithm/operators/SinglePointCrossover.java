package gr.auth.ee.lcs.geneticalgorithm.operators;

import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ExtendedBitSet;
import gr.auth.ee.lcs.geneticalgorithm.IBinaryGeneticOperator;

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
	public Classifier operate(final Classifier classifierA, final Classifier classifierB) {
		int chromosomeSize = classifierB.size();
		Classifier child;
		/*
		 * The point at which the crossover will occur
		 */
		int mutationPoint = (int) Math
				.round(Math.random() * chromosomeSize - 1);
		child = new Classifier(performCrossover(classifierA, classifierB,
				mutationPoint));
		child.fitness = (classifierA.fitness + classifierB.fitness) / 2;
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
	protected final ExtendedBitSet performCrossover(final ExtendedBitSet chromosomeA,
			final ExtendedBitSet chromosomeB, final int position) {
		ExtendedBitSet child = (ExtendedBitSet) chromosomeA.clone();
		child.setSubSet(position,
				chromosomeB.getSubSet(position, chromosomeB.size() - position));

		return child;
	}

}