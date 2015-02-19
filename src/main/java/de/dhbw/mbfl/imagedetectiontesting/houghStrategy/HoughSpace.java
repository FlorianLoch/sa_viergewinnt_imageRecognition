/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dhbw.mbfl.imagedetectiontesting.houghStrategy;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;

/**
 *
 * @author florian
 */
public class HoughSpace {
    
    private final int[][] raster;
    
    public static HoughSpace createHoughSpace(BufferedImage img, int threshold, int radiusMin, int radiusMax, int stepsize) {
        if (radiusMax < radiusMin || radiusMax < 0  || radiusMin < 0 || threshold < 0 || stepsize <= 0) throw new IllegalArgumentException();
        
        int[][] raster = new int[img.getWidth()][img.getHeight()];
        
        for (int i = 0; i < raster.length; i++) {
            for (int j = 0; j < raster[0].length; j++) {
                if ((img.getRGB(i, j) & 0xFF) > threshold) {
                    for (int r = radiusMin; r <= radiusMax; r = r + stepsize) {
                        //raster[i][j] = 50;
                        increaseCirclewise(raster, new Point(i, j), r);
                    }
                }
            }
        }
        
        return new HoughSpace(raster);
    }
    
    private static void increaseCirclewise(int[][] raster, Point origin, int radius) {
        int radiusSquare = (int) Math.pow(radius, 2);
        for (int a = -radius; a <= radius; a++) {
            int b = (int) Math.round(Math.sqrt(radiusSquare - Math.pow(a, 2)));
            if (isPointInRaster(raster, origin.x + a, origin.y + b)) {
                if (raster[origin.x + a][origin.y + b] != Integer.MAX_VALUE) {
                    raster[origin.x + a][origin.y + b]++;
                }
            }
            if (isPointInRaster(raster, origin.x + a, origin.y - b)) {
                if (raster[origin.x + a][origin.y - b] != Integer.MAX_VALUE) {
                    raster[origin.x + a][origin.y - b]++;
                }
            }            
        }
    }
    
    private static boolean isPointInRaster(int[][] raster, int x, int y) {
        return (x >= 0 && y >= 0 && x < raster.length && y < raster[0].length);
    }
    
    private HoughSpace(int[][] raster) {
        this.raster = raster;
    }
    
    public int getMinimalValue() {
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < this.raster.length; i++) {
            for (int j = 0; j < this.raster[0].length; j++) {
                if (this.raster[i][j] < min) min = this.raster[i][j];
            }
        }
        
        return min;
    }
    
    public int getMaximalValue() {
        int max = 0;
        for (int i = 0; i < this.raster.length; i++) {
            for (int j = 0; j < this.raster[0].length; j++) {
                if (this.raster[i][j] > max) max = this.raster[i][j];
            }
        }        
        
        return max;
    }
    
    public BufferedImage getAsImage() {
        BufferedImage img = new BufferedImage(this.raster.length, this.raster[0].length, BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster writableRaster = img.getRaster();
        double factor = (1.0 / (this.getMaximalValue() - this.getMinimalValue())) * 255;
        
        for (int i = 0; i < this.raster.length; i++) {
            for (int j = 0; j < this.raster[0].length; j++) {
                double value = this.raster[i][j];
                value = value * factor;
                writableRaster.setSample(i, j, 0, value);
            }
        }    
        
        return img;
    }
    
    public Set<Point> findClusters2(int squareSize, int threshold) {
        HashMap<Point, Object> map = new HashMap<Point, Object>();
        ArrayList<Point> list = new ArrayList<Point>();
        int halfSquaresize = (int) (squareSize / 2);
        
        for (int i = 0; i < this.raster.length; i++) {
            for (int j = 0; j < this.raster[0].length; j++) {
                if (this.raster[i][j] >= threshold) {
                    Point p = new Point(i, j);
                    map.put(p, null);
                    list.add(p);
                }
            }
        }
        
        for (Point p : list) {
            if (!map.containsKey(p)) continue;
            for (int i = -halfSquaresize; i <= halfSquaresize; i++) {
                for (int j = -halfSquaresize; j <= halfSquaresize; j++) {
                    if (i == 0 && j == 0) continue;
                    if (p.x + i < 0 || p.y + j < 0 || p.x + i >= this.raster.length || p.y + j >= this.raster[0].length) continue; //This could be removed, make perfomance test!
                    map.remove(new Point(p.x + i, p.y + j));
                }
            }
        }
        
        return map.keySet();
    }
    
    public ArrayList<Point> findClusters(int squareSize, int stepsize, int bestN) {
        TreeMap<Integer, Point> map = new TreeMap<Integer, Point>();
        //ArrayList<Cluster> clusterCenters = new ArrayList<Cluster>();
        int halfSquaresize = (int) (squareSize / 2);
        
        for (int i = 0; i + squareSize <= this.raster.length; i = i + stepsize) {
            for (int j = 0; j + squareSize <= this.raster[0].length; j = j + stepsize) {
                int sum = 0;
                for (int h = 0; h < squareSize; h++) {
                    for (int k = 0; k < squareSize; k++) {
                        sum = sum + this.raster[i + h][j + k];
                    }
                }
                map.put(sum, new Point(i + halfSquaresize, j + halfSquaresize));
            }
        }
        
        ArrayList<Point> points = new ArrayList<Point>();
        for (int i = 0; i < bestN && !map.isEmpty(); i++) {
            points.add(map.pollLastEntry().getValue());
        }
       
        return points;
    }
}
