/**
 * 
 */
package gr.auth.ee.lcs.data;

import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ExtendedBitSet;

import java.io.IOException;

/**
 * @author miltiadis
 *
 */
public class UnilabelRepresentation extends ComplexRepresentation {

	/**
	 * Call superclass's constructor
	 */
	public UnilabelRepresentation(Attribute[] attributes,
			String[] ruleConsequents) {
		super(attributes, ruleConsequents);		
	}

	/**
	 * Call superclass's constructor
	 */
	public UnilabelRepresentation(String inputArff, int precision)
			throws IOException {
		super(inputArff, precision);
	}

	/* (non-Javadoc)
	 * @see gr.auth.ee.lcs.data.ComplexRepresentation#createClassRepresentation()
	 */
	@Override
	protected void createClassRepresentation() {
		
		attributeList[attributeList.length-1] = new UniLabel(chromosomeSize,"class",0,ruleConsequents);

	}
	
	@Override
	public int getClassification(Classifier aClassifier) {
		return ((UniLabel)attributeList[attributeList.length-1]).getValue(aClassifier.chromosome);
	}
	
	@Override
	public void setClassification(Classifier aClassifier, int action) {
		((UniLabel)attributeList[attributeList.length-1]).setValue(aClassifier.chromosome, action);
		
	}
	
	public class UniLabel extends Attribute{

		/**
		 * The classes
		 */
		private String[] classes;
		
		public UniLabel(int startPosition, String attributeName,
				double generalizationRate, String[] classNames) {
			super(startPosition, attributeName, generalizationRate);
			lengthInBits = (int)Math.ceil(Math.log10(classNames.length)/Math.log10(2));
			chromosomeSize+= lengthInBits;
			classes = classNames;
		}

		@Override
		public String toString(ExtendedBitSet convertingClassifier) {
			int index = convertingClassifier.getIntAt(positionInChromosome, lengthInBits);
			return classes[index];
		}

		@Override
		public boolean isMatch(float attributeVision,
				ExtendedBitSet testedChromosome) {
			return testedChromosome.getIntAt(positionInChromosome, lengthInBits)==(int)attributeVision;
		}

		@Override
		public void randomCoveringValue(float attributeValue,
				Classifier generatedClassifier) {
			int coverClass = (int) attributeValue;
			generatedClassifier.chromosome.setIntAt(positionInChromosome, lengthInBits, coverClass);
		}

		@Override
		public void fixAttributeRepresentation(
				ExtendedBitSet generatedClassifier) {
			if (generatedClassifier.getIntAt(positionInChromosome, lengthInBits)>=classes.length){
				int randClass = (int) Math.floor(Math.random()*classes.length);
				generatedClassifier.setIntAt(positionInChromosome, lengthInBits, randClass);
			}
			
		}

		@Override
		public boolean isMoreGeneral(ExtendedBitSet baseChromosome,
				ExtendedBitSet testChromosome) {
			if (baseChromosome.getIntAt(positionInChromosome, lengthInBits)==testChromosome.getIntAt(positionInChromosome, lengthInBits))
				return true;
			else
				return false;
		}

		@Override
		public boolean isEqual(ExtendedBitSet baseChromosome,
				ExtendedBitSet testChromosome) {
			if (baseChromosome.getIntAt(positionInChromosome, lengthInBits)==testChromosome.getIntAt(positionInChromosome, lengthInBits))
				return true;
			else
				return false;
		}
		
		public int getValue(ExtendedBitSet chromosome){
			return chromosome.getIntAt(positionInChromosome, lengthInBits);
		}
		
		public void setValue(ExtendedBitSet chromosome, int value){
			chromosome.setIntAt(positionInChromosome, lengthInBits, value);
		}
		
	}

	

	

}
