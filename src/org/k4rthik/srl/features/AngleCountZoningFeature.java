package org.k4rthik.srl.features;

import com.sun.istack.internal.Nullable;
import org.k4rthik.srl.dom.beans.Arg;
import org.k4rthik.srl.dom.beans.Point;
import org.k4rthik.srl.dom.beans.Sketch;
import org.k4rthik.srl.dom.beans.Stroke;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Map;

/**
 * Author: Karthik
 * Date  : 7/24/2014.
 */
@SuppressWarnings("unused")
public class AngleCountZoningFeature extends IFeature
{
    Dimension gridSize = new Dimension(5, 5);

    // Feature matrix (angles: 0, 45, 90, 135)
    double[][] angleCounts= null;

    // 2*errorInterval is the snap value of the angles
    double errorInterval = Math.PI/8;

    public AngleCountZoningFeature()
    {
        ATTRIBUTE_NAME_PREFIX = "ZONE_ANGLE_";

        // For each zone, store counts for each angle
        angleCounts = new double[gridSize.height*gridSize.width][(int)(Math.PI/errorInterval)/2];
        for(int i=0; i<angleCounts.length; i++)
        {
            for(int j=0; j<angleCounts[i].length; j++)
                angleCounts[i][j] = 0;
        }
    }

    public AngleCountZoningFeature(Dimension gridSize)
    {
        this.gridSize = gridSize;

        ATTRIBUTE_NAME_PREFIX = "ZONE_ANGLE_";

        // For each zone, store counts for each angle
        angleCounts = new double[gridSize.height*gridSize.width][(int)(Math.PI/errorInterval)/2];
        for(int i=0; i<angleCounts.length; i++)
        {
            for(int j=0; j<angleCounts[i].length; j++)
                angleCounts[i][j] = 0;
        }
    }

    @Override
    public void computeFeature(@Nullable BufferedImage sketchImage, @Nullable Sketch parsedSketch)
    {
        System.out.println("Extracting Angle Count Zoning feature from " + parsedSketch.getFileName());

        // Height and width of each zone
        double zoneWidth = sketchImage.getWidth()/gridSize.getWidth();
        double zoneHeight= sketchImage.getHeight()/gridSize.getHeight();

        float[] xyBounds = parsedSketch.getXYBounds();

        for(Stroke stroke : parsedSketch.getStrokes())
        {
            if(stroke.getArgs() == null)
                continue;

            ArrayList<Arg> argList = stroke.getArgs();
            for(int i=0; i<argList.size()-1; i++)
            {
                Point pointA = parsedSketch.getPointById(argList.get(i)  .getValue());
                Point pointB = parsedSketch.getPointById(argList.get(i+1).getValue());

                // Compute zone information for the point
                int zoneX_A = Math.min((int) ((pointA.getX() - xyBounds[0]) / zoneWidth), gridSize.width - 1);
                int zoneY_A = Math.min((int)((pointA.getY() - xyBounds[2])/zoneHeight), gridSize.height - 1);
                int zoneA   = (gridSize.width*zoneY_A) + zoneX_A;

                int zoneX_B = Math.min((int) ((pointB.getX() - xyBounds[0]) / zoneWidth), gridSize.width - 1);
                int zoneY_B = Math.min((int) ((pointA.getY() - xyBounds[2]) / zoneHeight), gridSize.height - 1);
                int zoneB   = (gridSize.width*zoneY_B) + zoneX_B;

                // Stroke angle in radians
                double strokeAngle = Math.atan(
                        (double)((pointA.getY() - pointB.getY())/
                                (pointA.getX() - pointB.getX())));

                if(strokeAngle < 0)
                    strokeAngle += Math.PI;

                int angleZone;
                if((strokeAngle < errorInterval) || (strokeAngle >= (Math.PI - errorInterval)))
                    angleZone = 0;
                else
                {
                   angleZone = (int)(((strokeAngle+errorInterval)/errorInterval)/2);
                }

                angleCounts[zoneA][angleZone] += 1;
                if(zoneA != zoneB)
                    angleCounts[zoneB][angleZone] += 1;
            }
        }

        for(int i=0; i<angleCounts.length; i++)
        {
            int countSum = 0;
            for(int j=0; j<angleCounts[i].length; j++)
            {
                countSum += angleCounts[i][j];
            }
            if(countSum != 0)
            {
                for (int j = 0; j < angleCounts[i].length; j++)
                {
                    angleCounts[i][j] /= countSum;
                }
            }
        }
    }

    @Override
    public void setAttributes(FastVector attributeList)
    {
        for(int i=0; i<angleCounts.length; i++)
        {
            for(int j=0;j<angleCounts[i].length; j++)
            {
                attributeList.addElement(new Attribute(ATTRIBUTE_NAME_PREFIX+i+"_"+j, attributeList.size()));
            }
        }
    }

    public void setAttributeValues(FastVector attributeList, Map<String, Integer> attributeNameMap, Instance thisInstance)
    {
        for(int i=0; i<angleCounts.length; i++)
        {
            for(int j=0; j<angleCounts[i].length; j++)
            {
                thisInstance.setValue(
                        (Attribute)attributeList.elementAt(attributeNameMap.get(ATTRIBUTE_NAME_PREFIX+i+"_"+j)),
                        angleCounts[i][j]);
            }
        }
    }
}
