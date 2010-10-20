package gr.auth.ee.lcs.geneticalgorithm;

import gr.auth.ee.lcs.classifiers.Classifier;

public interface IUnaryGeneticOperator {

  public Classifier operate(Classifier aClassifier);

}