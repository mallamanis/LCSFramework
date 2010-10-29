package gr.auth.ee.lcs.data;

import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ExtendedBitSet;



public class TzimaRepresentation extends ClassifierTransformBridge {

  /** 
   *  The rate by which the createRandomCoveringClassifier considers a specific condition as Don't Care
   */
  public double coveringGeneralizationRate;

  public double minValues;

  @Override
  public boolean isMatch(double[] visionVector, ExtendedBitSet chromosome) {
	// TODO Auto-generated method stub
	return false;
  }

  @Override
  public String toNaturalLanguageString(Classifier aClassifier) {
	// TODO Auto-generated method stub
	return null;
  }

  @Override
  public Classifier createRandomCoveringClassifier(double[] visionVector, int advocatingAction) {
	// TODO Auto-generated method stub
	return null;
  }

  @Override
  public boolean isMoreGeneral(Classifier baseClassifier,
		Classifier testClassifier) {
	// TODO Auto-generated method stub
	return false;
  }

  @Override
  public void fixChromosome(ExtendedBitSet aChromosome) {
	// TODO Auto-generated method stub
	
  }

  @Override
  public int getChromosomeSize() {
	// TODO Auto-generated method stub
	return 0;
  }

  @Override
  public String toBitSetString(Classifier classifier) {
	// TODO Auto-generated method stub
	return null;
  }

  @Override
  public void setRepresentationSpecificClassifierData(Classifier aClassifier) {
	// TODO Auto-generated method stub
	
  }

@Override
public void buildRepresentationModel() {
	// TODO Auto-generated method stub
	
}

}