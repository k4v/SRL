package org.k4rthik.srl.main;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Author: Karthik
 * Date  : 7/9/2014.
 */
public class Main
{
    public static void main(String[] args) throws Exception
    {
        if(args.length > 0)
        {
            // Start the program in training mode
            if(args[0].equals("T"))
            {
                TrainingHandler trainingHandler = new TrainingHandler();
                Class classifierClass = Class.forName("org.k4rthik.srl.weka."+args[args.length - 1]);

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_hh_mm");
                String outputFolder = dateFormat.format(new Date());
                System.out.println(outputFolder);

                for (int i=1; i<args.length-1; i++)
                {
                    String baseFolder = args[i];

                    System.out.println("Training from " + baseFolder);
                    Path baseDir = FileSystems.getDefault().getPath(baseFolder);
                    trainingHandler.processTrainingSet(baseDir, false);
                }

                // Save training instances to ARFF file
                String trainingArffLocation = outputFolder+"/"+args[args.length-1]+"."+(args.length-2)+".arff";
                trainingHandler.saveTrainingSetArff(trainingArffLocation);

                // Save trained classifier to model file
                String modelLocation = outputFolder+"/"+args[args.length-1]+"."+(args.length-2)+".model";
                trainingHandler.buildClassifier(classifierClass, modelLocation);

                System.out.println("Training complete with " + (args.length - 2) + " datasets");
            }
            // Start the program in prediction mode
            else if(args[0].equals("P"))
            {
                String testDataLocation = args[1];
                String modelFileLocation = args[2];
                Class classifierClass = Class.forName("org.k4rthik.srl.weka."+args[3]);

                PredictionHandler predictionHandler = new PredictionHandler(classifierClass, modelFileLocation);
                predictionHandler.classifyInstances(testDataLocation);
            }
            else
            {
                printUsage();
            }
        }
        else
        {
            printUsage();
        }
    }

    private static void printUsage()
    {
        System.out.println("Usage: java -jar -Xmx2048\n\t" +
                "  [T <list of training dataset folders> <classifier class name>]\n\t" +
                "| [P <test dataset folder> <model file> <classifier class name>]");
    }
}
