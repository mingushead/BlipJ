package be.tarsos.dsp.example;

import javax.swing.*;

/**
 * Created by Moose on 18/09/2015.
 */
public class TTMFDetectionFilter {

    char punctuator;
    char[] rawTextStream;
    char[] filteredTextStream;
    boolean isStreamEnded = false;

    public TTMFDetectionFilter(char _punctuator) {
        punctuator = _punctuator;
        rawTextStream = new char[1000];
        filteredTextStream = new char[50];
    }

    public MessageResult populateIncoming(char newChar) {

        MessageResult result;
        if(newChar != '^'){
            isStreamEnded = false;
        }

        for (int i = rawTextStream.length-1; i > 0; i--) {
            rawTextStream[i] = rawTextStream[i-1];
        }
        rawTextStream[0] = newChar;
        if(newChar == '^' && !isStreamEnded) {
            isStreamEnded = true;
            filterRawTextStream();
            String textStream = "";
            for (int i = 0; i < filteredTextStream.length; i++) {
                // System.out.print(filteredTextStream[i]);
                if(filteredTextStream[i] == '^') {
                    result = new MessageResult(textStream, true);
                    rawTextStream = new char[1000];
                    filteredTextStream = new char[50];
                    return result;
                }
                textStream = textStream + filteredTextStream[i];
            }
        }
        result = new MessageResult("", false);
        return result;
    }

    public void filterRawTextStream(){
        System.out.println("start");
        for(int i = 0; i < rawTextStream.length; i++) {
            // System.out.print(rawTextStream[i] + "\t");
        }
        System.out.println("end");
        char currentChar, possibility1, possibility2, possibility3;
        possibility1 = possibility2 = possibility3 = '¬';
        int currentIndexInFiltered, hits1, hits2, hits3;
        currentIndexInFiltered = hits1 = hits2 = hits3 = 0;
        populateFilteredStream(rawTextStream[0]);
        currentChar = rawTextStream[0];
        for(int i = 0; i < rawTextStream.length; i++) {
            if(currentChar != rawTextStream[i]) {

                currentChar = rawTextStream[i];
                if (currentChar != punctuator) {
                    if (currentChar == possibility1) {
                        hits1++;
                    } else if (currentChar == possibility2) {
                        hits2++;
                    } else if (currentChar == possibility3) {
                        hits3++;
                    } else if (possibility1 == '¬') {
                        possibility1 = currentChar;
                        System.out.println("Possibility 1: " + currentChar);
                        hits1++;
                    } else if (possibility2 == '¬') {
                        possibility2 = currentChar;
                        System.out.println("Possibility 2: " + currentChar);
                        hits2++;
                    } else if (possibility3 == '¬') {
                        possibility3 = currentChar;
                        hits3++;
                    } else System.out.println("one too many possibilities");
                } else {
                    if (hits1 != 0 || hits2 != 0 || hits3 != 0) {
                        System.out.println("Got a '.'");
                        if (hits1 > hits2 && hits1 > hits3) {
                            populateFilteredStream(possibility1);
                        } else if (hits2 > hits1 && hits2 > hits3) {
                            populateFilteredStream(possibility2);
                        } else if (hits3 > hits2 && hits3 > hits1) {
                            populateFilteredStream(possibility3);
                        } else populateFilteredStream(possibility1);
                    }
                    hits1 = hits2 = hits3 = 0;
                    possibility1 = possibility2 = possibility3 = '¬';
                }
            }
            //System.out.println("Hits1: " + hits1 + "\t" + "Hits2: " + hits2 + "\t" + "Hits3: " + hits3);
        }
    }
    void populateFilteredStream(char newChar){
        for (int i = filteredTextStream.length-1; i > 0; i--) {
            filteredTextStream[i] = filteredTextStream[i-1];
        }
        filteredTextStream[0] = newChar;
    }
}
class MessageResult{
    public String text;
    public boolean valid;

    public MessageResult(String _text, boolean _valid){
        this.text = _text;
        this.valid = _valid;
    }
}
