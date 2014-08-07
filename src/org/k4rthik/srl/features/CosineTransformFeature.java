package org.k4rthik.srl.features;

import com.sun.istack.internal.Nullable;
import org.k4rthik.srl.common.CommonUtils;
import org.k4rthik.srl.dom.beans.Sketch;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.Arrays;

/**
 * Author: Karthik
 * Date  : 7/23/2014.
 */
public class CosineTransformFeature implements IFeature
{
    // Feature matrix
    double[][] cosineTransform = null;

    // In order to train data using constant number of features,
    // each image matrix is resized to 100x100 before computing
    // cosine transforms.
    Dimension gridDimensions = new Dimension(100, 100);

    @Override
    public void computeFeature(@Nullable BufferedImage sketchImage, @Nullable Sketch parsedSketch)
    {
        System.out.println("Extracting features from "+parsedSketch.getFileName());

        // Resize the image and save to pixel array
        int[][] pixelArr = fitToGrid(CommonUtils.getBinaryArray_BinaryImage(sketchImage));

        cosineTransform = new double[pixelArr.length][];
        // Compute cosine tranform for each element in image matrix
        for(int v=0; v<pixelArr.length; v++)
        {
            // pixelArr is a square matrix. So I've used
            // pixelArr.length in place of pixelArr[v].
            // Bounds of all x-coords should ideally be
            // pixelArr[v].length

            // Fill cosine transform matrix with 0 values
            cosineTransform[v] = new double[pixelArr[v].length];
            Arrays.fill(cosineTransform[v], 0);

            for(int u=0; u<pixelArr.length; u++)
            {
                for(int x=0; x<pixelArr.length; x++)
                {
                    for(int y=0; y<pixelArr.length; y++)
                    {
                        cosineTransform[v][u] += pixelArr[y][x]
                                * Math.cos((Math.PI / pixelArr.length) * (x + 0.5) * u)
                                * Math.cos((Math.PI / pixelArr.length) * (y + 0.5) * v);
                    }
                }

                // Normalize cosine tranforms
                cosineTransform[v][u] = Math.sqrt(2f/pixelArr.length)
                                        * Math.sqrt(2f/pixelArr.length)
                                        * getBasis(v)*getBasis(u)*cosineTransform[v][u];
            }
        }
    }

    private double getBasis(int w)
    {
        return (w == 0) ? (1/Math.sqrt(2)) : 1;
    }

    // Resize image matrix to fit gridDimensions
    private int[][] fitToGrid(int[][] pixelArr)
    {
        int[][] fitGrid = new int[gridDimensions.height][];

        float ySkip = pixelArr.length/((float)gridDimensions.getHeight());
        float xSkip = pixelArr[0].length/((float)gridDimensions.getWidth());

        for(int y=0; y<fitGrid.length; y++)
        {
            fitGrid[y] = new int[gridDimensions.width];
            for (int x=0; x<fitGrid[y].length; x++)
            {
                fitGrid[y][x] = 0;

                // Find pixels in actual array between which this location in fit array is to be approximated
                int yStart = (int)Math.floor(y*ySkip);
                int yEnd = (ySkip <= 1) ? yStart : (yStart + ((int)ySkip));
                int xStart = (int)Math.floor(x*xSkip);
                int xEnd = (xSkip < 1) ? xStart : (xStart + ((int)xSkip));

                // Get the pixels with the computed bounds and get the average black value
                for(int yCoord = yStart; yCoord<=yEnd; yCoord++)
                {
                    for (int xCoord = xStart; xCoord <= xEnd; xCoord++)
                    {
                        fitGrid[y][x] += pixelArr[yCoord][xCoord];
                    }
                }

                fitGrid[y][x] = (int)Math.round(fitGrid[y][x]/(Math.ceil(ySkip)*Math.ceil(xSkip)));
            }
        }

        return fitGrid;
    }
}
