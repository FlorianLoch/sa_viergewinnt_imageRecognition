package de.dhbw.mbfl.imagedetection;

import de.dhbw.mbfl.imagedetection.platformIndependence.*;

/**
 * Created by florian on 19.02.15.
 */
public class ImageUtils {

    public static AbstractRasterImage createBufferedImageFromBlueprint(AbstractRasterImage blueprint) {
        return new PortableRasterImage(blueprint.getWidth(), blueprint.getHeight());
    }


    public static double computeColorDistance(AbstractColor color1, AbstractColor color2) {
        double x = Math.pow(color1.getRed() - color2.getRed(), 2);
        x += Math.pow(color1.getGreen() - color2.getGreen(), 2);
        x += Math.pow(color1.getBlue() - color2.getBlue(), 2);

        return Math.sqrt(x);
    }

    /**
     * Computes the average color in a square with edge length of 2n+1. This algorithm is not suitable for building an average over more than 2^32/255 pixels
     * @param n Edge length of square will be 2n+1
     * @param center
     * @param image
     * @return
     */
    public static AbstractColor averageColor(AbstractRasterImage image, PortablePoint center, int n) throws IndexOutOfBoundsException{
        int sumRed = 0;
        int sumGreen = 0;
        int sumBlue = 0;

        int nl = n;
        int nr = n;
        int nt = n;
        int nb = n;

        int size = 0;

        //With every fulfilled conditional n has to get smaller
        if (center.x - n < 0) {
            nl = center.x;
        }
        if (center.y - n < 0) {
            nt = center.y;
        }
        if (center.x + n >= image.getWidth()) {
            nr = image.getWidth() - 1 - center.x;
        }
        if (center.y + n >= image.getHeight()) {
            nb = image.getHeight() - 1 - center.y;
        }

        for (int x = -nl; x <= nr; x++) {
            for (int y = -nt; y <= nb; y++) {
                AbstractColor c = image.getPixel(center.x + x, center.y + y);
                sumRed += c.getRed();
                sumGreen += c.getGreen();
                sumBlue += c.getBlue();
                size++;
            }
        }

        sumRed /= size;
        sumGreen /= size;
        sumBlue /= size;

        return new PortableColor(sumRed, sumGreen, sumBlue);
    }

}
