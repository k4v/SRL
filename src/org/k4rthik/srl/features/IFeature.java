package org.k4rthik.srl.features;

import com.sun.istack.internal.Nullable;
import org.k4rthik.srl.dom.beans.Sketch;

import java.awt.image.BufferedImage;

/**
 * Author: Karthik
 * Date  : 7/23/2014.
 */
public interface IFeature
{
    public void extractFeature(@Nullable BufferedImage sketchImage, @Nullable Sketch parsedSketch);
}
