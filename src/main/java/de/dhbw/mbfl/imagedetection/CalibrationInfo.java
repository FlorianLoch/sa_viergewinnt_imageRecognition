package de.dhbw.mbfl.imagedetection;

import de.dhbw.mbfl.imagedetection.colorFillingStrategy.PartitionedImage;

import java.awt.*;

/**
 * Created by florian on 20.02.15.
 */
public class CalibrationInfo {
    private Color yellow;
    private Color red;
    private PartitionedImage partitions;


    public Color getYellow() {
        return yellow;
    }

    public void setYellow(Color yellow) {
        this.yellow = yellow;
    }

    public Color getRed() {
        return red;
    }

    public void setRed(Color red) {
        this.red = red;
    }

    public PartitionedImage getPartitions() {
        return partitions;
    }

    public void setPartitions(PartitionedImage partitions) {
        this.partitions = partitions;
    }
}
