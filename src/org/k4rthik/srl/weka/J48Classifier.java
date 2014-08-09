package org.k4rthik.srl.weka;

import weka.classifiers.trees.J48;

/**
 * Author: Karthik
 * Date  : 8/4/2014.
 */
public class J48Classifier extends IClassifier
{
    public J48Classifier() throws Exception
    {
        classifierInstance = new J48();
        String[] classifierOptions = new String[3];
        classifierOptions[0] = "-U";
        classifierOptions[1] = "-C 0.25";
        classifierOptions[2] = "-M 2";

        classifierInstance.setOptions(classifierOptions);
    }
}
