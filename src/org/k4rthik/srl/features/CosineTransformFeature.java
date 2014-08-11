package org.k4rthik.srl.features;

import com.sun.istack.internal.Nullable;
import org.k4rthik.srl.common.CommonUtils;
import org.k4rthik.srl.dom.beans.Sketch;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Map;

/**
 * Author: Karthik
 * Date  : 7/23/2014.
 */
public class CosineTransformFeature  extends IFeature
{
    // Feature matrix
    double[][] cosineTransform = null;

    // In order to train data using constant number of features,
    // each image matrix is resized to 100x100 before computing
    // cosine transforms.
    Dimension gridDimensions = new Dimension(100, 100);

    public CosineTransformFeature()
    {
        ATTRIBUTE_NAME_PREFIX = "COS_TRANSFORM_";

        cosineTransform = new double[gridDimensions.height][gridDimensions.width];
        for(int i=0; i<gridDimensions.getHeight(); i++)
        {
            Arrays.fill(cosineTransform[i], 0);
        }
    }

    @Override
    public void computeFeature(@Nullable BufferedImage sketchImage, @Nullable Sketch parsedSketch)
    {
        System.out.println("Extracting Cosine Transform features from "+parsedSketch.getFileName());

        // Resize the image and save to pixel array
        int[][] pixelArr = fitToGrid(CommonUtils.getBinaryArray_BinaryImage(sketchImage));

        // Compute cosine tranform for each element in image matrix
        for(int v=0; v<pixelArr.length; v++)
        {
            // pixelArr is a square matrix. So I've used
            // pixelArr.length in place of pixelArr[v].
            // Bounds of all x-coords should ideally be
            // pixelArr[v].length

            // Fill cosine transform matrix with 0 values
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
                try
                {
                    fitGrid[y][x] = 0;

                    // Find pixels in actual array between which this location in fit array is to be approximated
                    int yStart = (int) Math.floor(y * ySkip);
                    int yEnd = (ySkip <= 1) ? yStart : (yStart + ((int) ySkip));
                    int xStart = (int) Math.floor(x * xSkip);
                    int xEnd = (xSkip < 1) ? xStart : (xStart + ((int) xSkip));

                    // Get the pixels with the computed bounds and get the average black value
                    for (int yCoord = yStart; yCoord <= Math.min(yEnd, pixelArr.length -1); yCoord++)
                    {
                        for (int xCoord = xStart; xCoord <= Math.min(xEnd, pixelArr[yCoord].length -1); xCoord++)
                        {
                            fitGrid[y][x] += pixelArr[yCoord][xCoord];
                        }
                    }

                    fitGrid[y][x] = (int) Math.round(fitGrid[y][x] / (Math.ceil(ySkip) * Math.ceil(xSkip)));
                } catch(Exception e)
                {
                    System.err.println("Error computing cosine transform feature for ("+y+","+x+")");
                    e.printStackTrace(System.err);
                }
            }
        }

        return fitGrid;
    }

    @Override
    public void setAttributes(FastVector attributeList)
    {
        for(int i=0; i<cosineTransform.length; i++)
        {
            for(int j=0; j<cosineTransform[i].length; j++)
            {
                attributeList.addElement(new Attribute(ATTRIBUTE_NAME_PREFIX+i+"_"+j, attributeList.size()));
            }
        }
    }

    @Override
    public void setAttributeValues(FastVector attributeList, Map<String, Integer> attributeNameMap, Instance thisInstance)
    {
        for(int i=0; i<gridDimensions.height; i++)
        {
            for(int j=0; j<gridDimensions.width; j++)
            {
                thisInstance.setValue(
                        (Attribute)attributeList.elementAt(attributeNameMap.get(ATTRIBUTE_NAME_PREFIX+i+"_"+j)),
                        cosineTransform[i][j]);
            }
        }
    }
}
