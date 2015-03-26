package de.dhbw.mbfl.imagedetection;

import com.sun.corba.se.impl.orbutil.graph.Graph;
import de.dhbw.mbfl.imagedetection.ImagePartitioning.ImagePartition;
import de.dhbw.mbfl.imagedetection.ImagePartitioning.PartitionedImage;
import de.dhbw.mbfl.imagedetection.platformIndependence.AbstractColor;
import de.dhbw.mbfl.imagedetection.platformIndependence.AbstractRasterImage;
import de.dhbw.mbfl.imagedetection.platformIndependence.PortablePoint;
import de.dhbw.mbfl.jconnect4lib.board.Board;
import de.dhbw.mbfl.jconnect4lib.board.BoardUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class BoardDetectorTest {
    private static int counter = 0;
    final int COLUMNS = 7;
    final int ROWS = 6;

    private String calibrationImagePath;
    private String imagePath;
    private String expectedBoardAllocation;
    private PortablePoint yellowSpot;
    private PortablePoint redSpot;


    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {"sample_images/captured_with_app.jpg", "", "", new PortablePoint(314, 206), new PortablePoint(242, 196)},
                {"sample_images/real_game_calibration_small.png", "", "", new PortablePoint(339, 174), new PortablePoint(337, 236)},
                {"sample_images/real_game_2_calib.jpg", "", "", new PortablePoint(318, 138), new PortablePoint(244, 58)}
        });
    }

    public BoardDetectorTest(String calibrationImagePath, String imagePath, String expectedBoardAllocation, PortablePoint yellowSpot, PortablePoint redSpot) {
        this.calibrationImagePath = calibrationImagePath;
        this.imagePath = imagePath;
        this.expectedBoardAllocation = expectedBoardAllocation;
        this.yellowSpot = yellowSpot;
        this.redSpot = redSpot;
    }

    @Test
    public void testCalibrationAndBoardDetection() throws Exception {
        CalibrationInfo calibration = new CalibrationInfo();

        JVMImage calibrationImage = new JVMImage(ImageIO.read(new File(this.calibrationImagePath)));

        long startTime = System.currentTimeMillis();

        try {
            BoardDetector.calibrate(calibration, calibrationImage, yellowSpot, redSpot, COLUMNS, ROWS, true); //true activates debug mode, so images are attached to calibration object
        }
        catch (ImageAnalysisException e) {
            //throw new Exception("Error in BoardDetector.calibrate()", e);
            e.printStackTrace();
        }

        //Write all images to disk
        ImageIO.write(abstractRasterImageToBufferedImage(calibration.getAfterConversion()), "png", new File(counter + "_after_conversion.png"));
        ImageIO.write(abstractRasterImageToBufferedImage(calibration.getAfterErotation()), "png", new File(counter + "_after_eroding.png"));
        ImageIO.write(abstractRasterImageToBufferedImage(calibration.getAfterDilatation()), "png", new File(counter + "_after_dilating.png"));
        BufferedImage markedImage = markPartitionsInImage(abstractRasterImageToBufferedImage(calibration.getAfterDilatation()), calibration.getPartitions());
        ImageIO.write(markedImage, "png", new File(counter + "_after_processing.png"));

        counter++;

        long endTime = System.currentTimeMillis();
        System.out.println("Calibration took " + (endTime - startTime) + " ms");

        assertNotNull(calibration.getAfterConversion());
        assertNotNull(calibration.getAfterErotation());
        assertNotNull(calibration.getPartitions());
        assertEquals(42, calibration.getPartitions().size());

        assertEquals(calibrationImage.getHeight(), calibration.getAfterDilatation().getHeight());

        if (this.imagePath.equals("") || this.expectedBoardAllocation.equals("")) {
            return;
        }

        JVMImage image = new JVMImage(ImageIO.read(new File(this.imagePath)));

        Board expectedBoard = new Board();
        BoardUtils.addStonesToBoard(expectedBoard, this.expectedBoardAllocation);

        BoardDetector detector = new BoardDetector(calibration);
        Board detectedBoard = detector.detectBoardAllocation(image);

        assertTrue(expectedBoard.areBoardOccupationsEqual(detectedBoard));
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

    private static BufferedImage markPartitionsInImage(BufferedImage img, PartitionedImage partitions) {
        Graphics g = img.getGraphics();
        g.setColor(Color.RED);

        for (int i = 0; i < partitions.size(); i++) {
            ImagePartition partition = partitions.get(i);
            PortablePoint center = partition.getCenter();

            g.fillRect(center.x - 2, center.y - 2, 4, 4);
        }

        return img;
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