package com.example.moose.androidbeeper;

import android.app.Activity;
import android.graphics.Color;
import android.widget.TextView;

/**
 * Created by Moose on 26/08/2015.
 */
public class GraphicsHandler extends Activity {

    TextView label;

    public GraphicsHandler(){
        //label = (TextView) findViewById(R.id.frequencyLabel);
    }

    void setRed() {
        //label.setTextColor(Color.argb(255, 0, 0, 255));
    }

    void setBlack(){
        //label.setTextColor(Color.argb(255,0,0,0));
    }
}
