package be.tarsos.dsp;

;

class BeepDetectionHandler implements Runnable {

    private float[] incoming = new float[40];
    //private float[] isOnTime = new float[20];
    int threshold = 5;


    private void newTrigHandler(float newTrig) {
        this.populateIncoming(newTrig);
        System.out.println(getWindowAndProb(incoming)[0]);

    }

    public void populateIncoming(float newTrigTime) {

        for (int i = incoming.length - 1; i > 0; i--) {
            incoming[i] = incoming[i - 1];
        }
        incoming[0] = newTrigTime;
    }

    public float[] getWindowAndProb(float[] beepsOnTime) {

        int whichBeepIsBestForBase[] = new int[beepsOnTime.length];
        double windowBase;

        for (int i = 0; i < beepsOnTime.length; i++) {

            windowBase = beepsOnTime[i] - (threshold / 2);

            for (int j = 0; j < beepsOnTime.length; j++) {

                if (beepsOnTime[j] - windowBase > 0 && beepsOnTime[j] - windowBase < threshold) {
                    whichBeepIsBestForBase[i]++;
                }
            }
        }

        int indexOfBestBeep = scanner(whichBeepIsBestForBase);
        float bestBeep = beepsOnTime[indexOfBestBeep];
        float suitability = (float) whichBeepIsBestForBase[indexOfBestBeep] / (beepsOnTime.length);
        float[] results = {bestBeep, suitability};
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

    public void run() {

    }
}

