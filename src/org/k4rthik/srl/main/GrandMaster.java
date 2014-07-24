package org.k4rthik.srl.main;

import org.k4rthik.srl.dom.SketchXMLReader;
import org.k4rthik.srl.dom.beans.Sketch;
import org.k4rthik.srl.features.CosineTransformFeature;
import org.k4rthik.srl.features.IFeature;
import org.k4rthik.srl.gui.ImageHandler;
import org.k4rthik.srl.gui.SketchCanvas;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Author: Karthik
 * Date  : 7/10/2014.
 */
public class GrandMaster
{
    private static GrandMaster INSTANCE = null;

    // Map of BufferedImage to parsed Sketch object
    // We'll be doing feature extraction from all these images later
    private Map<Image, Sketch> GRAND_MAP = new HashMap<Image, Sketch>();


    // Get singletone instance of Labeller
    public static synchronized GrandMaster getInstance()
    {
        if(INSTANCE == null)
            INSTANCE = new GrandMaster();
        // Return singleton instance
        return INSTANCE;
    }

    // Private constructor for Labeller to make singleton class
    private GrandMaster() { }



    /**
     * There's a reason this class is the GrandMaster:
     *
     * This function does everything. First it labels
     * training set files, then extract features from
     * each image read.
     */
    public void processTrainingSet(Path baseDir, boolean forceRelabel)
    {
        // Label all images in the given path
        labelXmls(baseDir, forceRelabel);


        // Extract features from the images we found
        for(Map.Entry<Image, Sketch> mapEntry : GRAND_MAP.entrySet())
        {
            // 1. Cosine transform
            IFeature featureExtractor = new CosineTransformFeature();
            featureExtractor.extractFeature((BufferedImage)mapEntry.getKey(), mapEntry.getValue());
        }
    }

    // For each sketch XML in base directory, get label as training data.
    // forceRelabel forces the application to relabel all sketch folders,
    // even if label already exists.
    private void labelXmls(Path baseDir, boolean forceRelabel)
    {
        // Loads XML (text) file and computes points
        SketchXMLReader xmlReader = new SketchXMLReader();

        try
        {
            // Read contents of baseDir
            DirectoryStream<Path> dirStream = Files.newDirectoryStream(baseDir);
            // Each sub directory contains XML files for a particular character
            for (Path charDir : dirStream)
            {
                int filesLoaded = 0;
                System.out.println("Opening directory: " + charDir.toString());
                // Read sub dir
                DirectoryStream<Path> charDirStream = Files.newDirectoryStream(charDir);
                // Map of <Label, count>
                List<Image> imageList = new ArrayList<Image>();
                for (Path charFile : charDirStream)
                {
                    // Read XML files from the directory
                    if(charFile.toString().endsWith(".xml"))
                    {
                        Image drawnImage= drawImageForXml(charFile, xmlReader);
                        // Add label with count to map
                        imageList.add(drawnImage);
                        filesLoaded++;
                    }
                }
                System.out.println(filesLoaded+" files read\n");

                // If a label already exists don't get one again
                if(!forceRelabel && (new File(charDir.toString()+"/label").exists()))
                    continue;

                // Write majority label to folder
                if(filesLoaded > 0)
                {
                    // Draw all images on a canvas
                    new SketchCanvas(imageList).createAndShowUI();
                    // Get label for image from user
                    System.out.print("Enter label for: " + charDir.toString() + ": ");
                    String dirLabel = new Scanner(System.in).nextLine();
                    FileWriter fileWriter = new FileWriter(charDir.toString() + "/" + "label");
                    fileWriter.write(dirLabel);
                    fileWriter.close();
                }
            }
        } catch (IOException e)
        {
            System.err.println("Error reading files in base directory: "+e.toString());
        }
    }

    private Image drawImageForXml(Path charFile, SketchXMLReader xmlReader) throws IOException
    {
        // Load XML file into Sketch object
        System.out.println("Reading file: " + charFile.toString());
        Sketch xmlSketch = xmlReader.loadXML(new File(charFile.toString()));
        xmlSketch.setFileName(charFile.toString());

        // Draw image from points in sketch
        BufferedImage drawImage = ImageHandler.drawImage(xmlSketch);
        ImageIO.write(drawImage, "png", new File(charFile+".png"));

        // Add image to grand map for feature extraction later
        GRAND_MAP.put(drawImage, xmlSketch);

        return drawImage;
    }
}
