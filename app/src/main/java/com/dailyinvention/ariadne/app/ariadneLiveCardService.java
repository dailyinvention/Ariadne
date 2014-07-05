package com.dailyinvention.ariadne.app;


import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.IBinder;
import android.widget.RemoteViews;

import com.google.android.glass.timeline.LiveCard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by dailyinvention on 6/29/14.
 */
public class ariadneLiveCardService extends Service {
    private static final String LIVE_CARD_ID = "ariadne";
    private LiveCard liveCard;

    private Context context = this;

    public static LocationManager manager;
    public static AudioManager mAudioManager;

    @Override
    public void onCreate() {
        super.onCreate();
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        //setContentView(R.layout.activity_start_ariadne);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID) {
        if (liveCard == null) {
            liveCard = new LiveCard(this, LIVE_CARD_ID);

            RemoteViews liveCardRemote = new RemoteViews(context.getPackageName(),
                    R.layout.activity_start_ariadne);

            String latitude = String.valueOf(getLastLocation(this).getLatitude());
            String longitude = String.valueOf(getLastLocation(this).getLongitude());

            String location = "Latitude: " + latitude + "\r\n" + "Longitude: " + longitude;

            liveCardRemote.setTextViewText(R.id.locationText, location);

            liveCard.setViews(liveCardRemote);

            Intent menuIntent = new Intent(this, ariadneMenu.class);
            menuIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            liveCard.setAction(PendingIntent.getActivity(this, 100, menuIntent, 0));
            liveCard.publish(LiveCard.PublishMode.REVEAL);


        }
        return Service.START_STICKY;
    }

    public void onDestroy() {
      if(liveCard != null) {
          if(liveCard.isPublished()) {
              liveCard.unpublish();
              liveCard = null;
          }
      }
      super.onDestroy();
    }


    public IBinder onBind(Intent arg0) {
      /*
       *  If you need to set up interprocess communication
       * (activity to a service, for instance), return a binder object
       * so that the client can receive and modify data in this service.
       *
       * A typical use is to give a menu activity access to a binder object
       * if it is trying to change a setting that is managed by the live card
       * service. The menu activity in this sample does not require any
       * of these capabilities, so this just returns null.
       */
        return null;
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
