package gr.auth.ee.lcs.data;

import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ExtendedBitSet;


public abstract class ClassifierTransformBridge {

  /** 
   *  the singleton instance of the bridge
   */
  public static ClassifierTransformBridge instance;

  /** 
   *  checks if the visionVector matches the condition of the given chromosome
   */
  public abstract boolean isMatch(double[] visionVector, ExtendedBitSet chromosome);

  /** 
   *  converts the given classifier to a natural language rule
   */
  public abstract String toNaturalLanguageString(Classifier aClassifier);

  /** 
   *  sets the static instance of the bridge
   */
  public static void setInstance(ClassifierTransformBridge aBridge) {
	  ClassifierTransformBridge.instance=aBridge;
  }

  /** 
   *  Creates a random classifier to cover the visionVector
   */
  public abstract Classifier createRandomCoveringClassifier(double[] visionVector);

  /** 
   *  Tests the given chromosomes if the baseClassifier is a more general version of the testClassifier
   */
  public abstract boolean isMoreGeneral(Classifier baseClassifier, Classifier testClassifier);

  /** 
   *  Fixes a chromosome so as to be in the correct value range (e.g. after mutation or crossover)
   */
  public abstract void fixChromosome(ExtendedBitSet aChromosome);

  /** 
   *  Returns the size of the chromosome (used for the chromosome construction)
   */
  public abstract int getChromosomeSize();

  public abstract String toBitSetString(Classifier classifier);

  public abstract void setRepresentationSpecificClassifierData(Classifier aClassifier);

  public abstract void buildRepresentationModel();

}