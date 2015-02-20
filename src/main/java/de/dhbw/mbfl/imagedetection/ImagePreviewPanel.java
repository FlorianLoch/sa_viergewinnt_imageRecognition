/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dhbw.mbfl.imagedetection;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 *
 * @author florian
 */
public class ImagePreviewPanel extends JPanel {

    private Point mousePosition = null;
    private BufferedImage img = null;
    private BufferedImage overlay = null;
    private BufferedImage imgToRender = null;

    public ImagePreviewPanel(BufferedImage img, BufferedImage overlay) {
        this(true);
        this.setImage(img);
        this.setOverlay(overlay);
    }

    public ImagePreviewPanel() {
        this(true);
    }

    public ImagePreviewPanel(LayoutManager layout) {
        this(layout, true);
    }

    public ImagePreviewPanel(boolean isDoubleBuffered) {
        this(new FlowLayout(), isDoubleBuffered);
    }

    public ImagePreviewPanel(LayoutManager layout, boolean isDoubleBuffered) {
        super(layout, isDoubleBuffered);
        
        this.setFocusable(true);
        
        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (e.isControlDown() && overlay != null) {
                    imgToRender = overlay;
                }
                else {
                    imgToRender = img;
                }
                
                mousePosition = e.getPoint();
                repaint();
            }
        });

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                mousePosition = null;
                imgToRender = img;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e); //To change body of generated methods, choose Tools | Templates.
            }
            
            
        });

        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem savesImgMenuItem = new JMenuItem("Save as png");
        savesImgMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    JFileChooser fileChooser = new JFileChooser();
                    if (fileChooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) return;
                    
                    ImageIO.write(img, "png", fileChooser.getSelectedFile());
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Image could not be saved!", "Error during saving", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        popupMenu.add(savesImgMenuItem);
        
        this.setComponentPopupMenu(popupMenu);
    }

    public final void setImage(BufferedImage img) {
        this.img = img;
        this.imgToRender = this.img;
        this.setPreferredSize(new Dimension(img.getWidth(), img.getHeight()));
    }
    
    public final void setOverlay(BufferedImage overlay) {
        this.overlay = overlay;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); //To change body of generated methods, choose Tools | Templates.
        if (this.imgToRender == null) {
            return;
        }

        g.drawImage(this.imgToRender, 0, 0, null);

        if (this.mousePosition == null) {
            return;
        }
 
        FontMetrics fontMetrics = g.getFontMetrics();
        String output = "x|y|c: " + this.mousePosition.x + "|" + this.mousePosition.y + "|" + (this.imgToRender.getRGB(this.mousePosition.x, this.mousePosition.y) & 0xFF);        
        
        int x = this.mousePosition.x;
        int y = this.mousePosition.y;
        int width = fontMetrics.stringWidth(output) + 4;
        int height = fontMetrics.getHeight() + 2 + 5;
        
        if (x + width > this.getWidth()) x = x - width;
        if (y + height > this.getHeight()) y = y - height;
        
        g.setColor(Color.YELLOW);
        g.fillRect(x, y, width, height);
        g.setColor(Color.BLACK);
        
        g.drawString(output, x + 2, y + fontMetrics.getHeight() + 1);
    }
}
