package de.dhbw.mbfl.imagedetection.BitImage;

import de.dhbw.mbfl.imagedetection.platformIndependence.AbstractColor;
import de.dhbw.mbfl.imagedetection.platformIndependence.PortableColor;
import de.dhbw.mbfl.imagedetection.platformIndependence.PortablePoint;
import de.dhbw.mbfl.imagedetection.platformIndependence.PortableRasterImage;
import org.junit.Test;

import java.util.HashSet;

import static org.junit.Assert.*;

public class BitImageTest {

    @Test
    public void testSimpleBitImage() {
        PortableRasterImage img = new PortableRasterImage(10, 10);

        BitImage instance = new BitImage(img, new BitImageConverter() {
            @Override
            public boolean isPixelSet(AbstractColor c) {
                return false;
            }
        });

        instance.setPixel(new PortablePoint(2, 8), true);
        assertTrue(instance.getPixel(new PortablePoint(2, 8)));
        assertFalse(instance.getPixel(new PortablePoint(2, 7)));
    }

    @Test
    public void testCachingMechanism() {
        PortableRasterImage img = new PortableRasterImage(10, 10);
        img.setPixel(0, 0, new PortableColor(255, 0, 0));
        img.setPixel(0, 1, new PortableColor(0, 255, 0));
        img.setPixel(0, 2, new PortableColor(255, 0, 0));
        img.setPixel(9, 9, new PortableColor(0, 255, 0));

        final MutableInteger counter = new MutableInteger();
        BitImage instance = new BitImage(img, new BitImageConverter() {
            @Override
            public boolean isPixelSet(AbstractColor c) {
                counter.value++;
                return false;
            }
        });

        assertEquals(3, counter.value);
    }

    @Test
    public void testConversionCall() {
        PortableRasterImage img = new PortableRasterImage(10, 10);
        img.setPixel(9, 9, new PortableColor(0, 255, 0));

        BitImage instance = new BitImage(img, new BitImageConverter() {
            @Override
            public boolean isPixelSet(AbstractColor c) {
                if (c.equals(new PortableColor(0, 255, 0))) return true;
                return false;
            }
        });

        assertTrue(instance.getPixel(new PortablePoint(9, 9)));
    }

    @Test
    public void testGetAllSetPixels() {
        PortableRasterImage img = new PortableRasterImage(10, 10);
        PortableColor blue = new PortableColor(0, 0, 255);
        img.setPixel(0, 0, blue);
        img.setPixel(9, 9, blue);
        img.setPixel(4, 4, blue);

        //Determine all blue pixels as "set", afterwards there should be 3 pixels set
        BitImage instance = new BitImage(img, new BitImageConverter() {
            @Override
            public boolean isPixelSet(AbstractColor c) {
                if (c.equals(new PortableColor(0, 0, 255))) return true;
                return false;
            }
        });

        HashSet<PortablePoint> setPixels = instance.getAllSetPixels();
        assertEquals(3, setPixels.size());
        assertTrue(instance.getPixel(new PortablePoint(4, 4)));
        assertTrue(setPixels.contains(new PortablePoint(0, 0)));
    }

    //TODO Write tests for dilate() and erode()

    private class MutableInteger {
        public int value = 0;
    }
}