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
            public boolean isPixelSet(AbstractColor c) {
                counter.value++;
                return false;
            }
        });

        assertEquals(3, counter.value);
    }

//
//    @Test(expected = IndexOutOfBoundsException.class)
//    public void getPixelShallThrowOOBE() {
//        BitImage image = new BitImage(5, 5);
//        image.getPixel(new PortablePoint(6, 6));
//    }
//
//    @Test(expected = IndexOutOfBoundsException.class)
//    public void setPixelShallThrowOOBE() {
//        BitImage image = new BitImage(5, 5);
//        image.setPixel(new PortablePoint(6, 6));
//    }
//

    @Test
    public void testConversionCall() {
        PortableRasterImage img = new PortableRasterImage(10, 10);
        img.setPixel(9, 9, new PortableColor(0, 255, 0));

        BitImage instance = new BitImage(img, new BitImageConverter() {
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
    @Test
    public void testMorphMatrix() {
        byte[][] expected = new byte[][] {
                {1, 1, 1},
                {1, 1, 1},
                {1, 1, 1}
        };

        byte[][] computed = BitImage.buildMorphMatrix(3);

        assertArrayEquals(expected, computed);
    }

    @Test
    public void testDilating() {
        BitImage origImage = new BitImage(5, 5);
        origImage.setPixel(new PortablePoint(2, 2), true);
        origImage.setPixel(new PortablePoint(4, 4), true); //This shall be ignored, because the morphMatrix cannot be moved here!

        byte[][] morphMatrix = BitImage.buildMorphMatrix(3);
        morphMatrix[1][1] = 0; //Center point

        BitImage image = origImage.dilate(morphMatrix, new PortablePoint(1, 1));

        assertEquals(origImage.getHeight(), image.getHeight());
        assertEquals(origImage.getWidth(), image.getWidth());

        assertEquals(false, image.getPixel(new PortablePoint(0, 0)));
        assertEquals(false, image.getPixel(new PortablePoint(0, 1)));
        assertEquals(false, image.getPixel(new PortablePoint(0, 2)));
        assertEquals(false, image.getPixel(new PortablePoint(0, 3)));
        assertEquals(false, image.getPixel(new PortablePoint(0, 4)));
        assertEquals(false, image.getPixel(new PortablePoint(1, 0)));
        assertEquals(true , image.getPixel(new PortablePoint(1, 1)));
        assertEquals(true , image.getPixel(new PortablePoint(1, 2)));
        assertEquals(true , image.getPixel(new PortablePoint(1, 3)));
        assertEquals(false, image.getPixel(new PortablePoint(1, 4)));
        assertEquals(false, image.getPixel(new PortablePoint(2, 0)));
        assertEquals(true , image.getPixel(new PortablePoint(2, 1)));
        assertEquals(false, image.getPixel(new PortablePoint(2, 2)));
        assertEquals(true , image.getPixel(new PortablePoint(2, 3)));
        assertEquals(false, image.getPixel(new PortablePoint(2, 4)));
        assertEquals(false, image.getPixel(new PortablePoint(3, 0)));
        assertEquals(true , image.getPixel(new PortablePoint(3, 1)));
        assertEquals(true , image.getPixel(new PortablePoint(3, 2)));
        assertEquals(true , image.getPixel(new PortablePoint(3, 3)));
        assertEquals(false, image.getPixel(new PortablePoint(3, 4)));
        assertEquals(false, image.getPixel(new PortablePoint(4, 0)));
        assertEquals(false, image.getPixel(new PortablePoint(4, 1)));
        assertEquals(false, image.getPixel(new PortablePoint(4, 2)));
        assertEquals(false, image.getPixel(new PortablePoint(4, 3)));
        assertEquals(false, image.getPixel(new PortablePoint(4, 4)));
    }

    @Test
    public void testEroding() {
        BitImage origImage = new BitImage(5, 5);

        origImage.setPixel(new PortablePoint(0, 0));
        origImage.setPixel(new PortablePoint(1, 1));
        origImage.setPixel(new PortablePoint(2, 2));
        origImage.setPixel(new PortablePoint(2, 3));
        origImage.setPixel(new PortablePoint(2, 4));
        origImage.setPixel(new PortablePoint(3, 2));
        origImage.setPixel(new PortablePoint(3, 4));
        origImage.setPixel(new PortablePoint(4, 2));
        origImage.setPixel(new PortablePoint(4, 3));
        origImage.setPixel(new PortablePoint(4, 4));

        byte[][] morphMatrix = BitImage.buildMorphMatrix(3);
        morphMatrix[1][1] = 0;

        BitImage image = origImage.erode(morphMatrix, new PortablePoint(1, 1));

        assertEquals(origImage.getHeight(), image.getHeight());
        assertEquals(origImage.getWidth(), image.getWidth());

        assertEquals(false, image.getPixel(new PortablePoint(0, 0)));
        assertEquals(false, image.getPixel(new PortablePoint(0, 1)));
        assertEquals(false, image.getPixel(new PortablePoint(0, 2)));
        assertEquals(false, image.getPixel(new PortablePoint(0, 3)));
        assertEquals(false, image.getPixel(new PortablePoint(0, 4)));
        assertEquals(false, image.getPixel(new PortablePoint(1, 0)));
        assertEquals(false, image.getPixel(new PortablePoint(1, 1)));
        assertEquals(false, image.getPixel(new PortablePoint(1, 2)));
        assertEquals(false, image.getPixel(new PortablePoint(1, 3)));
        assertEquals(false, image.getPixel(new PortablePoint(1, 4)));
        assertEquals(false, image.getPixel(new PortablePoint(2, 0)));
        assertEquals(false, image.getPixel(new PortablePoint(2, 1)));
        assertEquals(false, image.getPixel(new PortablePoint(2, 2)));
        assertEquals(false, image.getPixel(new PortablePoint(2, 3)));
        assertEquals(false, image.getPixel(new PortablePoint(2, 4)));
        assertEquals(false, image.getPixel(new PortablePoint(3, 0)));
        assertEquals(false, image.getPixel(new PortablePoint(3, 1)));
        assertEquals(false, image.getPixel(new PortablePoint(3, 2)));
        assertEquals(true , image.getPixel(new PortablePoint(3, 3)));
        assertEquals(false, image.getPixel(new PortablePoint(3, 4)));
        assertEquals(false, image.getPixel(new PortablePoint(4, 0)));
        assertEquals(false, image.getPixel(new PortablePoint(4, 1)));
        assertEquals(false, image.getPixel(new PortablePoint(4, 2)));
        assertEquals(false, image.getPixel(new PortablePoint(4, 3)));
        assertEquals(false, image.getPixel(new PortablePoint(4, 4)));
    }

    @Test
    public void expectMorphOpToThrowOOBE() {
        BitImage image = new BitImage(5, 5);
        image.setPixel(new PortablePoint(2, 2), true);
        image.setPixel(new PortablePoint(4, 4), true); //This shall be ignored, because the morphMatrix cannot be moved here!

        byte[][] morphMatrix = BitImage.buildMorphMatrix(3);
        morphMatrix[1][1] = 0; //Center point

        boolean exceptionThrown = false;

        try {
            image.dilate(morphMatrix, new PortablePoint(3, 2));
        }
        catch (IndexOutOfBoundsException e) {
            exceptionThrown = true;
        }

        assertTrue(exceptionThrown);

        exceptionThrown = false;

        try {
            image.erode(morphMatrix, new PortablePoint(-1, 1));
        }
        catch (IndexOutOfBoundsException e) {
            exceptionThrown = true;
        }

        assertTrue(exceptionThrown);

    }

    private class MutableInteger {
        public int value = 0;
    }
}