package org.k4rthik.srl.main;

import java.nio.file.FileSystems;
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
        GrandMaster.getInstance().processTrainingSet(baseDir, false);
    }
}
