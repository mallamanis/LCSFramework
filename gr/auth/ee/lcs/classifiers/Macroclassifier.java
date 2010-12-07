package gr.auth.ee.lcs.classifiers;

import java.io.Serializable;


/** 
 *  Represents a macroclassifier. A macroclassifier is a classifier with a numerosity
 */
public class Macroclassifier implements Serializable {

  /**
	 * Serialization id for versioning
	 */
	private static final long serialVersionUID = 1705517271818439866L;

public int numerosity;

  public Classifier myClassifier;
  
  public boolean equals(Classifier aClassifier) {
	  return this.myClassifier.equals(aClassifier);
  }
  
  public boolean equals(Macroclassifier macroClassifier) {
	  return this.myClassifier.equals(macroClassifier.myClassifier);
  }
  
  public Macroclassifier(Classifier newClassifier,int classifierNumerosity){
	  myClassifier=newClassifier;
	  numerosity=classifierNumerosity;
  }

}