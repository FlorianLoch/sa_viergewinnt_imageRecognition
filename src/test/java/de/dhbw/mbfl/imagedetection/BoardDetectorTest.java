package de.dhbw.mbfl.imagedetection;

import de.dhbw.mbfl.jconnect4lib.board.Board;
import de.dhbw.mbfl.jconnect4lib.board.Position;
import de.dhbw.mbfl.jconnect4lib.board.Stone;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class BoardDetectorTest {

    @Test
    public void overallTest() throws IOException, ImageAnalysisException {
        Board expected = new Board();
        expected.addStone("A1");
        expected.addStone("C1");
        expected.addStone("C2");
        expected.addStone(new Position("D1"), Stone.YELLOW);

        BufferedImage calibrationImage = ImageIO.read(new File("sample_images/real_game_calibration_small.png"));
        BufferedImage sampleImageForDetection = ImageIO.read(new File("sample_images/real_game_detection_small.png"));

        Point yellowSpot = new Point(272, 177);
        Point redSpot = new Point(403, 171);

        final int COLUMNS = 7;
        final int ROWS = 6;

        long startTime = System.currentTimeMillis();
        CalibrationInfo calibration = BoardDetector.calibrate(calibrationImage, yellowSpot, redSpot, COLUMNS, ROWS);
        long endTime = System.currentTimeMillis();
        System.out.println("Calibration took " + (endTime - startTime) + " ms");

        BoardDetector detector = new BoardDetector(calibration);

        startTime = System.currentTimeMillis();
        Board board = detector.detectBoardAllocation(sampleImageForDetection);
        endTime = System.currentTimeMillis();
        System.out.println("Detection took " + (endTime - startTime) + " ms");

        System.out.println(board);

        assertTrue(expected.areBoardOccupationsEqual(board));
    }

}