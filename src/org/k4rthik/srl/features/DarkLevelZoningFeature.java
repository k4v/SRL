package org.k4rthik.srl.features;

import com.sun.istack.internal.Nullable;
import org.k4rthik.srl.common.CommonUtils;
import org.k4rthik.srl.dom.beans.Sketch;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.Map;

/**
 * Author: Karthik
 * Date  : 7/24/2014.
 */
@SuppressWarnings("unused")
public class DarkLevelZoningFeature  extends IFeature
{
    Dimension gridSize = new Dimension(5, 5);

    // Feature matrix
    double[][] darkLevels = null;

    public DarkLevelZoningFeature()
    {
        ATTRIBUTE_NAME_PREFIX = "DARK_LEVEL_";
        darkLevels = new double[gridSize.height][gridSize.width];
    }

    public DarkLevelZoningFeature(Dimension gridSize)
    {
        ATTRIBUTE_NAME_PREFIX = "DARK_LEVEL_";
        this.gridSize = gridSize;
        darkLevels = new double[gridSize.height][gridSize.width];
    }

    @Override
    public void computeFeature(@Nullable BufferedImage sketchImage, @Nullable Sketch parsedSketch)
    {
        System.out.println("Extracting Dark Level Zoning feature from "+parsedSketch.getFileName());

        int[][] bwImage = CommonUtils.getBinaryArray_BinaryImage(sketchImage);

        for(int y=0; y<gridSize.height; y++)
        {
            for(int x=0; x<gridSize.width; x++)
            {
                darkLevels[y][x] = getDarkRatio(bwImage,
                        (x*bwImage[0].length)/gridSize.width, (((1+x)*bwImage[0].length)/gridSize.width) - 1,
                        (y*bwImage.length)/gridSize.height, (((1+y)*bwImage.length)/gridSize.height) - 1);
            }
        }
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

        return ((double)blackCount)/(blackCount + whiteCount);
    }

    @Override
    public void setAttributes(FastVector attributeList)
    {
        for(int i=0; i<gridSize.getHeight(); i++)
        {
            for(int j=0; j<gridSize.getWidth(); j++)
            {
                attributeList.addElement(new Attribute(ATTRIBUTE_NAME_PREFIX+i+"_"+j, attributeList.size()));
            }
        }
    }

    @Override
    public void setAttributeValues(FastVector attributeList, Map<String, Integer> attributeNameMap, Instance thisInstance)
    {
        for(int i=0; i<darkLevels.length; i++)
        {
            for(int j=0; j<darkLevels[i].length; j++)
            {
                thisInstance.setValue(
                        (Attribute)attributeList.elementAt(attributeNameMap.get(ATTRIBUTE_NAME_PREFIX+i+"_"+j)),
                        darkLevels[i][j]);
            }
        }
    }
}
