package de.dhbw.mbfl.imagedetection;

import org.junit.Test;

import java.awt.*;
import java.awt.image.BufferedImage;

import static org.junit.Assert.*;

public class ImageUtilsTest {

    @Test
    public void testAverageColor() throws Exception {
        BufferedImage testImage = new BufferedImage(3, 3, BufferedImage.TYPE_INT_RGB);

        testImage.setRGB(0, 0, Color.RED.getRGB());
        testImage.setRGB(1, 0, Color.RED.getRGB());
        testImage.setRGB(2, 0, Color.GREEN.getRGB());
        testImage.setRGB(0, 1, Color.WHITE.getRGB());
        testImage.setRGB(1, 1, Color.BLUE.getRGB());
        testImage.setRGB(2, 1, Color.BLUE.getRGB());
        testImage.setRGB(0, 2, Color.RED.getRGB());
        testImage.setRGB(1, 2, Color.BLACK.getRGB());
        testImage.setRGB(2, 2, Color.WHITE.getRGB());

        Color avg = ImageUtils.averageColor(testImage, new Point(1, 1), 1);

        assertEquals(141, avg.getRed());
        assertEquals(85, avg.getGreen());
        assertEquals(113, avg.getBlue());
    }
}