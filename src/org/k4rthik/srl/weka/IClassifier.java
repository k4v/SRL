package org.k4rthik.srl.weka;

import com.sun.istack.internal.Nullable;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

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
    public double[] classifyInstance(Instance testInstance, boolean getDistribution)
    {
        double[] classLabels = new double[]{Instance.missingValue()};

        try
        {
            if(getDistribution)
            {
                classLabels = classifierInstance.distributionForInstance(testInstance);
                return getHighestValues(classLabels, 2, 0.1f);
            }
            else
            {
                classLabels = new double[]{classifierInstance.classifyInstance(testInstance)};
            }
        } catch(Exception e)
        {
            System.err.println("Error classifying test instance: "+e.toString());
        }

        return classLabels;
    }

    private double[] getHighestValues(double[] doubleArr, int n, double cutoff)
    {
        int validValues = 0;
        for (double elem : doubleArr)
        {
            if (elem >= cutoff)
            {
                validValues += 1;
            }
        }

        if(validValues == 0)
        {
            return new double[]{Instance.missingValue()};
        }


        double[] highestValues = new double[Math.min(validValues, n)];
        Arrays.fill(highestValues, -1f);

        int currIndex = 0;
        int minIndex = 0;
        for(int i=0; i<doubleArr.length; i++)
        {
            if(highestValues[currIndex] < 0)
            {
                highestValues[currIndex] = i;
            }
            else if(doubleArr[(int)highestValues[minIndex]] < doubleArr[i])
            {
                highestValues[minIndex] = i;
            }

            currIndex = (currIndex+1)%highestValues.length;
            for(int j=0; j<highestValues.length; j++)
            {
                if((highestValues[j] >= 0) && (doubleArr[(int)highestValues[j]] < doubleArr[(int)highestValues[minIndex]]))
                {
                    minIndex = j;
                }
            }
        }

        return highestValues;
    }
}
