package de.dhbw.mbfl.imagedetectiontesting.colorFillingStrategy;

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
}
