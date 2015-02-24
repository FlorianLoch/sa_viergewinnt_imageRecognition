package de.dhbw.mbfl.imagedetection.platformIndependence;

import de.dhbw.mbfl.imagedetection.platformIndependence.AbstractColor;
import de.dhbw.mbfl.imagedetection.platformIndependence.PortableColor;
import de.dhbw.mbfl.imagedetection.platformIndependence.PortablePoint;
import de.dhbw.mbfl.imagedetection.platformIndependence.PortableRasterImage;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PortableRasterImageTest {

    @Test
    public void testPortableRasterImage() {
        PortableRasterImage instance = new PortableRasterImage(2, 2);
        instance.setPixel(new PortablePoint(0, 0), new PortableColor(255, 0, 127));
        instance.setPixel(new PortablePoint(1, 1), new PortableColor(128, 128, 128));

        for (int i = 0; i < 4; i++) {
            PortablePoint p = new PortablePoint(i % 2, i / 2);
            AbstractColor c = instance.getPixel(p);
            if (p.equals(new PortablePoint(0, 0))) {
                assertEquals(255, c.getRed());
                assertEquals(0, c.getGreen());
                assertEquals(127, c.getBlue());
            }
            else if (p.equals(new PortablePoint(1, 1))) {
                assertEquals(128, c.getRed());
                assertEquals(128, c.getGreen());
                assertEquals(128, c.getBlue());
            }
            else {
                assertEquals(0, c.getRed());
                assertEquals(0, c.getGreen());
                assertEquals(0, c.getBlue());
            }
        }
    }

}