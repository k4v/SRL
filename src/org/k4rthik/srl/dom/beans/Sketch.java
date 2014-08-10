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

    private float[] xyBounds = new float[4];

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

        float minX = -1f, maxX = 0f;
        float minY = -1f, maxY = 0f;

        for(Point point : points)
        {
            minX = ((minX < 0) || (minX > point.getX())) ? point.getX() : minX;
            maxX = (maxX < point.getX()) ? point.getX() : maxX;
            minY = ((minY < 0) || (minY > point.getY())) ? point.getY() : minY;
            maxY = (maxY < point.getY()) ? point.getY() : maxY;
        }

        xyBounds = new float[]{minX, maxX, minY, maxY};
    }

    public int getPointCount()
    {
        return (points == null) ? 0 : points.size();
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

    public int getStrokeCount()
    {
        return (strokes == null) ? 0 : strokes.size();
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

    public float[] getXYBounds()
    {
        return xyBounds;
    }

    public boolean isPointInStrokes(int startStrokeIndex, int endStrokeIndex, String pointId)
    {
        int strokeCount = strokes.size();

        // Fixing possible bad values of given indices
        if((startStrokeIndex < 0) || (startStrokeIndex >= strokeCount))
        {
            startStrokeIndex = 0;
        }

        if((endStrokeIndex < 0) || (endStrokeIndex < startStrokeIndex) || (endStrokeIndex >= strokeCount))
        {
            endStrokeIndex = strokeCount - 1;
        }

        for(Stroke stroke : strokes.subList(startStrokeIndex, endStrokeIndex))
        {
            if(stroke.containsPoint(pointId))
                return true;
        }

        return false;
    }

    /**
     * Get a sketch object containing all the points in the original sketch
     * but a subset of strokes defined by start and end indices
     */
    public Sketch getSubsketch(int startStrokeIndex, int endStrokeIndex)
    {
        Sketch subSketch = new Sketch();
        subSketch.setPoints(this.getPoints());

        int strokeCount = strokes.size();

        // Fixing possible bad values of given indices
        if((startStrokeIndex < 0) || (startStrokeIndex >= strokeCount)
                || (endStrokeIndex < 0) || (endStrokeIndex < startStrokeIndex) || (endStrokeIndex >= strokeCount))
        {
            return null;
        }

        if(strokes != null)
        {
            ArrayList<Stroke> strokeSublist = new ArrayList<Stroke>(strokes.subList(startStrokeIndex, endStrokeIndex));
            subSketch.setStrokes(strokeSublist);
        }

        subSketch.setId(this.id);
        subSketch.setType(this.id);
        subSketch.setFileName(this.fileName);

        return subSketch;
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
