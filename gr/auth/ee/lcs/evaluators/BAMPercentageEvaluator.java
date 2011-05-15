/**
 * 
 */
package gr.auth.ee.lcs.evaluators;

import java.util.Arrays;
import java.util.Vector;

import gr.auth.ee.lcs.AbstractLearningClassifierSystem;
import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.data.IEvaluator;
import gr.auth.ee.lcs.utilities.ExtendedBitSet;

/**
 * Evaluates a ClassifierSet for the exact percentage of the BAM that it
 * contains.
 * 
 * @author Miltiadis Allamanis
 * 
 */
public class BAMPercentageEvaluator implements IEvaluator {

	private final Vector<Classifier> bestActionMap;

	public BAMPercentageEvaluator(final Vector<Classifier> bamChromosomes) {
		bestActionMap = bamChromosomes;
	}

	@Override
	public double evaluateSet(ClassifierSet classifiers) {
		final int bamSize = bestActionMap.size();
		boolean[] covered = new boolean[bamSize];
		Arrays.fill(covered, false);
		final int setSize = classifiers.getNumberOfMacroclassifiers();
		for (int i = 0; i < setSize; i++) {
			final Classifier actual = classifiers.getClassifier(i);

			for (int j = 0; j < bamSize; j++) {
				if (covered[j])
					continue;
				Classifier cl = bestActionMap.elementAt(j);
				covered[j] = cl.equals(actual);
			}
		}

		// Count covered instances
		int coveredInstances = 0;
		for (int i = 0; i < bamSize; i++) {
			if (covered[i])
				coveredInstances++;
		}

		final double bamPercentage = ((double) coveredInstances)
				/ ((double) bamSize);
		return bamPercentage;
	}

}
