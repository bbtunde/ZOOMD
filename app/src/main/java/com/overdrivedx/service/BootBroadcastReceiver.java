package com.overdrivedx.service;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by babatundedennis on 11/13/15.
 */
public class BootBroadcastReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Launch the specified service when this message is received
        //Intent startServiceIntent = new Intent(context, PushLatLon.class);
        Intent startServiceIntent = new Intent(context, GPlayService.class);
        context.startService(startServiceIntent);
    }
}
