package be.tarsos.dsp.example;

/**
 * Created by Moose on 11/08/2015.
 */
public class DistanceMeasurer {

    private double nearDistanceRef ;
    private double farDistanceRef;
    private double mediumDistanceRef;

    private double distanceReferences[] = { nearDistanceRef, mediumDistanceRef, farDistanceRef };

    private double nearSoundLevelRef;
    private double farSoundLevelRef;
    private double mediumSoundLevelRef;

    private double soundLevelReferences[] = { nearSoundLevelRef, mediumSoundLevelRef, farSoundLevelRef };

    private double currentDistance = 0;

    public DistanceMeasurer(double[] _distances, double[] _levelRefs){

        double[] distances = _distances;
        double[] levelRefs = _levelRefs;

        nearDistanceRef = distances[0];
        mediumDistanceRef = distances[1];
        farDistanceRef = distances[2];

        nearSoundLevelRef = levelRefs[0];
        mediumSoundLevelRef = levelRefs[1];
        farSoundLevelRef = levelRefs[2];

    }

    void setnearDistanceRef(double distanceInMetres) {

        nearDistanceRef = distanceInMetres;
    }

    void setFarDistanceRef(double distanceInMetres) {

        farDistanceRef = distanceInMetres;
    }

    void setNearSoundLevelRef(double soundLevelInDb) {

        nearSoundLevelRef = soundLevelInDb;
    }

    void setFarSoundLevelRef(double soundLevelInDb){

        farSoundLevelRef = soundLevelInDb;
    }

    void setCurrentDistance(double distanceInMetres) {

        currentDistance = distanceInMetres;
    }

    double getNearDistanceRef() {

        return nearDistanceRef;
    }

    double getFarDistanceRef() {

        return farDistanceRef;
    }

    double getNearSoundLevelRef() {

        return nearSoundLevelRef;
    }

    double getFarSoundLevelRef() {

        return farSoundLevelRef;
    }

    double getCurrentDistance(double currentSoundLevel){
        double currentDistanceInMetres;
        double nearGuess = Math.pow(nearDistanceRef * Math.pow(10, (nearSoundLevelRef-currentSoundLevel)/20), 1);//ath.pow(nearDistanceRef, 1);
        double mediumGuess = Math.pow(mediumDistanceRef * Math.pow(10, (mediumSoundLevelRef-currentSoundLevel)/20), 1);//farDistanceRef;
        double farGuess = Math.pow(farDistanceRef * Math.pow(10, (farSoundLevelRef-currentSoundLevel)/20), 1);//farDistanceRef;

        if(Math.abs(currentSoundLevel - nearSoundLevelRef) < Math.abs(currentSoundLevel - farSoundLevelRef) && Math.abs(currentSoundLevel - nearSoundLevelRef) < Math.abs(currentSoundLevel - mediumSoundLevelRef)) { currentDistanceInMetres = nearGuess; }

        else if(Math.abs(currentSoundLevel - mediumSoundLevelRef) < Math.abs(currentSoundLevel - nearSoundLevelRef) && Math.abs(currentSoundLevel - mediumSoundLevelRef) < Math.abs(currentSoundLevel - farSoundLevelRef)) { currentDistanceInMetres = mediumGuess; }

        else if(Math.abs(currentSoundLevel - farSoundLevelRef) < Math.abs(currentSoundLevel - nearSoundLevelRef) && Math.abs(currentSoundLevel - farSoundLevelRef) < Math.abs(currentSoundLevel - mediumSoundLevelRef)) { currentDistanceInMetres = farGuess; }

        else currentDistanceInMetres = ( nearGuess + mediumGuess +  farGuess ) /3;

       // System.out.println("Near ref: " + nearDistanceRef * Math.pow(10, (nearSoundLevelRef-currentSoundLevel)/20));
       // System.out.println("Far ref: " +  farDistanceRef * Math.pow(10, (farSoundLevelRef-currentSoundLevel)/20));
       // System.out.println("Guess based on near ref: " + nearGuess);//Math.pow(nearDistanceRef, 1.5));
       // System.out.println("Guess based on medium ref: " +  mediumGuess);
       // System.out.println("Guess based on far ref: " +  farGuess);

        return currentDistanceInMetres;
    }
}
