package de.dhbw.mbfl.imagedetection.BitImage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by florian on 20.02.15.
 */
public class BitImage {
    private BitSet pixels;
    private int origImageWidth;
    public static byte[][] MORPHOLOGICAL_5_SQUARE_MATRIX = buildMorphMatrix(5);
    public static int MORPHOLOGICAL_5_SQUARE_MATRIX_X_CENTER = 2;
    public static int MORPHOLOGICAL_5_SQUARE_MATRIX_Y_CENTER = 2;

    public BitImage(BufferedImage image, BitImageConverter converter) {
        this.pixels = new BitSet(image.getWidth() * image.getWidth());
        this.origImageWidth = image.getWidth();
        this.convertFromBufferedImage(image, converter);
    }

    public BitImage(int width, int height) {
        this.origImageWidth = width;
        this.pixels = new BitSet(width * height);
    }

    public boolean getPixel(Point p) {
        int i = this.calculatePositionInBitSet(p);
        return this.pixels.get(i);
    }

    public void setPixel(Point p, boolean state) {
        int i = this.calculatePositionInBitSet(p);
        this.pixels.set(i, state);
    }

    public int getWidth() {
        return this.origImageWidth;
    }

    public int getHeight() {
        return this.pixels.size() / this.origImageWidth;
    }

    public HashSet<Point> getAllSetPixels() {
        HashSet<Point> result = new HashSet<Point>();

        for (int i = this.pixels.nextSetBit(0); i >= 0; i = this.pixels.nextSetBit(i+1)) {
            result.add(this.calculateCartesianPosition(i));
        }

        return result;
    }

    public BitImage dilate(byte[][] dilateMatrix, int centerX, int centerY) {
        BitImage copy = new BitImage(this.getWidth(), this.getHeight());

        int marginLeft = centerX;
        int marginRight = dilateMatrix.length - 1 - centerX;
        int marginTop = centerY;
        int marginBottom = dilateMatrix[0].length - 1 - centerY;

        for (int i = marginLeft; i < this.getWidth() - marginRight; i++) {
            for (int j = marginTop; j < this.getHeight() - marginBottom; j++) {
                Point p = new Point(i, j);
                if (!this.getPixel(p)) continue;

                copy.setPixel(p, true);

                for (int h = 0; h < dilateMatrix.length; h++) {
                    for (int k = 0; k < dilateMatrix[0].length; k++) {
                        if (dilateMatrix[h][k] == 1) {
                            int x = (i - centerX) + h;
                            int y = (j - centerY) + k;
                            copy.setPixel(new Point(x, y), true);
                        }
                    }
                }
            }
        }

        return copy;
    }

    public BitImage erode(byte[][] erodeMatrix, int centerX, int centerY) {
        BitImage copy = new BitImage(this.getWidth(), this.getHeight());

        int marginLeft = centerX;
        int marginRight = erodeMatrix.length - 1 - centerX;
        int marginTop = centerY;
        int marginBottom = erodeMatrix[0].length - 1 - centerY;

        for (int i = marginLeft; i < this.getWidth() - marginRight; i++) {
            outer:
            for (int j = marginTop; j < this.getHeight() - marginBottom; j++) {

                for (int h = 0; h < erodeMatrix.length; h++) {
                    for (int k = 0; k < erodeMatrix[0].length; k++) {
                        int x = (i - centerX) + h;
                        int y = (j - centerY) + k;
                        if (erodeMatrix[h][k] == 1 && this.getPixel(new Point(x, y)) == false) continue outer;
                    }
                }

                copy.setPixel(new Point(i, j), true);
            }
        }

        return copy;
    }

    public BufferedImage toBufferedImage() {
        BufferedImage img = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_BYTE_GRAY);

        for (Point p : this.getAllSetPixels()) {
            img.setRGB(p.x, p.y, Color.WHITE.getRGB());
        }

        return img;
    }

    private int calculatePositionInBitSet(Point p) {
        return p.x + p.y * this.origImageWidth;
    }

    private Point calculateCartesianPosition(int index) {
        int x = index % this.origImageWidth;
        int y = index / this.origImageWidth;
        return new Point(x, y);
    }

    private void convertFromBufferedImage(BufferedImage image, BitImageConverter converter) {
        HashMap<Color, Boolean> cache = new HashMap<Color, Boolean>();

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color c = new Color(image.getRGB(x, y));

                Boolean value = cache.get(c);
                if (value == null) {
                    value = new Boolean(converter.isPixelSet(c));
                }

                this.setPixel(new Point(x, y), value);

                cache.put(c, value);
            }
        }
    }

    public static byte[][] buildMorphMatrix(int n) {
        byte[][] matrix = new byte[n][n];

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                matrix[i][j] = 1;
            }
        }

        return matrix;
    }
}
