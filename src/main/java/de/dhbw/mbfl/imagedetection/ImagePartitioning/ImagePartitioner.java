package de.dhbw.mbfl.imagedetection.ImagePartitioning;

import de.dhbw.mbfl.imagedetection.BitImage.BitImage;
import de.dhbw.mbfl.imagedetection.platformIndependence.PortablePoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by florian on 19.02.15.
 */
public class ImagePartitioner {

    private static final Logger log = LoggerFactory.getLogger(ImagePartitioner.class);
    private BitImage image;
    private HashSet<PortablePoint> setPixels = new HashSet<PortablePoint>();
    private NeighbourSearchMode mode;

    public ImagePartitioner(BitImage image) {
        this(image, NeighbourSearchMode.HOR_VER);
    }

    public ImagePartitioner(BitImage image, NeighbourSearchMode mode) {
        this.image = image;
        this.mode = mode;
        this.setPixels = image.getAllSetPixels();
    }

    public PartitionedImage partition() {
        PartitionedImage partitionedImage = new PartitionedImage();

        while (!this.setPixels.isEmpty()) {
            PortablePoint startingPoint = this.setPixels.iterator().next();
            ImagePartition nextPartition = this.findNextPartition(startingPoint);
            partitionedImage.add(nextPartition);
        }

        return partitionedImage;
    }

    private ImagePartition findNextPartition(PortablePoint startingPoint) {
        LinkedList<PortablePoint> pointsToVisit = new LinkedList<PortablePoint>();
        Set<PortablePoint> partition = new HashSet<PortablePoint>();

        pointsToVisit.add(startingPoint);
        partition.add(startingPoint);
        this.setPixels.remove(startingPoint);

        while (pointsToVisit.size() > 0) {
            PortablePoint p = pointsToVisit.removeFirst();
            ArrayList<PortablePoint> neighbours = this.getNeighbours(p);
            for (PortablePoint neighbour : neighbours) {
                if (!this.setPixels.contains(neighbour)) continue;

                pointsToVisit.addLast(neighbour);
                partition.add(neighbour);
                this.setPixels.remove(neighbour);
            }
        }

        return new ImagePartition(partition);
    }

    private ArrayList<PortablePoint> getNeighbours(PortablePoint p) {
        ArrayList<PortablePoint> neighbours = new ArrayList<PortablePoint>();

        if (p.x + 1 < this.image.getWidth()) neighbours.add(new PortablePoint(p.x + 1, p.y));
        if (p.x - 1 >= 0) neighbours.add(new PortablePoint(p.x - 1, p.y));
        if (p.y + 1 < this.image.getHeight()) neighbours.add(new PortablePoint(p.x, p.y + 1));
        if (p.y - 1 >= 0) neighbours.add(new PortablePoint(p.x, p.y - 1));

        if (this.mode == NeighbourSearchMode.HOR_VER_DIAG) {
            // TODO
        }

        return neighbours;
    }

}
