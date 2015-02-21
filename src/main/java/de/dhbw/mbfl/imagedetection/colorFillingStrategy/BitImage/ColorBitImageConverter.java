package de.dhbw.mbfl.imagedetection.colorFillingStrategy.BitImage;

import java.awt.*;

/**
 * Created by florian on 20.02.15.
 */
public class ColorBitImageConverter implements BitImageConverter {
    private Color color;

    public ColorBitImageConverter(Color color) {
        this.color = color;
    }

    @Override
    public boolean isPixelSet(Color c) {
        return (c.equals(this.color));
    }
}
