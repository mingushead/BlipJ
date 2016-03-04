package be.tarsos.dsp.example;

/**
 * Created by Moose on 14/09/2015.
 */

public class QTMFHandler {

    private double[] frequencies;
    private int[][][][] output;
    private int[] dimensions;
    private int[] location;
    private int[] charIndex;

    QTMFHandler(double[]input, int[]dims){

        frequencies = new double[input.length];
        dimensions = new int[dims.length];

        frequencies = input;
        dimensions = dims;
        output = new int[dims[0]][dims[1]][dims[2]][dims[3]];
        charIndex = new int[dims[0]*dims[1]*dims[2]];
        location = new int[3];

        //97 represents 'a' in UTF-8.
        //                    a   b   c   d    e    f    g    h    i    j    k    l    m    n    o
        charIndex = new int[]{97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111,
        //      p    q    r    s    t    u    v    w    x    y    z    ^   ,   ?   !   #   *   $   ~    SP  .
                112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 94, 44, 63, 33, 35, 42, 36, 126, 32, 46};

        int placeholder = 0;
        for(int i = 0; i < dims[0]; i++){
            for(int j = 0; j < dims[1]; j++){
                for(int k = 0; k < dims[2]; k++){
                    for(int l = 0; l < dims[3]; l++) {
                        output[i][j][k][l] = charIndex[placeholder];
                        placeholder++;
                        System.out.println(placeholder);
                    }
                }
            }
        }
        output[dims[0]-1][dims[1]-1][dims[2]-1][dims[3]-1] = 46;
        //output[1][2][1] = 32;

    }

    public char parseQTMF(int[] detectedFreqs){
        char result;
        for(int i = 0; i < frequencies.length; i++){
            if(detectedFreqs[0] == frequencies[i]){
                location[0] = i;
            }
            else if(detectedFreqs[1] == frequencies[i]){
                location[1] = i - dimensions[0];
            }
            else if(detectedFreqs[2] == frequencies[i]){
                location[2] = i - dimensions[0] - dimensions[1];
            }
            else if(detectedFreqs[3] == frequencies[i]){
                location[3] = i - dimensions[0] - dimensions[1] - dimensions[2];
            }
        }
        result = (char) output[location[0]][location[1]][location[2]][location[3]];
        return result;
    }

    public QTMFResult parseQTMF(double[] detectedFreqs){
        char resultLoc;
        for(int i = 0; i < frequencies.length; i++){
            if(detectedFreqs[0] == frequencies[i]){
                location[0] = i;
            }
            if(detectedFreqs[1] == frequencies[i]) {
                location[1] = i - dimensions[0];
            }
            if(detectedFreqs[2] == frequencies[i]){
                location[2] = i - dimensions[0] - dimensions[1];
            }
            else if(detectedFreqs[3] == frequencies[i]){
                location[3] = i - dimensions[0] - dimensions[1] - dimensions[2];
            }
        }
        // System.out.println("Loc: " + location[0] + "\t"+ location[1] + "\t"+ location[2] + "\t");
        resultLoc = 'E';
        //System.out.println(location[0] + "\t" + location[1] + "\t" + location[2] + "\t");
        if(location[0] >= 0 && location[1] >= 0 && location[2] >= 0) {
            try {
                resultLoc = (char) output[location[0]][location[1]][location[2]][location[3]];
            }
            catch(Exception e){
                System.out.println(location[0] + "\t" + location[1] + "\t" + location[2] + "\t");
            }
        }
        QTMFResult result = new QTMFResult(resultLoc, ((location[0]+1)*3) + (location[1]*4) + (location[2]));
        return result;
    }
}
class  QTMFResult {
    char charResult;
    int code;

    public QTMFResult (char ch, int co){
        this.charResult = ch;
        this.code = co;
    }
}

