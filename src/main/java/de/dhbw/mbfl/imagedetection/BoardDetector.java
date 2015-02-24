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

import java.io.IOException;

/**
 * Created by florian on 19.02.15.
 */
public class BoardDetector {

    public static final int WINDOW_SIZE_FOR_AVG = 2;
    public static final double COLOR_EQUALITY_TOLERANCE = 70D;

    private CalibrationInfo calibration;

    public static CalibrationInfo calibrate(AbstractRasterImage image, PortablePoint yellowSpot, PortablePoint redSpot, int columns, int rows) throws IndexOutOfBoundsException, ImageAnalysisException, IOException {
        return calibrate(image, yellowSpot, redSpot, columns, rows, false);
    }

    public static CalibrationInfo calibrate(AbstractRasterImage image, PortablePoint yellowSpot, PortablePoint redSpot, int columns, int rows, boolean debugOutput) throws IndexOutOfBoundsException, ImageAnalysisException, IOException {
        CalibrationInfo info = new CalibrationInfo();

        AbstractColor yellowAvg = ImageUtils.averageColor(image, yellowSpot, WINDOW_SIZE_FOR_AVG);
        AbstractColor redAvg = ImageUtils.averageColor(image, redSpot, WINDOW_SIZE_FOR_AVG);

        info.setYellow(yellowAvg);
        info.setRed(redAvg);

        BitImageConverter converter = new ColorBitImageConverter(new AbstractColor[]{yellowAvg, redAvg}, COLOR_EQUALITY_TOLERANCE);
        BitImage bitImage = new BitImage(image, converter);
        if (debugOutput) {
            info.setAfterConversion(bitImage.toPortableRasterImage());
        }

        bitImage = bitImage.erode(BitImage.buildMorphMatrix(15), 7, 7);
        if (debugOutput) {
            info.setAfterErotation(bitImage.toPortableRasterImage());
        }

        bitImage = bitImage.dilate(BitImage.buildMorphMatrix(15), 7, 7);
        if (debugOutput) {
            info.setAfterDilatation(bitImage.toPortableRasterImage());
        }

        ImagePartitioner partitioner = new ImagePartitioner(bitImage);
        PartitionedImage partitions = partitioner.partition();

        if (partitions.size() != columns * rows) {
            throw new ImageAnalysisException("In the given image " + partitions.size() + " fields haven been found. " +
                    "But " + columns * rows + " have been expected.");
        }

        partitions = partitions.sortPartitions(columns);

        info.setPartitions(partitions);

        return info;
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
