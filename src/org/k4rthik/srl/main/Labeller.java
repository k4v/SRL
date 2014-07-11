package org.k4rthik.srl.main;

import org.k4rthik.srl.dom.ImageHandler;
import org.k4rthik.srl.dom.SketchXMLReader;
import org.k4rthik.srl.dom.beans.Sketch;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
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
public class Labeller
{
    private static Labeller INSTANCE = null;

    // Get singletone instance of Labeller
    public static synchronized Labeller getInstance()
    {
        if(INSTANCE == null)
            INSTANCE = new Labeller();
        // Return singleton instance
        return INSTANCE;
    }

    // Private constructor for Labeller to make singleton class
    private Labeller() { }

    // For each sketch XML in base directory, get label as training data.
    // forceRelabel forces the application to relabel all sketch folders,
    // even if label already exists.
    public void labelXmls(Path baseDir, boolean forceRelabel)
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
                // If a label already exists don't get one again
                if(!forceRelabel && (new File(charDir.toString()+"/label").exists()))
                    continue;

                int filesLoaded = 0;
                System.out.println("Opening directory: " + charDir.toString());
                // Read sub dir
                DirectoryStream<Path> charDirStream = Files.newDirectoryStream(charDir);
                // Map of <Label, count>
                Map<String, Integer> labelMap = new HashMap<String, Integer>();
                String majLabel = null;
                for (Path charFile : charDirStream)
                {
                    // Read XML files from the directory
                    if(charFile.toString().endsWith(".xml"))
                    {
                        String imageLabel = drawAndLabelXml(charFile, xmlReader);
                        // Add label with count to map
                        labelMap.put(imageLabel,
                                (labelMap.get(imageLabel) == null) ? 1 : 1+labelMap.get(imageLabel));
                        // Update majLabel if new count is greater
                        majLabel = ((majLabel == null) || (labelMap.get(imageLabel) > labelMap.get(majLabel))) ?
                                imageLabel : majLabel;
                        filesLoaded++;
                    }
                }
                // Write majority label to folder
                if(majLabel != null)
                {
                    FileWriter fileWriter = new FileWriter(charDir.toString() + "/" + "label");
                    fileWriter.write(majLabel);
                    fileWriter.close();
                }
                System.out.println(filesLoaded+" files read\n");
            }
        } catch (IOException e)
        {
            System.err.println("Error reading files in base directory: "+e.toString());
        }
    }

    private String drawAndLabelXml(Path charFile, SketchXMLReader xmlReader) throws IOException
    {
        // Load XML file into Sketch object
        System.out.println("Reading file: " + charFile.toString());
        Sketch xmlSketch = xmlReader.loadXML(new File(charFile.toString()));

        // Draw image from points in sketch
        BufferedImage drawImage = ImageHandler.drawImage(xmlSketch);
        ImageIO.write(drawImage, "png", new File(charFile+".png"));
        // Get label for image from user
        System.out.print("Enter label for " + charFile + ": ");
        return new Scanner(System.in).nextLine();
    }
}
