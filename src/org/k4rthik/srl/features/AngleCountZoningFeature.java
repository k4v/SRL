package org.k4rthik.srl.features;

import com.sun.istack.internal.Nullable;
import org.k4rthik.srl.common.CommonUtils;
import org.k4rthik.srl.dom.beans.Arg;
import org.k4rthik.srl.dom.beans.Point;
import org.k4rthik.srl.dom.beans.Sketch;
import org.k4rthik.srl.dom.beans.Stroke;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Author: Karthik
 * Date  : 7/24/2014.
 */
@SuppressWarnings("unused")
public class AngleCountZoningFeature implements IFeature
{
    Dimension gridSize = new Dimension(5, 5);

    // Feature matrix (angles: 0, 45, 90, 135)
    int[][] angleCounts= null;

    public AngleCountZoningFeature() { }

    public AngleCountZoningFeature(Dimension gridSize)
    {
        this.gridSize = gridSize;
    }

    @Override
    public void computeFeature(@Nullable BufferedImage sketchImage, @Nullable Sketch parsedSketch)
    {
        System.out.println("Extracting Dark Level Zoning feature from " + parsedSketch.getFileName());

        // For each zone, store counts for each angle
        angleCounts = new int[gridSize.width*gridSize.height][];
        double errorInterval = Math.PI/8;
        for(int i=0; i<angleCounts.length; i++)
        {
            angleCounts[i] = new int[(int)(Math.PI/errorInterval)/2];
            for(int j=0; j<angleCounts[i].length; j++)
                angleCounts[i][j] = 0;
        }
        // Height and width of each zone
        double zoneWidth = sketchImage.getWidth()/gridSize.getWidth();
        double zoneHeight= sketchImage.getHeight()/gridSize.getHeight();

        float[] xyBounds = parsedSketch.getXYBounds();

        for(Stroke stroke : parsedSketch.getStrokes())
        {
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
                   angleZone = (int)((strokeAngle+errorInterval)/errorInterval)/2;
                }

                angleCounts[zoneA][angleZone] += 1;
                if(zoneA != zoneB)
                    angleCounts[zoneB][angleZone] += 1;
            }
        }

        CommonUtils.print2DArray(angleCounts);
    }
}
