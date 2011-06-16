/**
 * 
 */
package gr.auth.ee.lcs.impementations;

import java.io.IOException;

import gr.auth.ee.lcs.BAMEvaluator;
import gr.auth.ee.lcs.evaluators.bamevaluators.IdentityBAMEvaluator;
import gr.auth.ee.lcs.utilities.SettingsLoader;

/**
 * @author Miltiadis Allamanis
 *
 */
public class BatchASLCSExperiments {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		SettingsLoader.loadSettings();

		final String file = SettingsLoader.getStringSetting("filename", "");
		
		final int type = BAMEvaluator.TYPE_IDENTITY;
		
		for (int i = 0; i < 10; i++) {
			final SequentialGUCS sgucs = new SequentialGUCS();
			BAMEvaluator eval = new BAMEvaluator(sgucs, file,
					type, 7,
					IdentityBAMEvaluator.GENERIC_REPRESENTATION,"sgucs");
			eval.evaluate();
			
			final SequentialUCS sucs = new SequentialUCS();
			eval = new BAMEvaluator(sucs, file,
					type, 7,
					IdentityBAMEvaluator.STRICT_REPRESENTATION,"sucs");
			eval.evaluate();
			
			final DirectASLCS daslcs = new DirectASLCS();
			eval = new BAMEvaluator(daslcs, file,
					type, 7,
					IdentityBAMEvaluator.STRICT_REPRESENTATION,"daslcs");
			eval.evaluate();
			
			
			final DirectGASLCS dgaslcs = new DirectGASLCS();
			eval = new BAMEvaluator(dgaslcs, file,
					type, 7,
					IdentityBAMEvaluator.GENERIC_REPRESENTATION,"dgaslcs");
			eval.evaluate();
			
			final SequentialASLCS saslcs = new SequentialASLCS();
			eval = new BAMEvaluator(saslcs, file,
					type, 7,
					IdentityBAMEvaluator.STRICT_REPRESENTATION,"saslcs");
			eval.evaluate();
			
			
			final SequentialGASLCS sgaslcs = new SequentialGASLCS();
			eval = new BAMEvaluator(sgaslcs, file,
					type, 7,
					IdentityBAMEvaluator.GENERIC_REPRESENTATION,"sgaslcs");
			eval.evaluate();
			
			final MlASLCS mlaslcs = new MlASLCS();
			eval = new BAMEvaluator(mlaslcs, file,
					type, 7,
					IdentityBAMEvaluator.STRICT_REPRESENTATION,"mlaslcs");
			eval.evaluate();
			
			
			final GMlASLCS gmlaslcs = new GMlASLCS();
			eval = new BAMEvaluator(gmlaslcs, file,
					type, 7,
					IdentityBAMEvaluator.GENERIC_REPRESENTATION,"gmlaslcs");
			eval.evaluate(); 
		}

	}

}
