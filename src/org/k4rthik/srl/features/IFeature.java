package org.k4rthik.srl.features;

import com.sun.istack.internal.Nullable;
import org.k4rthik.srl.dom.beans.Sketch;
import weka.core.FastVector;
import weka.core.Instance;

import java.awt.image.BufferedImage;
import java.util.Map;

/**
 * Author: Karthik
 * Date  : 7/23/2014.
 */
public abstract class IFeature
{
    // Prefix for attribute names from this feature extractor
    String ATTRIBUTE_NAME_PREFIX = null;

    public abstract void computeFeature(@Nullable BufferedImage sketchImage, @Nullable Sketch parsedSketch);
    public abstract void setAttributes(FastVector attributeList);
    public abstract void setAttributeValues(FastVector attributeList, Map<String, Integer> attributeNameMap, Instance thisInstance);
}
