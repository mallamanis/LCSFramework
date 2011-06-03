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

	public final static int TYPE_IDENTITY = 1;
	public final static int TYPE_POSITION = 2;

	final AbstractLearningClassifierSystem lcs;

	public BAMEvaluator(final AbstractLearningClassifierSystem myLcs,
			final String filename, final int type, final int size,
			final int representationType) {
		lcs = myLcs;
		ArffTrainTestLoader loader = new ArffTrainTestLoader(myLcs);
		try {
			loader.loadInstances(filename, false);
		} catch (IOException e) {
			e.printStackTrace();
		}

		lcs.registerHook(new FileLogger("acc", new AccuracyRecallEvaluator(
				lcs.instances, false, lcs,
				AccuracyRecallEvaluator.TYPE_ACCURACY)));
		lcs.registerHook(new FileLogger("ex", new ExactMatchEvalutor(
				lcs.instances, false, lcs)));

		if (type == TYPE_IDENTITY) {
			lcs.registerHook(new FileLogger("bam", new IdentityBAMEvaluator(
					size, representationType, lcs)));
		} else {
			lcs.registerHook(new FileLogger("bam", new PositionBAMEvaluator(
					size, representationType, lcs)));
		}
	}

	public void evaluate() {
		lcs.train();
		SortPopulationControl srt = new SortPopulationControl(
				AbstractUpdateStrategy.COMPARISON_MODE_EXPLOITATION);
		srt.controlPopulation(lcs.rulePopulation);
		lcs.rulePopulation.print();
	}
}
