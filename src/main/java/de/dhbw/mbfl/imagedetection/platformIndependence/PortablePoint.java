package de.dhbw.mbfl.imagedetection.platformIndependence;

/**
 * Created by florian on 23.02.15.
 */
public class PortablePoint {
    public int x;
    public int y;

    public PortablePoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PortablePoint that = (PortablePoint) o;

        if (x != that.x) return false;
        if (y != that.y) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }
}
