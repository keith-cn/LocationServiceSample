package com.training;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;


public class LocationService extends Service {
	// Logging tag and flag
    private static final String TAG = LocationService.class.getSimpleName();
    private static final boolean DEBUG = true;
    // Broadcast event action value
    public static final String ACTION_LOCATION_CHANGED = "com.example.location.CHANGED";
    // parameter in broadcast event
    public static final String EXTRA_LOCATION = "app.location";
    // listener to get location update
    private LocationListener locationListener;
    // Handle startService command for service
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (DEBUG) {
            Log.d(TAG, "Service started, starting new location updater");
        }
        register();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (DEBUG) {
            Log.d(TAG, "Exiting service");
        }
        destroy();
    }
    // We don't implement binding in this sample
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    // Register listener to get notification about location changing
    private void register() {
    	// get location service
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener();
        // invoke callback method manually first time to get current position
        locationListener.onLocationChanged(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
        String providerName = LocationManager.GPS_PROVIDER; /* Keith modify */
        if (DEBUG) {
            Log.d(TAG, "Register for location updates from provider " + providerName);
        }
        // register location listener to get updates on location change
        locationManager.requestLocationUpdates(providerName, 1000, 0, locationListener, getMainLooper());
    }
    // unregister location update listener
    private void destroy() {
        if (DEBUG) {
            Log.d(TAG, "Unregister location listener");
        }
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.removeUpdates(locationListener);
        locationListener = null;
    }

    // listener to handle onLocationChanged callback
    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            if (location == null) {
                if (DEBUG) {
                    Log.w(TAG, "Delivered null location object to listener");
                }
                return;
            }

            if (DEBUG) {
                Log.d(TAG, "New location update: (" + location.getLatitude() + ", " + location.getLongitude() + ")");
            }
            // create and send broadcast with new location data
            Intent locIntent = new Intent(ACTION_LOCATION_CHANGED);
            locIntent.putExtra(EXTRA_LOCATION, location);            
            sendBroadcast(locIntent);
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
        @Override
        public void onProviderEnabled(String provider) {
        }
        @Override
        public void onProviderDisabled(String provider) {
        }
    }
}
