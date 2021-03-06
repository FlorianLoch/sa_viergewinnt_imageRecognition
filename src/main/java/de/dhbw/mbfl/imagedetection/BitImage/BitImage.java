package de.dhbw.mbfl.imagedetection.BitImage;

import de.dhbw.mbfl.imagedetection.platformIndependence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by florian on 20.02.15.
 */
public class BitImage {
    private static final Logger log = LoggerFactory.getLogger(BitImage.class);
    private BitSet pixels;
    private int origImageWidth;
    public static byte[][] MORPHOLOGICAL_5_SQUARE_MATRIX = buildMorphMatrix(5);
    public static int MORPHOLOGICAL_5_SQUARE_MATRIX_X_CENTER = 2;
    public static int MORPHOLOGICAL_5_SQUARE_MATRIX_Y_CENTER = 2;

    public BitImage(AbstractRasterImage image, BitImageConverter converter) {
        this.pixels = new BitSet(image.getWidth() * image.getHeight());
        this.origImageWidth = image.getWidth();
        this.convertFromAbstractRasterImage(image, converter);
    }

    public BitImage(int width, int height) {
        this.origImageWidth = width;
        this.pixels = new BitSet(width * height);

//        for (int i = 0; i < width * height; i++) {
//            this.pixels.set(i, false);
//        }
    }

    public boolean getPixel(PortablePoint p) {
        int i = this.calculatePositionInBitSet(p);

//        if (i < 0 || i >= this.pixels.size()) {
//            throw new IndexOutOfBoundsException();
//        }

        return this.pixels.get(i);
    }

    public void setPixel(PortablePoint p, boolean state) {
        int i = this.calculatePositionInBitSet(p);

//        if (i < 0 || i >= this.pixels.size()) {
//            throw new IndexOutOfBoundsException();
//        }

        this.pixels.set(i, state);
    }

    public void setPixel(PortablePoint p) {
        this.setPixel(p, true);
    }

    public int getWidth() {
        return this.origImageWidth;
    }

    public int getHeight() {
        return this.pixels.size() / this.origImageWidth;
    }

    public HashSet<PortablePoint> getAllSetPixels() {
        HashSet<PortablePoint> result = new HashSet<PortablePoint>();

        for (int i = this.pixels.nextSetBit(0); i >= 0; i = this.pixels.nextSetBit(i+1)) {
            result.add(this.calculateCartesianPosition(i));
        }

        return result;
    }

    public BitImage dilate(byte[][] matrix, PortablePoint center) {
        return morphOpProxy(MorphOps.DILATE, matrix, center);
    }

    public BitImage erode(byte[][] matrix, PortablePoint center) {
        return morphOpProxy(MorphOps.ERODE, matrix, center);
    }

    private BitImage morphOpProxy(MorphOps op, byte[][] matrix, PortablePoint center) {
        if (matrix.length > this.getWidth() || matrix[0].length > this.getHeight()) {
            throw new IndexOutOfBoundsException("Given matrix is bigger than current image!");
        }

        int marginLeft = center.x;
        int marginRight = matrix.length - 1 - center.x;
        int marginTop = center.y;
        int marginBottom = matrix[0].length - 1 - center.y;

        if (isBelowZero(marginLeft) || isBelowZero(marginRight) || isBelowZero(marginTop) || isBelowZero(marginBottom)) {
            throw new IndexOutOfBoundsException("Given center of matrix is invalid!");
        }

        if (MorphOps.DILATE == op) {
            return dilateImpl(matrix, center, marginLeft, marginRight, marginTop, marginBottom);
        }

        if (MorphOps.ERODE == op) {
            return erodeImpl(matrix, center, marginLeft, marginRight, marginTop, marginBottom);
        }

        throw new IllegalArgumentException("No valid operation given!");
    }

    private BitImage dilateImpl(byte[][] dilateMatrix, PortablePoint center, int marginLeft, int marginRight, int marginTop, int marginBottom) {
        BitImage copy = new BitImage(this.getWidth(), this.getHeight());

        for (int i = marginLeft; i < this.getWidth() - marginRight; i++) {
            for (int j = marginTop; j < this.getHeight() - marginBottom; j++) {
                PortablePoint p = new PortablePoint(i, j);
                if (!this.getPixel(p)) continue;

                for (int h = 0; h < dilateMatrix.length; h++) {
                    for (int k = 0; k < dilateMatrix[0].length; k++) {
                        if (dilateMatrix[h][k] == 1) {
                            int x = (i - center.x) + h;
                            int y = (j - center.y) + k;
                            copy.setPixel(new PortablePoint(x, y), true);
                        }
                    }
                }
            }
        }

        return copy;
    }

    private static boolean isBelowZero(int value) {
        return (value < 0);
    }

    public BitImage erodeImpl(byte[][] erodeMatrix, PortablePoint center, int marginLeft, int marginRight, int marginTop, int marginBottom) {
        BitImage copy = new BitImage(this.getWidth(), this.getHeight());
        int counter = 0;

        for (int i = marginLeft; i < this.getWidth() - marginRight; i++) {
            outer:
            for (int j = marginTop; j < this.getHeight() - marginBottom; j++) {

                for (int h = 0; h < erodeMatrix.length; h++) {
                    for (int k = 0; k < erodeMatrix[0].length; k++) {
                        int x = (i - center.x) + h;
                        int y = (j - center.y) + k;
                        if (erodeMatrix[h][k] == 1 && this.getPixel(new PortablePoint(x, y)) == false) {
                            continue outer;
                        }
                    }
                }

                counter++;
                copy.setPixel(new PortablePoint(i, j), true);
            }
        }

        return copy;
    }

    public AbstractRasterImage toPortableRasterImage() {
        PortableRasterImage img = new PortableRasterImage(this.getWidth(), this.getHeight());

        for (PortablePoint p : this.getAllSetPixels()) {
            img.setPixel(p.x, p.y, new PortableColor(255, 255, 255));
        }

        return img;
    }

    private int calculatePositionInBitSet(PortablePoint p) {
        return p.x + p.y * this.origImageWidth;
    }

    private PortablePoint calculateCartesianPosition(int index) {
        int x = index % this.origImageWidth;
        int y = index / this.origImageWidth;
        return new PortablePoint(x, y);
    }

    private void convertFromAbstractRasterImage(AbstractRasterImage image, BitImageConverter converter) {
        HashSet<AbstractColor> cacheTrue = new HashSet<AbstractColor>();
        HashSet<AbstractColor> cacheFalse = new HashSet<AbstractColor>();


        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                AbstractColor c = image.getPixel(x, y);

                boolean value = cacheTrue.contains(c);
                if (!value) {
                    value = cacheFalse.contains(c);

                    if (!value) {
                        value = converter.isPixelSet(c);

                        if (value) {
                            cacheTrue.add(c);
                        }
                        else {
                            cacheFalse.add(c);
                        }
                    }
                    else {
                        value = !value;
                    }
                }

                this.setPixel(new PortablePoint(x, y), value);
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

    private enum MorphOps {
        DILATE, ERODE;
    }
}
