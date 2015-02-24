package de.dhbw.mbfl.imagedetection.platformIndependence;

/**
 * Created by florian on 24.02.15.
 */
public class PortableColor extends AbstractColor {
    private int red = 0;
    private int green = 0;
    private int blue = 0;

    public PortableColor(int red, int green, int blue) {
        this.setRed(red);
        this.setGreen(green);
        this.setBlue(blue);
    }

    @Override
    protected void setRed(int red) {
        this.red = red;
    }

    @Override
    protected void setGreen(int green) {
        this.green = green;
    }

    @Override
    protected void setBlue(int blue) {
        this.blue = blue;
    }

    @Override
    public int getRed() {
        return this.red;
    }

    @Override
    public int getGreen() {
        return this.green;
    }

    @Override
    public int getBlue() {
        return this.blue;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PortableColor == false) return false;

        PortableColor c = (PortableColor) obj;
        return (c.getRed() == this.getRed() && c.getGreen() == this.getGreen() && c.getBlue() == this.getBlue());
    }

    @Override
    public int hashCode() {
        int result = red;
        result = 31 * result + green;
        result = 31 * result + blue;
        return result;
    }

    @Override
    public String toString() {
        return "PortableColor{" +
                "red=" + red +
                ", green=" + green +
                ", blue=" + blue +
                '}';
    }
}
