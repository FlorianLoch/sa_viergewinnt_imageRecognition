package de.dhbw.mbfl.imagedetection;

import de.dhbw.mbfl.imagedetection.ImagePartitioning.ImagePartition;
import de.dhbw.mbfl.imagedetection.ImagePartitioning.PartitionedImage;
import de.dhbw.mbfl.imagedetection.platformIndependence.*;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by florian on 20.02.15.
 */
public class CalibrationInfo {
    private AbstractColor yellow;
    private AbstractColor red;
    private PartitionedImage partitions;
    private AbstractRasterImage afterConversion;
    private AbstractRasterImage afterErotation;
    private AbstractRasterImage afterDilatation;
    private AbstractRasterImage afterProcessing;

    public AbstractColor getYellow() {
        return yellow;
    }

    public void setYellow(AbstractColor yellow) {
        this.yellow = yellow;
    }

    public AbstractColor getRed() {
        return red;
    }

    public void setRed(AbstractColor red) {
        this.red = red;
    }

    public PartitionedImage getPartitions() {
        return partitions;
    }

    public void setPartitions(PartitionedImage partitions) {
        this.partitions = partitions;
    }

    public AbstractRasterImage getAfterConversion() {
        return afterConversion;
    }

    public void setAfterConversion(AbstractRasterImage afterConversion) {
        this.afterConversion = afterConversion;
    }

    public AbstractRasterImage getAfterErotation() {
        return afterErotation;
    }

    public void setAfterErotation(AbstractRasterImage afterErotation) {
        this.afterErotation = afterErotation;
    }

    public AbstractRasterImage getAfterDilatation() {
        return afterDilatation;
    }

    public void setAfterDilatation(AbstractRasterImage afterDilatation) {
        this.afterDilatation = afterDilatation;
    }

    public AbstractRasterImage getAfterProcessing() {
        if (null == this.afterProcessing) {
            this.afterProcessing = markPartitionsInImage();
        }

        return afterProcessing;
    }

    private AbstractRasterImage markPartitionsInImage() {
        AbstractRasterImage clone = cloneImage(this.afterDilatation);

        for (int i = 0; i < partitions.size(); i++) {
            ImagePartition partition = partitions.get(i);
            PortablePoint center = partition.getCenter();

            drawRectangle(clone, center);
        }

        return clone;
    }

    private static AbstractRasterImage cloneImage(AbstractRasterImage image) {
        PortableRasterImage newImg = new PortableRasterImage(image.getWidth(), image.getHeight());

        for (int i = 0; i < newImg.getWidth(); i++) {
            for (int j = 0; j < newImg.getHeight(); j++) {
                newImg.setPixel(new PortablePoint(i, j), image.getPixel(i, j));
            }
        }

        return newImg;
    }

    private static void drawRectangle(AbstractRasterImage img, PortablePoint pos) {
        int width = 7;
        int height = 7;

        PortableColor color = new PortableColor(255, 0, 0);

        for (int i = -width / 2; i < width / 2; i++) {
            for (int j = -height / 2; j < height / 2; j++) {
                setDot(img, new PortablePoint(pos.x + i, pos.y + j), color);
            }
        }
    }

    private static void setDot(AbstractRasterImage img, PortablePoint pos, AbstractColor color) {
        if (0 > pos.x || 0 > pos.y || pos.x >= img.getWidth() || pos.y >= img.getHeight()) {
            return;
        }

        img.setPixel(pos, color);
    }
}
