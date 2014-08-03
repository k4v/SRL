package org.k4rthik.srl.features;

import com.sun.istack.internal.Nullable;
import org.k4rthik.srl.common.CommonUtils;
import org.k4rthik.srl.dom.beans.Sketch;

import java.awt.image.BufferedImage;

/**
 * Author: Karthik
 * Date  : 7/31/2014.
 */

@SuppressWarnings("unused")
public class GeometricMomentsFeature implements IFeature
{
    int[][] pixelMatrix;

    // General transform invariant moment feature
    double I1, I2, I3, I4;
    double u_00;
    double[] momentFeature = new double[4];

    @Override
    public void computeFeature(@Nullable BufferedImage sketchImage, @Nullable Sketch parsedSketch)
    {
        this.pixelMatrix = CommonUtils.getBinaryArray_BinaryImage(sketchImage);

        u_00 = u_pq(0, 0);

        I1 = (u_pq(2, 0)*u_pq(0, 2)) - (u_pq(1, 1)*u_pq(1, 1));

        I2 = Math.pow((u_pq(3, 0)*u_pq(0, 3)) - (u_pq(2, 1)*u_pq(1, 2)), 2) -
                4*(u_pq(3, 0)*u_pq(1, 2) - Math.pow(u_pq(2, 1), 2))*(u_pq(2, 1)*u_pq(0, 3) - Math.pow(u_pq(1, 2), 2));

        I3 = (u_pq(2, 0)*((u_pq(2, 1)*u_pq(0, 3)) - Math.pow(u_pq(1, 2), 2))) -
                (u_pq(1, 1)*((u_pq(3, 0)*u_pq(0, 3)) - (u_pq(2, 1)*u_pq(1, 2)))) +
                (u_pq(0, 2)*((u_pq(3, 0)*u_pq(1, 2)) - Math.pow(u_pq(2, 1), 2)));

        I4 = Math.pow(u_pq(3, 0), 2)*Math.pow(u_pq(0, 2), 2) -
                ( 6*u_pq(3, 0)*u_pq(2, 1)*u_pq(1, 1)*Math.pow(u_pq(0, 2), 2)) +
                (12*u_pq(3, 0)*u_pq(1, 2)*u_pq(0, 2)*Math.pow(u_pq(1, 1), 2)) -
                ( 6*u_pq(3, 0)*u_pq(1, 2)*u_pq(2, 0)*Math.pow(u_pq(0, 2), 2)) +
                ( 6*u_pq(3, 0)*u_pq(0, 3)*u_pq(2, 0)*u_pq(1, 1)*u_pq(0, 2)) -
                ( 8*u_pq(3, 0)*u_pq(0, 3)*Math.pow(u_pq(1, 1), 3)) +
                ( 9*u_pq(2, 0)*Math.pow(u_pq(2, 1), 2)*Math.pow(u_pq(0, 2), 2)) -
                (18*u_pq(2, 1)*u_pq(1, 2)*u_pq(2, 0)*u_pq(1, 1)*u_pq(0, 2)) +
                (12*u_pq(2, 1)*u_pq(0, 3)*u_pq(2, 0)*Math.pow(u_pq(1, 1), 2)) -
                ( 6*u_pq(2, 1)*u_pq(0, 3)*u_pq(0, 2)*Math.pow(u_pq(2, 0), 2)) -
                ( 9*Math.pow(u_pq(1, 2), 2)*Math.pow(u_pq(2, 0), 2)*u_pq(0, 2)) -
                ( 6*u_pq(1, 2)*u_pq(0, 3)*u_pq(1, 1)*Math.pow(u_pq(2, 0), 2)) +
                (Math.pow(u_pq(0, 3), 2)*Math.pow(u_pq(2, 0), 3));

        momentFeature = new double[]{
                I1/Math.pow(u_00, 4),
                I2/Math.pow(u_00, 10),
                I3/Math.pow(u_00, 7),
                I4/Math.pow(u_00, 11)
        };
    }

    // Regular moments
    private int m_pq(int pOrder, int qOrder)
    {
        int mPQ = 0;

        for(int y=0; y<pixelMatrix.length; y++)
        {
            for(int x=0; x<pixelMatrix[0].length; x++)
            {
                mPQ += pixelMatrix[y][x]*Math.pow(x, pOrder)*Math.pow(y, qOrder);
            }
        }
        return mPQ;
    }

    // Translational invariant moments
    private double u_pq(int pOrder, int qOrder)
    {
        double uPQ = 0;

        double xAvg = ((double)m_pq(1, 0))/u_00;
        double yAvg = ((double)m_pq(0, 1))/u_00;

        for(int y=0; y<pixelMatrix.length; y++)
        {
            for(int x=0; x<pixelMatrix[0].length; x++)
            {
                uPQ += pixelMatrix[y][x]*
                        Math.pow(x-xAvg, pOrder)*
                        Math.pow(y-yAvg, qOrder);
            }
        }
        return uPQ;
    }

    // Scale invariant moments
    private double v_pq(int pOrder, int qOrder)
    {
        double vPQ = 0;

        return u_pq(pOrder, qOrder)/Math.pow(u_00, 1+((pOrder+qOrder)/2));
    }
}
