/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dhbw.mbfl.imagedetectiontesting.houghStrategy;

import java.awt.*;

/**
 *
 * @author florian
 */
public class Cluster {
    private int sum;
    private Point center;

    public Cluster(int sum, Point center) {
        this.sum = sum;
        this.center = center;
    }
    
    /**
     * @return the sum
     */
    public int getSum() {
        return sum;
    }

    /**
     * @return the center
     */
    public Point getCenter() {
        return center;
    }
    
}
