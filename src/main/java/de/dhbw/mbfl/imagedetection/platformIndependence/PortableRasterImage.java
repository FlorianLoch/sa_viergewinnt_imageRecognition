package de.dhbw.mbfl.imagedetection.platformIndependence;

import java.util.BitSet;

/**
 * Representing an image as a BitSet where each pixel is made up of 8 bits each per red, green and blue.
 * This class doesn't implement transparency, because it isn't needed and by skipping alpha 25% of storage can be saved.
 * Internal representation of is:  RGBRGBRGB...
 * The most significant bit of color information is at the rightmost position.
 *
 * Created by florian on 24.02.15.
 */
public class PortableRasterImage extends AbstractRasterImage {
    private BitSet image;
    private int width;

    public PortableRasterImage(int width, int height) {
        this.image = new BitSet(width * height * 3 * 8);
        this.width = width;
    }

    private void writeToBitSet(int index, int value) {
        int remainder = value;
        int dividend = 128;
        for (int i = index + 8 - 1; i >= index; i--) {
            boolean bit = (remainder / dividend == 1);
            this.image.set(i, bit);
            remainder %= dividend;
            dividend /= 2;
        }
    }

    private int readFromBitSet(int index) {
        int factor = 1;
        int sum = 0;
        for (int i = index; i < index + 8; i++) {
            if (this.image.get(i)) {
                sum += factor;
            }
            factor = factor * 2;
        }

        return sum;
    }

    private int calculateIndexOfPoint(PortablePoint p) {
        return (p.x + p.y * this.width) * 24;
    }

    @Override
    public AbstractColor getPixel(PortablePoint p) {
        int baseIndex = this.calculateIndexOfPoint(p);
        int red = this.readFromBitSet(baseIndex);
        int green = this.readFromBitSet(baseIndex + 8);
        int blue = this.readFromBitSet(baseIndex + 16);

        return new PortableColor(red, green, blue);
    }

    @Override
    public void setPixel(PortablePoint p, AbstractColor color) {
        int baseIndex = this.calculateIndexOfPoint(p);

        this.writeToBitSet(baseIndex, color.getRed());
        this.writeToBitSet(baseIndex + 8, color.getGreen());
        this.writeToBitSet(baseIndex + 16, color.getBlue());
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.image.size() / 24 / this.getWidth();
    }
}
