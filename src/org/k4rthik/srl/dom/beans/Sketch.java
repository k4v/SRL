package org.k4rthik.srl.dom.beans;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: Karthik
 * Date  : 7/9/2014.
 */
@SuppressWarnings("unused")
@XmlRootElement(name = "sketch")
public class Sketch
{
    // Name of the XML file frp, which this object was parsed.
    // Set manually after unmarshalling
    private String fileName;

    private String id;
    private String type;

    private ArrayList<Point> points;
    private Map<String, Point> pointMap = new HashMap<String, Point>();

    private ArrayList<Stroke> strokes;
    private Map<String, Stroke> strokeMap = new HashMap<String, Stroke>();

    public String getId()
    {
        return id;
    }

    @XmlAttribute(name = "id")
    public void setId(String id)
    {
        this.id = id;
    }

    public String getType()
    {
        return type;
    }

    @XmlAttribute(name = "type")
    public void setType(String type)
    {
        this.type = type;
    }

    public ArrayList<Point> getPoints()
    {
        return points;
    }

    @XmlElement(name = "point")
    public void setPoints(ArrayList<Point> points)
    {
        this.points = points;
        for(Point point : points)
        {
            pointMap.put(point.getId(), point);
        }
    }

    public ArrayList<Stroke> getStrokes()
    {
        return strokes;
    }

    @XmlElement(name = "stroke")
    public void setStrokes(ArrayList<Stroke> strokes)
    {
        this.strokes = strokes;
        for(Stroke stroke : strokes)
        {
            strokeMap.put(stroke.getId(), stroke);
        }
    }

    public Point getPointById(String id)
    {
        return pointMap.get(id);
    }

    public Stroke getStrokeById(String id)
    {
        return strokeMap.get(id);
    }

    public String getFileName()
    {
        return this.fileName;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    @Override
    public String toString()
    {
        StringBuilder sketchBuilder = new StringBuilder();

        sketchBuilder.append("<sketch id=\"").append(id).append("\" type=\"").append(type).append("\">\n");

        if(points != null)
        {
            for (Point point : points)
            {
                sketchBuilder.append("\t<point id=\"").append(point.getId())
                        .append("\" time=\"").append(point.getTime())
                        .append("\" x=\"").append(point.getX()).append("\" y=\"").append(point.getY()).append("\"/>\n");
            }
        }

        if(strokes != null)
        {
            for (Stroke stroke : strokes)
            {
                sketchBuilder.append("\t<stroke id=\"").append(stroke.getId())
                        .append("\" visible=\"").append(stroke.isVisible()).append("\">\n");

                for (Arg arg : stroke.getArgs())
                {
                    sketchBuilder.append("\t\t<arg type=\"")
                            .append(arg.getType()).append("\">").append(arg.getValue()).append("</arg>\n");
                }

                sketchBuilder.append("\t</stroke>\n");
            }
        }

        sketchBuilder.append("</sketch>\n");

        return sketchBuilder.toString();
    }
}
