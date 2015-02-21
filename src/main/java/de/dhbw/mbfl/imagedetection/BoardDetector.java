package de.dhbw.mbfl.imagedetection;

import de.dhbw.mbfl.imagedetection.BitImage.BitImage;
import de.dhbw.mbfl.imagedetection.BitImage.BitImageConverter;
import de.dhbw.mbfl.imagedetection.BitImage.ColorBitImageConverter;
import de.dhbw.mbfl.imagedetection.ImagePartitioning.ImagePartition;
import de.dhbw.mbfl.imagedetection.ImagePartitioning.ImagePartitioner;
import de.dhbw.mbfl.imagedetection.ImagePartitioning.PartitionedImage;
import de.dhbw.mbfl.jconnect4lib.board.Board;
import de.dhbw.mbfl.jconnect4lib.board.Position;
import de.dhbw.mbfl.jconnect4lib.board.Stone;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by florian on 19.02.15.
 */
public class BoardDetector {

    public static final int WINDOW_SIZE_FOR_AVG = 2;
    public static final double COLOR_EQUALITY_TOLERANCE = 65D;

    private CalibrationInfo calibration;

    public static CalibrationInfo calibrate(BufferedImage image, Point yellowSpot, Point redSpot, int columns, int rows) throws IndexOutOfBoundsException, ImageAnalysisException {
        CalibrationInfo info = new CalibrationInfo();

        Color yellowAvg = ImageUtils.averageColor(image, yellowSpot, WINDOW_SIZE_FOR_AVG);
        Color redAvg = ImageUtils.averageColor(image, redSpot, WINDOW_SIZE_FOR_AVG);

        info.setYellow(yellowAvg);
        info.setRed(redAvg);

        BitImageConverter converter = new ColorBitImageConverter(new Color[]{yellowAvg, redAvg}, COLOR_EQUALITY_TOLERANCE);
        BitImage bitImage = new BitImage(image, converter);

        bitImage = bitImage.erode(BitImage.MORPHOLOGICAL_5_SQUARE_MATRIX, BitImage.MORPHOLOGICAL_5_SQUARE_MATRIX_X_CENTER, BitImage.MORPHOLOGICAL_5_SQUARE_MATRIX_Y_CENTER);
        bitImage = bitImage.dilate(BitImage.MORPHOLOGICAL_5_SQUARE_MATRIX, BitImage.MORPHOLOGICAL_5_SQUARE_MATRIX_X_CENTER, BitImage.MORPHOLOGICAL_5_SQUARE_MATRIX_Y_CENTER);

        ImagePartitioner partitioner = new ImagePartitioner(bitImage);
        PartitionedImage partitions = partitioner.partition();

        if (partitions.size() != columns * rows) {
            throw new ImageAnalysisException("In the given image " + partitions.size() + " fields haven been found. " +
                    "But only " + columns * rows + " have been expected.");
        }

        partitions = partitions.sortPartitions(columns);

        info.setPartitions(partitions);

        return info;
    }

    public BoardDetector(CalibrationInfo calibration) {
        this.calibration = calibration;
    }

    public Board detectBoardAllocation(BufferedImage image) {
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

    private Stone analyseAllocationOfField(ImagePartition field, BufferedImage image) {
        Stone result;

        int x = field.getCenter().x;
        int y = field.getCenter().y;
        Color colorInImage = new Color(image.getRGB(x, y));

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
