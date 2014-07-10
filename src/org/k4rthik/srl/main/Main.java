package org.k4rthik.srl.main;

import org.k4rthik.srl.dom.SketchXMLReader;
import org.k4rthik.srl.dom.beans.Sketch;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Author: Karthik
 * Date  : 7/9/2014.
 */
public class Main
{
    public static void main(String[] args)
    {
        String baseFolder = args[0];
        Path baseDir = FileSystems.getDefault().getPath(baseFolder);

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
                for (Path charFile : charDirStream)
                {
                    // Read XML files from the directory
                    if(charFile.toString().endsWith(".xml"))
                    {
                        System.out.println("Reading file: " + charFile.toString());
                        Sketch xmlSketch = xmlReader.loadXML(new File(charFile.toString()));
                        System.out.println(xmlSketch.toString());
                        filesLoaded++;
                    }
                }
                System.out.println(filesLoaded+" files read\n");

            }
        } catch (IOException e)
        {
            System.err.println("Error reading files in base directory: "+e.toString());
        }

    }
}
