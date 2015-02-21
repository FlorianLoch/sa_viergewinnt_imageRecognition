package de.dhbw.mbfl.imagedetection.colorFillingStrategy.ImagePartitioning;

import java.awt.*;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by florian on 19.02.15.
 */
public class ImagePartition implements Iterable<Point> {

    private Set<Point> pixels;

    ImagePartition(Set<Point> pixels) {
        this.pixels = pixels;
    }

    public Point getCenter() {
        int xSum = 0, ySum = 0;

        for (Point p : this.pixels) {
            xSum += p.x;
            ySum += p.y;
        }

        return new Point(xSum / this.pixels.size(), ySum / this.pixels.size());
    }

    @Override
    public Iterator<Point> iterator() {
        return pixels.iterator();
    }
}
