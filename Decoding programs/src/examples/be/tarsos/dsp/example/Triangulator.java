package be.tarsos.dsp.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


/**
 * Created by Moose on 28/08/2015.
 */
public class Triangulator extends JFrame {

    public Location location1;
    public Location location2;
    public Location location3;
    public Location location4;
    public int noOfLocations;
    boolean initialized;
    CircleCircleIntersection loc1And2, loc1And3, loc2And3, loc1And4, loc2And4, loc3And4;
    TriangulationAnimation visualizer;
    private Vector2 loc1Centre, loc2Centre, loc3Centre, loc4Centre;
    private float[] userLocation;
    int multiplier;
    float maxX;
    float maxY;

    Triangulator(Location loc1, Location loc2, Location loc3, Location loc4) {
        location1 = loc1;
        location2 = loc2;
        location3 = loc3;
        location4 = loc4;

        loc1Centre = new Vector2(loc1.getLocation()[0], loc1.getLocation()[1]);
        loc2Centre = new Vector2(loc2.getLocation()[0], loc2.getLocation()[1]);
        loc3Centre = new Vector2(loc3.getLocation()[0], loc3.getLocation()[1]);
        loc4Centre = new Vector2(loc4.getLocation()[0], loc4.getLocation()[1]);

        userLocation = new float[2];
        noOfLocations = 4;

        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                visualizer = new TriangulationAnimation(location1, location2, location3, location4, userLocation,
                        loc1And2, loc1And3, loc2And3, loc1And4, loc2And4, loc3And4);
                createAndShowGUI(visualizer);
            }
        });
    }


    Triangulator(Location loc1, Location loc2, Location loc3) {
        location1 = loc1;
        location2 = loc2;
        location3 = loc3;

        loc1Centre = new Vector2(loc1.getLocation()[0], loc1.getLocation()[1]);
        loc2Centre = new Vector2(loc2.getLocation()[0], loc2.getLocation()[1]);
        loc3Centre = new Vector2(loc3.getLocation()[0], loc3.getLocation()[1]);

        userLocation = new float[2];
        noOfLocations = 3;

        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                visualizer = new TriangulationAnimation(location1, location2, location3, userLocation, loc1And2, loc1And3, loc2And3);
                createAndShowGUI(visualizer);
            }
        });

    }

    private static void createAndShowGUI(TriangulationAnimation visualizer) {

        JFrame f = new JFrame("Location Visualizer");
        f.add(visualizer);
        f.pack();
        f.setVisible(true);
    }

    public float[] getUserLocation() {  return userLocation;    }

    public void setUserLocation(){
        multiplier = 150;
        Vector2 centreOfRadicals = new Vector2(0,0);

        //location3.setCurrentDistance(1);
        Circle loc1Circ = new Circle(loc1Centre, location1.getCurrentDistance());
        Circle loc2Circ = new Circle(loc2Centre, location2.getCurrentDistance());
        Circle loc3Circ = new Circle(loc3Centre, location3.getCurrentDistance());

        loc1And2 = new CircleCircleIntersection(loc1Circ, loc2Circ);
        loc1And3 = new CircleCircleIntersection(loc1Circ, loc3Circ);
        loc2And3 = new CircleCircleIntersection(loc2Circ, loc3Circ);

        //visualizer.setRadicals(loc1And2, visualizer.loc12);
        //visualizer.setRadicals(loc1And3, visualizer.loc13);
        //visualizer.setRadicals(loc2And3, visualizer.loc23);

        if(noOfLocations == 3) {
            centreOfRadicals = loc1And2.radicalPoint.add(loc1And3.radicalPoint).add(loc2And3.radicalPoint).scale(0.3333);

            if(loc1Circ.r < loc2Circ.r && loc1Circ.r < loc3Circ.r){
                double modifier = loc1Circ.r - (loc2Circ.r + loc3Circ.r)/2;
                centreOfRadicals = centreOfRadicals.add(loc1Circ.c).scale(0.5);
            }
            if(loc2Circ.r < loc1Circ.r/2 && loc2Circ.r < loc3Circ.r/2){
                centreOfRadicals = centreOfRadicals.add(loc2Circ.c).scale(0.5);
            }
            if(loc3Circ.r < loc2Circ.r/2 && loc3Circ.r < loc1Circ.r/2){
                centreOfRadicals = centreOfRadicals.add(loc3Circ.c).scale(0.5);
            }
        }
                //.add(loc2And3.radicalPoint).scale(0.33);
           // centreOfRadicals = loc1And3.radicalPoint;}

        if(noOfLocations == 4) {
            System.out.println("4locs)");
            Circle loc4Circ = new Circle(loc4Centre, location4.getCurrentDistance());
            CircleCircleIntersection loc1And4 = new CircleCircleIntersection(loc1Circ, loc4Circ);
            CircleCircleIntersection loc2And4 = new CircleCircleIntersection(loc2Circ, loc4Circ);
            CircleCircleIntersection loc3And4 = new CircleCircleIntersection(loc3Circ, loc4Circ);

            //visualizer.setRadicals(loc1And4, visualizer.loc14);
            //visualizer.setRadicals(loc2And4, visualizer.loc24);
            //visualizer.setRadicals(loc3And4, visualizer.loc34);

            System.out.println("Radical12: " + loc1And2.radicalPoint.x + "," + loc1And2.radicalPoint.y);
            System.out.println("Radical13: " + loc1And3.radicalPoint.x + "," + loc1And3.radicalPoint.y);
            System.out.println("Radical23: " + loc2And3.radicalPoint.x + "," + loc2And3.radicalPoint.y);

            centreOfRadicals = loc1And2.radicalPoint.add(loc1And3.radicalPoint).add(loc2And3.radicalPoint)
                    .add(loc1And4.radicalPoint).add(loc2And4.radicalPoint).add(loc3And4.radicalPoint).scale(0.16667);
        }
        userLocation = new float[] { (float) centreOfRadicals.x, (float) centreOfRadicals.y } ;
       //visualizer.moveUserSprite( Math.round(userLocation[0] / 1500), Math.round(userLocation[1] / 1500));
        System.out.println("User location: (" + userLocation[0] + "," + userLocation[1] + ")");
    }

    public void animate() {
       // updateTriangulationAnimation();
        visualizer.moveUserSprite(200 + Math.round(userLocation[0] * multiplier),50 + Math.round(userLocation[1] * multiplier));
    }

    private void updateTriangulationAnimation(){
        visualizer.location1 = location1;
        visualizer.location2 = location2;
        visualizer.location3 = location3;
        visualizer.userLocation = userLocation;
        visualizer.loc12 = loc1And2;
        visualizer.loc13 = loc1And3;
        visualizer.loc23 = loc2And3;
    }

    public void updateIntersects(){
        visualizer.setRadicals(this.loc1And2, visualizer.loc12);
        visualizer.setRadicals(this.loc1And3, visualizer.loc13);
        visualizer.setRadicals(this.loc2And3, visualizer.loc23);
        if(location4 != null) {
            visualizer.setRadicals(this.loc1And4, visualizer.loc14);
            visualizer.setRadicals(this.loc3And4, visualizer.loc34);
            visualizer.setRadicals(this.loc2And4, visualizer.loc24);
        }
    }

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
    }
}


