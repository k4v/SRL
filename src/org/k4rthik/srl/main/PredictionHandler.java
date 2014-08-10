package org.k4rthik.srl.main;

import org.k4rthik.srl.dom.SketchXMLReader;
import org.k4rthik.srl.dom.beans.Sketch;
import org.k4rthik.srl.weka.IClassifier;
import weka.classifiers.Classifier;
import weka.core.Instances;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Author: Karthik
 * Date  : 08/09/2014.
 */
@SuppressWarnings("unused")
public class PredictionHandler
{
    IClassifier trainedClassifier = null;
    Instances testSet = null;

    // This is the list of words to which the classifier should stick
    // This is a simple non-learning modelof "context" to the entered
    // text
    List<String> labeDictionary;

    int minStrokesInCharacter = 1;
    int maxStrokesInCharacter = 7;

    char unknownLabelCharacter = '?';

    // Load a predictor from a given model using the given classifier
    public PredictionHandler(Class classifierClass, String classifierModelFile) throws Exception
    {
        // Load classifier from model file
        Classifier trainedClassifier = (Classifier)new ObjectInputStream(new FileInputStream(classifierModelFile)).readObject();

        // This creates an IClassifier containing an untrained Classifier object
        this.trainedClassifier = (IClassifier)classifierClass.newInstance();
        // This replaces the untrained Classifier with a trained Classifier object from the model file
        this.trainedClassifier.setTrainedClassifier(trainedClassifier);
    }

    public void setWordlist(Collection<String> wordList)
    {
        this.labeDictionary = new ArrayList<String>(wordList);
    }

    // Given a folder containing test set of SOUSA XMLs, classify each one of them
    // Prediction handler works assuming that test set could be words
    public void classifyInstances(String testSetDir) throws Exception
    {
        Set<String> possibleLabels = new HashSet<String>();

        try
        {
            File testFolder = new File(testSetDir);
            File[] fileList = testFolder.listFiles();

            if(fileList == null || fileList.length == 0)
            {
                throw new FileNotFoundException("No files found in test dataset folder");
            }

            SketchXMLReader xmlReader = new SketchXMLReader();

            for(File xmlFile : fileList)
            {
                if(xmlFile.toString().endsWith(".xml"))
                {
                    // Load XML file into Sketch object
                    System.out.println("Reading file: " + xmlFile.toString());
                    xmlReader.loadXML(xmlFile);
                    Sketch xmlSketch = xmlReader.getXmlSketch();

                    Set<String> sketchLabels = classifySketch(xmlSketch);
                    System.out.println("\nClasses for "+xmlFile.toString()+": "+sketchLabels);
                }
            }
        } catch (Exception e)
        {
            System.err.println("Error reading files in test set directory: "+e.toString());
        }
    }

    // Get label for a (possibly) multi character sketch
    private Set<String> classifySketch(Sketch testSketch)
    {
        Set<String> possibleLabels = new HashSet<String>();

        if(testSketch == null)
        {
            return possibleLabels;
        }

        int strokeCount = testSketch.getStrokeCount();

        if(strokeCount < minStrokesInCharacter)
            return possibleLabels;

        for(int i=minStrokesInCharacter; i<=Math.min(maxStrokesInCharacter, strokeCount); i++)
        {
            // Subsketch is assumed to be 1 character.
            Sketch subSketch = testSketch.getSubsketch(0, i-1);

            Set<String> firstCharLabels = getCharSketchLabel(subSketch);
            // Classify the rest of the sketch (multiple characters, one at a time)
            Set<String> remainingLabels = classifySketch(subSketch.getSubsketch(i, strokeCount-1));

            // Get all possible labels for this sketch as sum of first character + remaining characters
            if(firstCharLabels.isEmpty())
            {
                possibleLabels.addAll(remainingLabels);
            }
            else if(remainingLabels.isEmpty())
            {
                possibleLabels.addAll(firstCharLabels);
            }
            else
            {
                for(String firstChar : firstCharLabels)
                {
                    for(String remainingChars : remainingLabels)
                    {
                        possibleLabels.add(firstChar+remainingChars);
                    }
                }
            }
        }

        return possibleLabels;
    }

    // Get classification for a sketch containing a single character
    private Set<String> getCharSketchLabel(Sketch testSketch)
    {
        return null;
    }
}
