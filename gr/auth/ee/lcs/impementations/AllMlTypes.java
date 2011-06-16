/**
 * 
 */
package gr.auth.ee.lcs.impementations;

import java.io.IOException;

import gr.auth.ee.lcs.AbstractLearningClassifierSystem;
import gr.auth.ee.lcs.ArffTrainTestLoader;
import gr.auth.ee.lcs.FoldEvaluator;
import gr.auth.ee.lcs.utilities.SettingsLoader;

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
		if (testFile == "") {
			FoldEvaluator loader = new FoldEvaluator(10, lcs, file);
			loader.evaluate();
		} else {
			ArffTrainTestLoader loader = new ArffTrainTestLoader(lcs);
			loader.loadInstancesWithTest(file, testFile);
			loader.evaluate();
		}		

	}
	
	private static AbstractLearningClassifierSystem getLCS(String name) throws IOException {
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
		} else if (name.equals("GMlUCS")) {
			return new GMlUCS();
		} else if (name.equals("MlASLCS")) {
			return new MlASLCS();
		} else if (name.equals("GMlASLCS")) {
			return new GMlASLCS();
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
		}
		return null;
	}

}
