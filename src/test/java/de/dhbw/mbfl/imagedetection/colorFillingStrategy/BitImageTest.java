package de.dhbw.mbfl.imagedetection.colorFillingStrategy;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.util.HashSet;

import static org.junit.Assert.*;

import de.dhbw.mbfl.imagedetection.colorFillingStrategy.BitImage.BitImage;
import de.dhbw.mbfl.imagedetection.colorFillingStrategy.BitImage.BitImageConverter;
import org.junit.Test;

public class BitImageTest {

    @Test
    public void testSimpleBitImage() {
        BufferedImage img = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);

        BitImage instance = new BitImage(img, new BitImageConverter() {
            @Override
            public boolean isPixelSet(Color c) {
                return false;
            }
        });

        instance.setPixel(new Point(2, 8), true);
        assertTrue(instance.getPixel(new Point(2, 8)));
        assertFalse(instance.getPixel(new Point(2, 7)));
    }

    @Test
    public void testCachingMechanism() {
        BufferedImage img = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        img.setRGB(0, 0, new Color(255, 0, 0).getRGB());
        img.setRGB(0, 1, new Color(0, 255, 0).getRGB());
        img.setRGB(0, 2, new Color(255, 0, 0).getRGB());

        final MutableInteger counter = new MutableInteger();
        BitImage instance = new BitImage(img, new BitImageConverter() {
            @Override
            public boolean isPixelSet(Color c) {
                counter.value++;
                return false;
            }
        });

        assertEquals(3, counter.value);
    }

    @Test
    public void testConversionCall() {
        BufferedImage img = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        img.setRGB(9, 9, new Color(0, 255, 0).getRGB());

        BitImage instance = new BitImage(img, new BitImageConverter() {
            @Override
            public boolean isPixelSet(Color c) {
                if (c.equals(Color.GREEN)) return true;
                return false;
            }
        });

        assertTrue(instance.getPixel(new Point(9, 9)));
    }

    @Test
    public void testGetAllSetPixels() {
        BufferedImage img = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        int blue = new Color(0, 0, 255).getRGB();
        img.setRGB(0, 0, blue);
        img.setRGB(9, 9, blue);
        img.setRGB(4, 4, blue);

        //Determine all blue pixels as "set", afterwards there should be 3 pixels set
        BitImage instance = new BitImage(img, new BitImageConverter() {
            @Override
            public boolean isPixelSet(Color c) {
                if (c.equals(Color.BLUE)) return true;
                return false;
            }
        });

        HashSet<Point> setPixels = instance.getAllSetPixels();
        assertEquals(3, setPixels.size());
        assertTrue(instance.getPixel(new Point(4, 4)));
        assertTrue(setPixels.contains(new Point(0, 0)));
    }

    //TODO Write tests for dilate() and erode()


    private class MutableInteger {
        public int value = 0;
    }
}