package com.motirolabs.onibusfacil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

public class LocationReceiver extends BroadcastReceiver {

    private static final String TAG = "LocationReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        // If you got a Location extra, use it

        Location location = (Location) intent.getParcelableExtra(LocationManager.KEY_LOCATION_CHANGED);

        if (location != null) {

            int counter = intent.getIntExtra("counter", 0);

            onLocationReceived(context, location, counter);

            return;

        }

        // If you get here, something else has happened

        if (intent.hasExtra(LocationManager.KEY_PROVIDER_ENABLED)) {

            boolean enabled = intent.getBooleanExtra(LocationManager.KEY_PROVIDER_ENABLED, false);

            onProviderEnabledChanged(enabled);

        }

    }

    protected void onLocationReceived(Context context, Location location, int counter) {

        // Log.d(TAG, this + " Got #" + Integer.toString(counter) + " location from " + location.getProvider() + ": " + location.getLatitude() + ", " + location.getLongitude());

    }

    protected void onProviderEnabledChanged(boolean enabled) {

        // Log.d(TAG, "Provider " + (enabled ? "enabled" : "disabled"));

    }

}
