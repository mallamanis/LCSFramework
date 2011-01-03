package gr.auth.ee.lcs.classifiers;

import java.io.Serializable;

import gr.auth.ee.lcs.data.ClassifierTransformBridge;
import gr.auth.ee.lcs.data.UpdateAlgorithmFactoryAndStrategy;


/** 
 *  Represents a single classifier/ rule
 *  @author Miltos Allamanis
 */
public class Classifier implements Serializable{

  /**
	 * Serialization code for versioning
	 */
	private static final long serialVersionUID = 8628765535406768159L;

/** 
   *  the fitness of the classifier
   */
  public double fitness=.5; //TODO: Through setters-getters

  /** 
   *  An object (of undefined type) that is used by the update algorithms
   */
  public Serializable updateData;
  
  /**
   * A boolean array indicating which dataset instances the rule matches
   */
  transient private boolean[] matchInstances;
  
  /**
   * A float showing the matches percentage
   */
  transient public float coverage=0;
  
  /**
   * 
   */
  public int experience=1;
  
  /** 
   *  the timestamp is the last iteration the classifier has participated in a GA Evolution
   */
  public int timestamp=0;

  /** 
   * A boolean representing the classifier's ability to subsume.
   */
  public boolean canSubsume=false;

  /** 
   *  Is an integer representing the action this classifier advocates for
   */
  public int actionAdvocated;

  /** 
   *  An object for saving the transformation specific data
   */
  public Serializable transformData;

  /** 
   *  The classifier's chromosome
   */
  public ExtendedBitSet chromosome;
  
  /** 
   *  a getter for the fitness of the classifier
   */
  public double getFitness() {
	  return fitness;
  }

  /**
   * The default constructor.
   * Creates a chromosome of the given size
   */
  public Classifier(){
	  chromosome=new ExtendedBitSet(ClassifierTransformBridge.instance.getChromosomeSize());
	  updateData=UpdateAlgorithmFactoryAndStrategy.createDefaultDataObject();
	  ClassifierTransformBridge.instance.setRepresentationSpecificClassifierData(this);
  }
  
  /**    
   * @param anotherClassifier 
   * @return true if the classifiers have equal chromosomes
   */
  public boolean equals(Classifier anotherClassifier) {
	  return ClassifierTransformBridge.instance.areEqual(this, anotherClassifier);
	  }

  /** 
   *  Calls the bridge to detect if the classifier is matching the vision vector
   */
  public boolean isMatch(double[] visionVector) {
	  return ClassifierTransformBridge.instance.isMatch(visionVector, chromosome);
  }
  
  /** 
   *  Checks if Classifier is matches an instance vector
   */
  public boolean isMatch(int instanceIndex) {
	  if (this.matchInstances==null) buildMatches();
	  return this.matchInstances[instanceIndex];
  }
  

  /** 
   *  Calls the bridge to convert it self to natural language string
   */
  public String toString() {
	  return ClassifierTransformBridge.instance.toNaturalLanguageString(this);
  }

  /** 
   *  calls the bridge to fix itself
   */
  public void fixChromosome() {
	  ClassifierTransformBridge.instance.fixChromosome(chromosome);
  }

  /** 
   *  calls the bridge to divide bits into attributes
   */
  public String toBitString() {
	  return ClassifierTransformBridge.instance.toBitSetString(this);
  }

  /**
   * Getter of the ExtendedBitSet
   * @return the classifier's chromosome as an extendedBitSet
   */
  public ExtendedBitSet getChromosome() {
	  return chromosome;
  }
  
  public double getComparisonValue(int mode){
	  return UpdateAlgorithmFactoryAndStrategy.currentStrategy.getComparisonValue(this, mode);
  }
  
  public void buildMatches(){
	  this.matchInstances=new boolean[ClassifierTransformBridge.instances.length];
	  for (int i=0;i<ClassifierTransformBridge.instances.length;i++)
		  matchInstances[i]= ClassifierTransformBridge.instance.isMatch(ClassifierTransformBridge.instances[i], this.chromosome);
	  
	  int matchingInstances=0;
	  for (int i=0;i<ClassifierTransformBridge.instances.length;i++)
		  if (matchInstances[i]) matchingInstances++;
	  
	  this.coverage= ((float)matchingInstances)/((float)ClassifierTransformBridge.instances.length);
  }

}