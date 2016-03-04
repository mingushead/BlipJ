package com.example.moose.androidbeeper;

import android.app.Activity;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.Arrays.*;

/**
 * Created by Moose on 15/09/2015.
 */
public class TextTransmissionHandler {
    private Timer timer;
    String textToProcess;
    TTMFGenerator ttmfGenerator;
    Beeper[] beepList;


    public TextTransmissionHandler(String txtTransmission, TTMFGenerator generator) {
        textToProcess = txtTransmission;
        ttmfGenerator = generator;
        short[] punctuator = new SynGen().getSyntheticData(ttmfGenerator.getTTMFIndex('#'), 10000);
        short[] messageCloser = new SynGen().getSyntheticData(ttmfGenerator.getTTMFIndex('^'), 10000);
        beepList = new Beeper[(txtTransmission.length()*2) + 2];
        for(int i = 0; i < txtTransmission.length(); i+=1) {
            short[] theseSamples = new SynGen().getSyntheticData(ttmfGenerator.getTTMFIndex(textToProcess.charAt(i)), 10000);
            beepList[(i*2) +1] = new Beeper(new double[]{0,0,0}, false, theseSamples, theseSamples.length);
            beepList[(i*2)+2] = new Beeper(new double[]{0,0,0}, false, punctuator, punctuator.length);
            beepList[0] = new Beeper(new double[]{0,0,0}, false, punctuator, punctuator.length);
        }
        beepList[beepList.length-1] = new Beeper(new double[]{0,0,0}, false, messageCloser, messageCloser.length);
        //System.out.println("constructed " + beeper.frequencies + " " + beeper.isMuted);
    }

    void startBeeper(){

        timer = new Timer();
        for(int i = 0; i < beepList.length; i++) {
            timer.schedule(beepList[i], (long) i * 350);
            // timer.schedule(myBeeper, 0, 250);
        }
        System.out.println("timer's up");
    }
}
