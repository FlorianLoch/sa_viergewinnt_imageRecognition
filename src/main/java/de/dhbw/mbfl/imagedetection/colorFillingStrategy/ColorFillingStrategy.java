package de.dhbw.mbfl.imagedetection.colorFillingStrategy;

import de.dhbw.mbfl.imagedetection.AnalysisStrategy;
import de.dhbw.mbfl.jconnect4lib.board.Board;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by florian on 19.02.15.
 */
public class ColorFillingStrategy implements AnalysisStrategy {
    private static final double COLOR_DISTANCE_TOLERANCE = 65D;
    private static final Color YELLOW = new Color(255, 216, 0);
    private static final Color RED = new Color(233, 82, 73);

    private BufferedImage originalImage;
    private BufferedImage yellowMap;
    private BufferedImage redMap;


    public ColorFillingStrategy(BufferedImage image) throws IOException {
        this.originalImage = image;
    }

    private int width() {
        return this.originalImage.getWidth();
    }

    private int height() {
        return this.originalImage.getHeight();
    }

    @Override
    public Board analyse() {
        this.yellowMap = ImageUtils.createBufferedImageFromBlueprint(this.originalImage);
        this.redMap = ImageUtils.createBufferedImageFromBlueprint(this.originalImage);

        this.extractColorMaps();

        int[][] dilateMatrix = new int[][] {
            {1,1,1,1,1}, {1,1,1,1,1}, {1,1,1,1,1}, {1,1,1,1,1}, {1,1,1,1,1}
        };

//        this.yellowMap = erode(this.yellowMap, dilateMatrix, 2, 2);
//        this.redMap = erode(this.redMap, dilateMatrix, 2, 2);
//
//        this.yellowMap = dilate(this.yellowMap, dilateMatrix, 2, 2);
//        this.redMap = dilate(this.redMap, dilateMatrix, 2, 2);

        ImagePartitioner yellowPartitioner = new ImagePartitioner(this.yellowMap);
        ArrayList<ImagePartition> yellowPartitions = yellowPartitioner.partition();

        ImagePartitioner redPartitioner = new ImagePartitioner(this.redMap);
        ArrayList<ImagePartition> redPartitions = redPartitioner.partition();

        BufferedImage partitionsImage = ImageUtils.createBufferedImageFromBlueprint(this.yellowMap);
        Graphics g = partitionsImage.getGraphics();

        g.setColor(Color.YELLOW);
        for (ImagePartition partition : yellowPartitions) {
            Point center = partition.getCenter();
            g.drawRect(center.x - 20, center.y - 20, 40, 40);
        }
        g.setColor(Color.RED);
        for (ImagePartition partition : redPartitions) {
            Point center = partition.getCenter();
            g.drawRect(center.x - 20, center.y - 20, 40, 40);
        }

        try {
            ImageIO.write(this.yellowMap, "png", new File("yellow_map.png"));
            ImageIO.write(this.redMap, "png", new File("red_map.png"));
            ImageIO.write(partitionsImage, "png", new File("yellow_partitions.png"));
        } catch (Exception ex) {
            System.out.println(ex);
        }

        return null;
    }

    private static BufferedImage dilate(BufferedImage img, int[][] dilateMatrix, int centerX, int centerY) {
        BufferedImage copy = ImageUtils.createBufferedImageFromBlueprint(img);

        int marginLeft = centerX;
        int marginRight = dilateMatrix.length - 1 - centerX;
        int marginTop = centerY;
        int marginBottom = dilateMatrix[0].length - 1 - centerY;

        for (int i = marginLeft; i < img.getWidth() - marginRight; i++) {
            for (int j = marginTop; j < img.getHeight() - marginBottom; j++) {
                if (img.getRGB(i, j) != Color.WHITE.getRGB()) continue;

                copy.setRGB(i, j, Color.WHITE.getRGB());

                for (int h = 0; h < dilateMatrix.length; h++) {
                    for (int k = 0; k < dilateMatrix[0].length; k++) {
                        if (dilateMatrix[h][k] == 1) {
                            copy.setRGB(i + h, j + k, Color.WHITE.getRGB());
                        }
                    }
                }
            }
        }

        return copy;
    }

    private static BufferedImage erode(BufferedImage img, int[][] erodeMatrix, int centerX, int centerY) {
        BufferedImage copy = ImageUtils.createBufferedImageFromBlueprint(img);

        int marginLeft = centerX;
        int marginRight = erodeMatrix.length - 1 - centerX;
        int marginTop = centerY;
        int marginBottom = erodeMatrix[0].length - 1 - centerY;

        for (int i = marginLeft; i < img.getWidth() - marginRight; i++) {
            outer:
            for (int j = marginTop; j < img.getHeight() - marginBottom; j++) {
                for (int h = 0; h < erodeMatrix.length; h++) {
                    for (int k = 0; k < erodeMatrix[0].length; k++) {
                        if (erodeMatrix[h][k] == 1 && img.getRGB(i+h, j+k) != Color.WHITE.getRGB()) continue outer;
                    }
                }

                copy.setRGB(i, j, Color.WHITE.getRGB());
            }
        }

        return copy;
    }


    private void extractColorMaps() {
        BufferedImage result = ImageUtils.createBufferedImageFromBlueprint(this.originalImage);

        for (int i = 0; i < this.width(); i++) {
            for (int j = 0; j < this.height(); j++) {
                Color color = new Color(this.originalImage.getRGB(i, j));
                this.assignPixelToRespectiveColorMap(i, j, color);
            }
        }
    }

    private void assignPixelToRespectiveColorMap(int x, int y, Color color) {
        if (this.matchesExpectedColor(YELLOW, color)) {
            this.yellowMap.setRGB(x, y, Color.WHITE.getRGB());
        }
        else if (this.matchesExpectedColor(RED, color)) {
            this.redMap.setRGB(x, y, Color.WHITE.getRGB());
        }
    }

    private boolean matchesExpectedColor(Color expected, Color found) {
        double distance = ImageUtils.computeColorDistance(expected, found);
        //System.out.println(distance);
        return (distance <= COLOR_DISTANCE_TOLERANCE);
    }
}
