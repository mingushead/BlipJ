
/**
 * Created by Moose on 16/09/2015.
 */


package be.tarsos.dsp.example;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.filters.BandPass;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;
import be.tarsos.dsp.pitch.Goertzel;

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
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class TTMFDetector extends JFrame {

    private static final long serialVersionUID = 3501426880288136245L;
    public double arrivalTime;
    private final JTextArea textArea;

    float sampleRate = 96000;
    int bitDepth = 16;
    int bufferSize = 2048;
    int overlap = 1024;
    public static final double[] toneFreq= { 60000 };

    private AudioDispatcher dispatcher;
    private Mixer currentMixer;
    private BeepDetectionHandler beepMatriculator;
    private DistanceMeasurer distanceMeasurer;
    private TTMFHandler ttmfHandler;
    private TTMFDetectionFilter ttmfFilter;

    private ActionListener algoChangeListener = new ActionListener(){

        @Override
        public void actionPerformed(final ActionEvent e) {



            try {
                setNewMixer(currentMixer);
            } catch (LineUnavailableException e1) {
                e1.printStackTrace();
            } catch (UnsupportedAudioFileException e1) {
                e1.printStackTrace();
            }
        }};

    public TTMFDetector() {
        this.setLayout(new GridLayout(0, 1));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("TTMF Pitch Detector");

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


        JPanel pitchDetectionPanel = new PitchDetectionPanel(algoChangeListener);

        add(pitchDetectionPanel);


        textArea = new JTextArea();
        textArea.setEditable(false);
        add(new JScrollPane(textArea));
        beepMatriculator = new BeepDetectionHandler();
    }



    private void setNewMixer(Mixer mixer) throws LineUnavailableException,
            UnsupportedAudioFileException {

        if(dispatcher!= null){
            dispatcher.stop();
        }
        currentMixer = mixer;
        textArea.setFont(new Font("Serif",Font.PLAIN, 100));
        textArea.append("Started listening with " + Shared.toLocalString(mixer.getMixerInfo().getName()) + "\n");

        final AudioFormat format = new AudioFormat(sampleRate, bitDepth, 1, true, true);
        final DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, format);
        TargetDataLine line;
        line = (TargetDataLine) mixer.getLine(dataLineInfo);
        final int numberOfSamples = bufferSize;
        System.out.println(format.getSampleRate());
        line.open(format, numberOfSamples);
        line.start();
        final AudioInputStream stream = new AudioInputStream(line);

        JVMAudioInputStream audioStream = new JVMAudioInputStream(stream);
        // create a new dispatcher
        dispatcher = new AudioDispatcher(audioStream, bufferSize, overlap);

        // add a processor
        dispatcher.addAudioProcessor(goertzelAudioProcessor);
        dispatcher.addAudioProcessor(tTMFAudioProcessor);
        new Thread(dispatcher,"Audio dispatching").start();
        ttmfHandler = new TTMFHandler(myBeeps, new int[]{4,3,3});
        ttmfFilter = new TTMFDetectionFilter('#');

    }

    private float[] applyBandPassFilter(float[] audioDataToFilter) {
        float myFreq = (float) toneFreq[0];
        BandPass bandpassFilter = new BandPass(myFreq, myFreq/10, sampleRate);
        TarsosDSPAudioFormat mTarsosFormat = new TarsosDSPAudioFormat(sampleRate, bitDepth, 1, true, true);
        AudioEvent audioEvent = new AudioEvent(mTarsosFormat);
        audioEvent.setFloatBuffer(audioDataToFilter);
        bandpassFilter.process(audioEvent);
        //bandpassFilter.process(audioEvent);

        float[] filteredBuffer = audioEvent.getFloatBuffer();

        return filteredBuffer;
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
                JFrame frame = new TTMFDetector();
                frame.pack();
                frame.setVisible(true);
            }
        });
    }

   //   public static final double[] myBeeps = { 13250, 14000, 14750, 15500, 16250, 17000, 17750, 18500, 19250, 20000 };

     public static final double[] myBeeps = { 750, 1500, 2250, 3000, 3750, 4500, 5250, 6000, 6750, 7500 };
    float[] filteredAudioData = {};
    double recentFilteredDbPeak = -120.0;
    double recentUnfilteredDbPeak = -120.0;
    int noOfDetections = 0;
    double sumOfFilteredAmps = 0;
    double sumOfUnfilteredAmps = 0;
    double avOfFilteredAmps = 0;
    double avOfUnfilteredAmps = 0;
    String myString;
    //char[] letterIndex = new char[27];

    private final AudioProcessor goertzelAudioProcessor = new Goertzel(sampleRate, bufferSize, toneFreq, new Goertzel.FrequenciesDetectedHandler() {
        @Override
        public void handleDetectedFrequencies(final double[] frequencies, final double[] powers, final double[] allFrequencies, final double allPowers[], final float[] audioData) {

            //get and log temporal data for latest detection
            long newTime = System.currentTimeMillis();
            double timeSinceLast = newTime - arrivalTime;
            int missedBeats = (int) Math.round(((timeSinceLast) / 2000) - 1);
            arrivalTime = newTime;
            for (int i = 0; i < missedBeats; i++) {
                System.out.print("missed beat");
                if(i > 0) System.out.print("x" + (1 + i));
                System.out.print("\n");
            }

            //establish whether new detection is a false positive, by comparing it's detection time with it's expected detection time
            if (timeSinceLast > 0) {

                filteredAudioData = applyBandPassFilter(audioData.clone());
                if(getSoundPressureLevel(audioData) - getSoundPressureLevel(filteredAudioData) > -20) {


                    noOfDetections++;
                    sumOfFilteredAmps += getSoundPressureLevel(filteredAudioData);
                    //System.out.println("this peak = " + getSoundPressureLevel(filteredAudioData));
                    sumOfUnfilteredAmps += getSoundPressureLevel(audioData);
                    avOfFilteredAmps = sumOfFilteredAmps/noOfDetections;
                    avOfUnfilteredAmps = sumOfUnfilteredAmps/noOfDetections;

                    if (getSoundPressureLevel(filteredAudioData) > recentFilteredDbPeak) recentFilteredDbPeak = getSoundPressureLevel(filteredAudioData);
                    if (getSoundPressureLevel(audioData) > recentUnfilteredDbPeak) recentUnfilteredDbPeak = getSoundPressureLevel(audioData);

                    if(timeSinceLast > 500) {
                        //find detections offset within the audioDatabuffer

                        //if (getSoundPressureLevel(filteredAudioData) > recentFilteredDbPeak) recentFilteredDbPeak = getSoundPressureLevel(filteredAudioData);
                        //if (getSoundPressureLevel(audioData) > recentUnfilteredDbPeak) recentUnfilteredDbPeak = getSoundPressureLevel(audioData);

                        //noOfDetections++;
                        //sumOfFilteredAmps += getSoundPressureLevel(filteredAudioData);
                        //sumOfUnfilteredAmps += getSoundPressureLevel(audioData);
                        //avOfFilteredAmps = sumOfFilteredAmps/noOfDetections;
                        //avOfUnfilteredAmps = sumOfUnfilteredAmps/noOfDetections;

                        System.out.println("Peak: \t" + recentFilteredDbPeak);
                        beepMatriculator.populateIncoming(recentFilteredDbPeak, beepMatriculator.getIncomingPower());

                        String myDistance, myDbs, myProb;
                        myDistance = myDbs = myProb = "";

                        myDistance = myDistance.format("%.3f", distanceMeasurer.getCurrentDistance(beepMatriculator.getWindowAndProb(beepMatriculator.getIncomingPower(), 10)[0]));
                        myDbs = myDbs.format("%.3f", recentFilteredDbPeak);
                        myProb = myProb.format("%.2f", ( beepMatriculator.getWindowAndProb(beepMatriculator.getIncomingPower(), 10)[1] * 100));
                        myString = "You are " + myDistance + "m away from your device. \n I am " + myProb + "% sure. (" + myDbs + "dB)";
                        myString = "" + myDbs;


                        recentFilteredDbPeak = -120.0;
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
            else //System.out.println("Out of range - discard. (" + arrivalTime % 1000 + ")");
                textArea.setText(myString);
        }
    });

    private final AudioProcessor tTMFAudioProcessor = new Goertzel(sampleRate, bufferSize, myBeeps, new Goertzel.FrequenciesDetectedHandler() {
        @Override
        public void handleDetectedFrequencies(final double[] frequencies, final double[] powers, final double[] allFrequencies, final double allPowers[], final float[] audioData) {
            if(frequencies.length == 3) {
                //get and log temporal data for latest detection
                long newTime = System.currentTimeMillis();
                double timeSinceLast = newTime - arrivalTime;
                int missedBeats = (int) Math.round(((timeSinceLast) / 2000) - 1);
                arrivalTime = newTime;
                //System.out.println("Freq1: " + frequencies[0] + "\t" + "Freq2: " + frequencies[1] + "\t" + "Freq3: " + frequencies[2] + "\t");
                String freqString = "";
                for (int i = 0; i < frequencies.length; i++) {
                    freqString += frequencies[i] + "\t";
                }
                //System.out.println(freqString);
                TTMFResult result = ttmfHandler.parseTTMF(frequencies);
                MessageResult messageResult;
                if(result.charResult != 'E') {
                    messageResult =  ttmfFilter.populateIncoming(result.charResult);
                    if(messageResult.valid && messageResult.text.length() > 5 ) textArea.setText(messageResult.text);
                }
                System.out.print(result.charResult /*+ "\t" + System.currentTimeMillis() % 10000*/);
            }

        }
    });

}

