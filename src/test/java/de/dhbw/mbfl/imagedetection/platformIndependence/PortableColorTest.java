package de.dhbw.mbfl.imagedetection.platformIndependence;

import de.dhbw.mbfl.imagedetection.platformIndependence.PortableColor;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PortableColorTest {

    @Test
    public void testPortableColor() {
        PortableColor instance = new PortableColor(255, 10, 255);

        assertEquals(255, instance.getRed());
        assertEquals(10, instance.getGreen());
        assertEquals(255, instance.getBlue());
    }

    @Test
    public void testEquals() {
        PortableColor instance = new PortableColor(127, 128, 129);

        assertEquals(new PortableColor(127, 128, 129), instance);
    }

    @Test
    public void testHashCode() {
        PortableColor instance = new PortableColor(127, 128, 129);

        assertTrue((new PortableColor(127, 128, 129).hashCode() == instance.hashCode()));
    }

}