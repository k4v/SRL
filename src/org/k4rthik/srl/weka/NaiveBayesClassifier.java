package org.k4rthik.srl.weka;

import weka.classifiers.bayes.NaiveBayes;

/**
 * Author: Karthik
 * Date  : 08/10/2014.
 */
public class NaiveBayesClassifier extends IClassifier
{
    public NaiveBayesClassifier() throws Exception
    {
        classifierInstance = new NaiveBayes();
    }
}
