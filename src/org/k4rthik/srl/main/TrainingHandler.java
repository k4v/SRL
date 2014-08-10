package org.k4rthik.srl.main;

import com.sun.istack.internal.Nullable;
import org.k4rthik.srl.common.CommonUtils;
import org.k4rthik.srl.dom.beans.Sketch;
import org.k4rthik.srl.gui.SketchCanvas;
import org.k4rthik.srl.weka.IClassifier;
import weka.core.converters.ArffSaver;
import weka.core.converters.ConverterUtils.DataSource;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Author: Karthik
 * Date  : 7/10/2014.
 */
@SuppressWarnings("unused, unchecked")
public class TrainingHandler extends AbstractLearningHandler
{
    private boolean doFeatureExtract = true;

    public TrainingHandler() throws Exception
    {
    }

    public void setDoFeatureExtract(boolean doFeatureExtract)
    {
        this.doFeatureExtract = doFeatureExtract;
    }

    public void loadTrainingInstances(String arffLocation) throws Exception
    {
        this.dataSet = new DataSource(arffLocation).getDataSet();
    }

    /**
     * This function does everything. First it labels
     * training set files, then extracts features from
     * each image read.
     */
    public void processTrainingSet(Path baseDir, boolean forceRelabel)
    {
        // For each sketch XML in base directory, get label as training data.
        // forceRelabel forces the application to relabel all sketch folders,
        // even if label already exists.

        try
        {
            // Read contents of baseDir
            DirectoryStream<Path> dirStream = Files.newDirectoryStream(baseDir);
            // Each sub directory contains XML files for a particular character
            for (Path charDir : dirStream)
            {
                // Map of images within this directory (having common label)
                Map<Image, Sketch> imageMap = new HashMap<Image, Sketch>();

                int filesLoaded = 0;
                System.out.println("Opening directory: " + charDir.toString());
                // Read sub dir
                DirectoryStream<Path> charDirStream = Files.newDirectoryStream(charDir);
                for (Path charFile : charDirStream)
                {
                    // Read XML files from the directory
                    if(charFile.toString().endsWith(".xml"))
                    {
                        CommonUtils.Pair<Image, Sketch> imageSketchPair = CommonUtils.drawImageForXml(charFile);
                        // Add label with count to map
                        imageMap.put(imageSketchPair.getKey(), imageSketchPair.getValue());
                        filesLoaded++;
                    }
                }
                System.out.println(filesLoaded+" files read\n");

                // If a label already exists don't get one again
                if(filesLoaded > 0)
                {
                    String selectLabel;
                    if (forceRelabel || !(new File(charDir.toString() + "/label").exists()))
                    {
                        // Draw all images on a canvas
                        new SketchCanvas(imageMap.keySet()).createAndShowUI();

                        // Get label for image from user
                        System.out.print("Enter label for: " + charDir.toString() + ": ");
                        selectLabel = new Scanner(System.in).nextLine();
                        FileWriter fileWriter = new FileWriter(charDir.toString() + "/" + "label");
                        fileWriter.write(selectLabel);
                        fileWriter.close();
                    } else
                    {
                        BufferedReader fileReader = new BufferedReader(new FileReader(charDir.toString() + "/" + "label"));
                        selectLabel = fileReader.readLine();
                    }

                    // Compute features and add instance to training set
                    if(doFeatureExtract)
                    {
                        extractFeaturesForInstances(imageMap, selectLabel);
                    }
                }
            }
        } catch (IOException e)
        {
            System.err.println("Error reading files in base directory: "+e.toString());
        }
    }

    public void saveTrainingSetArff(String toFile)
    {
        if(doFeatureExtract)
        {
            // Save training set to ARFF file
            System.out.println("Saving training dataset to ARFF file");
            try
            {
                ArffSaver arffSaver = new ArffSaver();
                arffSaver.setInstances(dataSet);
                arffSaver.setFile(new File(toFile));
                arffSaver.writeBatch();
            } catch (Exception e)
            {
                System.err.println("Error writing training set to file: " + e.toString());
            }
        }
    }

    // Build a classifier with the trained instances and save a model file if required
    public IClassifier buildClassifier(Class<? extends IClassifier> classifierClass, @Nullable String saveToModelFile) throws Exception
    {
        if(classifierClass == null)
        {
            return null;
        }

        IClassifier classifierInstance = classifierClass.newInstance();
        classifierInstance.buildClassifierFromTrainingSet(this.dataSet, saveToModelFile);

        return classifierInstance;
    }
}
