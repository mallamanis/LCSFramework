/**
 * 
 */
package gr.auth.ee.lcs.tests.mocks;

import java.io.Serializable;

import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ClassifierSet;
import gr.auth.ee.lcs.data.AbstractUpdateStrategy;

/**
 * Simple Mock Update Strategy.
 * 
 * @author Miltos Allamanis
 * 
 */
public class MockUpdateStrategy extends AbstractUpdateStrategy {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.data.AbstractUpdateStrategy#cover(gr.auth.ee.lcs.classifiers
	 * .ClassifierSet, int)
	 */
	@Override
	public void cover(ClassifierSet population, int instanceIndex) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.data.AbstractUpdateStrategy#createStateClassifierObject()
	 */
	@Override
	public Serializable createStateClassifierObject() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.data.AbstractUpdateStrategy#getComparisonValue(gr.auth
	 * .ee.lcs.classifiers.Classifier, int)
	 */
	@Override
	public double getComparisonValue(Classifier aClassifier, int mode) {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.data.AbstractUpdateStrategy#getData(gr.auth.ee.lcs.classifiers
	 * .Classifier)
	 */
	@Override
	public String getData(Classifier aClassifier) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.data.AbstractUpdateStrategy#performUpdate(gr.auth.ee.lcs
	 * .classifiers.ClassifierSet, gr.auth.ee.lcs.classifiers.ClassifierSet)
	 */
	@Override
	public void performUpdate(ClassifierSet matchSet, ClassifierSet correctSet) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gr.auth.ee.lcs.data.AbstractUpdateStrategy#setComparisonValue(gr.auth
	 * .ee.lcs.classifiers.Classifier, int, double)
	 */
	@Override
	public void setComparisonValue(Classifier aClassifier, int mode,
			double comparisonValue) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gr.auth.ee.lcs.data.AbstractUpdateStrategy#updateSet(gr.auth.ee.lcs.
	 * classifiers.ClassifierSet, gr.auth.ee.lcs.classifiers.ClassifierSet, int,
	 * boolean)
	 */
	@Override
	public void updateSet(ClassifierSet population, ClassifierSet matchSet,
			int instanceIndex, boolean evolve) {
		// TODO Auto-generated method stub

	}

}
