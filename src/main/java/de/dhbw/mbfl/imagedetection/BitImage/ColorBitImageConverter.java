package de.dhbw.mbfl.imagedetection.BitImage;

import de.dhbw.mbfl.imagedetection.ImageUtils;

import java.awt.*;

/**
 * Created by florian on 20.02.15.
 */
public class ColorBitImageConverter implements BitImageConverter {
    private Color[] colors;
    private double tolerance;

    public ColorBitImageConverter(Color[] colors, double tolerance) {
        this.colors = colors;
        this.tolerance = tolerance;
    }

    @Override
    public boolean isPixelSet(Color c) {
        for (Color color : this.colors) {
            if (ImageUtils.computeColorDistance(c, color) <= this.tolerance) {
                return true;
            }
        }

        return false;
    }
}
