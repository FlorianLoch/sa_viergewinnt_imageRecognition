package de.dhbw.mbfl.imagedetection.colorFillingStrategy;

import java.awt.*;
import java.awt.image.BufferedImage;

import static org.junit.Assert.*;
import org.junit.Test;

public class BitImageTest {

    @Test
    public void testSimpleBitImage() {
        BufferedImage img = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);

        BitImage instance = new BitImage(img, new BitImageConverter() {
            @Override
            public boolean isPixelSet(BufferedImage image, Point p) {
                return false;
            }
        });

        instance.setPixel(new Point(2, 8), true);
        assertTrue(instance.getPixel(new Point(2, 8)));
        assertFalse(instance.getPixel(new Point(2, 7)));
    }

}