package org.k4rthik.srl.weka;

import weka.classifiers.functions.MultilayerPerceptron;

/**
 * Author: Karthik
 * Date  : 8/7/2014.
 */
public class MLPClassifier extends IClassifier
{
    public MLPClassifier() throws Exception
    {
        classifierInstance = new MultilayerPerceptron();

        String[] classifierOptions = new String[14];
        classifierOptions[0] = "-L";
        classifierOptions[1] = "0.3";
        classifierOptions[2] = "-M";
        classifierOptions[3] = "0.2";
        classifierOptions[4] = "-N";
        classifierOptions[5] = "500";
        classifierOptions[6] = "-V";
        classifierOptions[7] = "0";
        classifierOptions[8] = "-S";
        classifierOptions[9] = "0";
        classifierOptions[10] = "-E";
        classifierOptions[11] = "20";
        classifierOptions[12] = "-H";
        classifierOptions[13] = "a";


        classifierInstance.setOptions(classifierOptions);
    }
}
