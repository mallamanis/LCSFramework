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
package gr.auth.ee.lcs.implementations.meta;

import gr.auth.ee.lcs.AbstractLearningClassifierSystem;
import gr.auth.ee.lcs.evaluators.AccuracyRecallEvaluator;
import gr.auth.ee.lcs.evaluators.ExactMatchEvalutor;
import gr.auth.ee.lcs.evaluators.HammingLossEvaluator;
import gr.auth.ee.lcs.implementations.RTASLCS;
import gr.auth.ee.lcs.meta.BaggedEnsemble;
import gr.auth.ee.lcs.utilities.SettingsLoader;

import java.io.IOException;
import java.util.Arrays;

import weka.core.Instances;

/**
 * An ensemble of RTASLCSs
 * 
 * @author Miltiadis Allamanis
 * 
 */
public class EnsembleRTASLCS extends BaggedEnsemble {

	public EnsembleRTASLCS(AbstractLearningClassifierSystem[] lcss) {
		super((int) SettingsLoader.getNumericSetting("numberOfLabels", 1), lcss);

		ensemble = new RTASLCS[(int) SettingsLoader.getNumericSetting(
				"ensembleSize", 7)];

		for (int i = 0; i < ensemble.length; i++)
			try {
				ensemble[i] = new RTASLCS();
			} catch (IOException e) {
				e.printStackTrace();
			}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gr.auth.ee.lcs.meta.BaggedEnsemble#createNew()
	 */
	@Override
	public AbstractLearningClassifierSystem createNew() {

		final AbstractLearningClassifierSystem[] newEnsemble = new AbstractLearningClassifierSystem[ensemble.length];
		for (int i = 0; i < ensemble.length; i++) {
			newEnsemble[i] = ensemble[i].createNew();
		}
		return new EnsembleRTASLCS(newEnsemble);

	}

	@Override
	public String[] getEvaluationNames() {
		final String[] names = { "Accuracy(pcut)", "Recall(pcut)",
				"HammingLoss(pcut)", "ExactMatch(pcut)", "Accuracy(ival)",
				"Recall(ival)", "HammingLoss(ival)", "ExactMatch(ival)" };
		return names;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.meta.BaggedEnsemble#getEvaluations(weka.core.Instances)
	 */
	@Override
	public double[] getEvaluations(Instances testSet) {
		this.setElements(ensemble[0].getClassifierTransformBridge(), null);
		final double[] results = new double[8];
		Arrays.fill(results, 0);

		for (int i = 0; i < ensemble.length; i++)
			((RTASLCS) ensemble[i]).proportionalCutCalibration();

		final AccuracyRecallEvaluator accEval = new AccuracyRecallEvaluator(
				testSet, false, this, AccuracyRecallEvaluator.TYPE_ACCURACY);
		results[0] = accEval.getMetric(this);

		final AccuracyRecallEvaluator recEval = new AccuracyRecallEvaluator(
				testSet, false, this, AccuracyRecallEvaluator.TYPE_RECALL);
		results[1] = recEval.getMetric(this);

		final HammingLossEvaluator hamEval = new HammingLossEvaluator(testSet,
				false, numberOfLabels, this);
		results[2] = hamEval.getMetric(this);

		final ExactMatchEvalutor testEval = new ExactMatchEvalutor(testSet,
				false, this);
		results[3] = testEval.getMetric(this);

		for (int i = 0; i < ensemble.length; i++) {
			final AccuracyRecallEvaluator selfAcc = new AccuracyRecallEvaluator(
					ensemble[i].instances, false, ensemble[i],
					AccuracyRecallEvaluator.TYPE_ACCURACY);
			((RTASLCS) ensemble[i]).internalValidationCalibration(selfAcc);
		}

		results[4] = accEval.getMetric(this);
		results[5] = recEval.getMetric(this);
		results[6] = hamEval.getMetric(this);
		results[7] = testEval.getMetric(this);

		return results;
	}

}
