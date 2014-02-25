package com.training;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends Activity {
	// Logging tag and flag
	private static final String TAG = MainActivity.class.getSimpleName();
    private static final boolean DEBUG = true;
    // TextViews to diplay latitude and longitude values
    private TextView latitudeValue;    
    private TextView longitudeValue;
    // Message code to send to Handler
    private static final int MSG_NEW_LOCATION = 0;
    // Broadcast receiver to get location update from service 
    private LocationReceiver locationReceiver;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // link UI elements to class members
        latitudeValue = (TextView)findViewById(R.id.lat_value);
        longitudeValue = (TextView)findViewById(R.id.lon_value);
        // init broadcast receiver
        locationReceiver = new LocationReceiver();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // register broadcast receiver and start the service
        registerReceiver(locationReceiver, new IntentFilter(LocationService.ACTION_LOCATION_CHANGED));
        Intent serviceIntent = new Intent(this, LocationService.class);
        Log.e("Keith", "before startService()");
        startService(serviceIntent);
        Log.e("Keith", "after startService()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        // unregister broadcast receiver and stop the service
        unregisterReceiver(locationReceiver);
        Intent serviceIntent = new Intent(this, LocationService.class);
        stopService(serviceIntent);
    }
    // Handler to update TextViews on UI thread. It process MSG_NEW_LOCATION message
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (DEBUG) {
                Log.d(TAG, "Received message " + msg.what);
            }
            switch (msg.what) {
                case MSG_NEW_LOCATION:
                	// get Location object from message 
                    Location newLocation = (Location)msg.obj;
                    // update TextViews
                    latitudeValue.setText(Double.toString(newLocation.getLatitude()));
                    longitudeValue.setText(Double.toString(newLocation.getLongitude()));
                    break;
            }
        }
    };
    // Broadcast receiver to handle broadcast event from service
    private class LocationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (DEBUG) {
                Log.d(TAG, "Received new broadcast with " + intent.getAction() + " action");
            }
            // get Location object from broadcast event
            Location loc = intent.getParcelableExtra(LocationService.EXTRA_LOCATION);
            // create message
            Message message = Message.obtain();
            message.what = MSG_NEW_LOCATION;
            message.obj = loc;
            // send it to Handler
            handler.sendMessage(message);
        }
    }
}
