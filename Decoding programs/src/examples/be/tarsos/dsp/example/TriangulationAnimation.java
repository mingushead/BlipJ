package be.tarsos.dsp.example;

import be.tarsos.dsp.example.CircleCircleIntersection;
import be.tarsos.dsp.example.Location;

import javax.swing.*;
import java.awt.*;

class TriangulationAnimation extends JPanel {

    public Location location1, location2, location3, location4;
    public CircleCircleIntersection loc12, loc13, loc23, loc14, loc24, loc34;
    public float[] userLocation;
    private int screenWidth = 1000; private int screenHeight = 650;
    //float bounds = getTheseBounds();
    //private int multiplier = Math.round(450/bounds);
    private int multiplier = 150;
    private int squareW = 20;
    private float maxX, maxY;
    private int currUserX, currUserY;

    /*private int squareX = Math.round(userLocation[0] * multiplier);
    private int squareY = Math.round(userLocation[1] * multiplier);
    private float maxX, maxY;


    private int mic1X = Math.round(location1.getLocation()[0] * multiplier);
    private int mic1Y = Math.round(location1.getLocation()[1] * multiplier);
    private int mic2X = Math.round(location2.getLocation()[0] * multiplier);
    private int mic2Y = Math.round(location2.getLocation()[1] * multiplier);
    private int mic3X = Math.round(location3.getLocation()[0] * multiplier);
    private int mic3Y = Math.round(location3.getLocation()[1] * multiplier);*/
    private int squareX, squareY, mic1X, mic1Y, mic2X, mic2Y, mic3X, mic3Y, mic4X, mic4Y;

    // private int mic4X = location4.getLocation()[0] * multiplier;
    // private int mic4Y = location4.getLocation()[1] * multiplier;
    private int micDim = 30;
/*
    private float getTheseBounds() {
        float[] xPoints = new float[] {location1.getLocation()[0],location2.getLocation()[0],location3.getLocation()[0]} ;
        float[] yPoints = new float[] {location1.getLocation()[1],location2.getLocation()[1],location3.getLocation()[1]} ;
        maxX = xPoints[0];
        maxY = yPoints[0];
        for(int i = 0; i < xPoints.length; i++){
            if (xPoints[i] > maxX) maxX = xPoints[i];
            if (yPoints[i] > maxY) maxY = yPoints[i];
        }
        System.out.println(maxX + " " + maxY);
        if(maxY > maxX) return maxY;
        else return maxX;
    }*/

    void setRadicals(CircleCircleIntersection newIntersect, CircleCircleIntersection oldIntersect) {
        oldIntersect = newIntersect;
    }

    public TriangulationAnimation(Location loc1, Location loc2, Location loc3, float[] userPos, CircleCircleIntersection _loc12, CircleCircleIntersection _loc13, CircleCircleIntersection _loc23) {
        location1 = loc1;
        location2 = loc2;
        location3 = loc3;
        loc12 = _loc12;
        loc13 = _loc13;
        loc23 = _loc23;
        userLocation = new float[]{userPos[0], userPos[1]};
        // setBorder(BorderFactory.createLineBorder(Color.black));
        setupDims();

    }

    public TriangulationAnimation(Location loc1, Location loc2, Location loc3, Location loc4, float[] userPos, CircleCircleIntersection _loc12, CircleCircleIntersection _loc13, CircleCircleIntersection _loc23,
                   CircleCircleIntersection _loc14, CircleCircleIntersection _loc24, CircleCircleIntersection _loc34) {
        location1 = loc1;
        location2 = loc2;
        location3 = loc3;
        location4 = loc4;
        loc12 = _loc12;
        loc13 = _loc13;
        loc23 = _loc23;
        loc14 = _loc14;
        loc24 = _loc24;
        loc34 = _loc34;
        userLocation = new float[]{userPos[0], userPos[1]};
        // setBorder(BorderFactory.createLineBorder(Color.black));
        setupDims();

    }
    private void setupDims(){
        squareX = Math.round(userLocation[0] * multiplier);
        squareY = Math.round(userLocation[1] * multiplier);

        mic1X = Math.round(location1.getLocation()[0] * multiplier);
        mic1Y = Math.round(location1.getLocation()[1] * multiplier);
        mic2X = Math.round(location2.getLocation()[0] * multiplier);
        mic2Y = Math.round(location2.getLocation()[1] * multiplier);
        mic3X = Math.round(location3.getLocation()[0] * multiplier);
        mic3Y = Math.round(location3.getLocation()[1] * multiplier);
        if(location4 != null) {
            mic4X = Math.round(location4.getLocation()[0] * multiplier);
            mic4Y = Math.round(location4.getLocation()[1] * multiplier);
        }
    }

    public void moveUserSprite(int x, int y) {

        int OFFSET = 1;
        if ((this.squareX!=x) || (this.squareY!=y)) {
            repaint(this.squareX, this.squareY, this.squareW + OFFSET, this.squareW + OFFSET);
            this.squareX=x-(this.squareW/2);
            this.squareY=y-(this.squareW/2);
            repaint(this.squareX, this.squareY, this.squareW + OFFSET, this.squareW + OFFSET);
        }
    }


    public Dimension getPreferredSize() {
        return new Dimension(screenWidth, screenHeight);
    }

    protected void paintComponent(Graphics g) {
        repaint();
        super.paintComponent(g);

        //draw microphones in their respective positions
        //mic 1 (Green)
        g.setColor(new Color(0, 128, 0));
        g.fillOval(mic1X + 200 - (micDim / 2), mic1Y + 50 - (micDim / 2), micDim, micDim);
        g.setColor(new Color(64, 128, 64, 50));
        g.drawOval((int) (mic1X + 200 - ((this.location1.getCurrentDistance() * multiplier))), mic1Y + 50 - (int) ((this.location1.getCurrentDistance() * multiplier)), (int) (this.location1.getCurrentDistance() * multiplier * 2), (int) (this.location1.getCurrentDistance() * multiplier * 2));
        g.setColor(new Color(0, 255, 0, 20));
        g.fillOval((int) (mic1X + 200 - ((this.location1.getCurrentDistance() * multiplier))), mic1Y + 50 - (int) ((this.location1.getCurrentDistance() * multiplier)), (int) (this.location1.getCurrentDistance() * multiplier * 2), (int) (this.location1.getCurrentDistance() * multiplier * 2));

        g.setColor(new Color(0, 0, 0, 128));
       /* g.drawLine((int) Math.round(200 - (this.loc12.radicalPoint.x * multiplier)), (int) Math.round(200 - (this.loc12.radicalPoint.y * multiplier)),
                (int) Math.round(200 - (this.loc13.radicalPoint.x * multiplier)), (int) Math.round(200 - (this.loc13.radicalPoint.y * multiplier)));
        //g.drawLine((int) Math.round(200 - (this.loc23.radicalPoint.x * multiplier)), (int) Math.round(200 - (this.loc23.radicalPoint.y * multiplier)),
                (int) Math.round(200 - (this.loc13.radicalPoint.x * multiplier)), (int) Math.round(200 - (this.loc13.radicalPoint.y * multiplier)));
        //g.drawLine((int) Math.round(200 - (this.loc12.radicalPoint.x * multiplier)), (int) Math.round(200 - (this.loc12.radicalPoint.y * multiplier)),
                (int) Math.round(200 - (this.loc23.radicalPoint.x * multiplier)), (int) Math.round(200 - (this.loc23.radicalPoint.y * multiplier)));
       */// System.out.println("Rad1: " + this.loc12.radicalPoint.x);
        //System.out.println("Distance from source: " + this.location1.getCurrentDistance());
        //mic 2 (red)
        g.setColor(new Color(128, 0, 0));
        g.fillOval(mic2X + 200 - (micDim / 2), mic2Y + 50 - (micDim / 2), micDim, micDim);
        g.setColor(new Color(128, 64, 64, 50));
        g.drawOval((int) (mic2X + 200 - ((this.location2.getCurrentDistance() * multiplier))), mic2Y + 50 - (int) ((this.location2.getCurrentDistance() * multiplier)), (int) (this.location2.getCurrentDistance() * multiplier * 2), (int) (this.location2.getCurrentDistance() * multiplier * 2));
        g.setColor(new Color(255, 0, 0, 40));
        g.fillOval((int) (mic2X + 200 - ((this.location2.getCurrentDistance() * multiplier))), mic2Y + 50 - (int) ((this.location2.getCurrentDistance() * multiplier)), (int) (this.location2.getCurrentDistance() * multiplier * 2), (int) (this.location2.getCurrentDistance() * multiplier * 2));

        g.setColor(new Color(0, 0, 128));
        g.fillOval(mic3X + 200 - (micDim / 2), mic3Y + 50 - (micDim / 2), micDim, micDim);
        g.setColor(new Color(64, 64, 128, 50));
        g.drawOval((int) (mic3X + 200 - ((this.location3.getCurrentDistance() * multiplier))), mic3Y + 50 - (int) ((this.location3.getCurrentDistance() * multiplier)), (int) (this.location3.getCurrentDistance() * multiplier * 2), (int) (this.location3.getCurrentDistance() * multiplier * 2));
        g.setColor(new Color(0, 0, 255, 40));
        g.fillOval((int) (mic3X + 200 - ((this.location3.getCurrentDistance() * multiplier))), mic3Y + 50 - (int) ((this.location3.getCurrentDistance() * multiplier)), (int) (this.location3.getCurrentDistance() * multiplier * 2), (int) (this.location3.getCurrentDistance() * multiplier * 2));
        g.setColor(new Color(128, 0, 128));
        g.fillOval(mic4X + 200 - (micDim / 2), mic4Y + 50 - (micDim / 2), micDim, micDim);
        if(location4 != null) {
            g.setColor(new Color(128, 64, 128, 50));
            g.drawOval((int) (mic4X + 200 - ((this.location4.getCurrentDistance() * multiplier))), mic4Y + 50 - (int) ((this.location4.getCurrentDistance() * multiplier)), (int) (this.location4.getCurrentDistance() * multiplier * 2), (int) (this.location4.getCurrentDistance() * multiplier * 2));
            g.setColor(new Color(255, 0, 255, 40));
            g.fillOval((int) (mic4X + 200 - ((this.location4.getCurrentDistance() * multiplier))), mic4Y + 50 - (int) ((this.location4.getCurrentDistance() * multiplier)), (int) (this.location4.getCurrentDistance() * multiplier * 2), (int) (this.location4.getCurrentDistance() * multiplier * 2));
        }
        g.setColor(Color.BLACK);
        //draw user's location
        if(currUserX < squareX-(this.squareW/2)) currUserX+=2 ;
        else currUserX-= 2;
        if(currUserY < squareY-(this.squareW/2)) currUserY+= 2;
        else currUserY-= 2;
        g.fillOval(currUserX, currUserY, squareW, squareW);
        g.drawOval(currUserX, currUserY, squareW, squareW);

    }
}
