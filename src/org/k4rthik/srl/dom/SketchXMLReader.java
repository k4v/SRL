package org.k4rthik.srl.dom;

import org.k4rthik.srl.dom.beans.Sketch;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;

/**
 * Author: Karthik
 * Date  : 7/9/2014.
 */
public class SketchXMLReader
{
    Sketch xmlObject = null;

    public Sketch loadXML(File xmlFile) throws IOException
    {
        try
        {
            JAXBContext jaxbContext = JAXBContext.newInstance(Sketch.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            xmlObject = (Sketch)(jaxbUnmarshaller.unmarshal(xmlFile));

            return xmlObject;
        } catch (Exception e)
        {
            throw new IOException(e);
        }
    }

    public Sketch getXmlObject()
    {
        return xmlObject;
    }
}
