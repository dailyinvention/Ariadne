package com.dailyinvention.ariadne.app;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.google.android.glass.app.Card;
import com.google.android.glass.media.Sounds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class StartAriadne extends Activity {

    private Context context = this;

    public static LocationManager manager;
    public static AudioManager mAudioManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        //setContentView(R.layout.activity_start_ariadne);


    }

    @Override
    protected void onStart() {
        super.onStart();
        String latitude = String.valueOf(getLastLocation(this).getLatitude());
        String longitude = String.valueOf(getLastLocation(this).getLongitude());
        Card card1 = new Card(context);
        card1.setText("Latitude: " + latitude + "\r\n" + "Longitude: " + longitude);
        View card1View = card1.getView();
        setContentView(card1View);
        Log.i("Location: ", latitude);
    }

    @Override
    protected void onStop() {
        super.onStop();
        setContentView(null);
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

    public static Location getLastLocation(Context context) {

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.NO_REQUIREMENT);
        List<String> providers = manager.getProviders(criteria, true);
        List<Location> locations = new ArrayList<Location>();
        for (String provider : providers) {
            Location location = manager.getLastKnownLocation(provider);
            if (location != null && location.getAccuracy() != 0.0) {
                locations.add(location);
            }
        }
        Collections.sort(locations, new Comparator<Location>() {
            @Override
            public int compare(Location location, Location location2) {
                return (int) (location.getAccuracy() - location2.getAccuracy());
            }
        });
        if (locations.size() > 0) {
            return locations.get(0);
        }
        else {
            return null;
        }
    }

}
