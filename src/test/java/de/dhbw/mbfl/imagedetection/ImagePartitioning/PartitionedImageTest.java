package de.dhbw.mbfl.imagedetection.ImagePartitioning;

import de.dhbw.mbfl.imagedetection.ImageAnalysisException;
import de.dhbw.mbfl.imagedetection.platformIndependence.PortablePoint;
import org.junit.Test;

import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PartitionedImageTest {

    @Test
    public void testSortPartitionsAccordingToBoard() throws ImageAnalysisException {
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

        partImg = partImg.sortPartitionsAccordingToBoard(2);

        assertEquals(new PortablePoint(1, 3), partImg.get(0).getCenter());
        assertEquals(new PortablePoint(3, 4), partImg.get(1).getCenter());
        assertEquals(new PortablePoint(1, 1), partImg.get(2).getCenter());
        assertEquals(new PortablePoint(2, 2), partImg.get(3).getCenter());
    }

    @Test
    public void filterForNBiggestPartitions() {
        PartitionedImage instance = new PartitionedImage();
        instance.add(new ImagePartition(new HashSet<PortablePoint>(){{
            add(new PortablePoint(0, 0));
            add(new PortablePoint(1, 1));
            add(new PortablePoint(2, 2));
        }}));
        instance.add(new ImagePartition(new HashSet<PortablePoint>(){{
            add(new PortablePoint(1, 1));
            add(new PortablePoint(2, 2));
        }}));
        instance.add(new ImagePartition(new HashSet<PortablePoint>(){{
            add(new PortablePoint(1, 1));
        }}));

        assertEquals(3, instance.size());

        instance.filterForNBiggestPartitions(1);

        assertEquals(1, instance.size());
        assertEquals(new PortablePoint(1, 1), instance.get(0).getCenter());
    }

    @Test
    public void testSortPartitionsBySize() {
        PartitionedImage instance = new PartitionedImage();
        ImagePartition part1 = new ImagePartition(new HashSet<PortablePoint>(){{
            add(new PortablePoint(0, 0));
            add(new PortablePoint(1, 1));
            add(new PortablePoint(2, 2));
        }});
        instance.add(part1);

        ImagePartition part2 = new ImagePartition(new HashSet<PortablePoint>(){{
            add(new PortablePoint(1, 1));
            add(new PortablePoint(2, 2));
        }});
        instance.add(part2);

        ImagePartition part3 = new ImagePartition(new HashSet<PortablePoint>(){{
            add(new PortablePoint(1, 1));
        }});
        instance.add(part3);

        assertEquals(3, instance.size());

        PartitionedImage sorted = instance.sortPartitionsBySize();

        assertFalse(sorted == instance);
        assertEquals(3, sorted.size());

        assertTrue(sorted.get(0) == part3);
        assertTrue(sorted.get(1) == part2);
        assertTrue(sorted.get(2) == part1);
    }

    @Test
    public void testFilterByDeviationFromMedian() {
        PartitionedImage instance = new PartitionedImage();
        ImagePartition part1 = new ImagePartition(new HashSet<PortablePoint>(){{
            add(new PortablePoint(0, 0));
        }});
        instance.add(part1);

        ImagePartition part2 = new ImagePartition(new HashSet<PortablePoint>(){{
            add(new PortablePoint(1, 1));
            add(new PortablePoint(2, 2));
            add(new PortablePoint(2, 1));
            add(new PortablePoint(3, 2));
        }});
        instance.add(part2);

        ImagePartition part3 = new ImagePartition(new HashSet<PortablePoint>(){{
            add(new PortablePoint(1, 1));
            add(new PortablePoint(2, 2));
            add(new PortablePoint(2, 1));
            add(new PortablePoint(3, 2));
        }});
        instance.add(part3);

        ImagePartition part4 = new ImagePartition(new HashSet<PortablePoint>(){{
            add(new PortablePoint(1, 1));
            add(new PortablePoint(2, 1));
        }});
        instance.add(part4);

        instance.filterByDeviationFromMedian(0.5);

        assertEquals(3, instance.size());
        assertEquals(2, instance.get(0).size());
        assertEquals(4, instance.get(1).size());
        assertEquals(4, instance.get(2).size());
    }
}