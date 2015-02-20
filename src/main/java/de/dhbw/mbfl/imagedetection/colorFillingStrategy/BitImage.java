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
    private int origImageWidth;

    public BitImage(BufferedImage image, BitImageConverter converter) {
        this.pixels = new BitSet(image.getWidth() * image.getWidth());
        this.convertFromBufferedImage(image, converter);
    }

    public boolean getPixel(Point p) {
        int i = this.calculatePositionInBitSet(p);
        this.pixels.get(i);
    }

    public void setPixel(Point p, boolean state) {
        int i = this.calculatePositionInBitSet(p);
        this.pixels.set(i, state);
    }

    private int calculatePositionInBitSet(Point p) {
        return p.x + p.y * this.origImageWidth;
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

                this.setPixel(p, value);

                cache.put(p, value);
            }
        }
    }
}
