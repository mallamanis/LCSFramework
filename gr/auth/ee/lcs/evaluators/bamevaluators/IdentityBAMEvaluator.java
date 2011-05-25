/**
 * 
 */
package gr.auth.ee.lcs.evaluators.bamevaluators;

import java.util.Vector;

import gr.auth.ee.lcs.AbstractLearningClassifierSystem;
import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.data.IEvaluator;
import gr.auth.ee.lcs.evaluators.BAMPercentageEvaluator;
import gr.auth.ee.lcs.utilities.ExtendedBitSet;

/**
 * An mlidentity BAM evaluator.
 * 
 * @author Miltiadis Allamanis
 * 
 */
public class IdentityBAMEvaluator implements IEvaluator {

	private final AbstractLearningClassifierSystem lcs;

	/**
	 * Constructor.
	 * 
	 * @param n
	 *            the size of the mlPosition_N
	 * @param type
	 *            the type of representation to be used.
	 */
	public IdentityBAMEvaluator(final int n, final int type,
			final AbstractLearningClassifierSystem aLcs) {
		bamChromosomes = new Vector<Classifier>();
		lcs = aLcs;
		generateBAM(n, type);

	}

	/**
	 * Rules are represented with a strict ml representation.
	 */
	public static final int STRICT_REPRESENTATION = 0;
	/**
	 * Rules are represented with a generic ml representation.
	 */
	public static final int GENERIC_REPRESENTATION = 1;

	/**
	 * A vector containing the chromosomes of the BAM.
	 */
	private Vector<Classifier> bamChromosomes;

	/**
	 * Generate problem BAM.
	 * 
	 * @param n
	 *            the size of the mlPosition_N
	 * @param type
	 *            the type of representation to be used
	 */
	private void generateBAM(final int n, final int type) {
		switch (type) {
		case GENERIC_REPRESENTATION:
			for (int i = 0; i < 2 * n; i++) {
				bamChromosomes.add(lcs.getNewClassifier(generateGenericRule(i,
						n)));
			}
			break;
		case STRICT_REPRESENTATION:
			generateStrictRules("","",n);
			break;
		default:
		}

	}

	private ExtendedBitSet generateGenericRule(final int i, final int n) {
		String rule = "";
		final int activatedBit = i / 2;
		final String bit = (i % 2 == 0) ? "1" : "0";
		for (int j = 0; j < 2; j++) {
			for (int position = 0; position < n; position++) {
				if (activatedBit == position) {
					rule = bit + "1" + rule;
				} else {
					rule = "00" + rule;
				}
			}
		}
		return new ExtendedBitSet(rule);
	}
	
	private void generateStrictRules(final String rule, final String consequent, final int n) {
		if (n > 0) {
			generateStrictRules("01" + rule,"0"+consequent, n -1);
			generateStrictRules("11" + rule,"1"+consequent, n -1);
			return;
		} else {
			bamChromosomes.add(lcs.getNewClassifier(new ExtendedBitSet(consequent+rule)));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.data.IEvaluator#evaluateSet(gr.auth.ee.lcs.classifiers
	 * .ClassifierSet)
	 */
	@Override
	public double evaluateSet(ClassifierSet classifiers) {
		BAMPercentageEvaluator eval = new BAMPercentageEvaluator(bamChromosomes);
		return eval.evaluateSet(classifiers);
	}

}
