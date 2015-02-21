package de.dhbw.mbfl.imagedetection;

import de.dhbw.mbfl.jconnect4lib.board.Board;
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
        BufferedImage calibrationImage = ImageIO.read(new File("sample_calibration.png"));
        BufferedImage sampleImageForDetection = ImageIO.read(new File("sample_detection.png"));

        Point yellowSpot = new Point();
        Point redSpot = new Point();

        final int COLUMNS = 7;
        final int ROWS = 6;

        CalibrationInfo calibration = BoardDetector.calibrate(calibrationImage, yellowSpot, redSpot, COLUMNS, ROWS);

        BoardDetector detector = new BoardDetector(calibration);

        Board board = detector.detectBoardAllocation(sampleImageForDetection);
    }

}