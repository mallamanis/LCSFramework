package gr.auth.ee.lcs.classifiers;


/** 
 *  A strategy for deleting classifiers from the set when they exceed a specified value. The strategy deletes classifiers by creating an inverse fitness proportional tournaments.
 *  TODO: Buggy(?)
 */
public class OverpopulationInverseFitnessProportionateDeletion implements ISizeControlStrategy {

  /** 
   *  the maximum number of classifiers in the set
   */
  private int maxPermittableSetSize;
  
  public OverpopulationInverseFitnessProportionateDeletion(int maxPermittableSetSize){
	  this.maxPermittableSetSize=maxPermittableSetSize;
  }

  public void controlSize(ClassifierSet aSet) {
	  if (aSet.totalNumerosity<maxPermittableSetSize)
		  return;
	  //Exceeded Size, Perform Size Control Strategy
	//Find total sum
	  double fitnessSum=0;
	  for (int i=0;i<aSet.getNumberOfMacroclassifiers();i++){
		  fitnessSum+=aSet.getClassifierNumerosity(i)*1/aSet.getClassifier(i).fitness;
	  }
	  
	  //Repeat roulette until we have the correct size
	  do{
		  //Roulette
		  double rand=Math.random()*fitnessSum;
		  double tempSum=0;
		  int selectedIndex=-1;
		  do{
			  selectedIndex++;
			  tempSum+=aSet.getClassifierNumerosity(selectedIndex)/aSet.getClassifier(selectedIndex).fitness;			  
		  }while(tempSum<rand);	  
		  //Add selectedIndex
		  aSet.deleteClassifier(selectedIndex);
	  }while(aSet.totalNumerosity>maxPermittableSetSize);//next roulette 
	  
	  
  }

}