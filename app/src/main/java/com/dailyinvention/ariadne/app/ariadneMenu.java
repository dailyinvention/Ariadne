package com.dailyinvention.ariadne.app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class ariadneMenu extends Activity {



    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        openOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.start_ariadne, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Bundle bundle = getIntent().getExtras();
        String latitude = bundle.getString("latitude");
        String longitude = bundle.getString("longitude");

        // Handle item selection.

        switch (item.getItemId()) {
            case R.id.action_return:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("google.navigation:q=" + latitude + "," + longitude + "&mode=w"));
                startActivity(intent);
                stopService(new Intent(this, ariadneLiveCardService.class));
                return true;
            case R.id.action_stop:
                stopService(new Intent(this, ariadneLiveCardService.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onOptionsMenuClosed(Menu menu) {
        // Nothing else to do, closing the activity.
        finish();
    }

}
