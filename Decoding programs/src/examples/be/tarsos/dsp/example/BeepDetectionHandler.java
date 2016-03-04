package be.tarsos.dsp.example;

class BeepDetectionHandler {

    private double[] incomingTime = new double[40];
    private double[] incomingPower = new double[40];
    //private float[] isOnTime = new float[20];
    int threshold  = 3;

    //moves everything forward one slot in the a array, and assigns the incoming value to incoming[0]
    public void populateIncoming(double newValue, double[] incoming) {

        for (int i = incoming.length-1; i > 0; i--) {
            incoming[i] = incoming[i-1];
        }
        incoming[0] = newValue;
        //System.out.println(Arrays.toString(getIncomingPower()));
        //return incoming;
    }
    public double[] getIncomingTime() {
        return incomingTime;
    }

    public double[] getIncomingPower() {
        return incomingPower;
    }

    public double[] getWindowAndProb(double[] recordedValues, int length) {
        int whichBeepIsBestForBase[] = new int[recordedValues.length];
        int numberOfBeepsToUse = length;
        double validBeepsForAverage[] = new double[recordedValues.length];
        double toleranceBase;

        for(double index : validBeepsForAverage){
            index = 0;
        }
        for(int i = 0; i <numberOfBeepsToUse; i++) {

            toleranceBase = recordedValues[i] - (threshold/2);

            for(int j = 0; j < numberOfBeepsToUse; j++) {

                if (recordedValues[j] - toleranceBase > 0 && recordedValues[j] - toleranceBase < threshold) {
                    whichBeepIsBestForBase[i]++;
                }
            }
        }
        int indexOfBestBeep = scanner(whichBeepIsBestForBase);
        double bestBeep = recordedValues[indexOfBestBeep];
        float suitability = (float) whichBeepIsBestForBase[indexOfBestBeep]/(numberOfBeepsToUse);
        double[] results = { bestBeep, suitability };
        return results;
    }

    private int scanner(int[] myArray) {
        int max = myArray[0];
        int indexOfMax = 0;

        for (int i = 0; i < myArray.length; i++) {
            if (myArray[i] > max) {
                max = myArray[i];
                indexOfMax = i;
            }
        }
        return indexOfMax;
    }

}