package org.k4rthik.srl.main;

import org.k4rthik.srl.dom.SketchXMLReader;
import org.k4rthik.srl.dom.beans.Sketch;
import org.k4rthik.srl.weka.IClassifier;
import weka.classifiers.Classifier;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Author: Karthik
 * Date  : 08/09/2014.
 */
@SuppressWarnings("unused")
public class PredictionHandler extends AbstractLearningHandler
{
    IClassifier trainedClassifier = null;

    // This is the list of words to which the classifier should stick
    // This is a simple non-learning modelof "context" to the entered
    // text
    List<String> labeDictionary;

    int minStrokesInCharacter = 1;
    int maxStrokesInCharacter = 2;

    SketchXMLReader xmlReader;

    // Load a predictor from a given model using the given classifier
    public PredictionHandler(Class classifierClass, String classifierModelFile) throws Exception
    {
        // Load classifier from model file
        Classifier trainedClassifier = (Classifier)new ObjectInputStream(new FileInputStream(classifierModelFile)).readObject();

        // This creates an IClassifier containing an untrained Classifier object
        this.trainedClassifier = (IClassifier)classifierClass.newInstance();
        // This replaces the untrained Classifier with a trained Classifier object from the model file
        this.trainedClassifier.setTrainedClassifier(trainedClassifier);

        xmlReader = new SketchXMLReader();
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

            for(File xmlFile : fileList)
            {
                if(xmlFile.isFile() && xmlFile.toString().endsWith(".xml"))
                {
                    System.out.println("Reading file: " + xmlFile.toString());

                    // Load XML file into Sketch object
                    Sketch xmlSketch = xmlReader.xmlToSketch(xmlFile);

                    // Find possible classes for sketch
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
            Sketch subSketchPlusOne = testSketch.getSubsketch(i, i);

            // Check if next stroke is contained in or contained by this one. If so, skip till we get to a combination of both strokes
            if(subSketchPlusOne != null)
            {
                float[] thisXYBounds = subSketch.getXYBounds();
                float[] nextXYBounds = subSketchPlusOne.getXYBounds();
                /*
                if(
                   (thisXYBounds[0] <= nextXYBounds[0] && thisXYBounds[1] >= nextXYBounds[1])
                || (nextXYBounds[0] <= thisXYBounds[0] && nextXYBounds[1] >= thisXYBounds[1]))
                {
                    continue;
                }*/
            }

            Set<String> firstCharLabels = getCharSketchLabel(subSketch);
            // Classify the rest of the sketch (multiple characters, one at a time)
            Set<String> remainingLabels = classifySketch(testSketch.getSubsketch(i, strokeCount-1));

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

    // Get classification for a sketch or subsketch containing a single character
    private Set<String> getCharSketchLabel(Sketch testSketch)
    {
        Map<Image, Sketch> imageSketchMap = new HashMap<Image, Sketch>(1);
        BufferedImage sketchImage = xmlReader.drawImage(testSketch);
        try {
            ImageIO.write(sketchImage, "png", new File(testSketch.getFileName() + ".png"));
        } catch(IOException e)
        {
            /* Ah, frak it */
        }
        imageSketchMap.put(sketchImage, testSketch);
        initDataset();
        extractFeaturesForInstances(imageSketchMap, null);

        // Dataset only contains 1 instance
        double classLabel = trainedClassifier.classifyInstance(this.dataSet.firstInstance());

        Set<String> labelInstances = new HashSet<String>(1);
        String sketchLabel = (Double.isNaN(classLabel)) ? unknownLabelCharacter : this.dataSet.classAttribute().value((int)classLabel);
        labelInstances.add(sketchLabel);

        return labelInstances;
    }
}
