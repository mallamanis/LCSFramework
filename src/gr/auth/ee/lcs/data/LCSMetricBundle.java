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
package gr.auth.ee.lcs.data;

import gr.auth.ee.lcs.AbstractLearningClassifierSystem;

import java.util.Vector;

/**
 * A generic bundle of LCS Metrics
 * 
 * @author Miltiadis Allamanis
 * 
 */
public class LCSMetricBundle {

	/**
	 * A vector containing the metrics.
	 */
	private final Vector<ILCSMetric> metrics;

	/**
	 * The lcs to be used for the metrics.
	 */
	private final AbstractLearningClassifierSystem myLcs;

	public LCSMetricBundle(AbstractLearningClassifierSystem lcs,
			final Vector<ILCSMetric> initialMetrics) {
		metrics = initialMetrics;
		myLcs = lcs;
	}

	/**
	 * Add a metric to the bundle
	 * 
	 * @param metric
	 *            the metric to be added
	 */
	public void addMetric(ILCSMetric metric) {
		metrics.add(metric);
	}

	/**
	 * Returns a vector with the names of the metrics.
	 * 
	 * @return a String array of the bundle's metric names
	 */
	public String[] getMetricNames() {
		String[] metricNames = new String[metrics.size()];

		final int numOfMetrics = metrics.size();
		for (int i = 0; i < numOfMetrics; i++) {
			metricNames[i] = metrics.elementAt(i).getMetricName();
		}

		return metricNames;
	}

	/**
	 * Return a vector of the bundle's metrics.
	 * 
	 * @return a double array containing the metrics
	 */
	public double[] getMetrics() {
		double[] metricValues = new double[metrics.size()];

		final int numOfMetrics = metrics.size();
		for (int i = 0; i < numOfMetrics; i++) {
			metricValues[i] = metrics.elementAt(i).getMetric(myLcs);
		}

		return metricValues;
	}

	/**
	 * Convert the bundle to a string.
	 */
	public String toString() {
		final StringBuffer response = new StringBuffer();
		final int numOfMetrics = metrics.size();

		for (int i = 0; i < numOfMetrics; i++) {
			response.append(metrics.elementAt(i).getMetricName() + ": "
					+ metrics.elementAt(i).getMetric(myLcs));
			response.append(System.getProperty("line.separator"));
		}

		return response.toString();
	}

}
