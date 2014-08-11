package org.k4rthik.srl.weka;

import weka.classifiers.bayes.BayesNet;

/**
 * Author: Karthik
 * Date  : 8/10/2014.
 */
public class BayesNetClassifier extends IClassifier
{
    public BayesNetClassifier() throws Exception
    {
        classifierInstance = new BayesNet();
    }
}
