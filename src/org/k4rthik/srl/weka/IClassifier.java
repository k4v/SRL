package org.k4rthik.srl.weka;

import com.sun.istack.internal.Nullable;
import weka.classifiers.Classifier;
import weka.core.Instances;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

/**
 * Author: Karthik
 * Date  : 8/7/2014.
 */
public abstract class IClassifier
{
    Classifier classifierInstance;
    private Instances trainingInstances = null;

    public void buildClassifierFromTrainingSet(Instances trainingInstances, @Nullable String toModelFile) throws Exception
    {
        this.trainingInstances = trainingInstances;
        this.trainingInstances.setClassIndex(trainingInstances.numAttributes() - 1);
        classifierInstance.buildClassifier(trainingInstances);

        if(toModelFile != null)
        {
            // Save trained model to file
            ObjectOutputStream modelOutputStream = new ObjectOutputStream(new FileOutputStream(toModelFile));
            modelOutputStream.writeObject(classifierInstance);
            modelOutputStream.flush();
            modelOutputStream.close();
        }
    }

    public void setTrainedClassifier(Classifier trainedClassifier)
    {
        this.classifierInstance = trainedClassifier;
    }
}
