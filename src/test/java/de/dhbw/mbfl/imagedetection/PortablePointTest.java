package de.dhbw.mbfl.imagedetection;

import de.dhbw.mbfl.imagedetection.platformIndependence.PortablePoint;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PortablePointTest {
    private PortablePoint instance;

    @Before
    public void before() {
        this.instance = new PortablePoint(2, 8);
    }

    @Test
    public void testEquals() {
        assertEquals(new PortablePoint(2, 8), this.instance);
    }

    @Test
    public void testHashCode() {
        assertTrue(new PortablePoint(2, 8).hashCode() == this.instance.hashCode());
    }
}