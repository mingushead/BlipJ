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

/*
package be.tarsos.dsp.example;

import java.awt.GridLayout;
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

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;
import be.tarsos.dsp.pitch.*;
import be.tarsos.dsp.pitch.PitchProcessor.PitchEstimationAlgorithm;


public class DTMFDetector extends JFrame {


    private static final long serialVersionUID = 3501426880288136245L;
    public double arrivalTime;
    private final JTextArea textArea;

    float sampleRate = 44100;
    int bufferSize = 1024;
    int overlap = 512;
    String myString ="";

    private AudioDispatcher dispatcher;
    private Mixer currentMixer;

    private PitchEstimationAlgorithm algo;
    private ActionListener algoChangeListener = new ActionListener(){
        @Override
        public void actionPerformed(final ActionEvent e) {
            String name = e.getActionCommand();
            PitchEstimationAlgorithm newAlgo = PitchEstimationAlgorithm.valueOf(name);
            algo = newAlgo;
            try {
                setNewMixer(currentMixer);
            } catch (LineUnavailableException e1) {
                e1.printStackTrace();
            } catch (UnsupportedAudioFileException e1) {
                e1.printStackTrace();
            }
        }};

    public DTMFDetector() {
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

        algo = PitchEstimationAlgorithm.YIN;

        JPanel pitchDetectionPanel = new PitchDetectionPanel(algoChangeListener);

        add(pitchDetectionPanel);


        textArea = new JTextArea();
        textArea.setEditable(false);
        add(new JScrollPane(textArea));
    }

*/
/*
    private void setNewMixer(Mixer mixer) throws LineUnavailableException,
            UnsupportedAudioFileException {

        if(dispatcher!= null){
            dispatcher.stop();
        }
        currentMixer = mixer;

        textArea.append("Started listening with " + Shared.toLocalString(mixer.getMixerInfo().getName()) + "\n");

        final AudioFormat format = new AudioFormat(sampleRate, 16, 1, true,
                true);
        final DataLine.Info dataLineInfo = new DataLine.Info(
                TargetDataLine.class, format);
        System.out.println(mixer.getSourceLineInfo());
        TargetDataLine line;
        line = (TargetDataLine) mixer.getLine(dataLineInfo);
        final int numberOfSamples = bufferSize;
        line.open(format, numberOfSamples);
        line.start();
        final AudioInputStream stream = new AudioInputStream(line);

        JVMAudioInputStream audioStream = new JVMAudioInputStream(stream);
        // create a new dispatcher
        dispatcher = new AudioDispatcher(audioStream, bufferSize,
                overlap);

        // add a processor
        //dispatcher.addAudioProcessor(new PitchProcessor(algo, sampleRate, bufferSize, this));
        dispatcher.addAudioProcessor(goertzelAudioProcessor);
        new Thread(dispatcher,"Audio dispatching").start();
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

    public static final double[] myBeeps = { 697, 770, 852, 941, 1209, 1336, 1477, 1633, 1709, 2003, 2129, 2203, 2341 };
    private final AudioProcessor goertzelAudioProcessor = new Goertzel(sampleRate, bufferSize, myBeeps, new Goertzel.FrequenciesDetectedHandler() {
        @Override
        public void handleDetectedFrequencies(final double[] frequencies, final double[] powers, final double[] allFrequencies, final double allPowers[]) {

            double timeSinceLast =  System.currentTimeMillis() - arrivalTime;

            long newTime = System.currentTimeMillis();
            timeSinceLast = newTime - arrivalTime;
            int missedBeats = (int) Math.round(((newTime - arrivalTime) / 2000)-1);

            for (int i = 0; i < missedBeats; i++) {
                System.out.println("missed beat");
            }

            if(frequencies.length > 2) {
                myString = "";
                System.out.println(frequencies.length);
                for (int i = 0; i < frequencies.length; i++) {
                    myString += (frequencies[i] + "    " + powers[i] + "\n");
                    //else System.out.println("Out of range - discard. (" + arrivalTime % 1000 + ")");
                }
            }
            textArea.setText(myString);




        }
    });
*/


//}

