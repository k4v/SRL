package org.k4rthik.srl.dom.beans;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    private Map<String, Arg> argMap;

    public String getId()
    {
        return id;
    }

    @XmlAttribute(name = "id")
    public void setId(String id)
    {
        this.id = id;
    }

    public boolean isVisible()
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

        this.argMap = new HashMap<String, Arg>(args.size());
        for(Arg arg : args)
        {
            this.argMap.put(arg.getValue(), arg);
        }
    }

    public boolean containsPoint(String pointId)
    {
        return !(args == null || args.size() == 0)                  // Contains non-null list of points
                && (argMap.containsKey(pointId)                     // Contains arg with pointId as key
                && argMap.get(pointId).getValue().equals("point")); // Value of arg is of type "point"

    }
}
