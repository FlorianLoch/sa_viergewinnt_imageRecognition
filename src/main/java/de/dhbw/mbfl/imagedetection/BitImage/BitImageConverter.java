package de.dhbw.mbfl.imagedetection.BitImage;

import de.dhbw.mbfl.imagedetection.platformIndependence.AbstractColor;

/**
 * Created by florian on 20.02.15.
 */
public interface BitImageConverter {

    /**
     * Determines whether a pixel should be represented as "set" or "not set" in the BitImage
     * The BitImage instance does caching - so this method might not get called for every pixel
     * @param c
     * @return
     */
    public boolean isPixelSet(AbstractColor c);

}
