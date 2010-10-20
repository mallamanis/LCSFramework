package gr.auth.ee.lcs.classifiers;

import gr.auth.ee.lcs.data.ClassifierTransformBridge;

import java.util.Vector;


/** 
 *  implement abstractSet?
 */
public class ClassifierSet {

  public int totalNumerosity=0;

    /**
   * 
   * @element-type Macroclassifier
   */
  public Vector<Macroclassifier>  myMacroclassifier;
  /** 
   *  An interface for a strategy on deleting classifiers from the set
   */
  public ISizeControlStrategy myISizeControlStrategy;

 
  /** 
   *  Adds a classifier with the a given numerosity to the set. It checks whether the classifier already exists and increases its numerosity. It also checks for subsumption. It also updates the set's numerosity
   */
  public void addClassifier(Classifier aClassifier, int numerosity) {
	  //Add numerosity to the Set
	  this.totalNumerosity+=numerosity;
	  
	  //Subsume if possible
	  for (int i=0;i<myMacroclassifier.size();i++){
		  Classifier theClassifier=myMacroclassifier.elementAt(i).myClassifier;
		  if (theClassifier.canSubsume){
			  if (ClassifierTransformBridge.instance.isMoreGeneral(theClassifier, aClassifier)){
				  //Subsume and control size...
				  myMacroclassifier.elementAt(i).numerosity+=numerosity;
				  myISizeControlStrategy.controlSize(this);
				  return;
			  }
		  }else if(theClassifier.equals(aClassifier)){ //Or it can't subsume but it is equal
			  myMacroclassifier.elementAt(i).numerosity+=numerosity;
			  myISizeControlStrategy.controlSize(this);
			  return;
		  }
	  }
	  
	  //No matching or subsumable more general classifier could be found. Add and control size...
	  Macroclassifier newMacroclassifer=new Macroclassifier(aClassifier,numerosity);
	  this.myMacroclassifier.add(newMacroclassifer);		  
	  myISizeControlStrategy.controlSize(this);
  }

  /** 
   *  returns the set's total numerosity
   */
  public int getTotalNumerosity() {
	  return this.totalNumerosity;
  }

  /** 
   *  returns a classifier's numerosity
   */
  public int getClassifierNumerosity(Classifier aClassifier) {
	  for (int i=0;i<myMacroclassifier.size();i++){
		  if (myMacroclassifier.elementAt(i).equals(aClassifier))
			  return this.myMacroclassifier.elementAt(i).numerosity;
	  }
	 return 0;
  }

  /** 
   *  removes a classifier from the set by either completely deleting it (if the classsifier's numerosity is 0) or by decreasing the numerosity
   */
  public void deleteClassifier(Classifier aClassifier) {
	  		 
	  int index;
	  for (index=0;index<myMacroclassifier.size();index++){
		  if (myMacroclassifier.elementAt(index).equals(aClassifier)) break;
	  }
		  
	  if (index==myMacroclassifier.size()) return;
	  deleteClassifier(index);
		  
  }
  
  /**
   * Deletes a classifier with the given index
   * If the macroclassifier at the given index contains more than one classifier
   * the numerosity is increased
   * @param index the index of the classifier's macroclassifier to delete
   */
  public void deleteClassifier(int index){
	  this.totalNumerosity--;
	  if (this.myMacroclassifier.elementAt(index).numerosity>1)
		  this.myMacroclassifier.elementAt(index).numerosity--;
	  else
		  this.myMacroclassifier.remove(index);
  }

  public int getNumberOfMacroclassifiers() {
	  return this.myMacroclassifier.size();
  }

  /** 
   *  returns the classifier with the specified index in the macroclasifier vector
   */
  public Classifier getClassifier(int index) {
	  return this.myMacroclassifier.elementAt(index).myClassifier;
  }

  /** 
   *  returns true if the set is empty
   */
  public boolean isEmpty() {
	  return this.myMacroclassifier.isEmpty();
  }

  public void addFromClassifierSet(ClassifierSet set, int index) {
	  //TODO: Implement if needed...
	  
  }

  /**
   * The default constructor
   */
  public ClassifierSet(ISizeControlStrategy SizeControlStrategy) {
	  this.myISizeControlStrategy=SizeControlStrategy;
	  this.myMacroclassifier=new Vector<Macroclassifier>();
	  
  }
  
  /**
   * Overloaded function for getting a numerosity
   * @param index the index of the macroclassifier
   * @return the index'th macroclassifier numerosity
   */
  public int getClassifierNumerosity(int index){
	  return this.myMacroclassifier.elementAt(index).numerosity;
  }

}