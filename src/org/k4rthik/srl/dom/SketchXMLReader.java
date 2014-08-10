package org.k4rthik.srl.dom;

import org.k4rthik.srl.dom.beans.Arg;
import org.k4rthik.srl.dom.beans.Point;
import org.k4rthik.srl.dom.beans.Sketch;
import org.k4rthik.srl.dom.beans.Stroke;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Author: Karthik
 * Date  : 7/9/2014.
 */
public class SketchXMLReader
{
    public Sketch xmlToSketch(File xmlFile) throws IOException
    {
        Sketch sketchObject = null;
        try
        {
            JAXBContext jaxbContext = JAXBContext.newInstance(Sketch.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            sketchObject = (Sketch)(jaxbUnmarshaller.unmarshal(xmlFile));
            sketchObject.setFileName(xmlFile.toString());
        } catch (Exception e)
        {
            throw new IOException(e);
        }

        return sketchObject;
    }

    // Draw 2D image using all points in sketch
    public BufferedImage drawImage(Sketch sketchObject)
    {
        List<Point> pointList = sketchObject.getPoints();
        float maxX = 0f, minX = -1.0f, maxY = 0f, minY = -1.0f;
        if(pointList != null)
        {
            for (Point charPoint : pointList)
            {
                maxX = maxX < charPoint.getX() ? charPoint.getX() : maxX;
                minX = ((minX < 0) || (minX > charPoint.getX())) ? charPoint.getX() : minX;
                maxY = maxY < charPoint.getY() ? charPoint.getY() : maxY;
                minY = ((minY < 0) || (minY > charPoint.getY())) ? charPoint.getY() : minY;
            }
        }

        BufferedImage drawImage = new BufferedImage((int)(maxX - minX),
                (int)(maxY - minY),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = drawImage.createGraphics();

        graphics.setPaint(new Color(255, 255, 255));
        graphics.fillRect(0, 0, (int)(maxX - minX), (int)(maxY - minY));

        // Set drawing color to black
        graphics.setColor(new Color(255 << 24));

        // Draw character image from stroke information
        List<Stroke> strokeList = sketchObject.getStrokes();
        if(strokeList != null)
        {
            for (Stroke stroke : strokeList)
            {
                if (stroke.isVisible() && (stroke.getArgs() != null))
                {
                    List<Arg> args = stroke.getArgs();
                    for (int i = 0; i < args.size() - 1; i++)
                    {
                        Point pointA = sketchObject.getPointById(args.get(i).getValue());
                        Point pointB = sketchObject.getPointById(args.get(i + 1).getValue());

                        graphics.drawLine(
                                (int) (pointA.getX() - minX),
                                (int) (pointA.getY() - minY),
                                (int) (pointB.getX() - minX),
                                (int) (pointB.getY() - minY));
                    }
                }
            }
        }
        return drawImage;
    }
}
