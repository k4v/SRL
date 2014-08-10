package org.k4rthik.srl.weka;

import com.sun.istack.internal.Nullable;
import weka.classifiers.Classifier;
import weka.core.Instance;
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

    // This function is to be used right after training. It trains the classifier using the given training instances
    // and saves the trained classifier into a model file.
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

    // This function is to be used during testing/prediction.
    // It classifes a given test instance using the trained model
    public double classifyInstance(Instance testInstance)
    {
        double classLabel = Instance.missingValue();

        try
        {
            classLabel = classifierInstance.classifyInstance(testInstance);
        } catch(Exception e)
        {
            System.err.println("Error classifying test instance: "+e.toString());
        }

        return classLabel;
    }
}
