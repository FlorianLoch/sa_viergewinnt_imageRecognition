/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dhbw.mbfl.imagedetection.houghStrategy;

import de.dhbw.mbfl.imagedetection.ImagePreviewPanel;
import de.dhbw.mbfl.jconnect4lib.board.Board;
import de.dhbw.mbfl.jconnect4lib.board.Position;
import de.dhbw.mbfl.jconnect4lib.board.Stone;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;

/**
 *
 * @author florian
 */
public class EdgeDetectionFilter {

    private int WIDTH;
    private int HEIGHT;
    private int[][] sobelY = new int[][]{
        {1, 2, 1},
        {0, 0, 0},
        {-1, -2, -1}
    };
    private int[][] sobelX = new int[][]{
        {1, 0, -1},
        {2, 0, -2},
        {1, 0, -1}
    };
    private BufferedImage orig;
    private BufferedImage origGreyscale;
    private BufferedImage edgesX;
    private BufferedImage edgesY;
    private BufferedImage edgesCombined;
    private BufferedImage houghSpace;
    private BufferedImage clustersImg;
    private Board detectedBoard;
    private JFrame frame;

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Max memory: " + Runtime.getRuntime().maxMemory() / 1024 / 1024 + "mebiByte");

            new EdgeDetectionFilter(new File("real_game_small.jpg"));
        }
        else {
            File imgFile = new File(args[0]);

            if (!imgFile.exists()) {
                System.out.println("Input filename is not valid!");
                System.exit(0);
            }

            new EdgeDetectionFilter(imgFile);
        }
    }

    public EdgeDetectionFilter(File imgFile) {
        try {
            long start = System.currentTimeMillis();
            this.orig = ImageIO.read(imgFile);

            this.HEIGHT = this.orig.getHeight();
            this.WIDTH = this.orig.getWidth();

            this.origGreyscale = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_BYTE_GRAY);
            this.origGreyscale.getGraphics().drawImage(this.orig, 0, 0, null);

            //On this the x edges will be detected
            this.edgesX = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_BYTE_GRAY);
            this.edgesX.setData(this.origGreyscale.getData());

            //On this the y edges will be detected
            this.edgesY = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_BYTE_GRAY);
            this.edgesY.setData(this.origGreyscale.getData());

            this.edgesCombined = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_BYTE_GRAY);

            this.applyEdgeFilter(this.edgesX, sobelX);
            this.applyEdgeFilter(this.edgesY, sobelY);

            this.edgesCombined.setData(this.edgesX.getData());

            this.lightenImages(this.edgesCombined, this.edgesY, 50);

            HoughSpace hough = HoughSpace.createHoughSpace(this.edgesCombined, 150, 15, 18, 1);
            this.houghSpace = hough.getAsImage();

            int houghMax = hough.getMaximalValue();
            int squaresize = 15;
            int minClusterSum = (int) (houghMax * 0.60 * Math.pow(squaresize, 2));

            //ArrayList<Point> clusters = hough.findClusters(squaresize, 5, 42);
            Set<Point> clusters = hough.findClusters2(20, (int) (houghMax * 0.8));

            this.clustersImg = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
            this.clustersImg.getGraphics().drawImage(this.houghSpace, 0, 0, null);
            Graphics g = this.clustersImg.getGraphics();
            g.setColor(Color.red);
            for (Point p : clusters) {
                g.fillRect(p.x - squaresize / 2, p.y - squaresize / 2, squaresize, squaresize);
            }
            System.out.println("Clusters found: " + clusters.size());

            //this.detectedBoard = this.detectBoard(clusters, orig, origGreyscale);

            long duration = System.currentTimeMillis() - start;
            System.out.println("Time needed for circle detection: " + duration + "ms");
        } catch (IOException ex) {
            System.out.println("Could not load sample image!");
            return;
        }

        this.frame = new JFrame("Edge detection filter output");
        this.frame.setSize(new Dimension(WIDTH + 20, 800));
        this.frame.setResizable(false);

        ImagePreviewPanel panelEdgesX = new ImagePreviewPanel(this.edgesX, this.orig);

        ImagePreviewPanel panelEdgesY = new ImagePreviewPanel(this.edgesY, this.orig);

        ImagePreviewPanel panelEdgesCombined = new ImagePreviewPanel(this.edgesCombined, this.orig);

        ImagePreviewPanel panelHough = new ImagePreviewPanel(this.houghSpace, this.edgesCombined);

        ImagePreviewPanel panelOrig = new ImagePreviewPanel(this.orig, this.origGreyscale);

        ImagePreviewPanel panelCluster = new ImagePreviewPanel(this.clustersImg, this.orig);

        JTextArea editorPaneBoard = new JTextArea();
        editorPaneBoard.setFont(new Font("monospaced", Font.PLAIN, 12));
        JScrollPane scrollPaneBoard = new JScrollPane(editorPaneBoard);
        scrollPaneBoard.setPreferredSize(new Dimension(WIDTH, HEIGHT));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelOrig, panelHough);
        //splitPane.setLeftComponent(panelOrig);
        //splitPane.setRightComponent(panelHough);
        splitPane.setDividerLocation(0.5);
        splitPane.setPreferredSize(new Dimension(WIDTH, HEIGHT));

        JPanel scrollPanePanel = new JPanel(new GridLayout(7, 1, 0, 10));
        scrollPanePanel.add(panelOrig);
        scrollPanePanel.add(panelEdgesX);
        scrollPanePanel.add(panelEdgesY);
        scrollPanePanel.add(panelEdgesCombined);
        scrollPanePanel.add(panelHough);
        scrollPanePanel.add(panelCluster);
        scrollPanePanel.add(scrollPaneBoard);

        JScrollPane scrollPane = new JScrollPane(scrollPanePanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        this.frame.add(scrollPane);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setVisible(true);
    }

    //
    private void lightenImages(BufferedImage img1, BufferedImage img2, int threshold) {
        if (img1.getWidth() != img2.getWidth() || img2.getHeight() != img1.getHeight()) {
            System.out.println("Images have not the same dimension. No enlightment will be done!");
            return;
        }

        WritableRaster raster = img1.getRaster();

        for (int i = 0; i < img1.getWidth(); i++) {
            for (int j = 0; j < img1.getHeight(); j++) {
                int max = img1.getRGB(i, j) & 0xFF;
                int valImg2 = img2.getRGB(i, j) & 0xFF;
                if (valImg2 > max && valImg2 > threshold) {
                    raster.setSample(i, j, 0, valImg2);
                }
            }
        }
    }

    private void applyEdgeFilter(BufferedImage img, int[][] coefficients) {
        int max = 0;
        BufferedImage copy = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        copy.getGraphics().drawImage(img, 0, 0, null);

        WritableRaster raster = copy.getRaster();

        for (int i = 1; i < img.getHeight() - 1; i++) {
            for (int j = 1; j < img.getWidth() - 1; j++) {
                int sum = 0;
                for (int h = 0; h < coefficients.length; h++) {
                    for (int k = 0; k < coefficients[h].length; k++) {
                        sum += coefficients[h][k] * (img.getRGB(j - 1 + k, i - 1 + h) & 0xFF);
                    }
                }
                sum = Math.abs(sum);
                if (sum > max) {
                    max = sum;
                }
                raster.setSample(j, i, 0, (sum > 255) ? 255 : sum);
            }
        }

        img.setData(copy.getData());
        System.out.println("Max: " + max);
    }

    private Board detectBoard(Set<Point> clusterSet, BufferedImage orig, BufferedImage origGreyscale) {
        int avgStoneGreyscale = this.computeAverageStoneGreyscale(clusterSet, origGreyscale);
        ArrayList<Point> cluster = new ArrayList<Point>();
        
//        TreeMap<Point> cluster = new TreeSet<Point>(new Comparator<Point>() {
//            @Override
//            public int compare(Point o1, Point o2) {
//                //Sort by y
//                if (o1.y < o2.y) {
//                    return -1;
//                }
//                if (o1.y == o2.y) {
//                    return 0;
//                }
//                return 1;
//            }
//        });
        
        System.out.println("ClusterSet site: " + clusterSet.size());

        for (Point p : clusterSet) {
            cluster.add(p);
        }
        
        Collections.sort(cluster, new Comparator<Point>() {
            @Override
            public int compare(Point o1, Point o2) {
                //Sort by y DESCENDING
                if (o1.y < o2.y) {
                    return 1;
                }
                if (o1.y == o2.y) {
                    return 0;
                }
                return -1;
            }
        });
        
        System.out.println("Cluster size: " + cluster.size());
        
        Board b = new Board();
        ArrayList<Point> row = null;
        int counter = 0;
        
        for (Point p : cluster) {
            if (counter % 7 == 0) {
                row = new ArrayList<Point>();
            }
            
            row.add(p);
            
            if (counter % 7 == 6) {
                Collections.sort(row, new Comparator<Point>() {
                    @Override
                    public int compare(Point o1, Point o2) {
                        //Sort by x ASCENDING
                        if (o1.x < o2.x) {
                            return -1;
                        }
                        if (o1.x == o2.x) {
                            return 0;
                        }
                        return 1;
                    }
                });
                
                int i = -6;
                for (Point q : row) {
                    System.out.println(q);
                    Stone s = this.detectStoneColor(q, origGreyscale, avgStoneGreyscale);
                    if (s != null) b.addStone(new Position(counter + i), s);
                    i++;
                }
            }
            counter++;
        }
        System.out.println("Counter:" + counter);
        
        return b;
    }
    
    private int computeAverageStoneGreyscale (Set<Point> cluster, BufferedImage origGreyscale) {
        int sum = 0;
        for (Point p : cluster) {
            sum = sum + (origGreyscale.getRGB(p.x, p.y) & 0xFF);
        }
        return sum / cluster.size();
    }
    
    private Stone detectStoneColor(Point p, BufferedImage origGreyscale, int avgStoneGreyscale) {
        int greyscale = origGreyscale.getRGB(p.x, p.y) & 0xFF;
//        if (greyscale >= avgStoneGreyscale) return Stone.RED;
//        return Stone.YELLOW;
        
        if (between(greyscale, 200, 230)) return Stone.YELLOW;
        if (between(greyscale, 140, 160)) return Stone.RED;
        return null;
    }
    
    private static boolean between(int x, int a, int b) {
        return (x >= a && x <= b);
    }
}
