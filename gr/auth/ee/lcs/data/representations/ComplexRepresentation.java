package gr.auth.ee.lcs.data.representations;

import gr.auth.ee.lcs.classifiers.Classifier;
import gr.auth.ee.lcs.classifiers.ExtendedBitSet;
import gr.auth.ee.lcs.data.ClassifierTransformBridge;

import java.io.FileReader;
import java.io.IOException;
import java.util.Enumeration;

import weka.core.Instances;

/**
 * A Complex representation for the chromosome.
 * 
 * @author Miltos Allamanis
 * 
 */
public abstract class ComplexRepresentation extends ClassifierTransformBridge {

	/**
	 * The rate by which the createRandomCoveringClassifier considers a specific
	 * condition as Don't Care.
	 * 
	 * @deprecated
	 */
	public double coveringGeneralizationRate;

	/**
	 * The list of all attributes
	 */
	protected Attribute[] attributeList;

	/**
	 * The size of the chromosomes of the representation
	 */
	protected int chromosomeSize = 0;

	/**
	 * The string (names) of the rule consequents.
	 */
	protected String[] ruleConsequents;

	/**
	 * Constructor.
	 * 
	 * @param attributes
	 *            the attribute objects of the representation
	 * @param ruleConsequentsNames
	 *            the rule consequents
	 */
	public ComplexRepresentation(Attribute[] attributes,
			String[] ruleConsequentsNames) {
		this.attributeList = attributes;
		this.ruleConsequents = ruleConsequentsNames;
	}

	/**
	 * Implements the matching.
	 * 
	 * @param visionVector
	 *            the vision vector to match
	 * @param chromosome
	 *            the chromosome to match
	 */
	@Override
	public boolean isMatch(double[] visionVector, ExtendedBitSet chromosome) {
		for (int i = 0; i < attributeList.length - 1; i++)
			if (!attributeList[i].isMatch((float) visionVector[i], chromosome))
				return false;
		return true;
	}

	@Override
	public String toNaturalLanguageString(Classifier aClassifier) {
		String nlRule = "";
		for (int i = 0; i < attributeList.length - 1; i++)
			nlRule += attributeList[i].toString(aClassifier) + " AND ";

		// Add consequence
		nlRule += "=>"
				+ attributeList[attributeList.length - 1].toString(aClassifier);
		return nlRule;
	}

	@Override
	public Classifier createRandomCoveringClassifier(double[] visionVector,
			int advocatingAction) {
		Classifier generatedClassifier = new Classifier();
		for (int i = 0; i < attributeList.length - 1; i++)
			attributeList[i].randomCoveringValue((float) visionVector[i],
					generatedClassifier);

		attributeList[attributeList.length - 1].randomCoveringValue(
				advocatingAction, generatedClassifier);
		return generatedClassifier;
	}

	@Override
	public boolean isMoreGeneral(Classifier baseClassifier,
			Classifier testClassifier) {
		// If classifiers advocate for different actions, return false
		if (baseClassifier.getActionAdvocated() != testClassifier
				.getActionAdvocated())
			return false;

		ExtendedBitSet baseChromosome = baseClassifier.getChromosome();
		ExtendedBitSet testChromosome = testClassifier.getChromosome();

		for (int i = 0; i < attributeList.length - 1; i++)
			if (!attributeList[i].isMoreGeneral(baseChromosome, testChromosome))
				return false;

		return true;
	}

	@Override
	public void fixChromosome(ExtendedBitSet aChromosome) {
		for (int i = 0; i < attributeList.length; i++)
			attributeList[i].fixAttributeRepresentation(aChromosome);

	}

	@Override
	public int getChromosomeSize() {
		return chromosomeSize;
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

	@Override
	public boolean areEqual(Classifier cl1, Classifier cl2) {
		ExtendedBitSet baseChromosome = cl1.getChromosome();
		ExtendedBitSet testChromosome = cl2.getChromosome();

		// Check for equality starting with class
		for (int i = attributeList.length - 1; i >= 0; i--)
			if (!attributeList[i].isEqual(baseChromosome, testChromosome))
				return false;

		return true;
	}

	/**
	 * Create the class representation depending on the problem.
	 */
	protected abstract void createClassRepresentation();

	/**
	 * Arff Loader. TODO: In an inherited class (not representation specific)
	 * 
	 * @param inputArff
	 *            the input .arff
	 * @param precision
	 *            bits used for precision
	 * @throws IOException
	 *             when .arff not found
	 */
	public ComplexRepresentation(String inputArff, int precision)
			throws IOException {
		FileReader reader = new FileReader(inputArff);
		Instances instances = new Instances(reader);
		// TODO: Change 4 ml
		if (instances.classIndex() < 0)
			instances.setClassIndex(instances.numAttributes() - 1);

		attributeList = new Attribute[instances.numAttributes()];

		// Rule Consequents
		Enumeration<?> classNames = instances.classAttribute()
				.enumerateValues();
		ruleConsequents = new String[instances.numClasses()];
		for (int i = 0; i < instances.numClasses(); i++)
			ruleConsequents[i] = (String) classNames.nextElement();

		for (int i = 0; i < instances.numAttributes(); i++) {
			if (i == instances.classIndex())
				continue; // TODO: Change 4 ml

			String attributeName = instances.attribute(i).name();

			if (instances.attribute(i).isNominal()) {

				String[] attributeNames = new String[instances.attribute(i)
						.numValues()];
				Enumeration<?> values = instances.attribute(i)
						.enumerateValues();
				for (int j = 0; j < attributeNames.length; j++) {
					attributeNames[j] = (String) values.nextElement();
				}
				// Create boolean or generic nominal
				if (attributeNames.length > 2)
					attributeList[i] = new ComplexRepresentation.NominalAttribute(
							this.chromosomeSize, attributeName, attributeNames,
							0.33);
				else
					attributeList[i] = new ComplexRepresentation.BooleanAttribute(
							chromosomeSize, attributeName, 0.33);

			} else if (instances.attribute(i).isNumeric()) {
				// Find min-max values
				float minValue, maxValue;
				minValue = (float) instances.instance(0).toDoubleArray()[i];
				maxValue = minValue;
				for (int sample = 0; sample < instances.numInstances(); sample++) {
					float currentVal = (float) instances.instance(sample)
							.toDoubleArray()[i];
					if (currentVal > maxValue)
						maxValue = currentVal;
					if (currentVal < minValue)
						minValue = currentVal;
				}

				attributeList[i] = new ComplexRepresentation.IntervalAttribute(
						this.chromosomeSize + 1, attributeName, minValue,
						maxValue, precision, 0.33);
			}

		}

		// Build class into gene
		createClassRepresentation();

	}

	/**
	 * A inner abstract class that represents a single attribute
	 * 
	 * @author Miltos Allamanis
	 */
	public abstract class Attribute {
		/**
		 * The length in bits of the attribute in the chromosome.
		 */
		protected int lengthInBits;

		/**
		 * The attribute's position in the chromosome.
		 */
		protected int positionInChromosome;

		/**
		 * The human-readable name of the attribute.
		 */
		protected String nameOfAttribute;

		/**
		 * The generalization rate used when covering.
		 */
		protected double generalizationRate;

		/**
		 * Convert Attribute to human-readable String.
		 * 
		 * @param convertingClassifier
		 *            the chromosome of the classifier to convert
		 * @return the string representation of the attribute
		 */
		public abstract String toString(ExtendedBitSet convertingClassifier);

		/**
		 * Check if the attribute vision is a match to the chromosome.
		 * 
		 * @param attributeVision
		 *            the attribute vision value
		 * @param testedChromosome
		 *            the chromosome to test
		 * @return true if it is a match
		 */
		public abstract boolean isMatch(float attributeVision,
				ExtendedBitSet testedChromosome);

		/**
		 * Create a random gene for the vision attribute.
		 * 
		 * @param attributeValue
		 *            the attribute value to cover
		 * @param generatedClassifier
		 *            the classifier where the chromosome will be generated
		 */
		public abstract void randomCoveringValue(float attributeValue,
				Classifier generatedClassifier);

		/**
		 * Fix an attribute representation.
		 * 
		 * @param generatedClassifier
		 *            the chromosome to fix
		 */
		public abstract void fixAttributeRepresentation(
				ExtendedBitSet generatedClassifier);

		/**
		 * The default constructor.
		 * 
		 * @param startPosition
		 *            the position in the chromosome to start the attribute
		 *            representation
		 * @param attributeName
		 *            the name of the attribute
		 * @param generalizationRate
		 *            the generalization rate used
		 */
		public Attribute(int startPosition, String attributeName,
				double generalizationRate) {
			nameOfAttribute = attributeName;
			positionInChromosome = startPosition;
			this.generalizationRate = generalizationRate;
		}

		/**
		 * @return the length in bits of the chromosome.
		 */
		public int getLengthInBits() {
			return lengthInBits;
		}

		/**
		 * Tests is baseChromsome's gene is more general than the test
		 * chromosome.
		 * 
		 * @param baseChromosome
		 *            the base chromosome
		 * @param testChromosome
		 *            the test chromosome
		 * @return true if base is more general than test
		 */
		public abstract boolean isMoreGeneral(ExtendedBitSet baseChromosome,
				ExtendedBitSet testChromosome);

		/**
		 * Tests equality between genes.
		 * 
		 * @param baseChromosome
		 *            the base chromosome
		 * @param testChromosome
		 *            the test chromosome
		 * @return true if the genes are equivalent
		 */
		public abstract boolean isEqual(ExtendedBitSet baseChromosome,
				ExtendedBitSet testChromosome);
	}

	/**
	 * A nominal attribute. It is supposed that the vision vector "sees" a
	 * number from 0 to n-1. Where n is the number of all possible values.
	 * 
	 * @author Miltos Allamanis
	 */
	public class NominalAttribute extends Attribute {

		private String[] nominalValuesNames;

		public NominalAttribute(int startPosition, String attributeName,
				String[] nominalValuesNames, double generalizationRate) {
			super(startPosition, attributeName, generalizationRate);
			this.nominalValuesNames = nominalValuesNames;
			// We are going to use one bit per possible value plus one
			// activation bit
			lengthInBits = nominalValuesNames.length + 1;
			chromosomeSize += lengthInBits;
		}

		@Override
		public String toString(ExtendedBitSet convertingChromosome) {
			// Check if attribute is active
			if (!convertingChromosome.get(positionInChromosome))
				return nameOfAttribute + ":#";
			String attr;
			attr = nameOfAttribute + " in [";
			for (int i = 0; i < nominalValuesNames.length; i++)
				if (convertingChromosome.get(positionInChromosome + 1 + i))
					attr += nominalValuesNames[i] + ", ";
			return attr + "]";
		}

		@Override
		public void randomCoveringValue(float attributeValue,
				Classifier myChromosome) {
			// Clear everything
			myChromosome.clear(positionInChromosome, this.lengthInBits);
			if (Math.random() < (1 - generalizationRate))
				myChromosome.set(positionInChromosome);
			else
				myChromosome.clear(positionInChromosome);

			// Randomize all bits of gene
			for (int i = 1; i < lengthInBits; i++) {
				if (Math.random() < 0.5) // TODO: Variable probability?
					myChromosome.set(positionInChromosome + i);
				else
					myChromosome.clear(positionInChromosome + i);
			}

			// and set as "1" the nominal values that we are trying to match
			myChromosome.set(positionInChromosome + 1 + (int) attributeValue);

		}

		@Override
		public boolean isMatch(float attributeVision,
				ExtendedBitSet testedChromosome) {
			// if condition is not active
			if (!testedChromosome.get(positionInChromosome))
				return true;
			int genePosition = (int) attributeVision + 1 + positionInChromosome;

			return testedChromosome.get(genePosition);

		}

		@Override
		public void fixAttributeRepresentation(ExtendedBitSet chromosome) {
			int ones = 0;
			if (chromosome.get(positionInChromosome)) // Specific
				for (int i = 1; i < this.lengthInBits; i++)
					if (chromosome.get(positionInChromosome + i))
						ones++;

			// Fix (we have none or all set)
			if (ones == 0 || ones == lengthInBits - 1)
				chromosome.clear(positionInChromosome);
		}

		@Override
		public boolean isMoreGeneral(ExtendedBitSet baseChromosome,
				ExtendedBitSet testChromosome) {
			if (!baseChromosome.get(positionInChromosome))
				return true;
			if (!testChromosome.get(positionInChromosome))
				return false;
			if (!baseChromosome.getSubSet(positionInChromosome + 1,
					nominalValuesNames.length).equals(
					testChromosome.getSubSet(positionInChromosome + 1,
							nominalValuesNames.length)))
				return false;

			return true;
		}

		@Override
		public boolean isEqual(ExtendedBitSet baseChromosome,
				ExtendedBitSet testChromosome) {
			if (baseChromosome.get(positionInChromosome) != testChromosome
					.get(positionInChromosome))
				return false;
			if (!baseChromosome.get(positionInChromosome))
				return true;
			if (!baseChromosome.getSubSet(positionInChromosome + 1,
					nominalValuesNames.length).equals(
					testChromosome.getSubSet(positionInChromosome + 1,
							nominalValuesNames.length)))
				return false;

			return true;

		}

	}

	/**
	 * A private inner abstract class that represents a numeric interval.
	 * 
	 * @author Miltos Allamanis
	 */
	public class IntervalAttribute extends Attribute {

		// The minimum and the maximum value that the attribute can receive
		float minValue, maxValue;
		int precisionBits = 0;
		int totalParts = 0;

		/**
		 * The Interval Attribute Constructor.
		 * 
		 * @param startPosition
		 *            the position in the chromosome where the gene starts
		 * @param attributeName
		 *            the name of the attribute
		 * @param minValue
		 *            the minimum value of the attribute
		 * @param maxValue
		 *            the maximum value of the attribute
		 * @param precisionBits
		 *            the number of bits to use per number
		 * @param generalizationRate
		 *            the rate at which to create general attributes when
		 *            covering
		 */
		public IntervalAttribute(int startPosition, String attributeName,
				float minValue, float maxValue, int precisionBits,
				double generalizationRate) {
			super(startPosition, attributeName, generalizationRate);
			this.minValue = minValue;
			this.maxValue = maxValue;
			this.precisionBits = precisionBits;
			// 2 values of bits + 1 activation bit
			lengthInBits = 2 * precisionBits + 1;
			for (int i = 0; i < precisionBits; i++)
				totalParts |= 1 << i;
			chromosomeSize += lengthInBits;
		}

		/**
		 * Return the numeric lower bound of the interval.
		 * 
		 * @param chromosome
		 *            the chromosome containing the interval
		 * @return the numeric value of the lower bound
		 */
		private float getLowBoundValue(ExtendedBitSet chromosome) {
			int part = chromosome.getIntAt(positionInChromosome + 1,
					precisionBits);

			return ((float) part) / ((float) totalParts)
					* (maxValue - minValue) + minValue;
		}

		/**
		 * Return the numeric high bound of the interval.
		 * 
		 * @param chromosome
		 *            the chromosome containing the interval
		 * @return the numeric value of the high bound
		 */
		private float getHighBoundValue(ExtendedBitSet chromosome) {
			int part = chromosome.getIntAt(positionInChromosome + 1
					+ precisionBits, precisionBits);

			return ((float) part) / ((float) totalParts)
					* (maxValue - minValue) + minValue;
		}

		@Override
		public String toString(ExtendedBitSet convertingChromosome) {
			// Check if condition active
			if (!convertingChromosome.get(positionInChromosome))
				return nameOfAttribute + ":#";
			String value = nameOfAttribute + " in ["
					+ getLowBoundValue(convertingChromosome) + ","
					+ getHighBoundValue(convertingChromosome) + "]";
			return value;
		}

		@Override
		public boolean isMatch(float attributeVision,
				ExtendedBitSet testedChromosome) {

			if (!testedChromosome.get(positionInChromosome)) // if rule inactive
				return true;

			return (attributeVision >= getLowBoundValue((testedChromosome)) && attributeVision <= getHighBoundValue(testedChromosome));

		}

		@Override
		public void randomCoveringValue(float attributeValue,
				Classifier generatedClassifier) {
			// First find a random value that is smaller than the attribute
			// value & convert it to fraction
			int newLowBound = (int) Math
					.floor(((attributeValue - minValue) * Math.random())
							/ (maxValue - minValue) * totalParts);
			int newMaxBound = (int) Math
					.ceil(((maxValue - minValue - (maxValue - attributeValue)
							* Math.random())
							/ (maxValue - minValue) * totalParts));

			// Then set at chromosome
			if (Math.random() < (1 - generalizationRate))
				generatedClassifier.set(positionInChromosome);
			else
				generatedClassifier.clear(positionInChromosome);

			generatedClassifier.setIntAt(positionInChromosome + 1,
					precisionBits, newLowBound);
			generatedClassifier.setIntAt(positionInChromosome + 1
					+ precisionBits, precisionBits, newMaxBound);
		}

		@Override
		public void fixAttributeRepresentation(ExtendedBitSet chromosome) {
			if (getLowBoundValue(chromosome) > getHighBoundValue(chromosome)) {
				// Swap
				ExtendedBitSet low = chromosome.getSubSet(
						positionInChromosome + 1, precisionBits);
				ExtendedBitSet high = chromosome.getSubSet(positionInChromosome
						+ 1 + precisionBits, precisionBits);
				chromosome.setSubSet(positionInChromosome + 1, high);
				chromosome.setSubSet(positionInChromosome + 1 + precisionBits,
						low);
			}

		}

		@Override
		public boolean isMoreGeneral(ExtendedBitSet baseChromosome,
				ExtendedBitSet testChromosome) {
			if (!baseChromosome.get(positionInChromosome))
				return true;
			if (!testChromosome.get(positionInChromosome))
				return false;
			if (getHighBoundValue(baseChromosome) >= getHighBoundValue(testChromosome)
					&& getLowBoundValue(baseChromosome) <= getLowBoundValue(testChromosome))
				return true;
			return false;
		}

		@Override
		public boolean isEqual(ExtendedBitSet baseChromosome,
				ExtendedBitSet testChromosome) {

			if (baseChromosome.get(positionInChromosome) != testChromosome
					.get(positionInChromosome))
				return false;

			if (!baseChromosome.get(positionInChromosome))
				return true;
			if (!baseChromosome.getSubSet(positionInChromosome + 1,
					precisionBits).equals(
					testChromosome.getSubSet(positionInChromosome + 1,
							precisionBits)))
				return false;

			return true;
		}

	}

	/**
	 * A boolean attribute representation.
	 * 
	 * @author Miltos Allamanis
	 * 
	 */
	public class BooleanAttribute extends Attribute {

		public BooleanAttribute(int startPosition, String attributeName,
				double generalizationRate) {
			super(startPosition, attributeName, generalizationRate);
			lengthInBits = 2;
			chromosomeSize += lengthInBits;
		}

		@Override
		public String toString(ExtendedBitSet convertingClassifier) {
			if (convertingClassifier.get(this.positionInChromosome)) {
				return convertingClassifier.get(this.positionInChromosome + 1) ? "1"
						: "0";
			} else {
				return "#";
			}

		}

		@Override
		public boolean isMatch(float attributeVision,
				ExtendedBitSet testedChromosome) {

			if (testedChromosome.get(this.positionInChromosome)) {
				if ((attributeVision == 0 ? false : true) == testedChromosome
						.get(this.positionInChromosome + 1))
					return true;
				else
					return false;
			} else {
				return true;
			}
		}

		@Override
		public void randomCoveringValue(float attributeValue,
				Classifier generatedClassifier) {
			if (attributeValue == 0)
				generatedClassifier.getChromosome().clear(
						positionInChromosome + 1);
			else
				generatedClassifier.getChromosome().set(
						positionInChromosome + 1);

			if (Math.random() < generalizationRate) // TODO: Configurable
													// generalization rate
				generatedClassifier.getChromosome().clear(positionInChromosome);
			else
				generatedClassifier.getChromosome().set(positionInChromosome);

		}

		@Override
		public void fixAttributeRepresentation(
				ExtendedBitSet generatedClassifier) {
			return;
		}

		@Override
		public boolean isMoreGeneral(ExtendedBitSet baseChromosome,
				ExtendedBitSet testChromosome) {
			// if the base classifier is specific and the test is # return false
			if (baseChromosome.get(positionInChromosome)
					&& !testChromosome.get(positionInChromosome))
				return false;
			if (baseChromosome.get(positionInChromosome + 1) != testChromosome
					.get(positionInChromosome + 1)
					&& baseChromosome.get(positionInChromosome))
				return false;

			return true;
		}

		@Override
		public boolean isEqual(ExtendedBitSet baseChromosome,
				ExtendedBitSet testChromosome) {

			// if the base classifier is specific and the test is # return false
			if (baseChromosome.get(positionInChromosome) != testChromosome
					.get(positionInChromosome))
				return false;
			else if (baseChromosome.get(positionInChromosome + 1) != testChromosome
					.get(positionInChromosome + 1)
					&& baseChromosome.get(positionInChromosome))
				return false;

			return true;
		}

	}
}