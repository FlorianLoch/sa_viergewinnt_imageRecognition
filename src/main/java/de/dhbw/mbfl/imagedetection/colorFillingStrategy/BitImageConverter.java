package de.dhbw.mbfl.imagedetection.colorFillingStrategy;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by florian on 20.02.15.
 */
public interface BitImageConverter {

    /**
     * Determines whether a pixel should be represented as "set" or "not set" in the BitImage
     * The BitImage instance does caching - so this method might not get called for every pixel
     * @param image
     * @param p
     * @return
     */
    public boolean isPixelSet(BufferedImage image, Point p);

}
