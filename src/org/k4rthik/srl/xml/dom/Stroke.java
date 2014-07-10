package org.k4rthik.srl.xml.dom;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;

/**
 * Author: Karthik
 * Date  : 7/9/2014.
 */
@SuppressWarnings("unused")
public class Stroke
{
    private String id;
    private boolean visible;
    private ArrayList<Arg> args;

    public String getId()
    {
        return id;
    }

    @XmlAttribute(name = "id")
    public void setId(String id)
    {
        this.id = id;
    }

    public boolean getVisible()
    {
        return visible;
    }

    @XmlAttribute(name = "visible")
    public void setVisible(boolean visible)
    {
        this.visible = visible;
    }

    public ArrayList<Arg> getArgs()
    {
        return args;
    }

    @XmlElement(name = "arg")
    public void setArgs(ArrayList<Arg> args)
    {
        this.args = args;
    }
}
