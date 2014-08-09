package org.k4rthik.srl.main;

import java.nio.file.FileSystems;
import java.nio.file.Path;

/**
 * Author: Karthik
 * Date  : 7/9/2014.
 */
public class Main
{
    public static void main(String[] args) throws Exception
    {
        if(args != null)
        {
            TrainingHandler trainingHandler = TrainingHandler.getInstance();

            for(String baseFolder : args)
            {
                System.out.println("Training from "+baseFolder);
                Path baseDir = FileSystems.getDefault().getPath(baseFolder);
                trainingHandler.processTrainingSet(baseDir, false);
            }

            System.out.println("Training complete with "+args.length+" datasets");
        }
    }
}
