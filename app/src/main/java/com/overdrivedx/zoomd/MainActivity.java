package com.overdrivedx.zoomd;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.overdrivedx.adapter.FeedListAdapter;
import com.overdrivedx.database.DatabaseHandler;
import com.overdrivedx.fragments.AddUserFragment;
import com.overdrivedx.model.Item;
import com.overdrivedx.service.GPlayService;
import com.overdrivedx.utils.Constants;
import com.overdrivedx.utils.FontsOverride;
import com.overdrivedx.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity{

    private Utils util;
    private FeedListAdapter listAdapter;
    private List<Item> feedItems;
    private ListView listView;
    private Dialog dialog_waiting;
    private Activity context;
    private DatabaseHandler dh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        FontsOverride.setDefaultFont(this, "MONOSPACE", "Lato-Regular.ttf");
        dh = new DatabaseHandler(this);
        util = new Utils();

        if(util.getUser(this) == null || util.getUser(this).length() < 3){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);

            AddUserFragment newFragment = AddUserFragment.newInstance();
            newFragment.show(ft, "dialog");

        }

        setContentView(R.layout.activity_main);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        listView = (ListView)findViewById(R.id.listfeedView);
        context = this;
        feedItems = new ArrayList<Item>();


       // new GetFeed().execute();

        //scheduleAlarm();

        onStartService();
    }


    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {

                int resultCode = intent.getIntExtra("resultCode", RESULT_CANCELED);

                if (resultCode == RESULT_OK) {
                    Log.v(Constants.TAG, "lon: " + bundle.getString("lon"));
                    Log.v(Constants.TAG, "lat: " + bundle.getString("lat"));
                }
                else {
                    Log.v(Constants.TAG, "error here");
                }
            }

        }
    };



    public void onStartService() {
        Intent i = new Intent(this, GPlayService.class);
        startService(i);
    }


    public void refresh(View v) {
        feedItems = null;
        feedItems = new ArrayList<Item>();
        new GetFeed().execute();
    }

    public void running(View v) {
        Intent intent = new Intent(this, RunningActivity.class);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent i = new Intent(MainActivity.this, LogActivity.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
    public void scheduleAlarm() {
        // Construct an intent that will execute the AlarmReceiver
        Intent intent = new Intent(getApplicationContext(), LatLonReceiver.class);
        // Create a PendingIntent to be triggered when the alarm goes off
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, LatLonReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Setup periodic alarm every 5 seconds
        long firstMillis = System.currentTimeMillis(); // alarm is set right away
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        // First parameter is the type: ELAPSED_REALTIME, ELAPSED_REALTIME_WAKEUP, RTC_WAKEUP
        // Interval can be INTERVAL_FIFTEEN_MINUTES, INTERVAL_HALF_HOUR, INTERVAL_HOUR, INTERVAL_DAY
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis, 30000
                , pIntent);
        // AlarmManager.INTERVAL_FIFTEEN_MINUTES
    }

    */
    @Override
    public void onResume(){
        super.onResume();

        View dV = getWindow().getDecorView();
        int uiOpts = View.SYSTEM_UI_FLAG_FULLSCREEN;
        dV.setSystemUiVisibility(uiOpts);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

       IntentFilter filter = new IntentFilter(GPlayService.ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
    }


    @Override
    protected void onPause() {
        super.onPause();
       LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }


    private class GetFeed extends AsyncTask<Void, String, Void> {
        @Override
        protected void onPreExecute() {
            dialog_waiting = util.Waiting(MainActivity.this);
            if (!new Utils().isConnectedToInternet(MainActivity.this)) {
                onXError("No internet connection.");
                cancel(true);
            }

            if (isCancelled()) {
                cancel(true);
            }
        }

        @Override
        protected Void doInBackground(Void... params) {

            RequestQueue rq = Volley.newRequestQueue(MainActivity.this);

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                    Constants.LIVE + '0', null,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {

                            try {
                                JSONArray feedArray = response.getJSONArray("feed");
                                parseFeed(feedArray);


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    //Log.e(Constants.TAG, error.getMessage());
                    onXError("Network Error. Unable to fetch feed.");
                    cancel(true);
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    return null;
                }

            };

            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            rq.add(jsonObjReq);

            return null;
        }

    }

    private void parseFeed(JSONArray feedArray) throws JSONException {

        if(feedArray.length() > 0) {
            dh.truncateFeedTable();

            for (int i = 0; i < feedArray.length(); i++) {

                JSONObject feedObj = (JSONObject) feedArray.get(i);

                String id = feedObj.getString("id");
                String item_details = feedObj.getString("item");

                String sender_name = feedObj.getString("sender_name");
                String sender_phone = feedObj.getString("sender_phone");
                String sender_town = feedObj.getString("sender_town_id");
                String sender_address = feedObj.getString("sender_address");

                String recipient_first_name = feedObj.getString("recipient_first_name");
                String recipient_last_name = feedObj.getString("recipient_last_name");
                String recipient_phone = feedObj.getString("recipient_phone");
                String recipient_town = feedObj.getString("recipient_town_id");
                String recipient_address = feedObj.getString("recipient_address");

                String seller_money = feedObj.getString("seller_money");
                String seller_id = feedObj.getString("seller_id");
                String weight = feedObj.getString("weight");
                String size = feedObj.getString("size");
                String price = feedObj.getString("price");
                String type = feedObj.getString("type");
                //Log.e(Constants.TAG, price);

                String status = feedObj.getString("status");
                String date_posted = feedObj.getString("date_posted");

                Item item = new Item(id,item_details, sender_name, sender_phone, sender_town, sender_address,
                        recipient_address, recipient_first_name, recipient_last_name, recipient_phone, recipient_town, seller_id, seller_money, weight,
                        price, size, date_posted, status, type);


                dh.addItem(item);

                feedItems.add(item);


            }


            listAdapter = new FeedListAdapter(MainActivity.this, feedItems);
            listView.setAdapter(listAdapter);


        }
        else{
        util.onError("No request at the moment", context);
        }

        if(dialog_waiting != null) {
            dialog_waiting.dismiss();
        }

    }

    public void onXError(final String s) {
        util.onError(s, MainActivity.this);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if(dialog_waiting != null) {
                    dialog_waiting.dismiss();
                }
            }
        });
    }
}
