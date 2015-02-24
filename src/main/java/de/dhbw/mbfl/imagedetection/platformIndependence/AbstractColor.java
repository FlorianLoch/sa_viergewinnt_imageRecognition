package de.dhbw.mbfl.imagedetection.platformIndependence;

/**
 * Created by florian on 23.02.15.
 */
public abstract class AbstractColor {

    protected abstract void setRed(int red);

    protected abstract void setGreen(int green);

    protected abstract void setBlue(int blue);

    public abstract int getRed();

    public abstract int getGreen();

    public abstract int getBlue();

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public abstract int hashCode();
}
