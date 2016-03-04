package com.example.moose.androidbeeper;
        import android.content.Context;
        import android.graphics.Color;
        import android.hardware.Sensor;
        import android.hardware.SensorEvent;
        import android.hardware.SensorEventListener;
        import android.hardware.SensorManager;
        import android.os.Bundle;
        import android.app.Activity;
        import android.view.KeyEvent;
        import android.view.Menu;
        import android.view.View;
        import android.view.inputmethod.InputMethodManager;
        import android.widget.Button;
        import android.widget.CheckBox;
        import android.widget.EditText;
        import android.widget.LinearLayout;
        import android.widget.TextView;
        import java.lang.Math.*;

        import static android.util.FloatMath.cos;
        import static android.util.FloatMath.sin;
        import static android.util.FloatMath.sqrt;

public class MainActivity extends Activity {
    public Activity mainActivityReference;
    EditText fTextBox;
    EditText messageTextBox;
    Button soundOn;
    Button messageButton;
    LinearLayout mainLayout;
    BeeperScheduler scheduler;
    TTMFGenerator ttmfGenerator;
    CheckBox checkbox;
    double[] frequencies = new double[] {400,500,600};
    boolean isMuted = true;
    public boolean isTransmittingMessage = false;
    public int tester = 100;

    public void clickedBeepTransmitter(View v) {

        if (isMuted) {
            isMuted = false;
            // System.out.println("un-muted " + isMuted);
            setUpBeeper();
            scheduler.startBeeper();
            mainLayout.setBackgroundColor(Color.argb(100, 0, 255, 0));
            soundOn.setText("Halt transmission");

        } else {
            isMuted = true;
            mainLayout.setBackgroundColor(Color.argb(100, 255, 0, 0));
            soundOn.setText("Start transmission");
            try {
                if (scheduler != null) {
                    scheduler.stopBeeper();
                }
            } catch (Exception NullPointerException) {
                System.out.println("couldn't stop timer");
            }
        }
    }

    public void clickedMessageTransmitter(View v) {

        if(!isTransmittingMessage) {
            isTransmittingMessage = true;
            TextTransmissionHandler transmissionSetup = new TextTransmissionHandler(messageTextBox.getText().toString(), ttmfGenerator);
            transmissionSetup.startBeeper();
            isTransmittingMessage = false;
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mainActivityReference = this;
        setContentView(R.layout.activity_main);
        fTextBox = (EditText) findViewById(R.id.frequencyEditText);
        messageTextBox = (EditText) findViewById(R.id.messageEditText);
        soundOn = (Button) findViewById(R.id.soundToggle);
        messageButton = (Button) findViewById(R.id.messageButton);
        mainLayout = (LinearLayout) findViewById(R.id.mainLayout);
        checkbox = (CheckBox) findViewById(R.id.checkbox_low_or_high);

        fTextBox.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
               // updateFreq();
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    hideKeyboard(mainActivityReference);
                    return true;
                }
                return false;
            }
        });

        messageTextBox.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    hideKeyboard(mainActivityReference);
                    return true;
                }
                return false;
            }
        });

        ttmfGenerator = new TTMFGenerator();

    }

    void setUpBeeper() {
        scheduler = new BeeperScheduler(new Beeper(500, false, new SynGen().getSyntheticData( Double.parseDouble(fTextBox.getText().toString()), 10000)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void onDestroy() {
        super.onDestroy();
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if(view == null) {
            view = new View(activity);
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void onCheckboxClicked(View v){
        if(!checkbox.isChecked()){

            ttmfGenerator.myBeeps = new double[]{ 750, 1500, 2250, 3000, 3750, 4500, 5250, 6000, 6750, 7500 };
        }
        else{

            ttmfGenerator.myBeeps = new double[]{ 13250, 14000, 14750, 15500, 16250, 17000, 17750, 18500, 19250, 20000 };
        }
    }
}
