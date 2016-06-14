package com.stevenpg.roundthecorner;

import android.view.View;
import android.widget.Button;

/**
 * Created by Steven on 7/31/2015.
 * Abstract away individual button methods
 */
public class ButtonHandler {

    Button button;

    ButtonHandler(Button button){ this.button = button; }

    public void enable(){ this.button.setEnabled(true); }

    public void disable(){
        this.button.setEnabled(false);
    }

    public void updateText(String newText){ this.button.setText(newText, null);}

    public void hideButton(){ this.button.setVisibility(View.INVISIBLE); }

    public void showButton(){ this.button.setVisibility(View.VISIBLE); }
}
