package de.dhbw.mbfl.imagedetection.ImagePartitioning;

import de.dhbw.mbfl.imagedetection.platformIndependence.PortablePoint;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by florian on 19.02.15.
 */
public class ImagePartition implements Iterable<PortablePoint> {

    private Set<PortablePoint> pixels;
    private PortablePoint centerCache;
    private String tag;

    ImagePartition(Set<PortablePoint> pixels) {
        this.pixels = pixels;
    }

    public PortablePoint getCenter() {
        if (this.centerCache == null) {
            int xSum = 0, ySum = 0;

            for (PortablePoint p : this.pixels) {
                xSum += p.x;
                ySum += p.y;
            }

            int x = xSum / this.pixels.size();
            int y = ySum / this.pixels.size();
            this.centerCache = new PortablePoint(x, y);
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
    public Iterator<PortablePoint> iterator() {
        return pixels.iterator();
    }
}
