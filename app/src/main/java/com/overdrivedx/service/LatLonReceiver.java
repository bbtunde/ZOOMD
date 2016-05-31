package com.overdrivedx.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by babatundedennis on 11/6/15.
 */
public class LatLonReceiver extends BroadcastReceiver {

    public static final int REQUEST_CODE = 12345;
    public static final String ACTION_START = "SYNC_START";
    public static final String ACTION_RETRY = "SYNC_RETRY";
    public static final String ACTION_CANCEL = "SYNC_CANCEL";
    public static final String ACTION_SUCCESS = "SYNC_SUCCESS";
    public static final String ACTION_FAILURE = "SYNC_FAILURE";
    public static final String ACTION_FINISH = "SYNC_FINISH";

    @Override
    public void onReceive(Context context, Intent intent) {
        /*
        if(intent.getExtras() != null)
        {
            String rss = intent.getStringExtra(PushLatLon.INTENT_DATA);
            Log.v(Constants.TAG, rss);
        }

        */
        /*Intent i = new Intent(context, PushLatLon.class);
        context.startService(i);
        */

        Intent i = new Intent(context, GPlayService.class);
        context.startService(i);
    }


}
