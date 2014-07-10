package com.dailyinvention.ariadne.app;


import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.timeline.LiveCard;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
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
    private ariadnePopulateCard ariadnePopulate;
    private RemoteViews ariadneCardRemote;
    private static final int SPEECH_REQUEST = 0;
    private String descrip;



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
            String latitude;
            String longitude;
            String location;

            descrip = intent.getStringExtra("descrip");

            if(descrip == null) {

                latitude = String.valueOf(getLastLocation().getLatitude());
                longitude = String.valueOf(getLastLocation().getLongitude());

                location = "Latitude: " + latitude + "\r\n" + "Longitude: " + longitude;
            }
            else {
                latitude = intent.getStringExtra("latitude");
                longitude = intent.getStringExtra("longitude");
                location = descrip;
            }

            ariadnePopulate = new ariadnePopulateCard();
            ariadnePopulate.execute(latitude,longitude,location);


            Intent menuIntent = new Intent(this, ariadneMenu.class);
            menuIntent.putExtra("latitude", latitude);
            menuIntent.putExtra("longitude", longitude);

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

        return null;
    }

    public static Location getLastLocation() {

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


    public class ariadnePopulateCard extends AsyncTask<String, Void, Bitmap> {
        String latitude;
        String longitude;
        String location;

        @Override
        protected Bitmap doInBackground(String... params) {
            latitude = params[0];
            longitude = params[1];
            location = params[2];

            try {


                URL staticMapUrl = new URL("http://maps.googleapis.com/maps/api/staticmap?center=" + latitude + "," + longitude + "&zoom=16&size=240x360&maptype=terrain&format=png&zoom=16&scale=2&style=feature:all%7Celement:labels.text.fill%7Cvisibility:off&style=feature:all%7Celement:labels.text.stroke%7Ccolor:0xFFFFFF&style=feature:road%7Celement:geometry%7Ccolor:0x4f4f4f%7Cweight:2%7Cvisibility:on&style=feature:landscape%7Celement:geometry.fill%7Ccolor:0x0f0f0f&style=feature:poi%7Celement:geometry.fill%7Ccolor:0x2f2f2f&style=feature:poi.park%7Celement:geometry.fill%7Ccolor:0x006600&markers=color:red|" + latitude + "," + longitude);
                InputStream stream = staticMapUrl.openStream();
                BufferedInputStream bufferedStream = new BufferedInputStream(stream);

                Log.i("Bytes:", "Bytes: " + String.valueOf(bufferedStream.available()));

                Bitmap imageStream = BitmapFactory.decodeStream(bufferedStream);
                stream.close();
                bufferedStream.close();
                Log.i("Image Loaded:", "Loaded image");
                return imageStream;
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            ariadneCardRemote = new RemoteViews(getPackageName(), R.layout.activity_start_ariadne);
            if (bitmap != null) ariadneCardRemote.setImageViewBitmap(R.id.locationImage, bitmap);
            ariadneCardRemote.setTextViewText(R.id.locationText, location);

            if(descrip != null) {
                ariadneCardRemote.setTextViewText(R.id.smallType, "lat:" + latitude + "\r\n" + "lon:" + longitude);
            }

            liveCard.setViews(ariadneCardRemote);

            AudioManager audio = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
            audio.playSoundEffect(Sounds.SUCCESS);
        }
    }



}
