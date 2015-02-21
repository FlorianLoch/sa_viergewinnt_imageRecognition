package de.dhbw.mbfl.imagedetection.ImagePartitioning;

import java.awt.*;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by florian on 19.02.15.
 */
public class ImagePartition implements Iterable<Point> {

    private Set<Point> pixels;
    private Point centerCache;
    private String tag;

    ImagePartition(Set<Point> pixels) {
        this.pixels = pixels;
    }

    public Point getCenter() {
        if (this.centerCache == null) {
            int xSum = 0, ySum = 0;

            for (Point p : this.pixels) {
                xSum += p.x;
                ySum += p.y;
            }

            int x = xSum / this.pixels.size();
            int y = ySum / this.pixels.size();
            this.centerCache = new Point(x, y);
        }

        return this.centerCache;
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public Iterator<Point> iterator() {
        return pixels.iterator();
    }
}
