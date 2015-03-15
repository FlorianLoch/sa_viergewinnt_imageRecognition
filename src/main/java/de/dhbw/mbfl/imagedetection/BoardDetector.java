package de.dhbw.mbfl.imagedetection;

import de.dhbw.mbfl.imagedetection.BitImage.BitImage;
import de.dhbw.mbfl.imagedetection.BitImage.BitImageConverter;
import de.dhbw.mbfl.imagedetection.BitImage.ColorBitImageConverter;
import de.dhbw.mbfl.imagedetection.ImagePartitioning.ImagePartition;
import de.dhbw.mbfl.imagedetection.ImagePartitioning.ImagePartitioner;
import de.dhbw.mbfl.imagedetection.ImagePartitioning.PartitionedImage;
import de.dhbw.mbfl.imagedetection.platformIndependence.AbstractColor;
import de.dhbw.mbfl.imagedetection.platformIndependence.AbstractRasterImage;
import de.dhbw.mbfl.imagedetection.platformIndependence.PortablePoint;
import de.dhbw.mbfl.jconnect4lib.board.Board;
import de.dhbw.mbfl.jconnect4lib.board.Position;
import de.dhbw.mbfl.jconnect4lib.board.Stone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by florian on 19.02.15.
 */
public class BoardDetector {

    private static final Logger log = LoggerFactory.getLogger(BoardDetector.class);
    public static final int WINDOW_SIZE_FOR_AVG = 2;
    public static final double COLOR_EQUALITY_TOLERANCE = 65D;

    private static long timerStartedAt = -1;

    private CalibrationInfo calibration;

    public static CalibrationInfo calibrate(AbstractRasterImage image, PortablePoint yellowSpot, PortablePoint redSpot, int columns, int rows) throws IndexOutOfBoundsException, ImageAnalysisException, IOException {
        return calibrate(new CalibrationInfo(), image, yellowSpot, redSpot, columns, rows, false);
    }

    public static CalibrationInfo calibrate(CalibrationInfo calib, AbstractRasterImage image, PortablePoint yellowSpot, PortablePoint redSpot, int columns, int rows, boolean debugOutput) throws IndexOutOfBoundsException, ImageAnalysisException, IOException {
        log.info("LOG00000: Calibration started!");
        startTiming();

        AbstractColor yellowAvg = ImageUtils.averageColor(image, yellowSpot, WINDOW_SIZE_FOR_AVG);
        AbstractColor redAvg = ImageUtils.averageColor(image, redSpot, WINDOW_SIZE_FOR_AVG);

        long timeNeededForAverageColor = stopTiming();
        log.info("LOG00020: Average colors computed after " + timeNeededForAverageColor + "ms");

        calib.setYellow(yellowAvg);
        calib.setRed(redAvg);

        startTiming();

        BitImageConverter converter = new ColorBitImageConverter(new AbstractColor[]{yellowAvg, redAvg}, COLOR_EQUALITY_TOLERANCE);
        BitImage bitImage = new BitImage(image, converter);
        if (debugOutput) {
            calib.setAfterConversion(bitImage.toPortableRasterImage());
        }

        long timeNeededImageConversion = stopTiming();
        log.info("LOG00030: Image converted to BitImage in " + timeNeededImageConversion + "ms");

        startTiming();

        byte[][] morphMatrix = BitImage.buildMorphMatrix(15);
        bitImage = bitImage.erode(morphMatrix, 10, 10 );
        if (debugOutput) {
            calib.setAfterErotation(bitImage.toPortableRasterImage());
        }

        long timeNeededForEroding = stopTiming();
        log.info("LOG00040: Eroding completed after " + timeNeededForEroding + "ms");

        startTiming();

        bitImage = bitImage.dilate(morphMatrix, 7, 7);
        if (debugOutput) {
            calib.setAfterDilatation(bitImage.toPortableRasterImage());
        }

        long timeNeededForDilating = stopTiming();
        log.info("LOG00050: Dilating completed after " + timeNeededForDilating + "ms");

        startTiming();

        ImagePartitioner partitioner = new ImagePartitioner(bitImage);
        PartitionedImage partitions = partitioner.partition();

        partitions.filterForNBiggestPartitions(columns * rows);

        long timeNeededForPartitioning = stopTiming();
        log.info("LOG00060: Partitioning via flood filling done in " + timeNeededForPartitioning + "ms");

        if (partitions.size() < columns * rows) {
            throw new ImageAnalysisException("In the given image only " + partitions.size() + " fields haven been found. " +
                    "But " + columns * rows + " have been expected.");
        }

        startTiming();

        partitions = partitions.sortPartitionsAccordingToBoard(columns);

        long timeNeededForSortingPartitions = stopTiming();
        log.info("LOG00070: Sorting partitions done after " + timeNeededForSortingPartitions + "ms");

        calib.setPartitions(partitions);

        return calib;
    }

    private static void startTiming() {
        timerStartedAt = System.currentTimeMillis();
    }

    private static long stopTiming() {
        if (timerStartedAt == -1) {
            throw new IllegalStateException("Timer has not been started before!");
        }

        long duration = System.currentTimeMillis() - timerStartedAt;
        timerStartedAt = -1;

        return duration;
    }

    public BoardDetector(CalibrationInfo calibration) {
        this.calibration = calibration;
    }

    public Board detectBoardAllocation(AbstractRasterImage image) {
        Board board = new Board();

        //The fields/partition have already been sorted in the way that Position 42 is in the upper right corner and Position 0 in the bottom lef one
        int i = 0;
        for (ImagePartition partition : this.calibration.getPartitions()) {
            Stone s = this.analyseAllocationOfField(partition, image);
            board.addStone(new Position(i), s);
            i++;
        }

        return board;
    }

    private Stone analyseAllocationOfField(ImagePartition field, AbstractRasterImage image) {
        Stone result;

        AbstractColor colorInImage = ImageUtils.averageColor(image, field.getCenter(), WINDOW_SIZE_FOR_AVG);

        double distanceYellow = ImageUtils.computeColorDistance(this.calibration.getYellow(), colorInImage);
        double distanceRed = ImageUtils.computeColorDistance(this.calibration.getRed(), colorInImage);

        if (distanceYellow <= distanceRed) {
            result = Stone.YELLOW;
        }
        else {
            result = Stone.RED;
        }

        if (distanceYellow > COLOR_EQUALITY_TOLERANCE && distanceRed > COLOR_EQUALITY_TOLERANCE) {
            return null;
        }

        return result;
    }
}
