package com.example.moose.androidbeeper;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import java.util.TimerTask;

/**
 * Created by Moose on 23/08/2015.
 */
public class Beeper extends TimerTask {
    Thread t;
    int sr = 48000;
    public boolean isRunning = false;
   public double frequency;
    public int amp;
    int buffsize;
    public boolean isMuted;
    private short[] samples;

    Beeper(double _frequency, boolean _isMuted, short[] _samples) {
        frequency = _frequency;
        samples = new short[_samples.length];
        samples = _samples;
        if(!_isMuted) { amp = 10000; isMuted = false; }
        else { amp = 0; isMuted = true; }
        System.out.println(amp);
    }

    Beeper(double[] _frequencies, boolean _isMuted, short[] _samples, int length) {

        buffsize = length;
        samples = new short[_samples.length];
        samples = _samples;
        if(!_isMuted) { amp = 10000; isMuted = false; }
        else { amp = 0; isMuted = true; }
        System.out.println(amp);
    }

    public void runt() {

        setupAndPlayAudio();

    }

    public void run() {
        setupAndPlayAudio();
    }
    public void setupAndPlayAudio() {
        // start a new thread to synthesise audio
        t = new Thread() {
            public void run() {
                // set process priority
                setPriority(Thread.MAX_PRIORITY);
                // set the buffer size
              // int buffsize = (int) ((10000 + frequency) - ((10000 + frequency) % frequency));
                buffsize = 9000;
                buffsize*=2;


                // create an audiotrack object
                AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                        sr, AudioFormat.CHANNEL_OUT_MONO,
                        AudioFormat.ENCODING_PCM_16BIT, buffsize,
                        AudioTrack.MODE_STREAM);
               audioTrack.play();
               audioTrack.write(samples, 0, buffsize);
                audioTrack.stop();
                audioTrack.release();
            }
        };
        t.start();
    }
}
