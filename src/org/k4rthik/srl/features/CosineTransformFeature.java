package org.k4rthik.srl.features;

import com.sun.istack.internal.Nullable;
import org.k4rthik.srl.common.CommonUtils;
import org.k4rthik.srl.dom.beans.Sketch;

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

    @Override
    public void computeFeature(@Nullable BufferedImage sketchImage, @Nullable Sketch parsedSketch)
    {
        System.out.println("Extracting features from "+parsedSketch.getFileName());

        int[][] pixelArr = CommonUtils.getBinaryArray_BinaryImage(sketchImage);
        CommonUtils.print2DArray(pixelArr);

        System.out.println('\n');

        cosineTransform = new double[pixelArr.length][];
        // Compute cosine tranform for each element in intial matrix
        for(int v=0; v<sketchImage.getHeight(); v++)
        {
            // Fill cosine transform matrix with 0 values
            cosineTransform[v] = new double[sketchImage.getWidth()];
            Arrays.fill(cosineTransform[v], 0);

            for(int u=0; u<sketchImage.getWidth(); u++)
            {
                for(int x=0; x<sketchImage.getWidth(); x++)
                {
                    for(int y=0; y<sketchImage.getHeight(); y++)
                    {
                        cosineTransform[v][u] += pixelArr[y][x]
                                * Math.cos((Math.PI / sketchImage.getWidth()) * (x + 0.5) * u)
                                * Math.cos((Math.PI / sketchImage.getHeight()) * (y + 0.5) * v);
                    }
                }

                // Normalize cosine tranforms
                cosineTransform[v][u] = Math.sqrt(2f/sketchImage.getWidth())
                                        * Math.sqrt(2f/sketchImage.getHeight())
                                        * getBasis(v)*getBasis(u)*cosineTransform[v][u];
            }
        }

        CommonUtils.print2DArray(cosineTransform);
    }

    private double getBasis(int w)
    {
        return (w == 0) ? (1/Math.sqrt(2)) : 1;
    }
}
