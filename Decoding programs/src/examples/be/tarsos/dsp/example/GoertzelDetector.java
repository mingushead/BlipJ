/*
*      _______                       _____   _____ _____  
*     |__   __|                     |  __ \ / ____|  __ \ 
*        | | __ _ _ __ ___  ___  ___| |  | | (___ | |__) |
*        | |/ _` | '__/ __|/ _ \/ __| |  | |\___ \|  ___/ 
*        | | (_| | |  \__ \ (_) \__ \ |__| |____) | |     
*        |_|\__,_|_|  |___/\___/|___/_____/|_____/|_|     
*                                                         
* -------------------------------------------------------------
*
* TarsosDSP is developed by Joren Six at IPEM, University Ghent
*  
* -------------------------------------------------------------
*
*  Info: http://0110.be/tag/TarsosDSP
*  Github: https://github.com/JorenSix/TarsosDSP
*  Releases: http://0110.be/releases/TarsosDSP/
*  
*  TarsosDSP includes modified source code by various authors,
*  for credits and info, see README.
* 
*/


package be.tarsos.dsp.example;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.filters.BandPass;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;
import be.tarsos.dsp.pitch.*;

public class GoertzelDetector extends JFrame {

    float sampleRate = 96000;
    int bitDepth = 16;
    int bufferSize = 2048;
    int overlap = 1024;

    public double[] toneFreq = {18000};
    private static final long serialVersionUID = 3501426880288136245L;
    private final JTextArea textArea;
    public double arrivalTime;
    private float offset;
    float[] filteredAudioData = {};
    double[] recentFilteredDbPeak = {-120.0, 0};
    double recentUnfilteredDbPeak = -120.0;
    int noOfDetections = 0;
    double sumOfFilteredAmps = 0;
    double sumOfUnfilteredAmps = 0;
    double avOfFilteredAmps = 0;
    double avOfUnfilteredAmps = 0;

    JPanel myPanel, freqPanel;
    JTextArea nearDistTxt, medDistTxt, farDistTxt, nearLvlTxt, medLvlTxt, farLvlTxt, freqTxt;
    JButton myButton, freqButton;

    String myString;

    private AudioDispatcher dispatcher;
    private BeepDetectionHandler beepMatriculator;
    private DistanceMeasurer distanceMeasurer;

    private AudioProcessor goertzelAudioProcessor = new Goertzel(sampleRate, bufferSize, toneFreq, new Goertzel.FrequenciesDetectedHandler() {
        @Override
        public void handleDetectedFrequencies(final double[] frequencies, final double[] powers, final double[] allFrequencies, final double allPowers[], final float[] audioData) {
            //get and log temporal data for latest detection
            long newTime = System.currentTimeMillis();
            double timeSinceLast = newTime - arrivalTime;
            int missedBeats = (int) Math.round(((timeSinceLast) / 1000) - 1);
            arrivalTime = newTime;
            for (int i = 0; i < missedBeats; i++) {
                System.out.print("missed beat");
                if (i > 0) System.out.print("x" + (1 + i));
                System.out.print("\n");
            }

            filteredAudioData = applyBandPassFilter(audioData.clone());
            if (getSoundPressureLevel(audioData) - getSoundPressureLevel(filteredAudioData) < 3) {
                if (timeSinceLast > 500) {
                    System.out.println("Arrival time: " + System.currentTimeMillis() % 1000);
                }

                noOfDetections++;
                sumOfFilteredAmps += getSoundPressureLevel(filteredAudioData);
                //System.out.println("this peak = " + getSoundPressureLevel(filteredAudioData));
                sumOfUnfilteredAmps += getSoundPressureLevel(audioData);
                avOfFilteredAmps = sumOfFilteredAmps / noOfDetections;
                avOfUnfilteredAmps = sumOfUnfilteredAmps / noOfDetections;

                if (getSoundPressureLevel(filteredAudioData) > recentFilteredDbPeak[0]) {
                    recentFilteredDbPeak[0] = getSoundPressureLevel(filteredAudioData);
                    recentFilteredDbPeak[1] = arrivalTime % 10000;//+
                    offset = getOffset(filteredAudioData).offset;
                }
                if (getSoundPressureLevel(audioData) > recentUnfilteredDbPeak)
                    recentUnfilteredDbPeak = getSoundPressureLevel(audioData);

                if (timeSinceLast > 500) {
                    //System.out.println("Peak: \t" + recentFilteredDbPeak[0] + "\t" + recentFilteredDbPeak[1]);
                    beepMatriculator.populateIncoming(recentFilteredDbPeak[0], beepMatriculator.getIncomingPower());

                    String myDistance, myDbs, myProb;
                    myDistance = myDbs = myProb = "";

                    myDistance = myDistance.format("%.3f", distanceMeasurer.getCurrentDistance(beepMatriculator.getWindowAndProb(beepMatriculator.getIncomingPower(), 10)[0]));
                    myDbs = myDbs.format("%.3f", recentFilteredDbPeak[0]);
                    myProb = myProb.format("%.2f", (beepMatriculator.getWindowAndProb(beepMatriculator.getIncomingPower(), 10)[1] * 100));
                    myString = "You are " + myDistance + "m away from your device. \n I am " + myProb + "% sure. (" + myDbs + "dB)";

                    //System.out.println(myString);
                    recentFilteredDbPeak[0] = -120.0;
                    recentUnfilteredDbPeak = -120.0;
                    sumOfFilteredAmps = 0;
                    sumOfUnfilteredAmps = 0;
                    avOfFilteredAmps = 0;
                    avOfUnfilteredAmps = 0;
                    noOfDetections = 0;
                }
            }

            textArea.setText(myString);
        }
    });

    public GoertzelDetector() {
        this.setLayout(new GridLayout(0, 1));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Goertzel Pitch Detector");

        JPanel inputPanel = new InputPanel();
        add(inputPanel);
        inputPanel.addPropertyChangeListener("mixer",
                new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent arg0) {
                        try {
                            setNewMixer((Mixer) arg0.getNewValue());
                            arrivalTime = System.currentTimeMillis();
                        } catch (LineUnavailableException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (UnsupportedAudioFileException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                });


        textArea = new JTextArea();
        textArea.setEditable(false);
        add(new JScrollPane(textArea));
        beepMatriculator = new BeepDetectionHandler();
        distanceMeasurer = new DistanceMeasurer(new double[]{1, 2, 4}, new double[]{-50, -56, -62});

        Insets inset = new Insets(5, 35, 5, 35);
        myPanel = new JPanel();
        myPanel.setBorder(BorderFactory.createTitledBorder("Calibration - Input the location and observed amplitude data for microphone in use\""));
        //nearPanel.setToolTipText("Input the location and observed amplitude data for microphone in use");
        JPanel inner1Near = new JPanel();
        myPanel.add(inner1Near);
        JPanel inner1Med = new JPanel();
        myPanel.add(inner1Med);
        JPanel inner1Far = new JPanel();
        myPanel.add(inner1Far);
        JPanel inner1Loc = new JPanel();
        myPanel.add(inner1Loc);
        inner1Near.setBorder(BorderFactory.createTitledBorder("Near reference: (metres - dB)"));
        inner1Med.setBorder(BorderFactory.createTitledBorder("Med reference: (metres - dB)"));
        inner1Far.setBorder(BorderFactory.createTitledBorder("Far reference: (metres - dB)"));
        inner1Loc.setBorder(BorderFactory.createTitledBorder("Position of mic: (x,y)"));
        nearDistTxt = new JTextArea("1");
        nearDistTxt.setMargin(inset);
        nearLvlTxt = new JTextArea("-50.00");
        nearLvlTxt.setMargin(inset);
        medDistTxt = new JTextArea("2");
        medDistTxt.setMargin(inset);
        medLvlTxt = new JTextArea("-56.00");
        medLvlTxt.setMargin(inset);
        farDistTxt = new JTextArea("4");
        farDistTxt.setMargin(inset);
        farLvlTxt = new JTextArea("-62.00");
        farLvlTxt.setMargin(inset);
        inner1Near.add(nearDistTxt);
        inner1Near.add(nearLvlTxt);
        inner1Med.add(medDistTxt);
        inner1Med.add(medLvlTxt);
        inner1Far.add(farDistTxt);
        inner1Far.add(farLvlTxt);

        myButton = new JButton("Recalibrate distance/level references");
        //JPanel buttonPanel = new JPanel();
        //buttonPanel.add(myButton);
        //buttonPanel.setSize(new Dimension(400, 100));

        myButton.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        recalibrate();
                    }
                });
        myPanel.add(myButton);
        //this.getContentPane( ).add(buttonPanel, BorderLayout.SOUTH);
        this.getContentPane().add(myPanel, BorderLayout.CENTER);

    }

    private void recalibrate() {
        double[] newDistValues = new double[3];
        double[] newLvlValues = new double[3];
        newDistValues[0] = Double.parseDouble(nearDistTxt.getText());
        newDistValues[1] = Double.parseDouble(medDistTxt.getText());
        newDistValues[2] = Double.parseDouble(farDistTxt.getText());

        newLvlValues[0] = Double.parseDouble(nearLvlTxt.getText());
        newLvlValues[1] = Double.parseDouble(medLvlTxt.getText());
        newLvlValues[2] = Double.parseDouble(farLvlTxt.getText());

        distanceMeasurer = new DistanceMeasurer(newDistValues, newLvlValues);

    }

    public static void main(String... strings) throws InterruptedException,
            InvocationTargetException {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    //ignore failure to set default look en feel;
                }
                JFrame frame = new GoertzelDetector();
                frame.pack();
                frame.setVisible(true);
            }
        });
    }

    private void setNewMixer(Mixer mixer) throws LineUnavailableException,
            UnsupportedAudioFileException {

        if (dispatcher != null) {
            dispatcher.stop();
        }

        textArea.setFont(new Font("Serif", Font.PLAIN, 20));
        textArea.append("Started listening with " + Shared.toLocalString(mixer.getMixerInfo().getName()) + "\n");

        final AudioFormat format = new AudioFormat(sampleRate, bitDepth, 1, true, true);
        final DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, format);
        TargetDataLine line;
        line = (TargetDataLine) mixer.getLine(dataLineInfo);
        final int numberOfSamples = bufferSize;

        line.open(format, numberOfSamples);
        line.start();
        final AudioInputStream stream = new AudioInputStream(line);

        JVMAudioInputStream audioStream = new JVMAudioInputStream(stream);
        // create a new dispatcher
        dispatcher = new AudioDispatcher(audioStream, bufferSize, overlap);

        // add a processor
        //dispatcher.addAudioProcessor(new PitchProcessor(algo, sampleRate, bufferSize, this));
        dispatcher.addAudioProcessor(goertzelAudioProcessor);
        new Thread(dispatcher, "Audio dispatching").start();
    }

    private float[] applyBandPassFilter(float[] audioDataToFilter) {
        float myFreq = (float) toneFreq[0];
        BandPass bandpassFilter = new BandPass(myFreq, myFreq / 10, sampleRate);
        TarsosDSPAudioFormat mTarsosFormat = new TarsosDSPAudioFormat(sampleRate, bitDepth, 1, true, true);
        AudioEvent audioEvent = new AudioEvent(mTarsosFormat);
        audioEvent.setFloatBuffer(audioDataToFilter);
        bandpassFilter.process(audioEvent);

        float[] filteredBuffer = audioEvent.getFloatBuffer();

        return filteredBuffer;
    }

    private Result getOffset(float[] audioData) {
        int offset = 0;
        float max = 0;

        for (int i = 0; i < audioData.length; i++) {
            if (Math.abs(audioData[i]) > 100) {
                max = Math.abs(audioData[i]);
                offset = i;
                Result results = new Result(offset, max);
                return results;
            }
        }

        Result results = new Result(offset, max);
        return results;
    }

    private double getLocalEnergy(final float[] buffer) {
        double power = 0.0D;
        for (float element : buffer) {
            power += element * element;
        }
        return power;
    }

    private double linearToDecibel(final double value) {
        return 20.0 * Math.log10(value);
    }

    private double getSoundPressureLevel(final float[] buffer) {
        double value = Math.pow(getLocalEnergy(buffer), 0.5);
        value = value / buffer.length;
        return linearToDecibel(value);
    }
}

class Result {
    int offset;
    float max;

    public Result(int _offset, float _max) {
        this.offset = _offset;
        this.max = _max;
    }
}


