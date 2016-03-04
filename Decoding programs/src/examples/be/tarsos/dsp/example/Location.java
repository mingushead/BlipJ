package be.tarsos.dsp.example;

/**
 * Created by Moose on 29/08/2015.
 */
public class Location {

        //References for previously observed beep detections. Given in the format
        // { (distance from source when beep was detected, in metres) , (amplitude observed at aforementioned distance) }
        private float[] nearReference, medReference, farReference;

        //x,y co-ordinates for the location, within the overall grid. Measurements given in metres.
        private float[] location;

        //Stores the current distance from the sound source
        double currentDistance;

        Location(float[] nearRef, float[] medRef, float[] farRef, float[] loc, double currentDist) {

            nearReference = new float[2];
            medReference = new float[2];
            farReference = new float[2];
            location = new float[2];

            nearReference = nearRef;
            medReference = medRef;
            farReference = farRef;
            location = loc;
            currentDistance = currentDist;

        }

        public float[] getNearReference() {
            return nearReference;
        }

        public float[] getMedReference() {
            return  medReference;
        }

        public float[] getFarReference() {
            return farReference;
        }

        public float[] getLocation() {
            return location;
        }

        public void setFarReference(float[] farReference) {
            this.farReference = farReference;
        }

        public void setMedReference(float[] medReference) {
            this.medReference = medReference;
        }

        public void setNearReference(float[] nearReference) {
            this.nearReference = nearReference;
        }

        public void setLocation(float[] location) {
            this.location = location;
        }

        public void setCurrentDistanceFromCurrentDecibels(double currentSoundLevel) {
            double distanceInMetres = 0;
            double nearGuess = Math.pow(this.getNearReference()[0] * Math.pow(10, (this.getNearReference()[1]-currentSoundLevel)/20), 1);
            double mediumGuess = Math.pow(this.getMedReference()[0] * Math.pow(10, (this.getMedReference()[1]-currentSoundLevel)/20), 1);
            double farGuess = Math.pow(this.getFarReference()[0] * Math.pow(10, (this.getFarReference()[1]-currentSoundLevel)/20), 1);
            if(Math.abs(currentSoundLevel - this.getNearReference()[1]) < Math.abs(currentSoundLevel - this.getFarReference()[1])
                    && Math.abs(currentSoundLevel - this.getNearReference()[1]) < Math.abs(currentSoundLevel - this.getMedReference()[1]))
            { distanceInMetres = nearGuess;
               }

            else if(Math.abs(currentSoundLevel - this.getMedReference()[1]) < Math.abs(currentSoundLevel - this.getNearReference()[1])
                    && Math.abs(currentSoundLevel - this.getMedReference()[1]) < Math.abs(currentSoundLevel - this.getFarReference()[1]))
            { distanceInMetres = mediumGuess;
                }

            else if(Math.abs(currentSoundLevel - this.getFarReference()[1]) < Math.abs(currentSoundLevel - this.getNearReference()[1])
                    && Math.abs(currentSoundLevel - this.getFarReference()[1]) < Math.abs(currentSoundLevel - this.getMedReference()[1]))
            { distanceInMetres = farGuess;
               }

            else{ distanceInMetres = ( nearGuess + mediumGuess +  farGuess ) /3;  System.out.println(nearGuess + "\t" + mediumGuess + "\t" + farGuess);
            } this.currentDistance =  distanceInMetres;
            if(this.currentDistance > 4) this.currentDistance = 4;
        }
        public void setCurrentDistance(double distance){
            this.currentDistance = distance;
        }
        public double getCurrentDistance(){ return this.currentDistance; }
    }

