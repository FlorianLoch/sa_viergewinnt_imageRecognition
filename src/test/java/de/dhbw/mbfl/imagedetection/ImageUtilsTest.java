package de.dhbw.mbfl.imagedetection;

import de.dhbw.mbfl.imagedetection.platformIndependence.AbstractColor;
import de.dhbw.mbfl.imagedetection.platformIndependence.PortableColor;
import de.dhbw.mbfl.imagedetection.platformIndependence.PortablePoint;
import de.dhbw.mbfl.imagedetection.platformIndependence.PortableRasterImage;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ImageUtilsTest {

    @Test
    public void testAverageColor() {
        PortableRasterImage testImage = new PortableRasterImage(3, 3);

        testImage.setPixel(0, 0, new PortableColor(255, 0, 0));
        testImage.setPixel(1, 0, new PortableColor(255, 0, 0));
        testImage.setPixel(2, 0, new PortableColor(0, 255, 0));
        testImage.setPixel(0, 1, new PortableColor(255, 255, 255));
        testImage.setPixel(1, 1, new PortableColor(0, 0, 255));
        testImage.setPixel(2, 1, new PortableColor(0, 0, 255));
        testImage.setPixel(0, 2, new PortableColor(255, 0, 0));
        testImage.setPixel(1, 2, new PortableColor(0, 0, 0));
        testImage.setPixel(2, 2, new PortableColor(255, 255, 255));

        AbstractColor avg = ImageUtils.averageColor(testImage, new PortablePoint(1, 1), 1);

        assertEquals(141, avg.getRed());
        assertEquals(85, avg.getGreen());
        assertEquals(113, avg.getBlue());
    }

    @Test
    public void testAverageColorWorksAtEdges() {
        PortableRasterImage testImage = new PortableRasterImage(3, 3);

        testImage.setPixel(0, 0, new PortableColor(255, 255, 255));
        testImage.setPixel(0, 1, new PortableColor(0, 0, 0));
        testImage.setPixel(1, 0, new PortableColor(255, 255, 255));
        testImage.setPixel(1, 1, new PortableColor(0, 0, 0));

        AbstractColor avg = ImageUtils.averageColor(testImage, new PortablePoint(0, 0), 1);

        assertEquals(127, avg.getRed());
        assertEquals(127, avg.getGreen());
        assertEquals(127, avg.getBlue());
    }
}