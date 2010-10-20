package gr.auth.ee.lcs.geneticalgorithm;

import gr.auth.ee.lcs.classifiers.ClassifierSet;

public interface INaturalSelector {

  public void select(int howManyToSelect, ClassifierSet fromPopulation, ClassifierSet toPopulation);

}