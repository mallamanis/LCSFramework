package gr.auth.ee.lcs.classifiers;

import gr.auth.ee.lcs.data.ClassifierTransformBridge;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Vector;


/** 
 *  Implement set of Classifiers, counting numerosity fo classifiers
 *  This object is serializabe
 *  @author Miltos Allamanis
 */
public class ClassifierSet implements Serializable {

  /**
	 * Serialization id for versioning
	 */
	private static final long serialVersionUID = 2664983888922912954L;

public int totalNumerosity=0;

   /**
   * Macroclassifier
   */
  public Vector<Macroclassifier>  myMacroclassifier;
  /** 
   *  An interface for a strategy on deleting classifiers from the set. 
   *  This attribute is transient and therefore not serializable
   */
  transient public ISizeControlStrategy myISizeControlStrategy;

 
  /** 
   *  Adds a classifier with the a given numerosity to the set.
   *  It checks whether the classifier already exists and increases its numerosity.
   *  It also checks for subsumption nad updates the set's numerosity
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
   *  returns the set's total numerosity (the total number of microclassifiers)
   */
  public int getTotalNumerosity() {
	  return this.totalNumerosity;
  }

  /** 
   *  returns a classifier's numerosity (the number of microclassifiers)
   */
  public int getClassifierNumerosity(Classifier aClassifier) {
	  for (int i=0;i<myMacroclassifier.size();i++){
		  if (myMacroclassifier.elementAt(i).equals(aClassifier))
			  return this.myMacroclassifier.elementAt(i).numerosity;
	  }
	 return 0;
  }
  
  /** 
   *  removes a micro-classifier from the set by either completely deleting it (if the classsifier's numerosity is 0) or by decreasing the numerosity
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

  /**
   * 
   * @return the number of macroclassifiers in the set
   */
  public int getNumberOfMacroclassifiers() {
	  return this.myMacroclassifier.size();
  }

  /** 
   *  @return the classifier with the specified index in the macroclassifiers vector
   */
  public Classifier getClassifier(int index) {
	  return this.myMacroclassifier.elementAt(index).myClassifier;
  }

  /** 
   *  @return true if the set is empty
   */
  public boolean isEmpty() {
	  return this.myMacroclassifier.isEmpty();
  }

  /**
   * The default ClassifierSet constructor
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
  
  /**
   * A static function to save the classifier set
   * @param toSave the set to be saved
   * @param filename the path to save the set
   */
  public static void saveClassifierSet(ClassifierSet toSave, String filename){
	  FileOutputStream fos = null;
	  ObjectOutputStream out = null;
	  
	  try{
		  fos = new FileOutputStream(filename);
		  out = new ObjectOutputStream(fos);
		  out.writeObject(toSave);
		  out.close();
		  
	  }catch(IOException ex){
		  ex.printStackTrace();
	  }
	  
  }
  
  /**
   * Open a saved (and serialized) ClassifierSet
   * @param path the path of the ClassifierSet to be opened
   * @param SizeControlStrategy the ClassifierSet's
   * @return the opened classifier set 
   */
  public static ClassifierSet openClassifierSet(String path, ISizeControlStrategy SizeControlStrategy){
	  FileInputStream fis = null;
	  ObjectInputStream in = null;
	  ClassifierSet opened = null;
	  
	  try{
		  fis = new FileInputStream(path);
		  in = new ObjectInputStream(fis);
		  
		  opened=(ClassifierSet)in.readObject();
		  opened.myISizeControlStrategy=SizeControlStrategy;
		  
		  in.close();		  
	  }catch(IOException ex){
		  ex.printStackTrace();
	  }catch(ClassNotFoundException ex){
		  ex.printStackTrace();
	  }
	  
	  return opened;
  }

}