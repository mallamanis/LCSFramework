/**
 * 
 */
package gr.auth.ee.lcs.evaluators;

import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.data.IEvaluator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * An evaluator logging output to a file.
 * 
 * @author Miltos Allamanis
 * 
 */
public class FileLogger implements IEvaluator {

	/**
	 * The filename where output is logged.
	 */
	private final String file;

	/**
	 * The evaluator from which we log the output.
	 */
	private final IEvaluator actualEvaluator;

	/**
	 * FileLogger constructor.
	 * 
	 * @param filename
	 *            the filename of the file where log will be output.
	 * @param evaluator
	 *            the evaluator which we are going to output.
	 */
	public FileLogger(final String filename, final IEvaluator evaluator) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
		file = filename + sdf.format(cal.getTime()) + ".txt";
		actualEvaluator = evaluator;
		try {
			final FileWriter fstream = new FileWriter(file, false);
			final BufferedWriter buffer = new BufferedWriter(fstream);
			buffer.write("");
			buffer.flush();
			buffer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public final double evaluateSet(final ClassifierSet classifiers) {
		final double evalResult = actualEvaluator.evaluateSet(classifiers);
		try {
			final FileWriter fstream = new FileWriter(file, true);
			final BufferedWriter buffer = new BufferedWriter(fstream);
			buffer.write(String.valueOf(evalResult) + "\n");
			buffer.flush();
			buffer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

}
