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
package gr.auth.ee.lcs;

import gr.auth.ee.lcs.classifiers.populationcontrol.SortPopulationControl;
import gr.auth.ee.lcs.data.AbstractUpdateStrategy;
import gr.auth.ee.lcs.evaluators.AccuracyRecallEvaluator;
import gr.auth.ee.lcs.evaluators.ExactMatchEvalutor;
import gr.auth.ee.lcs.evaluators.FileLogger;
import gr.auth.ee.lcs.evaluators.bamevaluators.IdentityBAMEvaluator;
import gr.auth.ee.lcs.evaluators.bamevaluators.PositionBAMEvaluator;

import java.io.IOException;

/**
 * A best-action map evaluator
 * 
 * @author Miltos Allamanis
 * 
 */
public class BAMEvaluator {

	/**
	 * Constant indicating the mlIdentity testbed problem.
	 */
	public final static int TYPE_IDENTITY = 1;

	/**
	 * Constant indicating the mlPosition testbed problem.
	 */
	public final static int TYPE_POSITION = 2;

	/**
	 * The learning classifier system that the BAM will be evaluated on.
	 */
	final AbstractLearningClassifierSystem lcs;

	/**
	 * Constructor.
	 * 
	 * @param myLcs
	 *            the LCS that the evaluation will be performed on.
	 * @param filename
	 *            the filename of the testbed problem.
	 * @param type
	 *            the type of the testbed problem TYPE_IDENTITY, TYPE_POSITION
	 * @param size
	 *            the size of the testbed problem (N parameter)
	 * @param representationType
	 *            the type of the representation to be used
	 * @param name
	 */
	public BAMEvaluator(final AbstractLearningClassifierSystem myLcs,
			final String filename, final int type, final int size,
			final int representationType, final String name) {
		lcs = myLcs;
		final ArffTrainTestLoader loader = new ArffTrainTestLoader(myLcs);
		try {
			loader.loadInstances(filename, false);
		} catch (IOException e) {
			e.printStackTrace();
		}

		lcs.registerHook(new FileLogger(name + "_acc",
				new AccuracyRecallEvaluator(lcs.instances, false, lcs,
						AccuracyRecallEvaluator.TYPE_ACCURACY)));
		lcs.registerHook(new FileLogger(name + "_ex", new ExactMatchEvalutor(
				lcs.instances, false, lcs)));

		if (type == TYPE_IDENTITY) {
			lcs.registerHook(new FileLogger(name + "_bam",
					new IdentityBAMEvaluator(size, representationType, lcs)));
		} else {
			lcs.registerHook(new FileLogger(name + "_bam",
					new PositionBAMEvaluator(size, representationType, lcs)));
		}
	}

	/**
	 * Perform evaluation
	 */
	public void evaluate() {
		lcs.train();
		final SortPopulationControl srt = new SortPopulationControl(
				AbstractUpdateStrategy.COMPARISON_MODE_EXPLOITATION);
		srt.controlPopulation(lcs.rulePopulation);
		lcs.rulePopulation.print();
	}
}
