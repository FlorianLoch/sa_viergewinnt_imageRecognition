package de.dhbw.mbfl.imagedetection.colorFillingStrategy.ImagePartitioning;

import java.util.ArrayList;

/**
 * Created by florian on 20.02.15.
 */
public class PartitionedImage {
    private ArrayList<ImagePartition> partitions;

    public PartitionedImage() {
        this.partitions = new ArrayList<ImagePartition>();
    }

    public boolean add(ImagePartition partition) {
        return this.partitions.add(partition);
    }

}
