package de.dhbw.mbfl.imagedetection.ImagePartitioning;

import org.junit.Test;

import java.awt.*;
import java.util.HashSet;

import static org.junit.Assert.*;

public class PartitionedImageTest {

    @Test
    public void testSortPartitions() {
        PartitionedImage partImg = new PartitionedImage();
        HashSet<Point> part1 = new HashSet<Point>();
        HashSet<Point> part2 = new HashSet<Point>();
        HashSet<Point> part3 = new HashSet<Point>();
        HashSet<Point> part4 = new HashSet<Point>();

        part1.add(new Point(1, 1));
        part2.add(new Point(3, 4));
        part3.add(new Point(2, 2));
        part4.add(new Point(1, 3));

        partImg.add(new ImagePartition(part1));
        partImg.add(new ImagePartition(part2));
        partImg.add(new ImagePartition(part3));
        partImg.add(new ImagePartition(part4));

        partImg = partImg.sortPartitions(2);

        assertEquals(new Point(1, 3), partImg.get(0).getCenter());
        assertEquals(new Point(3, 4), partImg.get(1).getCenter());
        assertEquals(new Point(1, 1), partImg.get(2).getCenter());
        assertEquals(new Point(2, 2), partImg.get(3).getCenter());
    }

}