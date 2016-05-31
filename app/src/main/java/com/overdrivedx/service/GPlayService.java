package com.overdrivedx.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.overdrivedx.utils.Constants;
import com.overdrivedx.zoomd.AppController;
import com.overdrivedx.zoomd.MainActivity;
import com.overdrivedx.zoomd.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;


/**
 * Created by babatundedennis on 11/15/15.
 */
public class GPlayService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = "LocationService";
    public static final String ACTION = "com.overdrivedx.service.GPlayService";
    // use the websmithing defaultUploadWebsite for testing and then check your
    // location with your browser here: https://www.websmithing.com/gpstracker/displaymap.php
    private final AsyncHttpClient aClient = new AsyncHttpClient();
    private boolean currentlyProcessingLocation = false;
    private LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // if we are currently trying to get a location and the alarm manager has called this again,
        // no need to start processing a new location.
        WakefulBroadcastReceiver.completeWakefulIntent(intent);

        if (!currentlyProcessingLocation) {
            currentlyProcessingLocation = true;
            startTracking();
        }

        return START_NOT_STICKY;
    }

    private void startTracking() {
        Log.d(TAG, "---------------Tracking Started-----------------");

        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {

            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();

            if (!googleApiClient.isConnected() || !googleApiClient.isConnecting()) {
                googleApiClient.connect();
            }
        } else {
            Log.e(TAG, "Unable to connect to google play services.");
        }
    }

    protected void sendLocationDataToWebsite(Location location) {
        // formatted for mysql datetime format

        String lat = Double.toString(location.getLatitude());
        String lng = Double.toString(location.getLongitude());

        Log.e(Constants.TAG, "Pinging GPS Coordinates to server");

        final RequestParams requestParams = new RequestParams();
        requestParams.put("lat", Double.toString(location.getLatitude()));
        requestParams.put("lng", Double.toString(location.getLongitude()));



        aClient.get(this, "http://b3nd.zoom.com.ng/runner/ping?lat="+
                lat + "&lng=" + lng, new AsyncHttpResponseHandler(Looper.getMainLooper()) {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.e(Constants.TAG, "Got message from server...");

                Intent broadcast = new Intent(LatLonReceiver.ACTION_SUCCESS);

                try {
                    JSONObject myObject = new JSONObject(new String(responseBody));
                    Boolean err = myObject.getBoolean("error");

                    if(!err){

                        int count = myObject.getInt("count");

                        if(count > 0){
                            Log.e(Constants.TAG, "Found " + count + " request(s) within the GPS coordinates..." );

                            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                            // Vibrate for 500 milliseconds
                            v.vibrate(5000);

                            Intent notificationIntent = new Intent(AppController.getInstance(), MainActivity.class);
                            PendingIntent pendingIntent = PendingIntent.getActivity(AppController.getInstance(), 0,
                                    notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(AppController.getInstance())
                                    .setSmallIcon(R.drawable.ic_launcher)
                                    .setContentTitle("ZOOM Deliveries")
                                    .setContentText(count + " new request(s) near you!")
                                    .setContentIntent(pendingIntent);
                            Notification notification = mBuilder.build();
                            // default phone settings for notifications
                            notification.defaults |= Notification.DEFAULT_VIBRATE;
                            notification.defaults |= Notification.DEFAULT_SOUND;

                            // cancel notification after click
                            notification.flags |= Notification.FLAG_AUTO_CANCEL;
                            // show scrolling text on status bar when notification arrives

                            notification.tickerText = count + " new request(s) are near you!";

                            // notifiy the notification using NotificationManager
                            NotificationManager notificationManager = (NotificationManager) AppController.getInstance()
                                    .getSystemService(Context.NOTIFICATION_SERVICE);
                            notificationManager.notify(2333, notification);

                            try {
                                Uri noti= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                                final Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), noti);

                                r.play();
                                final Timer t = new Timer();
                                t.schedule(new TimerTask() {
                                    public void run() {
                                        r.stop();
                                        t.cancel();

                                    }
                                }, 5000);

                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                        else{
                            Log.e(Constants.TAG, "No ad found in this area. Retrying again..." );
                        }
                    }

                }
                catch (JSONException e) {
                    e.printStackTrace();
                }

                sendBroadcast(broadcast);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Intent broadcast = new Intent(LatLonReceiver.ACTION_FAILURE);
                sendBroadcast(broadcast);

                Log.v(Constants.TAG, "Unable to ping GPS coordinates to server. Reason: " + error);

            }


        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            Log.e(TAG, "Got GPS Coordinates lat:" + location.getLatitude() + ", lng:" + location.getLongitude() + " accuracy: " + location.getAccuracy());

            // we have our desired accuracy of 500 meters so lets quit this service,
            // onDestroy will be called and stop our location uodates
            sendLocationDataToWebsite(location);

        }
    }

    private void stopLocationUpdates() {
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    /**
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
    @Override
    public void onConnected(Bundle bundle) {
        Log.e(TAG, "Connected to Google Location Services");

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(60000); // milliseconds
        locationRequest.setFastestInterval(30000); // the fastest rate in milliseconds at which your app can handle location updates
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed");
        googleApiClient.connect();
        //stopLocationUpdates();
        //stopSelf();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "GoogleApiClient connection has been suspend");
    }
}