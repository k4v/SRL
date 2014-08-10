package org.k4rthik.srl.main;

import com.sun.istack.internal.Nullable;
import org.k4rthik.srl.common.CommonUtils;
import org.k4rthik.srl.dom.beans.Sketch;
import org.k4rthik.srl.features.AngleCountZoningFeature;
import org.k4rthik.srl.features.CosineTransformFeature;
import org.k4rthik.srl.features.DarkLevelZoningFeature;
import org.k4rthik.srl.features.GeometricMomentsFeature;
import org.k4rthik.srl.features.IFeature;
import org.k4rthik.srl.gui.SketchCanvas;
import org.k4rthik.srl.weka.IClassifier;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.ConverterUtils.DataSource;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Author: Karthik
 * Date  : 7/10/2014.
 */
@SuppressWarnings("unused, unchecked")
public class TrainingHandler
{
    private boolean doFeatureExtract = true;

    // Feature Extraction classes
    List<Class> featureExtractionClasses = new ArrayList<Class>(Arrays.asList(
        CosineTransformFeature.class,
        GeometricMomentsFeature.class,
        DarkLevelZoningFeature.class,
        AngleCountZoningFeature.class));

    // Weka training model members
    Map<String, Integer> attributeNameMap = new HashMap<String, Integer>();
    FastVector attributeList = new FastVector();
    Instances trainingSet = null;


    public TrainingHandler() throws Exception
    {
        // Add attributes corresponding to each feature extraction class
        for(Class featureClass : featureExtractionClasses)
        {
            ((IFeature)featureClass.newInstance()).setAttributes(attributeList);
        }

        // Add all characters as nominal attribute values
        FastVector labelValues = new FastVector();
        for(int i=(int)'A'; i<=(int)'Z'; i++)
        {
            labelValues.addElement((char) i + "");
            labelValues.addElement((char) (i+32) + "");
        }

        // Class (label) attribute. Should be the last attribute added to list
        attributeList.addElement(new Attribute("ClassAttribute", labelValues));

        Enumeration attributeElements = attributeList.elements();
        while(attributeElements.hasMoreElements())
        {
            Attribute currentAttribute = (Attribute)attributeElements.nextElement();
            attributeNameMap.put(currentAttribute.name(), currentAttribute.index());
        }

        // Create new set of training instances of variable capacity
        trainingSet = new Instances("SRL", attributeList, 100);
        // Set class attribute as last attribute in list
        trainingSet.setClassIndex(attributeList.size() - 1);
    }

    public void setDoFeatureExtract(boolean doFeatureExtract)
    {
        this.doFeatureExtract = doFeatureExtract;
    }

    public void loadTrainingInstances(String arffLocation) throws Exception
    {
        this.trainingSet = new DataSource(arffLocation).getDataSet();
    }

    /**
     * There's a reason this class is the GrandMaster:
     *
     * This function does everything. First it labels
     * training set files, then extract features from
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
                    } else {
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
                arffSaver.setInstances(trainingSet);
                arffSaver.setFile(new File(toFile));
                arffSaver.writeBatch();
            } catch (Exception e)
            {
                System.err.println("Error writing training set to file: " + e.getMessage());
            }
        }
    }

    private void extractFeaturesForInstances(Map<Image, Sketch> imageMap, String imageLabel)
    {
        for(Map.Entry<Image, Sketch> imageEntry : imageMap.entrySet())
        {
            Instance trainingInstance = new Instance(attributeList.size());
            trainingInstance.setDataset(trainingSet);

            for(Class featureClass : featureExtractionClasses)
            {
                try
                {
                    IFeature featureExtractor = (IFeature)featureClass.newInstance();
                    featureExtractor.computeFeature((BufferedImage)imageEntry.getKey(), imageEntry.getValue());
                    featureExtractor.setAttributeValues(attributeList, attributeNameMap, trainingInstance);
                }
                catch(Exception e)
                {
                    System.err.println("Error extracting features for "+featureClass.getName()+": "+e.getMessage());
                }
            }

            trainingInstance.setClassValue(imageLabel);
            trainingSet.add(trainingInstance);
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
        classifierInstance.buildClassifierFromTrainingSet(this.trainingSet, saveToModelFile);

        return classifierInstance;
    }
}
