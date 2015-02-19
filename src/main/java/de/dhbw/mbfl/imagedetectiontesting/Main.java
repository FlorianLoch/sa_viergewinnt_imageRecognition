package de.dhbw.mbfl.imagedetectiontesting;

import de.dhbw.mbfl.imagedetectiontesting.colorFillingStrategy.ColorFillingStrategy;
import de.dhbw.mbfl.jconnect4lib.board.Board;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by florian on 19.02.15.
 */
public class Main {

    public static void main(String[] args) throws IOException {
        BufferedImage image = ImageIO.read(new File("real_game_small.jpg"));

        long start = System.currentTimeMillis();

        AnalysisStrategy strategy = new ColorFillingStrategy(image);

        Board result = strategy.analyse();

        System.out.println(result);
    }

}
