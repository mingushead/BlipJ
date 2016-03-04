package com.example.moose.androidbeeper;

/**
 * Created by Moose on 14/09/2015.
 */
public class TTMFGenerator {

    public static double[] myBeeps = { 750, 1500, 2250, 3000, 3750, 4500, 5250, 6000, 6750, 7500 };
    private static final char[] charList = new char[]{ 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
            'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '.' };
    private int[] dims = new int[]{4,3,3};
    private double[][][] charTable = new double[4][3][3];

    public TTMFGenerator(){
    }

    public double[] getTTMFIndex(char character){
        System.out.println("accessing beep");

        switch(character) {
            case 'a':
                return new double[]{myBeeps[0],myBeeps[4],myBeeps[7]};
            case 'b':
                return new double[]{myBeeps[0],myBeeps[4],myBeeps[8]};
            case 'c':
                return new double[]{myBeeps[0],myBeeps[4],myBeeps[9]};
            case 'd':
                return new double[]{myBeeps[0],myBeeps[5],myBeeps[7]};
            case 'e':
                return new double[]{myBeeps[0],myBeeps[5],myBeeps[8]};
            case 'f':
                return new double[]{myBeeps[0],myBeeps[5],myBeeps[9]};
            case 'g':
                return new double[]{myBeeps[0],myBeeps[6],myBeeps[7]};
            case 'h':
                return new double[]{myBeeps[0],myBeeps[6],myBeeps[8]};
            case 'i':
                return new double[]{myBeeps[0],myBeeps[6],myBeeps[9]};
            case 'j':
                return new double[]{myBeeps[1],myBeeps[4],myBeeps[7]};
            case 'k':
                return new double[]{myBeeps[1],myBeeps[4],myBeeps[8]};
            case 'l':
                return new double[]{myBeeps[1],myBeeps[4],myBeeps[9]};
            case 'm':
                return new double[]{myBeeps[1],myBeeps[5],myBeeps[7]};
            case 'n':
                return new double[]{myBeeps[1],myBeeps[5],myBeeps[8]};
            case 'o':
                return new double[]{myBeeps[1],myBeeps[5],myBeeps[9]};
            case 'p':
                return new double[]{myBeeps[1],myBeeps[6],myBeeps[7]};
            case 'q':
                return new double[]{myBeeps[1],myBeeps[6],myBeeps[8]};
            case 'r':
                return new double[]{myBeeps[1],myBeeps[6],myBeeps[9]};
            case 's':
                return new double[]{myBeeps[2],myBeeps[4],myBeeps[7]};
            case 't':
                return new double[]{myBeeps[2],myBeeps[4],myBeeps[8]};
            case 'u':
                return new double[]{myBeeps[2],myBeeps[4],myBeeps[9]};
            case 'v':
                return new double[]{myBeeps[2],myBeeps[5],myBeeps[7]};
            case 'w':
                return new double[]{myBeeps[2],myBeeps[5],myBeeps[8]};
            case 'x':
                return new double[]{myBeeps[2],myBeeps[5],myBeeps[9]};
            case 'y':
                return new double[]{myBeeps[2],myBeeps[6],myBeeps[7]};
            case 'z':
                return new double[]{myBeeps[2],myBeeps[6],myBeeps[8]};
            case '~':
                return new double[]{myBeeps[2],myBeeps[6],myBeeps[9]};
            case ',':
                return new double[]{myBeeps[3],myBeeps[4],myBeeps[7]};
            case '?':
                return new double[]{myBeeps[3],myBeeps[4],myBeeps[8]};
            case '!':
                return new double[]{myBeeps[3],myBeeps[4],myBeeps[9]};
            case '.':
                return new double[]{myBeeps[3],myBeeps[5],myBeeps[7]};
            case '*':
                return new double[]{myBeeps[3],myBeeps[5],myBeeps[8]};
            case '$':
                return new double[]{myBeeps[3],myBeeps[5],myBeeps[9]};
            case '^':
                return new double[]{myBeeps[3],myBeeps[6],myBeeps[7]};
            case '\u0020':
                return new double[]{myBeeps[3],myBeeps[6],myBeeps[8]};
            case '#':
                return new double[]{myBeeps[3],myBeeps[6],myBeeps[9]};
            default:
                System.out.println("Error - unable to ientify character");
                return new double[]{0,0,0};
        }
    }
}
