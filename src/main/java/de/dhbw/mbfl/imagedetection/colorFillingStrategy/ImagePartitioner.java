package de.dhbw.mbfl.imagedetection.colorFillingStrategy;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;

/**
 * Created by florian on 19.02.15.
 */
public class ImagePartitioner {

    private BufferedImage image;
    private HashSet<Point> pixels = new HashSet<Point>();
    private NeighbourSearchMode mode;

    public ImagePartitioner(BufferedImage image) {
        this(image, NeighbourSearchMode.HOR_VER);
    }

    public ImagePartitioner(BufferedImage image, NeighbourSearchMode mode) {
        this.image = image;
        this.mode = mode;

        for (int i = 0; i < image.getWidth(); i ++) {
            for (int j = 0; j < image.getHeight(); j++) {
                if (image.getRGB(i, j) == Color.WHITE.getRGB()) {
                    pixels.add(new Point(i, j));
                }
            }
        }
    }

    public PartitionedImage partition() {
        PartitionedImage partitionedImage = new PartitionedImage();

        while (!this.pixels.isEmpty()) {
            Point startingPoint = this.pixels.iterator().next();
            ImagePartition nextPartition = this.findNextPartition(startingPoint);
            partitionedImage.add(nextPartition);
        }

        return partitionedImage;
    }

    private ImagePartition findNextPartition(Point startingPoint) {
        Stack<Point> pointsToVisit = new Stack<Point>();
        HashSet<Point> partition = new HashSet<Point>();

        pointsToVisit.add(startingPoint);
        partition.add(startingPoint);
        pixels.remove(startingPoint);

        while (!pointsToVisit.empty()) {
            Point p = pointsToVisit.pop();
            ArrayList<Point> neighbours = this.getNeighbours(p);
            for (Point neighbour : neighbours) {
                if (!this.isPixelSet(neighbour)) continue;
                if (partition.contains(neighbour)) continue;

                pointsToVisit.add(neighbour);
                partition.add(neighbour);
                pixels.remove(neighbour);
            }
        }

        return new ImagePartition(partition);
    }

    private boolean isPixelSet(Point p) {
        return (this.image.getRGB(p.x, p.y) == Color.WHITE.getRGB());
    }

    private ArrayList<Point> getNeighbours(Point p) {
        ArrayList<Point> neighbours = new ArrayList<Point>();

        if (p.x + 1 < this.image.getWidth()) neighbours.add(new Point(p.x + 1, p.y));
        if (p.x - 1 >= 0) neighbours.add(new Point(p.x - 1, p.y));
        if (p.y + 1 < this.image.getHeight()) neighbours.add(new Point(p.x, p.y + 1));
        if (p.y - 1 >= 0) neighbours.add(new Point(p.x, p.y - 1));

        if (this.mode == NeighbourSearchMode.HOR_VER_DIAG) {
            // TODO
        }

        return neighbours;
    }

    public enum NeighbourSearchMode {
        HOR_VER, HOR_VER_DIAG;
    }
}
