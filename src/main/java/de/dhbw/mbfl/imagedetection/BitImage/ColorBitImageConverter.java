package de.dhbw.mbfl.imagedetection.BitImage;

import de.dhbw.mbfl.imagedetection.platformIndependence.AbstractColor;
import de.dhbw.mbfl.imagedetection.ImageUtils;

/**
 * Created by florian on 20.02.15.
 */
public class ColorBitImageConverter implements BitImageConverter {
    private AbstractColor[] colors;
    private double tolerance;

    public ColorBitImageConverter(AbstractColor[] colors, double tolerance) {
        this.colors = colors;
        this.tolerance = tolerance;
    }

    @Override
    public boolean isPixelSet(AbstractColor c) {
        for (AbstractColor color : this.colors) {
            if (ImageUtils.computeColorDistance(c, color) <= this.tolerance) {
                return true;
            }
        }

        return false;
    }
}
