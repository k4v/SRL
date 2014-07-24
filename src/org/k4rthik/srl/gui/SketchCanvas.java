package org.k4rthik.srl.gui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.List;

/**
 * Author: Karthik
 * Date  : 7/12/2014.
 */
public class SketchCanvas extends JFrame
{
    ImagePanel imagePanel;
    public SketchCanvas(List<Image> imageList)
    {
        imagePanel = new ImagePanel(imageList);
    }

    public void createAndShowUI()
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                JFrame parentFrame = new JFrame("These pics need labelling");
                parentFrame.add(imagePanel);
                parentFrame.setSize(imagePanel.getPreferredSize());
                parentFrame.setVisible(true);
            }
        });
    }
}

class ImagePanel extends JPanel
{
    private List<Image> imageList;

    private Dimension imageDimensions = new Dimension(0, 0);
    private Dimension canvasDimension = new Dimension(0, 0);

    private final int imagesPerRow = 4;

    public ImagePanel(List<Image> imageList)
    {
        this.imageList = imageList;
        for (Image image : imageList)
        {
            imageDimensions.width = Math.max(imageDimensions.width, image.getWidth(this));
            imageDimensions.height = Math.max(imageDimensions.height, image.getHeight(this));
        }
        // Size of the canvas on which images are drawn
        // Padding to account for window decorations
        canvasDimension = new Dimension(
                imageDimensions.width*Math.min(imagesPerRow, imageList.size()) + 20,
                imageDimensions.height*(1+((imageList.size() - 1)/imagesPerRow)) + 40);
    }

    @Override
    public Dimension getPreferredSize()
    {
        return canvasDimension;
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;

        int i=0;
        for (Image anImage : imageList) {
            g2d.drawImage(anImage,
                    imageDimensions.width*(i%imagesPerRow), imageDimensions.height*(i/imagesPerRow),
                    imageDimensions.width, imageDimensions.height,
                    this);
            i++;
        }
    }
}
