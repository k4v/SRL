package org.k4rthik.srl.dom.beans;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * Author: Karthik
 * Date  : 7/9/2014.
 */
public class Point
{
    private String id;
    private String time;
    private float x;
    private float y;

    public String getId()
    {
        return id;
    }

    @XmlAttribute(name = "id")
    public void setId(String id)
    {
        this.id = id;
    }

    public String getTime()
    {
        return time;
    }

    @XmlAttribute(name= "time")
    public void setTime(String time)
    {
        this.time = time;
    }

    public float getX()
    {
        return x;
    }

    @XmlAttribute(name = "x")
    public void setX(float x)
    {
        this.x = x;
    }

    public float getY()
    {
        return y;
    }

    @XmlAttribute(name = "y")
    public void setY(float y)
    {
        this.y = y;
    }
}
