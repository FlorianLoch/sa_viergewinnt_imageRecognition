package de.dhbw.mbfl.imagedetection;

import de.dhbw.mbfl.imagedetection.platformIndependence.AbstractColor;
import de.dhbw.mbfl.imagedetection.platformIndependence.AbstractRasterImage;
import de.dhbw.mbfl.imagedetection.platformIndependence.PortablePoint;
import de.dhbw.mbfl.imagedetection.platformIndependence.PortableRasterImage;
import de.dhbw.mbfl.jconnect4lib.board.Board;
import de.dhbw.mbfl.jconnect4lib.board.Position;
import de.dhbw.mbfl.jconnect4lib.board.Stone;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class BoardDetectorTest {

    @Test
    public void overallTest() throws Exception {
        final String IMAGE_PATH = "sample_images/captured_with_app.jpg"; //sample_images/real_game_calibration_small.png
        CalibrationInfo calibration = new CalibrationInfo();

        Board expected = new Board();
        expected.addStone("A1");
        expected.addStone("C1");
        expected.addStone("C2");
        expected.addStone(new Position("D1"), Stone.YELLOW);

        JVMImage calibrationImage = new JVMImage(ImageIO.read(new File(IMAGE_PATH)));
        JVMImage sampleImageForDetection = new JVMImage(ImageIO.read(new File(IMAGE_PATH)));

//        PortablePoint yellowSpot = new PortablePoint(272, 177);
//        PortablePoint redSpot = new PortablePoint(403, 171);
        PortablePoint yellowSpot = new PortablePoint(314, 206);
        PortablePoint redSpot = new PortablePoint(242, 196);

        final int COLUMNS = 7;
        final int ROWS = 6;

        long startTime = System.currentTimeMillis();

        try {
            BoardDetector.calibrate(calibration, calibrationImage, yellowSpot, redSpot, COLUMNS, ROWS, true); //true activates debug mode, so images are attached to calibration object
        }
        catch (ImageAnalysisException e) {
            //throw new Exception("Error in BoardDetector.calibrate()", e);
        }

        //Write all images to disk
        ImageIO.write(abstractRasterImageToBufferedImage(calibration.getAfterConversion()), "png", new File("after_conversion.png"));
        ImageIO.write(abstractRasterImageToBufferedImage(calibration.getAfterErotation()), "png", new File("after_eroding.png"));

        long endTime = System.currentTimeMillis();
        System.out.println("Calibration took " + (endTime - startTime) + " ms");

        assertNotNull(calibration.getAfterConversion());
        assertNotNull(calibration.getAfterErotation());

        BoardDetector detector = new BoardDetector(calibration);

        startTime = System.currentTimeMillis();
        Board board = detector.detectBoardAllocation(sampleImageForDetection);
        endTime = System.currentTimeMillis();
        System.out.println("Detection took " + (endTime - startTime) + " ms");

        System.out.println(board);

        //assertTrue(expected.areBoardOccupationsEqual(board));
    }

    private static BufferedImage abstractRasterImageToBufferedImage(AbstractRasterImage ari) {
        BufferedImage bi = new BufferedImage(ari.getWidth(), ari.getHeight(), BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < ari.getWidth(); i++) {
            for (int j = 0; j < ari.getHeight(); j++) {
                AbstractColor c = ari.getPixel(i, j);

                bi.setRGB(i, j, new Color(c.getRed(), c.getGreen(), c.getBlue()).getRGB());
            }
        }

        return bi;
    }

    private class SampleImageSetting {
        private String imagePath;
        private PortablePoint yellowSpot;
        private PortablePoint redSpot;
        private Board expectedBoard;

    }

    private class JVMImage extends AbstractRasterImage {
        private BufferedImage image;

        public JVMImage(BufferedImage image) {
            this.image = image;
        }

        @Override
        public AbstractColor getPixel(PortablePoint p) {
            return new JVMColor(this.image.getRGB(p.x, p.y));
        }

        @Override
        public void setPixel(PortablePoint p, AbstractColor color) {
            this.image.setRGB(color.getRed(), color.getGreen(), color.getBlue());
        }

        @Override
        public int getWidth() {
            return this.image.getWidth();
        }

        @Override
        public int getHeight() {
            return this.image.getHeight();
        }
    }

    private class JVMColor extends AbstractColor {
        private Color color;

        public JVMColor(int red, int green, int blue) {
            this.color = new Color(red, green, blue);
        }

        public JVMColor(int rgb) {
            this.color = new Color(rgb);
        }

        @Override
        protected void setRed(int red) {
            this.color = new Color(red, this.getGreen(), this.getBlue());
        }

        @Override
        protected void setGreen(int green) {
            this.color = new Color(this.getRed(), green, this.getBlue());

        }

        @Override
        protected void setBlue(int blue) {
            this.color = new Color(this.getRed(), this.getGreen(), blue);

        }

        @Override
        public int getRed() {
            return this.color.getRed();
        }

        @Override
        public int getGreen() {
            return this.color.getGreen();
        }

        @Override
        public int getBlue() {
            return this.color.getBlue();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            JVMColor jvmColor = (JVMColor) o;

            if (!color.equals(jvmColor.color)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return color.hashCode();
        }
    }

}