package com.dailyinvention.ariadne.app;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.google.android.glass.app.Card;
import com.google.android.glass.media.Sounds;

public class StartAriadne extends Activity {
    private Card _card;
    private View _cardView;
    private TextView _statusTextView;

    private TextToSpeech _speech;

    private Context _context = this;

    AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_ariadne);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            openOptionsMenu();
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            mAudioManager.playSoundEffect(Sounds.DISMISSED);
            this.finish();
        }
        return false;
    }

}
