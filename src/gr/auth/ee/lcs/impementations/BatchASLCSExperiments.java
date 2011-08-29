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
package gr.auth.ee.lcs.impementations;

import gr.auth.ee.lcs.evaluators.bamevaluators.BAMEvaluator;
import gr.auth.ee.lcs.evaluators.bamevaluators.IdentityBAMEvaluator;
import gr.auth.ee.lcs.utilities.SettingsLoader;

import java.io.IOException;

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
			BAMEvaluator eval = new BAMEvaluator(sgucs, file, type, 7,
					IdentityBAMEvaluator.GENERIC_REPRESENTATION, "sgucs");
			eval.evaluate();

			final SequentialUCS sucs = new SequentialUCS();
			eval = new BAMEvaluator(sucs, file, type, 7,
					IdentityBAMEvaluator.STRICT_REPRESENTATION, "sucs");
			eval.evaluate();

			final DirectASLCS daslcs = new DirectASLCS();
			eval = new BAMEvaluator(daslcs, file, type, 7,
					IdentityBAMEvaluator.STRICT_REPRESENTATION, "daslcs");
			eval.evaluate();

			final DirectGASLCS dgaslcs = new DirectGASLCS();
			eval = new BAMEvaluator(dgaslcs, file, type, 7,
					IdentityBAMEvaluator.GENERIC_REPRESENTATION, "dgaslcs");
			eval.evaluate();

			final SequentialASLCS saslcs = new SequentialASLCS();
			eval = new BAMEvaluator(saslcs, file, type, 7,
					IdentityBAMEvaluator.STRICT_REPRESENTATION, "saslcs");
			eval.evaluate();

			final SequentialGASLCS sgaslcs = new SequentialGASLCS();
			eval = new BAMEvaluator(sgaslcs, file, type, 7,
					IdentityBAMEvaluator.GENERIC_REPRESENTATION, "sgaslcs");
			eval.evaluate();

			final MlASLCS mlaslcs = new MlASLCS();
			eval = new BAMEvaluator(mlaslcs, file, type, 7,
					IdentityBAMEvaluator.STRICT_REPRESENTATION, "mlaslcs");
			eval.evaluate();

			final GMlASLCS gmlaslcs = new GMlASLCS();
			eval = new BAMEvaluator(gmlaslcs, file, type, 7,
					IdentityBAMEvaluator.GENERIC_REPRESENTATION, "gmlaslcs");
			eval.evaluate();
		}

	}

}
