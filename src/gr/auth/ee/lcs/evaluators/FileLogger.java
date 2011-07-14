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
		final Calendar cal = Calendar.getInstance();
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
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
