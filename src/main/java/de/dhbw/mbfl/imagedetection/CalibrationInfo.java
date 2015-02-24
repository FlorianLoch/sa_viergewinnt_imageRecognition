package de.dhbw.mbfl.imagedetection;

import de.dhbw.mbfl.imagedetection.ImagePartitioning.PartitionedImage;
import de.dhbw.mbfl.imagedetection.platformIndependence.AbstractColor;

/**
 * Created by florian on 20.02.15.
 */
public class CalibrationInfo {
    private AbstractColor yellow;
    private AbstractColor red;
    private PartitionedImage partitions;


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
}
