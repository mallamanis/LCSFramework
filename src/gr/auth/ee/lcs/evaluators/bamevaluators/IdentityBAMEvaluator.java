/*
 *	Copyright (C) 2011 by Allamanis Miltiadis
 *
 *	Permission is hereby granted, free of charge, to any person obtaining a copy
 *	of this software and associated documentation files (the "Software"), to deal
 *	in the Software without restriction, including without limitation the rights
 *	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *	copies of the Software, and to permit persons to whom the Software is
 *	furnished to do so, subject to the following conditions:
 *
 *	The above copyright notice and this permission notice shall be included in
 *	all copies or substantial portions of the Software.
 *
 *	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *	THE SOFTWARE.
 */
/**
 * 
 */
package gr.auth.ee.lcs.evaluators.bamevaluators;

import gr.auth.ee.lcs.AbstractLearningClassifierSystem;
import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.data.ILCSMetric;
import gr.auth.ee.lcs.evaluators.BAMPercentageEvaluator;
import gr.auth.ee.lcs.utilities.ExtendedBitSet;

import java.util.Vector;

/**
 * An mlidentity BAM evaluator.
 * 
 * @author Miltiadis Allamanis
 * 
 */
public class IdentityBAMEvaluator implements ILCSMetric {

	/**
	 * The LCS being evaluated.
	 */
	private final AbstractLearningClassifierSystem lcs;

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
	private final Vector<Classifier> bamChromosomes;

	/**
	 * Constructor.
	 * 
	 * @param n
	 *            the size of the mlPosition_N
	 * @param type
	 *            the type of representation to be used.
	 * @param aLcs
	 *            the LCS to be evaluated on.
	 */
	public IdentityBAMEvaluator(final int n, final int type,
			final AbstractLearningClassifierSystem aLcs) {
		bamChromosomes = new Vector<Classifier>();
		lcs = aLcs;
		generateBAM(n, type);

	}

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
			for (int i = 0; i < (2 * n); i++) {
				bamChromosomes.add(lcs.getNewClassifier(generateGenericRule(i,
						n)));
			}
			break;
		case STRICT_REPRESENTATION:
			generateStrictRules("", "", n);
			break;
		default:
		}

	}

	/**
	 * Create an identity problem rule for the generalized rule representation.
	 * 
	 * @param i
	 * @param n
	 * @return an ExtendedBitSet containing the rule.
	 */
	private ExtendedBitSet generateGenericRule(final int i, final int n) {
		String rule = "";
		final int activatedBit = i / 2;
		final String bit = ((i % 2) == 0) ? "1" : "0";
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

	/**
	 * Generate Identity problem rules using the strict representation.
	 * 
	 * @param rule
	 * @param consequent
	 * @param n
	 */
	private void generateStrictRules(final String rule,
			final String consequent, final int n) {
		if (n > 0) {
			generateStrictRules("01" + rule, "0" + consequent, n - 1);
			generateStrictRules("11" + rule, "1" + consequent, n - 1);
			return;
		} else {
			bamChromosomes.add(lcs.getNewClassifier(new ExtendedBitSet(
					consequent + rule)));
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
	public double getMetric(final AbstractLearningClassifierSystem lcs) {
		final BAMPercentageEvaluator eval = new BAMPercentageEvaluator(
				bamChromosomes);
		return eval.getMetric(lcs);
	}

	public String getMetricName() {
		return "mlIdentity BAM coverage";
	}

}
