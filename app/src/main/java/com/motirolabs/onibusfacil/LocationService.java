package com.motirolabs.onibusfacil;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class LocationService extends Service implements LocationListener {

    public static final String ACTION_LOCATION = "com.motirolabs.onibusfacil.ACTION_LOCATION";

    private static final String TAG = "LocationService";

    private static final int TWO_MINUTES = 2 * 60 * 1000;

    int counter = 0;

    private long minTime = 2 * 60 * 1000;

    private float minDistance = 50;

    private LocationManager locationManager;

    private Location bestLocation = null;

    @Override
    public IBinder onBind(Intent intent) {

        return null;

    }

    @Override
    public void onCreate() {

        super.onCreate();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();

        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);

        String bestProvider = locationManager.getBestProvider(criteria, true);

        locationManager.requestLocationUpdates(bestProvider, minTime, minDistance, this);

        Location lastKnownLocation = locationManager.getLastKnownLocation(bestProvider);

        if (lastKnownLocation != null) {

            onLocationChanged(lastKnownLocation);

            bestLocation = lastKnownLocation;

        }

        // Log.d(TAG, "onCreate");

    }

    @Override
    public void onDestroy() {

        if (locationManager != null) {

            locationManager.removeUpdates(this);

        }

        // Log.d(TAG, "onDestroy");

        super.onDestroy();

    }

    @Override
    public void onLocationChanged(Location location) {

        Location location_ = new Location(LocationManager.GPS_PROVIDER);

        location_.setLatitude(location.getLatitude());
        location_.setLongitude(location.getLongitude());

        location = location_;

        if (isBetterLocation(location, bestLocation)) {

            broadcastLocation(location);

            bestLocation = location;

            // Log.d(TAG, "onLocationChanged");

        }

    }

    @Override
    public void onProviderDisabled(String provider) {

        // Log.d(TAG, "onProviderDisabled");

    }

    @Override
    public void onProviderEnabled(String provider) {

        // Log.d(TAG, "onProviderEnabled");

    }

    //

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

        // Log.d(TAG, "onStatusChanged");

    }

    protected boolean isBetterLocation(Location location, Location bestLocation) {

        if (bestLocation == null) {

            // A new location is always better than no location

            return true;

        }

        // Check whether the new location fix is newer or older

        long timeDelta = location.getTime() - bestLocation.getTime();

        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;

        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved

        if (isSignificantlyNewer) {

            return true;

            // If the new location is more than two minutes older, it must be worse

        } else if (isSignificantlyOlder) {

            return false;

        }

        // Check whether the new location fix is more or less accurate

        int accuracyDelta = (int) (location.getAccuracy() - bestLocation.getAccuracy());

        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;

        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider

        boolean isFromSameProvider = isSameProvider(location.getProvider(), bestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy

        if (isMoreAccurate) {

            return true;

        } else if (isNewer && !isLessAccurate) {

            return true;

        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {

            return true;

        }

        return false;

    }

    private boolean isSameProvider(String provider1, String provider2) {

        if (provider1 == null) {

            return provider2 == null;

        }

        return provider1.equals(provider2);

    }

    private void broadcastLocation(Location location) {

        counter++;

        Intent broadcast = new Intent(ACTION_LOCATION);

        broadcast.putExtra(LocationManager.KEY_LOCATION_CHANGED, location);
        broadcast.putExtra("counter", counter);

        sendBroadcast(broadcast);

    }

}
