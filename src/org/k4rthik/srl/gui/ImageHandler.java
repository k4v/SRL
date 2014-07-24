package org.k4rthik.srl.gui;

import org.k4rthik.srl.dom.beans.Arg;
import org.k4rthik.srl.dom.beans.Point;
import org.k4rthik.srl.dom.beans.Sketch;
import org.k4rthik.srl.dom.beans.Stroke;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Author: Karthik
 * Date  : 7/10/2014.
 */
public class ImageHandler
{
    // Draw 2D image using points in sketch
    public static BufferedImage drawImage(Sketch fromSketch)
    {
        List<Point> pointList = fromSketch.getPoints();
        float maxX = 0f, minX = -1.0f, maxY = 0f, minY = -1.0f;
        for (Point charPoint : pointList)
        {
            maxX = maxX < charPoint.getX() ? charPoint.getX() : maxX;
            minX = ((minX < 0) || (minX > charPoint.getX())) ? charPoint.getX() : minX;
            maxY = maxY < charPoint.getY() ? charPoint.getY() : maxY;
            minY = ((minY < 0) || (minY > charPoint.getY())) ? charPoint.getY() : minY;
        }

        int paddingSize = 5;
        BufferedImage drawImage = new BufferedImage((2 * paddingSize) + (int)(maxX - minX),
                                                    (2 * paddingSize) + (int)(maxY - minY),
                                                    BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = drawImage.createGraphics();

        graphics.setPaint(new Color(255, 255, 255));
        graphics.fillRect(0, 0, drawImage.getWidth(), drawImage.getHeight());

        // Set drawing color to black
        graphics.setColor(new Color(255 << 24));

        // Draw character image from stroke information
        List<Stroke> strokeList = fromSketch.getStrokes();
        for (Stroke stroke : strokeList)
        {
            if(stroke.isVisible() && (stroke.getArgs() != null))
            {
                List<Arg> args = stroke.getArgs();
                for(int i=0; i<args.size()-1; i++)
                {
                    Point pointA = fromSketch.getPointById(args.get(i)  .getValue());
                    Point pointB = fromSketch.getPointById(args.get(i+1).getValue());

                    graphics.drawLine((int)(paddingSize + pointA.getX() - minX),
                                    (int)(paddingSize + pointA.getY() - minY),
                                    (int)(paddingSize + pointB.getX() - minX),
                                    (int)(paddingSize + pointB.getY() - minY));
                }
            }
        }
        return drawImage;
    }
}
