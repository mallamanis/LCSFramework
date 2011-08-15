/**
 * 
 */
package gr.auth.ee.lcs.impementations.meta;

import gr.auth.ee.lcs.AbstractLearningClassifierSystem;
import gr.auth.ee.lcs.FoldEvaluator;
import gr.auth.ee.lcs.evaluators.AccuracyRecallEvaluator;
import gr.auth.ee.lcs.evaluators.ExactMatchEvalutor;
import gr.auth.ee.lcs.evaluators.HammingLossEvaluator;
import gr.auth.ee.lcs.meta.BaggedEnsemble;
import gr.auth.ee.lcs.utilities.SettingsLoader;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import weka.core.Instances;

/**
 * An Ensemble of the BRSeqUCS
 * 
 * @author Miltiadis Allamanis
 * 
 */
public class EnsembleBRSeqUCSComb extends BaggedEnsemble {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		SettingsLoader.loadSettings();
		final Handler fileLogging = new FileHandler("output.log");

		Logger.getLogger("").setLevel(Level.CONFIG);
		Logger.getLogger("").addHandler(fileLogging);
		final String file = SettingsLoader.getStringSetting("filename", "");

		final EnsembleBRSeqUCSComb trucs = new EnsembleBRSeqUCSComb(null);
		FoldEvaluator loader = new FoldEvaluator(10, trucs, file);
		loader.evaluate();

	}

	public EnsembleBRSeqUCSComb(AbstractLearningClassifierSystem[] lcss) {
		super((int) SettingsLoader.getNumericSetting("numberOfLabels", 1), lcss);

		ensemble = new BRSGUCSCombination[(int) SettingsLoader
				.getNumericSetting("ensembleSize", 7)];

		for (int i = 0; i < ensemble.length; i++)
			ensemble[i] = new BRSGUCSCombination();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gr.auth.ee.lcs.meta.BaggedEnsemble#createNew()
	 */
	@Override
	public AbstractLearningClassifierSystem createNew() {

		AbstractLearningClassifierSystem[] newEnsemble = new AbstractLearningClassifierSystem[ensemble.length];
		for (int i = 0; i < ensemble.length; i++) {
			newEnsemble[i] = ensemble[i].createNew();
		}
		return new EnsembleBRSeqUCSComb(newEnsemble);

	}

	@Override
	public String[] getEvaluationNames() {
		String[] names = { "Accuracy(pcut)", "Recall(pcut)",
				"HammingLoss(pcut)", "ExactMatch(pcut)", "Accuracy(ival)",
				"Recall(ival)", "HammingLoss(ival)", "ExactMatch(ival)",
				"Accuracy(best)", "Recall(best)", "HammingLoss(best)",
				"ExactMatch(best)" };
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
		double[] results = new double[12];
		Arrays.fill(results, 0);

		for (int i = 0; i < ensemble.length; i++)
			((BRSGUCSCombination) ensemble[i]).proportionalCutCalibration();

		final AccuracyRecallEvaluator accEval = new AccuracyRecallEvaluator(
				testSet, false, this, AccuracyRecallEvaluator.TYPE_ACCURACY);
		results[0] = accEval.evaluateLCS(this);

		final AccuracyRecallEvaluator recEval = new AccuracyRecallEvaluator(
				testSet, false, this, AccuracyRecallEvaluator.TYPE_RECALL);
		results[1] = recEval.evaluateLCS(this);

		final HammingLossEvaluator hamEval = new HammingLossEvaluator(testSet,
				false, numberOfLabels, this);
		results[2] = hamEval.evaluateLCS(this);

		final ExactMatchEvalutor testEval = new ExactMatchEvalutor(testSet,
				false, this);
		results[3] = testEval.evaluateLCS(this);

		for (int i = 0; i < ensemble.length; i++) {
			final AccuracyRecallEvaluator selfAcc = new AccuracyRecallEvaluator(
					ensemble[i].instances, false, ensemble[i],
					AccuracyRecallEvaluator.TYPE_ACCURACY);
			((BRSGUCSCombination) ensemble[i])
					.internalValidationCalibration(selfAcc);
		}

		results[4] = accEval.evaluateLCS(this);
		results[5] = recEval.evaluateLCS(this);
		results[6] = hamEval.evaluateLCS(this);
		results[7] = testEval.evaluateLCS(this);

		for (int i = 0; i < ensemble.length; i++)
			((BRSGUCSCombination) ensemble[i]).useBestClassificationMode();

		results[8] = accEval.evaluateLCS(this);
		results[9] = recEval.evaluateLCS(this);
		results[10] = hamEval.evaluateLCS(this);
		results[11] = testEval.evaluateLCS(this);

		return results;
	}

}
