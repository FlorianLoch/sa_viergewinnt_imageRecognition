package de.dhbw.mbfl.imagedetection.colorFillingStrategy;

import sun.misc.Cache;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.BitSet;
import java.util.HashMap;

/**
 * Created by florian on 20.02.15.
 */
public class BitImage {
    private BitSet pixels;

    public BitImage(BufferedImage image, BitImageConverter converter) {
        this.pixels = new BitSet(image.getWidth() * image.getWidth());

    }

    private boolean getPixel(Point p) {

    }

    private void setPixel(Point p, boolean state) {

    }

    private void convertFromBufferedImage(BufferedImage image, BitImageConverter converter) {
        HashMap<Point, Boolean> cache = new HashMap<Point, Boolean>();

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Point p = new Point(x, y);

                Boolean value = cache.get(p);
                if (value == null) {
                    value = new Boolean(converter.isPixelSet(image, p));
                }



                cache.put(p, value);
            }
        }
    }
}
