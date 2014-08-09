package org.k4rthik.srl.weka;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;

/**
 * Author: Karthik
 * Date  : 8/7/2014.
 */
public abstract class IClassifier
{
    Classifier classifierInstance;
    private Instances trainingInstances = null;

    public void setTrainingSet(Instances trainingInstances) throws Exception
    {
        this.trainingInstances = trainingInstances;
        this.trainingInstances.setClassIndex(trainingInstances.numAttributes() - 1);
        classifierInstance.buildClassifier(trainingInstances);
    }

    public Evaluation evaluateModel(Instances testInstances) throws Exception
    {
        Evaluation evalModel = new Evaluation(this.trainingInstances);
        evalModel.evaluateModel(classifierInstance, testInstances);
        return evalModel;
    }
}
