package de.dhbw.mbfl.imagedetection.ImagePartitioning;

import de.dhbw.mbfl.imagedetection.BitImage.BitImage;
import de.dhbw.mbfl.imagedetection.platformIndependence.PortablePoint;
import org.junit.Test;

import static org.junit.Assert.*;

public class ImagePartitionerTest {

    @Test
    public void testPartition() {
        final int IMG_SIZE = 5;
        BitImage image = new BitImage(IMG_SIZE, IMG_SIZE);

        for (int i = 0; i < IMG_SIZE; i++) {
            image.setPixel(new PortablePoint(i, i));
        }

        ImagePartitioner partitioner = new ImagePartitioner(image, NeighbourSearchMode.HOR_VER);
        PartitionedImage partitionedImage = partitioner.partition();

        assertEquals(5, partitionedImage.size());

        image.setPixel(new PortablePoint(3, 2));

        partitioner = new ImagePartitioner(image, NeighbourSearchMode.HOR_VER);
        partitionedImage = partitioner.partition();

        assertEquals(4, partitionedImage.size());
    }

}