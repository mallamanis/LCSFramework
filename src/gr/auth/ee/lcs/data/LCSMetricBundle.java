/**
 * 
 */
package gr.auth.ee.lcs.data;

import gr.auth.ee.lcs.AbstractLearningClassifierSystem;

import java.util.Vector;

/**
 * A generic bundle of LCS Metrics
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
	
	public LCSMetricBundle(AbstractLearningClassifierSystem lcs, final Vector<ILCSMetric> initialMetrics) {
		metrics = initialMetrics;
		myLcs = lcs;
	}
	
	/**
	 * Add a metric to the bundle
	 * @param metric the metric to be added
	 */
	public void addMetric(ILCSMetric metric) {
		metrics.add(metric);
	}
	
	/**
	 * Return a vector of the bundle's metrics.
	 * @return a double array containing the metrics
	 */
	public double[] getMetrics() {
		double[] metricValues = new double[metrics.size()];
		
		final int numOfMetrics = metrics.size();
		for (int i = 0; i < numOfMetrics; i++){
			metricValues[i] = metrics.elementAt(i).getMetric(myLcs);
		}
		
		return metricValues;
	}
	
	/**
	 * Returns a vector with the names of the metrics.
	 * @return a String array of the bundle's metric names 
	 */
	public String[] getMetricNames() {
		String[] metricNames = new String[metrics.size()];
		
		final int numOfMetrics = metrics.size();
		for (int i = 0; i < numOfMetrics; i++){
			metricNames[i] = metrics.elementAt(i).getMetricName();
		}
		
		return metricNames;
	}
	
	/**
	 * Convert the bundle to a string.
	 */
	public String toString() {
		final StringBuffer response = new StringBuffer();
		final int numOfMetrics = metrics.size();
		
		for (int i = 0; i < numOfMetrics; i++) {
			response.append(metrics.elementAt(i).getMetricName()+": "+ metrics.elementAt(i).getMetric(myLcs));
			response.append(System.getProperty("line.separator"));
		}
		
		
		return response.toString();
	}

}
