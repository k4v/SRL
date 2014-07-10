package org.k4rthik.srl.xml.dom;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

/**
 * Author: Karthik
 * Date  : 7/9/2014.
 */
@SuppressWarnings("unused")
public class Arg
{
    private String type;
    private String value;

    public String getType()
    {
        return type;
    }

    @XmlAttribute(name = "type")
    public void setType(String type)
    {
        this.type = type;
    }

    public String getValue()
    {
        return value;
    }

    @XmlValue
    public void setValue(String value)
    {
        this.value = value;
    }
}
