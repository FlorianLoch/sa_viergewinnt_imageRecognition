package de.dhbw.mbfl.imagedetection.platformIndependence;

/**
 * Created by florian on 23.02.15.
 */
public abstract class AbstractRasterImage {

    public abstract AbstractColor getPixel(PortablePoint p);

    public AbstractColor getPixel(int x, int y) {
        return this.getPixel(new PortablePoint(x, y));
    }

    public abstract void setPixel(PortablePoint p, AbstractColor color);

    public void setPixel(int x, int y, AbstractColor color) {
        this.setPixel(new PortablePoint(x, y), color);
    }

    public abstract int getWidth();

    public abstract int getHeight();

}
