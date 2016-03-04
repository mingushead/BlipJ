package be.tarsos.dsp.example;

import java.util.*;
import java.util.Date;
import javax.sound.sampled.*;
import java.io.*;
import java.nio.*;

class BeepScheduler {

    public static void main() {

        java.util.Timer timer = new java.util.Timer();
        int freq = 800;

        //Array to hold data describing our beep
        byte audioData[] = new byte[32000 * 2];
        new SynGen().getSyntheticData(audioData, freq);

        // Schedule beeper to run after every 2 second(2000 millisecond)
        timer.schedule( new Beep(audioData), 0, 2000);

    }
}

class Beep extends TimerTask {
    
    AudioFormat audioFormat;
    AudioInputStream audioInputStream;
    SourceDataLine sourceDataLine;
    byte audioData[] = new byte[32000 * 2];;

    public Beep(byte _audioData[]) {

      audioData = _audioData;

    }

  //The following are audio format parameters.
  // They may be modified by the signal generator
  // at runtime.  Values allowed by Java
  // SDK 1.4.1 are shown in comments.
  float sampleRate = 44100.0F;
  //Allowable 8000,11025,16000,22050,44100
  int sampleSizeInBits = 16;
  //Allowable 8,16
  int channels = 1;
  //Allowable 1,2
  boolean signed = true;
  //Allowable true,false
  boolean bigEndian = true;
  //Allowable true,false

  //A buffer to hold two seconds monaural and one
  // second stereo data at 16000 samp/sec for
  // 16-bit samples
  

  public void playOrFileData() {
    try{
      InputStream byteArrayInputStream = new ByteArrayInputStream(audioData);
      audioFormat = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
      audioInputStream = new AudioInputStream(byteArrayInputStream, audioFormat, audioData.length/audioFormat.getFrameSize());
      DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class,audioFormat);

      //Geta SourceDataLine object
      sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
      new ListenThread().start();
      }
      catch (Exception e) {
      e.printStackTrace();
      
    }//end catch
  }//end playOrFileData
//=============================================//

//Inner class to play back the data that was
// saved.
class ListenThread extends Thread{
  //This is a working buffer used to transfer
  // the data between the AudioInputStream and
  // the SourceDataLine.  The size is rather
  // arbitrary.
  byte playBuffer[] = new byte[16384];

  public void run(){
    try{
      //Open and start the SourceDataLine
      sourceDataLine.open(audioFormat);
      sourceDataLine.start();

      int cnt;

      //Transfer the audio data to the speakers
      while((cnt = audioInputStream.read(
                              playBuffer, 0,
                              playBuffer.length))
                                          != -1){
        //Keep looping until the input read
        // method returns -1 for empty stream.
        if(cnt > 0){
          //Write data to the internal buffer of
          // the data line where it will be
          // delivered to the speakers in real
          // time
          sourceDataLine.write(
                             playBuffer, 0, cnt);
        }//end if
      }//end while

      //Block and wait for internal buffer of the
      // SourceDataLine to become empty.
      sourceDataLine.drain();

      //Finish with the SourceDataLine
      sourceDataLine.stop();
      sourceDataLine.close();
    }catch (Exception e) {
      e.printStackTrace();
      System.exit(0);
    }//end catch

  }//end run
}//end inner class ListenThread
//=============================================//

    int count = 1;

    // run is a abstract method that defines task performed at scheduled time.
    public void run() {
      playOrFileData();
      int endTime = (int) new Date().getTime();
      System.out.println((endTime % 1000) + "ms");
      count++;
    }
}

class SynGen{
    ByteBuffer byteBuffer;
    ShortBuffer shortBuffer;
    int byteLength;
    int freq;
    void getSyntheticData(byte[] synDataBuffer, int _freq){

      byteBuffer = ByteBuffer.wrap(synDataBuffer);
      shortBuffer = byteBuffer.asShortBuffer();
      freq = _freq;
      byteLength = synDataBuffer.length;
      tones();
    }
    void tones(){
      int channels = 1;//Java allows 1 or 2
      int bytesPerSamp = 16;
      float sampleRate = 44100.0F;
      // Allowable 8000,11025,16000,22050,44100
      int sampLength = byteLength/bytesPerSamp;
      for(int cnt = 0; cnt < sampLength; cnt++){
        double time = cnt/sampleRate;
        double sinValue =
          Math.sin(2*Math.PI*freq*time);
        shortBuffer.put((short)(32000*sinValue));
      }//end for loop
    }//end method tones
  }
