package de.dhbw.mbfl.imagedetection;

import de.dhbw.mbfl.imagedetection.colorFillingStrategy.*;
import de.dhbw.mbfl.imagedetection.colorFillingStrategy.BitImage.BitImage;
import de.dhbw.mbfl.imagedetection.colorFillingStrategy.BitImage.BitImageConverter;
import de.dhbw.mbfl.imagedetection.colorFillingStrategy.BitImage.ColorBitImageConverter;
import de.dhbw.mbfl.imagedetection.colorFillingStrategy.ImagePartitioning.ImagePartitioner;
import de.dhbw.mbfl.imagedetection.colorFillingStrategy.ImagePartitioning.PartitionedImage;
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

        BitImageConverter converter = new ColorBitImageConverter(backgroundColor);
        BitImage bitImage = new BitImage(image, converter);

        bitImage = bitImage.erode(BitImage.MORPHOLOGICAL_5_SQUARE_MATRIX, BitImage.MORPHOLOGICAL_5_SQUARE_MATRIX_X_CENTER, BitImage.MORPHOLOGICAL_5_SQUARE_MATRIX_Y_CENTER);
        bitImage = bitImage.dilate(BitImage.MORPHOLOGICAL_5_SQUARE_MATRIX, BitImage.MORPHOLOGICAL_5_SQUARE_MATRIX_X_CENTER, BitImage.MORPHOLOGICAL_5_SQUARE_MATRIX_Y_CENTER);

        ImagePartitioner partitioner = new ImagePartitioner(bitImage);
        PartitionedImage partitions = partitioner.partition();

        //label partitions

        info.setPartitions(partitions);

        return info;
    }

}
