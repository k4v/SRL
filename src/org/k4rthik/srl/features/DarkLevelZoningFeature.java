package org.k4rthik.srl.features;

import com.sun.istack.internal.Nullable;
import org.k4rthik.srl.common.CommonUtils;
import org.k4rthik.srl.dom.beans.Sketch;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

/**
 * Author: Karthik
 * Date  : 7/24/2014.
 */
@SuppressWarnings("unused")
public class DarkLevelZoningFeature implements IFeature
{
    Dimension gridSize = new Dimension(5, 5);

    // Feature matrix
    double[][] darkLevels = null;

    public DarkLevelZoningFeature() { }

    public DarkLevelZoningFeature(Dimension gridSize)
    {
        this.gridSize = gridSize;
    }

    @Override
    public void computeFeature(@Nullable BufferedImage sketchImage, @Nullable Sketch parsedSketch)
    {
        System.out.println("Extracting Dark Level Zoning feature from "+parsedSketch.getFileName());

        int[][] bwImage = CommonUtils.getBinaryArray_BinaryImage(sketchImage);
        double[][] darkLevels = new double[gridSize.height][];

        CommonUtils.print2DArray(bwImage);

        for(int y=0; y<gridSize.height; y++)
        {
            darkLevels[y] = new double[gridSize.width];
            for(int x=0; x<gridSize.width; x++)
            {
                darkLevels[y][x] = getDarkRatio(bwImage,
                        (x*bwImage[0].length)/gridSize.width, (((1+x)*bwImage[0].length)/gridSize.width) - 1,
                        (y*bwImage.length)/gridSize.height, (((1+y)*bwImage.length)/gridSize.height) - 1);
            }
        }

        CommonUtils.print2DArray(darkLevels);
    }

    private double getDarkRatio(int[][] imagePixels, int startX, int endX, int startY, int endY)
    {
        int whiteCount = 0;
        int blackCount = 0;

        for(int y=startY; y<=endY; y++)
        {
            for(int x=startX; x<=endX; x++)
            {
                if(imagePixels[y][x] == 0)
                    whiteCount++;
                else
                    blackCount++;
            }
        }

        return ((double)blackCount)/whiteCount;
    }
}
