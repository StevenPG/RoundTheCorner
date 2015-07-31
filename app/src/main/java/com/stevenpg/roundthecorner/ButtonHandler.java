package com.stevenpg.roundthecorner;

import android.widget.Button;

/**
 * Created by Steven on 7/31/2015.
 */
public class ButtonHandler {

    Button button;

    ButtonHandler(Button button){
        this.button = button;
    }

    public void enable(){
        this.button.setEnabled(true);
    }

    public void disable(){
        this.button.setEnabled(false);
    }
}
