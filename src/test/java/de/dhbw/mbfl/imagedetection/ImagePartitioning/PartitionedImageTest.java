package de.dhbw.mbfl.imagedetection.ImagePartitioning;

import de.dhbw.mbfl.imagedetection.ImageAnalysisException;
import de.dhbw.mbfl.imagedetection.platformIndependence.PortablePoint;
import org.junit.Test;

import java.util.HashSet;

import static org.junit.Assert.assertEquals;

public class PartitionedImageTest {

    @Test
    public void testSortPartitions() throws ImageAnalysisException {
        PartitionedImage partImg = new PartitionedImage();
        HashSet<PortablePoint> part1 = new HashSet<PortablePoint>();
        HashSet<PortablePoint> part2 = new HashSet<PortablePoint>();
        HashSet<PortablePoint> part3 = new HashSet<PortablePoint>();
        HashSet<PortablePoint> part4 = new HashSet<PortablePoint>();

        part1.add(new PortablePoint(1, 1));
        part2.add(new PortablePoint(3, 4));
        part3.add(new PortablePoint(2, 2));
        part4.add(new PortablePoint(1, 3));

        partImg.add(new ImagePartition(part1));
        partImg.add(new ImagePartition(part2));
        partImg.add(new ImagePartition(part3));
        partImg.add(new ImagePartition(part4));

        partImg = partImg.sortPartitions(2);

        assertEquals(new PortablePoint(1, 3), partImg.get(0).getCenter());
        assertEquals(new PortablePoint(3, 4), partImg.get(1).getCenter());
        assertEquals(new PortablePoint(1, 1), partImg.get(2).getCenter());
        assertEquals(new PortablePoint(2, 2), partImg.get(3).getCenter());
    }

}