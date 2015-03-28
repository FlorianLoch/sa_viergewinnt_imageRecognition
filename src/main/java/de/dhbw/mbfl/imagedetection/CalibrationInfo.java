package de.dhbw.mbfl.imagedetection;

import de.dhbw.mbfl.imagedetection.ImagePartitioning.ImagePartition;
import de.dhbw.mbfl.imagedetection.ImagePartitioning.PartitionedImage;
import de.dhbw.mbfl.imagedetection.platformIndependence.*;

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
    private PortablePoint yellowSpot;
    private PortablePoint redSpot;

    public PortablePoint getYellowSpot() {
        return yellowSpot;
    }

    public void setYellowSpot(PortablePoint yellowSpot) {
        this.yellowSpot = yellowSpot;
    }

    public PortablePoint getRedSpot() {
        return redSpot;
    }

    public void setRedSpot(PortablePoint redSpot) {
        this.redSpot = redSpot;
    }

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
        PortableColor colorPartitions = new PortableColor(0, 0, 255);

        AbstractRasterImage clone = cloneImage(this.afterDilatation);

        for (int i = 0; i < partitions.size(); i++) {
            ImagePartition partition = partitions.get(i);
            PortablePoint center = partition.getCenter();

            drawRectangle(clone, center, colorPartitions);
        }

        //Draw spots
        drawRectangle(clone, getYellowSpot(), getYellow());
        drawRectangle(clone, getRedSpot(), getRed());

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

    private static void drawRectangle(AbstractRasterImage img, PortablePoint pos, AbstractColor color) {
        int width = 9;
        int height = 9;

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
