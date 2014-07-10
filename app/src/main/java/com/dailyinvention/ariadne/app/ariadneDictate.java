package com.dailyinvention.ariadne.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;

import java.util.List;



/**
 * Created by dailyinvention on 7/6/14.
 */
public class ariadneDictate extends Activity  {

    private static final int SPEECH_REQUEST = 0;
    private static Intent intent;
    private String latitude;
    private String longitude;

    public void onCreate(Bundle bundle) {

       displaySpeechRecognizer();
       super.onCreate(bundle);
    }

    private void displaySpeechRecognizer() {
        Bundle dictateBundle = getIntent().getExtras();
        latitude = dictateBundle.getString("latitude");
        longitude = dictateBundle.getString("longitude");
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        startActivityForResult(intent, SPEECH_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == SPEECH_REQUEST && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);

            //ariadneCardRemote.setTextViewText(R.id.locationText, spokenText);
            //ariadneLiveCardService.ariadnePopulateCard(latitude,longitude,spokenText);
            Intent serviceIntent = new Intent(this, ariadneLiveCardService.class);
            serviceIntent.putExtra("latitude", latitude);
            serviceIntent.putExtra("longitude", longitude);
            serviceIntent.putExtra("descrip", spokenText);
            startService(serviceIntent);


            Log.i("Text Returned:", spokenText);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
