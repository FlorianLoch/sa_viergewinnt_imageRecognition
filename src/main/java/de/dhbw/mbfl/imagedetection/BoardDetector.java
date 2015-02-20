package de.dhbw.mbfl.imagedetection;

import de.dhbw.mbfl.imagedetection.colorFillingStrategy.ColorFillingStrategy;
import de.dhbw.mbfl.imagedetection.colorFillingStrategy.ImageUtils;
import de.dhbw.mbfl.jconnect4lib.board.Board;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by florian on 19.02.15.
 */
public class BoardDetector {
    public static final int AVERAGE_WINDOW_SIZE = 2;
    public static final int[][] MORPH_OPS_MATRIX = new int[][] {
            {1,1,1,1,1}, {1,1,1,1,1}, {1,1,1,1,1}, {1,1,1,1,1}, {1,1,1,1,1}
    };

    public static CalibrationInfo calibrate(BufferedImage image, Point yellowSpot, Point redSpot, int boardSize, Color backgroundColor) throws IndexOutOfBoundsException {
        CalibrationInfo info = new CalibrationInfo();

        info.setYellow(ImageUtils.averageColor(image, yellowSpot, AVERAGE_WINDOW_SIZE));
        info.setRed(ImageUtils.averageColor(image, redSpot, AVERAGE_WINDOW_SIZE));


    }

    public static void main(String[] args) throws IOException {
        BufferedImage image = ImageIO.read(new File("real_game_small.jpg"));

//        long start = System.currentTimeMillis();

        AnalysisStrategy strategy = new ColorFillingStrategy(image);

        Board result = strategy.analyse();

        System.out.println(result);
    }

}
