package gr.auth.ee.lcs.geneticalgorithm;

import gr.auth.ee.lcs.classifiers.Classifier;

public interface IBinaryGeneticOperator {

  public Classifier operate(Classifier classifierA, Classifier classifierB);

}