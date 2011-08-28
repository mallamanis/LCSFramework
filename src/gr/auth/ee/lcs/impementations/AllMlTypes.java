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

import gr.auth.ee.lcs.AbstractLearningClassifierSystem;
import gr.auth.ee.lcs.ArffTrainTestLoader;
import gr.auth.ee.lcs.FoldEvaluator;
import gr.auth.ee.lcs.impementations.meta.BRSGUCSCombination;
import gr.auth.ee.lcs.impementations.meta.EnsembleBRSeqUCSComb;
import gr.auth.ee.lcs.impementations.meta.EnsembleGMlASLCS;
import gr.auth.ee.lcs.impementations.meta.EnsembleRTASLCS;
import gr.auth.ee.lcs.utilities.SettingsLoader;

import java.io.IOException;

/**
 * Trains any of the MlTypes of LCS
 * 
 * @author Miltiadis Allamanis
 * 
 */
public class AllMlTypes {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		final String lcsType = SettingsLoader.getStringSetting("lcsType", "");
		AbstractLearningClassifierSystem lcs = getLCS(lcsType);

		final String file = SettingsLoader.getStringSetting("filename", "");
		final String testFile = SettingsLoader.getStringSetting("testFile", "");

		if (testFile.equals("")) {
			FoldEvaluator loader = new FoldEvaluator(10, lcs, file);
			loader.evaluate();
		} else {
			ArffTrainTestLoader loader = new ArffTrainTestLoader(lcs);
			loader.loadInstancesWithTest(file, testFile);
			loader.evaluate();

		}

	}

	private static AbstractLearningClassifierSystem getLCS(String name)
			throws IOException {
		if (name.equals("DirectASLCS")) {
			return new DirectASLCS();
		} else if (name.equals("DirectUCS")) {
			return new DirectUCS();
		} else if (name.equals("DirectGASLCS")) {
			return new DirectGASLCS();
		} else if (name.equals("DirectGUCS")) {
			return new DirectGUCS();
		} else if (name.equals("RTUCS")) {
			return new RTUCS();
		} else if (name.equals("RTASLCS")) {
			return new RTASLCS();
		} else if (name.equals("MlUCS")) {
			return new MlUCS();
		} else if (name.equals("GMlSSLCS")) {
			return new GMlSSLCS();
		} else if (name.equals("GMlUCS")) {
			return new GMlUCS();
		} else if (name.equals("MlASLCS")) {
			return new MlASLCS();
		} else if (name.equals("GMlASLCS")) {
			return new GMlASLCS();
		} else if (name.equals("GMlASLCS2")) {
			return new GMlASLCS2();
		} else if (name.equals("SequentialUCS")) {
			return new SequentialUCS();
		} else if (name.equals("SequentialASLCS")) {
			return new SequentialASLCS();
		} else if (name.equals("SequentialGUCS")) {
			return new SequentialGUCS();
		} else if (name.equals("SequentialGASLCS")) {
			return new SequentialGASLCS();
		} else if (name.equals("BRUCS")) {
			return new TransformationUCS();
		} else if (name.equals("BRASLCS")) {
			return new TransformASLCS();
		} else if (name.equals("EGMlASLCS")) {
			return new EnsembleGMlASLCS(null);
		} else if (name.equals("BRSGUCS")) {
			return new BRSGUCSCombination();
		} else if (name.equals("EBRSGUCS")) {
			return new EnsembleBRSeqUCSComb(null);
		} else if (name.equals("ERTASLCS")) {
			return new EnsembleRTASLCS(null);
		}
		return null;
	}
}
