package de.dhbw.mbfl.imagedetection.colorFillingStrategy;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by florian on 19.02.15.
 */
public class ImageUtils {

    public static BufferedImage createBufferedImageFromBlueprint(BufferedImage blueprint) {
        return new BufferedImage(blueprint.getWidth(), blueprint.getHeight(), blueprint.getType());
    }


    public static double computeColorDistance(Color color1, Color color2) {
        double x = Math.pow(color1.getRed() - color2.getRed(), 2);
        x += Math.pow(color1.getGreen() - color2.getGreen(), 2);
        x += Math.pow(color1.getBlue() - color2.getBlue(), 2);

        return Math.sqrt(x);
    }

    /**
     * Computes the average color in a square with edge length of 2n. This algorithm is not suitable for building an average over more than 2^32/255 pixels
     * @param n Edge length of square will be 2n
     * @param center
     * @param image
     * @return
     */
    public static Color averageColor(BufferedImage image, Point center, int n) throws IndexOutOfBoundsException{
        int sumRed = 0;
        int sumGreen = 0;
        int sumBlue = 0;

        if (center.x - n < 0 || center.y - n < 0 || center.x + n >= image.getWidth() || center.y + n >= image.getHeight()) {
            throw new IndexOutOfBoundsException("Given center in combination with given edge length describes pixels outside the given image.");
        }

        for (int x = -n; x <= n; x++) {
            for (int y = -n; y <= n; y++) {
                Color c = new Color(image.getRGB(center.x + x, center.y + y));
                sumRed += c.getRed();
                sumGreen += c.getGreen();
                sumBlue += c.getBlue();
            }
        }

        int size = n*n;
        sumRed /= size;
        sumGreen /= size;
        sumBlue /= size;

        return new Color(sumRed, sumGreen, sumBlue);
    }
}
