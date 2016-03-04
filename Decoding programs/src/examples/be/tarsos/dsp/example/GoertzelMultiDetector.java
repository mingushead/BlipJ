package be.tarsos.dsp.example;

        import java.awt.*;
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
        import be.tarsos.dsp.AudioEvent;
        import be.tarsos.dsp.AudioProcessor;
        import be.tarsos.dsp.filters.BandPass;
        import be.tarsos.dsp.io.TarsosDSPAudioFormat;
        import be.tarsos.dsp.io.jvm.JVMAudioInputStream;
        import be.tarsos.dsp.pitch.*;


public class GoertzelMultiDetector extends JFrame {

    private static final long serialVersionUID = 3501426880288136245L;
    public double arrivalTime;
    public JTextArea textArea;
    public JTextArea output;
    float sampleRate = 48000;
    int bufferSize = 4096;
    int overlap = 2048;

    private AudioDispatcher dispatcher;
    private BeepDetectionHandler beepMatriculator;
    private DistanceMeasurer distanceMeasurer;

    public GoertzelMultiDetector(String title) {
        this.setLayout(new GridLayout(0, 1));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Goertzel Multi Pitch Detector " + title);

        JPanel inputPanel1 = new InputPanel();
        add(inputPanel1);
        inputPanel1.addPropertyChangeListener("mixer",
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

        output = new JTextArea();
        textArea = new JTextArea();
        textArea.setEditable(false);
        add(new JScrollPane(textArea));
        beepMatriculator = new BeepDetectionHandler();
        distanceMeasurer = new DistanceMeasurer(new double[]{1,2,4},new double[]{-50,-56,-62});

    }

    private void setNewMixer(Mixer mixer) throws LineUnavailableException,
            UnsupportedAudioFileException {

        if(dispatcher!= null){
            dispatcher.stop();
        }
        //currentMixer = mixer;
        textArea.setFont(new Font("Serif",Font.PLAIN, 20));
        textArea.append("Started listening with " + Shared.toLocalString(mixer.getMixerInfo().getName()) + "\n");

        final AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, true);
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
        dispatcher.addAudioProcessor(goertzelAudioProcessor);
        new Thread(dispatcher,"Audio dispatching").start();
    }

    private float[] applyBandPassFilter(float[] audioDataToFilter) {
        float myFreq = (float) myBeeps[0];
        BandPass bandpassFilter = new BandPass(myFreq, myFreq/10, sampleRate);
        TarsosDSPAudioFormat mTarsosFormat = new TarsosDSPAudioFormat(sampleRate, 16, 1, true, true);
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
                GoertzelMultiDetector detector1 = new GoertzelMultiDetector("4");
                GoertzelMultiDetector detector2 = new GoertzelMultiDetector("3");
                GoertzelMultiDetector detector3 = new GoertzelMultiDetector("2");
                GoertzelMultiDetector detector4 = new GoertzelMultiDetector("1");
                JFrame frame = detector1;
                frame.pack();
                frame.setVisible(true);
                JFrame frame2 = detector2;
                frame2.pack();
                frame2.setVisible(true);
                JFrame frame3 = detector3;
                frame3.pack();
                frame3.setVisible(true);
                JFrame frame4 = detector4;
                frame4.pack();
                frame4.setVisible(true);

                JFrame mainFrame = new TriangulationCalibration(detector1, detector2, detector3, detector4);
                mainFrame.pack();
                mainFrame.setVisible(true);

            }
        });
    }

    public static final double[] myBeeps = { 250 };
    float[] filteredAudioData = {};
    double recentFilteredDbPeak = -120.0;
    double recentUnfilteredDbPeak = -120.0;
    int noOfDetections = 0;
    double sumOfFilteredAmps = 0;
    double sumOfUnfilteredAmps = 0;
    double avOfFilteredAmps = 0;
    double avOfUnfilteredAmps = 0;
    String myString;

    private final AudioProcessor goertzelAudioProcessor = new Goertzel(sampleRate, bufferSize, myBeeps, new Goertzel.FrequenciesDetectedHandler() {
        @Override
        public void handleDetectedFrequencies(final double[] frequencies, final double[] powers, final double[] allFrequencies, final double allPowers[], final float[] audioData) {

                filteredAudioData = applyBandPassFilter(audioData.clone());
                if(getSoundPressureLevel(audioData) - getSoundPressureLevel(filteredAudioData) < 3) {
                    System.out.println(getSoundPressureLevel(audioData) - getSoundPressureLevel(filteredAudioData));
                    //get temporal data for latest detection
                    long newTime = System.currentTimeMillis();

                    double timeSinceLast = newTime - arrivalTime;
                    arrivalTime = newTime;

                    //reset averages
                    if(timeSinceLast > 300){
                        System.out.println("reset");
                        recentFilteredDbPeak = -120.0;
                        recentUnfilteredDbPeak = -120.0;
                        sumOfFilteredAmps = 0;
                        sumOfUnfilteredAmps = 0;
                        avOfFilteredAmps = 0;
                        avOfUnfilteredAmps = 0;
                        noOfDetections = 0;
                    }

                    //log missed beats
                    int missedBeats = (int) Math.round(((timeSinceLast) / 2000) - 1);
                    for (int i = 0; i < missedBeats; i++) {
                        System.out.print("missed beat");
                        if(i > 0) System.out.print("x" + (1 + i));
                        System.out.print("\n");
                    }

                    noOfDetections++;
                    sumOfFilteredAmps += getSoundPressureLevel(filteredAudioData);
                    //System.out.println("this peak = " + getSoundPressureLevel(filteredAudioData));
                    sumOfUnfilteredAmps += getSoundPressureLevel(audioData);
                    avOfFilteredAmps = sumOfFilteredAmps/noOfDetections;
                    avOfUnfilteredAmps = sumOfUnfilteredAmps/noOfDetections;

                    if (getSoundPressureLevel(filteredAudioData) > recentFilteredDbPeak) {
                        recentFilteredDbPeak = getSoundPressureLevel(filteredAudioData);
                    }
                    if (getSoundPressureLevel(audioData) > recentUnfilteredDbPeak) {
                        recentUnfilteredDbPeak = getSoundPressureLevel(audioData);
                    }

                    if(timeSinceLast > 500) {

                        System.out.println("Peak: \t" + recentFilteredDbPeak);
                        beepMatriculator.populateIncoming(recentFilteredDbPeak, beepMatriculator.getIncomingPower());

                        String myDistance, myDbs, myProb, myAverageDbs;
                        myDistance = myDbs = myProb = myAverageDbs = "";

                        myDistance = myDistance.format("%.3f", distanceMeasurer.getCurrentDistance(
                                beepMatriculator.getWindowAndProb(beepMatriculator.getIncomingPower(), 10)[0]));
                        myDbs = myDbs.format("%.3f", recentFilteredDbPeak);
                        myAverageDbs = "" + beepMatriculator.getWindowAndProb(beepMatriculator.getIncomingPower(), 10)[0];
                        myProb = myProb.format("%.2f", ( beepMatriculator.getWindowAndProb(beepMatriculator.getIncomingPower(), 10)[1] * 100));
                        //myString = "You are " + myDistance + "m away from your device. \n I am " + myProb + "% sure. (" + myDbs + "dB)";
                        textArea.setText( "" + myDbs);
                        output.setText("" + myDbs);

                    }
                }
        }
    });



}
