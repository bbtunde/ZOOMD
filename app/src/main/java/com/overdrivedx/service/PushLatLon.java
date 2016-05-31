package com.overdrivedx.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;
import com.overdrivedx.utils.Constants;
import com.overdrivedx.utils.IntentUtil;
import com.overdrivedx.zoomd.AppController;
import com.overdrivedx.zoomd.MainActivity;
import com.overdrivedx.zoomd.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;

/**
 * Created by babatundedennis on 11/6/15.
 */
public class PushLatLon extends IntentService {
    public static final String ACTION = "com.overdrivedx.service.PushLatLon";
    public static final String LAT = "lat";
    public static final String LON = "lon";
    public static final String INTENT_URL = "INTENT_URL";
    public static final String INTENT_STATUS_CODE = "INTENT_STATUS_CODE";
    public static final String INTENT_HEADERS = "INTENT_HEADERS";
    public static final String INTENT_DATA = "INTENT_DATA";
    public static final String INTENT_THROWABLE = "INTENT_THROWABLE";

    private final AsyncHttpClient aClient = new SyncHttpClient();

    public PushLatLon() {
        super(PushLatLon.class.getName());
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        GPSTracker gpsTracker = new GPSTracker(this);
        WakefulBroadcastReceiver.completeWakefulIntent(intent);

        Log.v(Constants.TAG, String.valueOf(gpsTracker.getLongitude()));

        String gmapurl =  "http://maps.googleapis.com/maps/api/geocode/json?latlng="+
                gpsTracker.getLatitude() + "," + gpsTracker.getLongitude() + "&sensor=true";

        aClient.get(this, "http://b3nd.zoom.com.ng/runner/ping?lat="+
                gpsTracker.getLatitude() + "&lng=" + gpsTracker.getLongitude(), new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                sendBroadcast(new Intent(LatLonReceiver.ACTION_START));
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.v(Constants.TAG, "success");

                Intent broadcast = new Intent(LatLonReceiver.ACTION_SUCCESS);
                broadcast.putExtra(INTENT_STATUS_CODE, statusCode);
                broadcast.putExtra(INTENT_HEADERS, IntentUtil.serializeHeaders(headers));
                broadcast.putExtra(INTENT_DATA, new String(responseBody));



                try {
                    JSONObject myObject = new JSONObject(new String(responseBody));
                    Boolean err = myObject.getBoolean("error");

                    if(!err){
                        int count = myObject.getInt("count");

                        if(count > 0){
                            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                            // Vibrate for 500 milliseconds
                            v.vibrate(5000);

                            Intent notificationIntent = new Intent(AppController.getInstance(), MainActivity.class);
                            PendingIntent pendingIntent = PendingIntent.getActivity(AppController.getInstance(), 0,
                                    notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(AppController.getInstance())
                                    .setSmallIcon(R.drawable.ic_launcher)
                                    .setContentTitle("ZOOM Deliveries")
                                    .setContentText(count + " new pick-ups are near you!")
                                    .setContentIntent(pendingIntent);
                            Notification notification = mBuilder.build();
                            // default phone settings for notifications
                            notification.defaults |= Notification.DEFAULT_VIBRATE;
                            notification.defaults |= Notification.DEFAULT_SOUND;

                            // cancel notification after click
                            notification.flags |= Notification.FLAG_AUTO_CANCEL;
                            // show scrolling text on status bar when notification arrives

                            notification.tickerText = count + " new pick-ups are near you!";

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
                broadcast.putExtra(INTENT_STATUS_CODE, statusCode);
                broadcast.putExtra(INTENT_HEADERS, IntentUtil.serializeHeaders(headers));
                broadcast.putExtra(INTENT_DATA, responseBody);
                broadcast.putExtra(INTENT_THROWABLE, error);
                sendBroadcast(broadcast);

                Log.v(Constants.TAG, "failure" + error);

            }

            @Override
            public void onCancel() {
                sendBroadcast(new Intent(LatLonReceiver.ACTION_CANCEL));
                Log.v(Constants.TAG, "cancel");
            }

            @Override
            public void onRetry(int retryNo) {
                sendBroadcast(new Intent(LatLonReceiver.ACTION_RETRY));
                Log.v(Constants.TAG, "retry");

            }

            @Override
            public void onFinish() {
                sendBroadcast(new Intent(LatLonReceiver.ACTION_FINISH));

            }
        });
    }



}
