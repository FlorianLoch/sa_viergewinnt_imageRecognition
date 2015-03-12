package de.dhbw.mbfl.imagedetection;

import de.dhbw.mbfl.imagedetection.ImagePartitioning.PartitionedImage;
import de.dhbw.mbfl.imagedetection.platformIndependence.AbstractColor;
import de.dhbw.mbfl.imagedetection.platformIndependence.AbstractRasterImage;

/**
 * Created by florian on 20.02.15.
 */
public class CalibrationInfo {
    private AbstractColor yellow;
    private AbstractColor red;
    private PartitionedImage partitions;
    private AbstractRasterImage afterConversion;
    private AbstractRasterImage afterErotation;

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

}
