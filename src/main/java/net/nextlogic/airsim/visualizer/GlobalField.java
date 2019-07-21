package net.nextlogic.airsim.visualizer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GlobalField extends JPanel implements KeyListener {

    private int panelWidth;
    private int panelHeight;

    private double boundary;
    private double captureL;

    public final static double headerAngle = Math.PI/18;
    public final static double headerLength = 1;

    // current positions and poses
    private Point2D pPos = null;
    private double pTheta;
    private Point2D ePos = null;
    private double eTheta;

    private List<Line2D> pursuerPath = new ArrayList<Line2D>();
    private List<Line2D> evaderPath = new ArrayList<Line2D>();

    private JFrame frame;

    private char keyPressed = 0;

    /**
     * Constructor
     */
    public GlobalField(int panelSize, double b, double l) {
        panelWidth = panelSize;
        panelHeight = panelSize;
        boundary = b;
        captureL = l;

        frame = new JFrame("Playing Field");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(panelWidth, panelHeight);
        frame.add(this);
        frame.addKeyListener(this);
        frame.setVisible(true);
    }

    public void saveImage(String filename) {
        BufferedImage image = new BufferedImage(panelWidth, panelHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();
        paint(g);

        try {
            ImageIO.write(image, "png", new File(filename));
        } catch (IOException ex) {
            System.out.println("Error saving image '" + filename + ".png'");
        }
    }

    public int scaleWorldToPanelWidth(double value) {
        return (int) ((panelWidth*(value+boundary))/(2*boundary));
    }

    public int scaleWorldToPanelHeight(double value) {
        return (int) ((panelHeight*(value+boundary))/(2*boundary));
    }

    public void resetBoundaryForMax() {
        double maxBoundary = boundary;

        double x, y;
        Point2D endpoint;
        for (Line2D evaderSegment : evaderPath) {
            endpoint = evaderSegment.getP2();

            x = Math.abs(endpoint.getX());
            y = Math.abs(endpoint.getY());

            if (x > maxBoundary) {
                maxBoundary = x;
            }

            if (y > maxBoundary) {
                maxBoundary = y;
            }
        }

        for (Line2D pursuerSegment : pursuerPath) {
            endpoint = pursuerSegment.getP2();

            x = Math.abs(endpoint.getX());
            y = Math.abs(endpoint.getY());

            if (x > maxBoundary) {
                maxBoundary = x;
            }

            if (y > maxBoundary) {
                maxBoundary = y;
            }
        }

        boundary = maxBoundary;
    }

    /**
     * Sets pursuer segments to draw
     * @param input List of Point2D
     */
    public void setPursuerPath(List<Point2D> input) {
        clearPursuer();

        for (int i = 0; i < input.size() - 1; i++) {
            pursuerPath.add(new Line2D.Float(input.get(i), input.get(i + 1)));
        }
    }

    /**
     * Sets evader segments to draw
     * @param input List of Point2D
     */
    public void setEvaderPath(List<Point2D> input) {
        clearEvader();
        for (int i = 0; i < input.size() - 1; i++) {
            evaderPath.add(new Line2D.Float(input.get(i), input.get(i + 1)));
        }
    }

    /**
     * Adds pursuer segment to draw, coordinates between 0 and 1
     * @param input Line2D
     */
    public void addPursuerSegment(Line2D input) {
        pursuerPath.add(input);
    }

    /**
     * Adds evader segment to draw, coordinates between 0 and 1
     * @param input Line2D
     */
    public void addEvaderSegment(Line2D input) {
        evaderPath.add(input);
    }

    public void setPursuerState(Point2D pos, double d) {
        pPos = pos;
        pTheta = d;
    }

    public void setEvaderState(Point2D pos, double theta) {
        ePos = pos;
        eTheta = theta;
    }


    public void clearAll() {
        clearPursuer();
        clearEvader();
    }

    public void clearPursuer() {
        pursuerPath.clear();
    }

    public void clearEvader() {
        evaderPath.clear();
    }

    /**
     * Call this to refresh the window
     */
    public void repaint() {
        if (frame != null) {
            frame.repaint();
        }
    }

    /**
     * Overloaded JPanel method
     */
    public void paint(Graphics g) {

        // Update size in case user has resized window
        panelHeight = this.getHeight();
        panelWidth = this.getWidth();

        // White background
        g.setColor(Color.gray);
        g.fillRect(0, 0, panelWidth, panelHeight);

        int x1, y1, x2, y2;


        // Draw evader path
        for (Line2D l : evaderPath) {
            x1 = scaleWorldToPanelWidth(l.getY1());
            y1 = scaleWorldToPanelHeight(l.getX1());
            x2 = scaleWorldToPanelWidth(l.getY2());
            y2 = scaleWorldToPanelHeight(l.getX2());
            g.setColor(Color.blue);
            g.drawLine(x1, panelHeight - y1, x2, panelHeight - y2);
        }

        // Draw pursuer path
        for (Line2D l : pursuerPath) {
            x1 = scaleWorldToPanelWidth(l.getY1());
            y1 = scaleWorldToPanelHeight(l.getX1());
            x2 = scaleWorldToPanelWidth(l.getY2());
            y2 = scaleWorldToPanelHeight(l.getX2());
            g.setColor(Color.red);
            g.drawLine(x1, panelHeight - y1, x2, panelHeight - y2);
        }

        if (pPos != null) {
            g.setColor(Color.red);
            drawStateTriangle(pPos, pTheta, g);

            // Draw pursuer capture radius
            x2 = scaleWorldToPanelWidth(pPos.getY());
            y2 = scaleWorldToPanelHeight(pPos.getX());
            int rx = (int) ((captureL/(2*boundary)) * panelWidth);
            int ry = (int) ((captureL/(2*boundary)) * panelHeight);
            g.setColor(Color.orange);
            g.drawOval(x2 - rx, panelHeight - (y2 + ry), 2*rx, 2*ry);
        }

        if (ePos != null) {
            g.setColor(Color.blue);
            drawStateTriangle(ePos, eTheta, g);
        }
    }

    private void drawStateTriangle(Point2D pos, double pTheta2, Graphics g) {
        double x = pos.getY();
        double y = pos.getX();

        double[] xCoords = {x, x + headerLength*Math.sin(pTheta2 + Math.PI + headerAngle),
                x + headerLength*Math.sin(pTheta2 + Math.PI - headerAngle)};
        double[] yCoords = {y, y + headerLength*Math.cos(pTheta2 + Math.PI + headerAngle),
                y + headerLength*Math.cos(pTheta2 + Math.PI - headerAngle)};

        int[] xCoordsPixels = new int[3];
        int[] yCoordsPixels = new int[3];
        for (int i = 0; i < 3; i++) {
            xCoordsPixels[i] = scaleWorldToPanelWidth(xCoords[i]);
            yCoordsPixels[i] = panelHeight - scaleWorldToPanelHeight(yCoords[i]);
        }

        g.drawPolygon(xCoordsPixels, yCoordsPixels, 3);
    }

    /**
     * Waits until user presses a key, then returns the key pressed
     * @return key pressed as char
     */
    public char waitKey() {
        keyPressed = 0;
        while (keyPressed == 0) {

            // Wait
            try {
                Thread.sleep(25);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return keyPressed;
    }

    /**
     * Waits until user presses a key, then returns the key pressed
     * Will return 0 if timeout occurs
     * @param timeout Max time to wait in milliseconds
     * @return key pressed as char
     */
    public char waitKey(long timeout) {
        long currentTime = System.currentTimeMillis();
        long startTime = System.currentTimeMillis();
        keyPressed = 0;
        while (currentTime - startTime < timeout && keyPressed == 0) {

            // Wait
            try {
                Thread.sleep(25);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            currentTime = System.currentTimeMillis();
        }
        return keyPressed;
    }

    public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub

    }

    public void keyPressed(KeyEvent e) {
        keyPressed = e.getKeyChar();
    }

    public void keyReleased(KeyEvent e) {
        // TODO Auto-generated method stub

    }
}
