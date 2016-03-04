package com.example.moose.androidbeeper;

/**
 * Created by Moose on 25/08/2015.
 */
public class SynGen {


    public SynGen(){
    }

    public short[] getSyntheticData(double[] _frequencies, int _amp){

        double[] frequencies = _frequencies;
        int sr = 48000;
        int amp = _amp;
        //int buffSize =  (int) ((10000 + frequency) - ((10000 + frequency) % frequency));
        int buffSize = 9000;
        buffSize *= 2;
        short[] audioData = new short[buffSize];

        double twopi = 8.*Math.atan(1.);
        double ph1 = 0.0;
        double ph2 = 0.0;
        double ph3 = 0.0;
        double ph4 = 0.0;

        // synthesis loop
        for(int i=0; i < buffSize; i++) {
            audioData[i] = (short) ((amp * Math.sin(ph1) / 3) + (amp * Math.sin(ph2) / 3) + (amp * Math.sin(ph3) / 3) );
            ph1 += (twopi * frequencies[0] / sr);
            ph2 += (twopi * (frequencies[1]) / sr);
            ph3 += (twopi * (frequencies[2]) / sr);
        }

         return audioData;
    }

    public short[] getSyntheticData(double _frequency, int _amp){

        double frequency = _frequency;
        int sr = 48000;
        int amp = _amp;
        //int buffSize =  (int) ((10000 + frequency) - ((10000 + frequency) % frequency));
        int buffSize = 9000;
        buffSize *= 2;
        short[] audioData = new short[buffSize];

        double twopi = 8.*Math.atan(1.);
        double ph = 0.0;

        // synthesis loop
        for(int i=0; i < buffSize; i++) {
            audioData[i] = (short) (amp * Math.sin(ph));
            ph += (twopi * frequency / sr);
        }

        return audioData;
    }

}
