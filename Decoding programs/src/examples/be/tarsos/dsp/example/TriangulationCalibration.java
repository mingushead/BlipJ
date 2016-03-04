package be.tarsos.dsp.example;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

/**
 * Created by Moose on 29/08/2015.
 */
public class TriangulationCalibration extends JFrame{

        GoertzelMultiDetector   detector1,      detector2,      detector3,      detector4;
        Location                location1,      location2,      location3,      location4;
        JTextArea               detectText1,    detectText2,    detectText3,    detectText4;
        Triangulator            triangulator;
        JTextArea               textArea;
        JButton                 button3, button4;

        JPanel      location1Panel,         location2Panel,         location3Panel,         location4Panel;
        JTextArea   location1NearRefDist,   location2NearRefDist,   location3NearRefDist,  location4NearRefDist,
                    location1NearRefLevel,  location2NearRefLevel,  location3NearRefLevel, location4NearRefLevel,
                    location1FarRefDist,    location2FarRefDist,    location3FarRefDist,   location4FarRefDist,
                    location1FarRefLevel,   location2FarRefLevel,   location3FarRefLevel,  location4FarRefLevel,
                    location1MedRefDist,    location2MedRefDist,    location3MedRefDist,   location4MedRefDist,
                    location1MedRefLevel,   location2MedRefLevel,   location3MedRefLevel,  location4MedRefLevel,
                    location1locX,          location2locX,          location3locX,         location4locX,
                    location1locY,          location2locY,          location3locY,         location4locY;
        String[]    textIds = {"1", "1", "1", "1"};
        String      resultString1,          resultString2,          resultString3,         resultString4,    errorMsg;;
        private boolean isTriangulatorSetup = false;

        public TriangulationCalibration(GoertzelMultiDetector _detector1, GoertzelMultiDetector _detector2, GoertzelMultiDetector _detector3) {
            detector1 = _detector1;
            detector2 = _detector2;
            detector3 = _detector3;

            detectText1 = new JTextArea();
            detectText1 = detector1.output;

            detectText1.getDocument().addDocumentListener(new MyDocumentListener());

            detectText2 = new JTextArea();
            detectText2 = detector2.output;

            detectText2.getDocument().addDocumentListener(new MyDocumentListener());

            detectText3 = new JTextArea();
            detectText3 = detector3.output;

            detectText3.getDocument().addDocumentListener(new MyDocumentListener());

            setupForm();

            this.setLayout(new GridLayout(0, 1));
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.setTitle("Calibration for triangulation");

            button3 = new JButton("Setup Triangulator (3 mics)");
            button4 = new JButton("Setup Triangulator (4 mics)");
            JPanel buttonPanel = new JPanel();
            buttonPanel.add(button3);
            buttonPanel.add(button4);
            buttonPanel.setSize(new Dimension(400, 100));
            JPanel textPanel = new JPanel();
            textPanel.setPreferredSize(new Dimension(500, 400));
            textArea = new JTextArea();
            textArea.setEditable(false);
            textPanel.add(textArea);

            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
           // this.setSize(1000, 700);
            this.getContentPane( ).add(textPanel, BorderLayout.CENTER);
            this.getContentPane( ).add(buttonPanel, BorderLayout.SOUTH);
            this.setVisible(true);
            float[] arr = {0,0};

            // Add event handler for  button
            button3.addActionListener(
                    new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            setupLocs();
                            triangulator = new Triangulator(location1,location2,location3);
                            triangulator.setUserLocation();
                            isTriangulatorSetup = true;
                        }
                    });
            button4.addActionListener(
                    new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            setupLocs();
                            triangulator = new Triangulator(location1,location2,location3, location4);
                            triangulator.setUserLocation();
                            isTriangulatorSetup = true;
                        }
                    });
        }

    public TriangulationCalibration(GoertzelMultiDetector _detector1, GoertzelMultiDetector _detector2, GoertzelMultiDetector _detector3, GoertzelMultiDetector _detector4) {
        detector1 = _detector1;
        detector2 = _detector2;
        detector3 = _detector3;
        detector4 = _detector4;

        detectText1 = new JTextArea();
        detectText1 = detector1.output;

        detectText1.getDocument().addDocumentListener(new MyDocumentListener());

        detectText2 = new JTextArea();
        detectText2 = detector2.output;

        detectText2.getDocument().addDocumentListener(new MyDocumentListener());

        detectText3 = new JTextArea();
        detectText3 = detector3.output;

        detectText3.getDocument().addDocumentListener(new MyDocumentListener());

        detectText4 = new JTextArea();
        detectText4 = detector4.output;

        detectText4.getDocument().addDocumentListener(new MyDocumentListener());

        setupForm();

        this.setLayout(new GridLayout(0, 1));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Calibration for triangulation");

        button3 = new JButton("Setup Triangulator (3 mics)");
        button4 = new JButton("Setup Triangulator (4 mics)");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(button3);
        buttonPanel.add(button4);
        JPanel textPanel = new JPanel();
        textArea = new JTextArea();
        textArea.setEditable(false);
        textPanel.add(textArea);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(300, 200);
        this.getContentPane( ).add(textPanel, BorderLayout.CENTER);
        this.getContentPane( ).add(buttonPanel, BorderLayout.AFTER_LAST_LINE);
        this.setVisible(true);
        float[] arr = {0,0};

        // Add event handler for  button
        button3.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        setupLocs();
                        triangulator = new Triangulator(location1,location2,location3);
                        triangulator.setUserLocation();
                        isTriangulatorSetup = true;
                    }
                });
        button4.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        setupLocs();
                        triangulator = new Triangulator(location1,location2,location3, location4);
                        triangulator.setUserLocation();
                        isTriangulatorSetup = true;
                    }
                });
    }



        void setResults(String id, String myString){
            System.out.println(textIds[0] + " " + textIds[1] + " " + textIds[2] + " " + textIds[3]);
            int whichMic = 4;
            for(int i = 0; i < textIds.length; i++) {
                if (Objects.equals(id, textIds[i])) {whichMic = i; }
            }
            switch(whichMic){
                case 0: resultString1 = myString;
                    if(isTriangulatorSetup) {
                        triangulator.location1.setCurrentDistanceFromCurrentDecibels(Double.parseDouble(myString));
                        triangulator.setUserLocation();
                        triangulator.updateIntersects();
                        triangulator.animate();
                         }
                    errorMsg = "";
                    break;
                case 1: resultString2 = myString;
                    if(isTriangulatorSetup) {
                        triangulator.location2.setCurrentDistanceFromCurrentDecibels(Double.parseDouble(myString));
                        triangulator.setUserLocation();
                        triangulator.animate();
                         }
                    errorMsg = "";
                    break;
                case 2: resultString3 = myString;
                    if(isTriangulatorSetup) {
                        triangulator.location3.setCurrentDistanceFromCurrentDecibels(Double.parseDouble(myString));
                        triangulator.setUserLocation();
                        triangulator.animate();
                        }
                    errorMsg = "";
                    break;
                case 3: resultString4 = myString;
                    if(isTriangulatorSetup) {
                        triangulator.location4.setCurrentDistanceFromCurrentDecibels(Double.parseDouble(myString));
                        triangulator.setUserLocation();
                        triangulator.animate();
                    }
                    errorMsg = "";
                    break;
                case 4: errorMsg = "whichMic is 3";
                    break;
                default: errorMsg = "There was an unidentified error";
                    break;
            }
            // System.out.println("myString" + myString + " myID: " + id + " textID; " + textIds[0]);
            textArea.setText("Input 1: " + resultString1 + "\n"
                    + "Input 2: " + resultString2 + "\n" +
                    "Input 3: " + resultString3 + "\n" +
                    "Input 4: " + resultString4 + "\n"
                    + errorMsg);
        }

        void populateIds(String id) {

            outerLoop:
            for(int i = 0; i < textIds.length; i++){
                if(Objects.equals(textIds[i], id)) break outerLoop;
                if(Objects.equals(textIds[i], "1")){
                    textIds[i] = id;
                    break outerLoop;
                }
            }
        }


        void setupForm() {
            Insets inset = new Insets(5,35,5,35);
            location1Panel = new JPanel();
            location1Panel.setBorder(BorderFactory.createTitledBorder("Data for microphone 1:"));
            location1Panel.setToolTipText("Input the location and observed amplitude data for microphone 1");
            JPanel inner1Near = new JPanel(); location1Panel.add(inner1Near);
            JPanel inner1Med = new JPanel(); location1Panel.add(inner1Med);
            JPanel inner1Far = new JPanel(); location1Panel.add(inner1Far);
            JPanel inner1Loc = new JPanel(); location1Panel.add(inner1Loc);
            inner1Near.setBorder(BorderFactory.createTitledBorder("Near reference: (metres - dB)"));
            inner1Med.setBorder(BorderFactory.createTitledBorder("Med reference: (metres - dB)"));
            inner1Far.setBorder(BorderFactory.createTitledBorder("Far reference: (metres - dB)"));
            inner1Loc.setBorder(BorderFactory.createTitledBorder("Position of mic: (x,y)"));
            location1NearRefDist = new JTextArea("1");
            location1NearRefDist.setMargin(inset);
            location1NearRefLevel = new JTextArea("-71.81");
            location1NearRefLevel.setMargin(inset);
            location1MedRefDist = new JTextArea("1.9");
            location1MedRefDist.setMargin(inset);
            location1MedRefLevel = new JTextArea("-74.35");
            location1MedRefLevel.setMargin(inset);
            location1FarRefDist = new JTextArea("3.7");
            location1FarRefDist.setMargin(inset);
            location1FarRefLevel = new JTextArea("-89.17");
            location1FarRefLevel.setMargin(inset);
            location1locX = new JTextArea("0");
            location1locX.setMargin(inset);
            location1locY = new JTextArea("0");
            location1locY.setMargin(inset);
            inner1Near.add(location1NearRefDist);
            inner1Near.add(location1NearRefLevel);
            inner1Med.add(location1MedRefDist);
            inner1Med.add(location1MedRefLevel);
            inner1Far.add(location1FarRefDist);
            inner1Far.add(location1FarRefLevel);
            inner1Loc.add(location1locX);
            inner1Loc.add(location1locY);
            this.getContentPane( ).add(location1Panel, BorderLayout.CENTER);

            location2Panel = new JPanel();
            location2Panel.setBorder(BorderFactory.createTitledBorder("Data for microphone 2:"));
            location2Panel.setToolTipText("Input the location and observed amplitude data for microphone 2");
            JPanel inner2Near = new JPanel(); location2Panel.add(inner2Near);
            JPanel inner2Med = new JPanel(); location2Panel.add(inner2Med);
            JPanel inner2Far = new JPanel(); location2Panel.add(inner2Far);
            JPanel inner2Loc = new JPanel(); location2Panel.add(inner2Loc);
            inner2Near.setBorder(BorderFactory.createTitledBorder("Near reference: (metres - dB)"));
            inner2Med.setBorder(BorderFactory.createTitledBorder("Med reference: (metres - dB)"));
            inner2Far.setBorder(BorderFactory.createTitledBorder("Far reference: (metres - dB)"));
            inner2Loc.setBorder(BorderFactory.createTitledBorder("Position of mic: (x,y)"));
            location2NearRefDist = new JTextArea("1");
            location2NearRefDist.setMargin(inset);
            location2NearRefLevel = new JTextArea("-73.02");
            location2NearRefLevel.setMargin(inset);
            location2MedRefDist = new JTextArea("1.9");
            location2MedRefDist.setMargin(inset);
            location2MedRefLevel = new JTextArea("-77.96");
            location2MedRefLevel.setMargin(inset);
            location2FarRefDist = new JTextArea("3.7");
            location2FarRefDist.setMargin(inset);
            location2FarRefLevel = new JTextArea("-93.08");
            location2FarRefLevel.setMargin(inset);
            location2locX = new JTextArea("3");
            location2locX.setMargin(inset);
            location2locY = new JTextArea("0");
            location2locY.setMargin(inset);
            inner2Near.add(location2NearRefDist);
            inner2Near.add(location2NearRefLevel);
            inner2Med.add(location2MedRefDist);
            inner2Med.add(location2MedRefLevel);
            inner2Far.add(location2FarRefDist);
            inner2Far.add(location2FarRefLevel);
            inner2Loc.add(location2locX);
            inner2Loc.add(location2locY);
            this.getContentPane( ).add(location2Panel, BorderLayout.CENTER);

            location3Panel = new JPanel();
            location3Panel.setBorder(BorderFactory.createTitledBorder("Data for microphone 3:"));
            location3Panel.setToolTipText("Input the location and observed amplitude data for microphone 3");
            JPanel inner3Near = new JPanel(); location3Panel.add(inner3Near);
            JPanel inner3Med = new JPanel(); location3Panel.add(inner3Med);
            JPanel inner3Far = new JPanel(); location3Panel.add(inner3Far);
            JPanel inner3Loc = new JPanel(); location3Panel.add(inner3Loc);
            inner3Near.setBorder(BorderFactory.createTitledBorder("Near reference: (metres - dB)"));
            inner3Med.setBorder(BorderFactory.createTitledBorder("Med reference: (metres - dB)"));
            inner3Far.setBorder(BorderFactory.createTitledBorder("Far reference: (metres - dB)"));
            inner3Loc.setBorder(BorderFactory.createTitledBorder("Position of mic: (x,y)"));
            location3NearRefDist = new JTextArea("1");
            location3NearRefDist.setMargin(inset);
            location3NearRefLevel = new JTextArea("-66.2");
            location3NearRefLevel.setMargin(inset);
            location3MedRefDist = new JTextArea("1.9");
            location3MedRefDist.setMargin(inset);
            location3MedRefLevel = new JTextArea("-77.4");
            location3MedRefLevel.setMargin(inset);
            location3FarRefDist = new JTextArea("3.7");
            location3FarRefDist.setMargin(inset);
            location3FarRefLevel = new JTextArea("-92.16");
            location3FarRefLevel.setMargin(inset);
            location3locX = new JTextArea("1.5");
            location3locX.setMargin(inset);
            location3locY = new JTextArea("3");
            location3locY.setMargin(inset);
            inner3Near.add(location3NearRefDist);
            inner3Near.add(location3NearRefLevel);
            inner3Med.add(location3MedRefDist);
            inner3Med.add(location3MedRefLevel);
            inner3Far.add(location3FarRefDist);
            inner3Far.add(location3FarRefLevel);
            inner3Loc.add(location3locX);
            inner3Loc.add(location3locY);
            this.getContentPane( ).add(location3Panel, BorderLayout.CENTER);

            location4Panel = new JPanel();
            location4Panel.setBorder(BorderFactory.createTitledBorder("Data for microphone 4:"));
            location4Panel.setToolTipText("Input the location and observed amplitude data for microphone 4");
            JPanel inner4Near = new JPanel(); location4Panel.add(inner4Near);
            JPanel inner4Med = new JPanel(); location4Panel.add(inner4Med);
            JPanel inner4Far = new JPanel(); location4Panel.add(inner4Far);
            JPanel inner4Loc = new JPanel(); location4Panel.add(inner4Loc);
            inner4Near.setBorder(BorderFactory.createTitledBorder("Near reference: (metres - dB)"));
            inner4Med.setBorder(BorderFactory.createTitledBorder("Med reference: (metres - dB)"));
            inner4Far.setBorder(BorderFactory.createTitledBorder("Far reference: (metres - dB)"));
            inner4Loc.setBorder(BorderFactory.createTitledBorder("Position of mic: (x,y)"));
            location4NearRefDist = new JTextArea("1");
            location4NearRefDist.setMargin(inset);
            location4NearRefLevel = new JTextArea("-73.02");
            location4NearRefLevel.setMargin(inset);
            location4MedRefDist = new JTextArea("1.9");
            location4MedRefDist.setMargin(inset);
            location4MedRefLevel = new JTextArea("-77.96");
            location4MedRefLevel.setMargin(inset);
            location4FarRefDist = new JTextArea("3.7");
            location4FarRefDist.setMargin(inset);
            location4FarRefLevel = new JTextArea("-93.08");
            location4FarRefLevel.setMargin(inset);
            location4locX = new JTextArea("4");
            location4locX.setMargin(inset);
            location4locY = new JTextArea("4");
            location4locY.setMargin(inset);
            inner4Near.add(location4NearRefDist);
            inner4Near.add(location4NearRefLevel);
            inner4Med.add(location4MedRefDist);
            inner4Med.add(location4MedRefLevel);
            inner4Far.add(location4FarRefDist);
            inner4Far.add(location4FarRefLevel);
            inner4Loc.add(location4locX);
            inner4Loc.add(location4locY);
            this.getContentPane( ).add(location4Panel, BorderLayout.CENTER);
        }

        void setupLocs(){
            location1 = new Location(new float[]{Float.parseFloat(location1NearRefDist.getText()),
                    Float.parseFloat(location1NearRefLevel.getText())},
                    new float[]{Float.parseFloat(location1MedRefDist.getText()),
                            Float.parseFloat(location1MedRefLevel.getText())},
                    new float[]{Float.parseFloat(location1FarRefDist.getText()),
                            Float.parseFloat(location1FarRefLevel.getText())},
                    new float[]{Float.parseFloat(location1locX.getText()),Float.parseFloat(location1locY.getText())},1);

            location2 = new Location(new float[]{Float.parseFloat(location2NearRefDist.getText()),
                    Float.parseFloat(location2NearRefLevel.getText())},
                    new float[]{Float.parseFloat(location2MedRefDist.getText()),
                            Float.parseFloat(location2MedRefLevel.getText())},
                    new float[]{Float.parseFloat(location2FarRefDist.getText()),
                            Float.parseFloat(location2FarRefLevel.getText())},
                    new float[]{Float.parseFloat(location2locX.getText()),Float.parseFloat(location2locY.getText())},1);

            location3 = new Location(new float[]{Float.parseFloat(location3NearRefDist.getText()),
                    Float.parseFloat(location3NearRefLevel.getText())},
                    new float[]{Float.parseFloat(location3MedRefDist.getText()),
                            Float.parseFloat(location3MedRefLevel.getText())},
                    new float[]{Float.parseFloat(location3FarRefDist.getText()),
                            Float.parseFloat(location3FarRefLevel.getText())},
                    new float[]{Float.parseFloat(location3locX.getText()),Float.parseFloat(location3locY.getText())},1);

            location4 = new Location(new float[]{Float.parseFloat(location4NearRefDist.getText()),
                    Float.parseFloat(location4NearRefLevel.getText())},
                    new float[]{Float.parseFloat(location4MedRefDist.getText()),
                            Float.parseFloat(location4MedRefLevel.getText())},
                    new float[]{Float.parseFloat(location4FarRefDist.getText()),
                            Float.parseFloat(location4FarRefLevel.getText())},
                    new float[]{Float.parseFloat(location4locX.getText()),Float.parseFloat(location4locY.getText())},1);
        }

        class MyDocumentListener implements DocumentListener {
            String newline = "\n";

            public void insertUpdate(DocumentEvent e) {
                displayEditInfo(e);
                updateLog(e, "inserted into");
            }

            public void removeUpdate(DocumentEvent e) {
                //displayEditInfo(e);
                updateLog(e, "removed from");
            }

            public void changedUpdate(DocumentEvent e) {
                //displayEditInfo(e);
                //Plain text components do not fire these events
            }

            public void updateLog(DocumentEvent e, String action) {
                //displayEditInfo(e);
            }

            private void displayEditInfo(DocumentEvent e) {
                Document document = (Document)e.getDocument();
                int changeLength = e.getLength();
                textArea.append(e.getType().toString() + ": "
                        + changeLength + " character"
                        + ((changeLength == 1) ? ". " : "s. ")
                        + " Text length = " + document.getLength()
                        + "." + newline);
                String test = "";
                String lineId = "";
                try {
                    test = e.getDocument().getText(0, e.getDocument().getEndPosition().getOffset());
                    lineId = e.getDocument().toString().replace("javax.swing.text.PlainDocument@", "");
                    populateIds(lineId);
                    setResults(lineId, test);
                }
                catch (BadLocationException exception) {

                }
                //textArea.setText("Input " + document + ": " + test + "\n");
            }

        }
    }

