package org.k4rthik.srl.dom.beans;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

        xyBounds = computeXYBounds(this.points);
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

    private float[] computeXYBounds(List<Point> points)
    {
        float minX = -1f, maxX = 0f;
        float minY = -1f, maxY = 0f;

        if(strokes == null)
            return new float[]{minX, maxX, minY, maxY};

        for(Stroke stroke : strokes)
        {
            if(stroke.getArgs() == null)
                continue;
            for(Arg arg : stroke.getArgs())
            {
                if(arg.getType().equals("point"))
                {
                    Point point = getPointById(arg.getValue());
                    minX = ((minX < 0) || (minX > point.getX())) ? point.getX() : minX;
                    maxX = (maxX < point.getX()) ? point.getX() : maxX;
                    minY = ((minY < 0) || (minY > point.getY())) ? point.getY() : minY;
                    maxY = (maxY < point.getY()) ? point.getY() : maxY;
                }
            }
        }

        return new float[]{minX, maxX, minY, maxY};
    }

    /**
     * Get a sketch object containing all the points in the original sketch
     * but a subset of strokes defined by start and end indices
     */
    public Sketch getSubsketch(int startStrokeIndex, int endStrokeIndex)
    {
        Sketch subSketch = new Sketch();
        int strokeCount = strokes.size();

        // Fixing possible bad values of given indices
        if((startStrokeIndex < 0) || (startStrokeIndex >= strokeCount)
                || (endStrokeIndex < 0) || (endStrokeIndex < startStrokeIndex) || (endStrokeIndex >= strokeCount))
        {
            return null;
        }

        if(strokes != null)
        {
            // Set subset of strokes for new sketch
            ArrayList<Stroke> strokeSublist = new ArrayList<Stroke>(strokes.subList(startStrokeIndex, endStrokeIndex + 1));
            subSketch.setStrokes(strokeSublist);

            // Set subset of points for new sketch
            ArrayList<Point> pointList = new ArrayList<Point>();
            for(Stroke stroke : strokeSublist)
            {
                if(stroke.getArgs() == null)
                {
                    continue;
                }

                for(Arg arg : stroke.getArgs())
                {
                    if(arg.getType().equals("point"))
                    {
                        pointList.add(getPointById(arg.getValue()));
                    }
                }
            }

            subSketch.setPoints(pointList);
        }

        subSketch.setId(this.id);
        subSketch.setType(this.type);

        // Set file name of sketch as a modification of original sketch's file name
        String fileNameNoExt = this.fileName.substring(0, this.fileName.lastIndexOf("."));
        subSketch.setFileName(fileNameNoExt+"_"+startStrokeIndex+"_"+endStrokeIndex
                +this.fileName.substring(this.fileName.lastIndexOf(".")));

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
