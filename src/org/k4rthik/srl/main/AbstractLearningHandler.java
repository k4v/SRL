package org.k4rthik.srl.main;

import org.k4rthik.srl.dom.beans.Sketch;
import org.k4rthik.srl.features.AngleCountZoningFeature;
import org.k4rthik.srl.features.CosineTransformFeature;
import org.k4rthik.srl.features.DarkLevelZoningFeature;
import org.k4rthik.srl.features.GeometricMomentsFeature;
import org.k4rthik.srl.features.IFeature;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Karthik
 * Date  : 8/10/2014.
 */
public class AbstractLearningHandler
{
    // Feature Extraction classes
    List<Class> featureExtractionClasses = new ArrayList<Class>(Arrays.asList(
            CosineTransformFeature.class,
            GeometricMomentsFeature.class,
            DarkLevelZoningFeature.class,
            AngleCountZoningFeature.class));

    // Weka training model members
    Map<String, Integer> attributeNameMap = new HashMap<String, Integer>();
    FastVector attributeList = new FastVector();
    Instances dataSet = null;

    String unknownLabelCharacter = "?";

    AbstractLearningHandler() throws Exception
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

        initDataset();
    }

    protected void initDataset()
    {
        // Create new set of training instances of variable capacity
        dataSet = new Instances("SRL", attributeList, 100);
        // Set class attribute as last attribute in list
        dataSet.setClassIndex(attributeList.size() - 1);
    }

    protected void extractFeaturesForInstances(Map<Image, Sketch> imageMap, String imageLabel)
    {
        for(Map.Entry<Image, Sketch> imageEntry : imageMap.entrySet())
        {
            Instance newInstance = new Instance(attributeList.size());
            newInstance.setDataset(dataSet);

            for(Class featureClass : featureExtractionClasses)
            {
                try
                {
                    IFeature featureExtractor = (IFeature)featureClass.newInstance();
                    featureExtractor.computeFeature((BufferedImage)imageEntry.getKey(), imageEntry.getValue());
                    featureExtractor.setAttributeValues(attributeList, attributeNameMap, newInstance);
                }
                catch(Exception e)
                {
                    System.err.println("Error extracting features for "+featureClass.getName()+": "+e.toString());
                }
            }

            if(imageLabel != null)
            {
                newInstance.setClassValue(imageLabel);
            }
            dataSet.add(newInstance);
        }
    }
}
