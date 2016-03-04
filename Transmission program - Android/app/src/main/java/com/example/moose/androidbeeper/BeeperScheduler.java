package com.example.moose.androidbeeper;

import java.util.Timer;

/**
 * Created by Moose on 23/08/2015.
 */
public class BeeperScheduler {

    private Timer timer;
    Beeper myBeeper;
    public short[] samples;

    public BeeperScheduler(Beeper beeper) {
        samples = new SynGen().getSyntheticData(beeper.frequency, 10000);
        myBeeper = new Beeper(beeper.frequency, beeper.isMuted, samples);
        myBeeper = beeper;
        System.out.println("constructed " + beeper.frequency + " " + beeper.isMuted);
    }

    void startBeeper(){
        timer = new Timer();
        timer.schedule(myBeeper, 0, 1000);
        System.out.println("timer's up");
    }

    void stopBeeper(){
        timer.cancel();
        timer.purge();
        System.out.println("timer's stopped");
    }
}
