package com.overdrivedx.zoomd;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.overdrivedx.adapter.ItemViewAdapter;
import com.overdrivedx.database.DatabaseHandler;
import com.overdrivedx.fragments.AcceptItemFragment;
import com.overdrivedx.utils.AcceptItemInterface;
import com.overdrivedx.utils.Constants;
import com.overdrivedx.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;


public class ViewVerificationActivity extends AppCompatActivity  implements AcceptItemInterface {

    Dialog dialog_waiting;
    Utils utils;
    private Activity context;
    private DatabaseHandler databaseHandler;

    private String posting_id;
    private String item_details;
    private String weight;
    private String size;
    private String sender_name;
    private String sender_phone;
    private String sender_address;
    private String sender_town;

    private String seller_id;
    private String seller_money;
    private String price;
    private String status;

    private Socket mSocket;{
        try {
            mSocket = IO.socket(Constants.SOCKETIO_HOST);
        }
        catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_verification);

        final ListView items_list = (ListView) findViewById(R.id.verifyItemListView);
        Bundle extras = getIntent().getExtras();
        utils = new Utils();
        context = this;
        databaseHandler = new DatabaseHandler(this);

        dialog_waiting = utils.Waiting(this);

        posting_id = extras.getString("id");
        item_details = extras.getString("item_details");

        sender_name = extras.getString("sender_name");
        sender_phone = "+234" + extras.getString("sender_phone");

        sender_town = extras.getString("sender_town");
        sender_address = extras.getString("sender_address");

        seller_id = extras.getString("seller_id");

        status = extras.getString("status");

        Button accept = (Button)findViewById(R.id.accept_verification_button);


        if(status.equalsIgnoreCase("1")){
            accept.setVisibility(View.GONE);
        }

        String[] label = new String[] {
                "Description",
                "Name",
                "Address",
                "Town",
                "Phone"

        };

        String[] field = new String[] {
                item_details,
                sender_name,
                sender_address,
                sender_town,
                sender_phone

        };

        final ItemViewAdapter adapter = new ItemViewAdapter(this,label, field);
        items_list.setAdapter(adapter);

        mSocket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                dialog_waiting.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.onError("Cannot Connect To Server", context);
                    }
                });
               //
            }

        });
        mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                dialog_waiting.dismiss();

            }
        });

        mSocket.on("server_msg", server_msg);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_verification, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Emitter.Listener server_msg = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];
            try {
                if(data.getBoolean("error")){
                    Log.e(Constants.TAG, data.getString("status"));
                }
                else {
                    Log.v(Constants.TAG, data.getString("status"));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    public void AcceptVerification(View v){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        AcceptItemFragment newFragment = AcceptItemFragment.newInstance();
        newFragment.show(ft, "dialog");
    }

    public void Verified(View v){
        mSocket.emit("verified", posting_id,3, seller_id);
        databaseHandler.updateStatus("3", posting_id);
    }

    @Override
    public void acceptItem(int time) {
        mSocket.emit("accept_verify", posting_id, time, sender_phone, sender_name, 1, seller_id, utils.getUser(context));
        databaseHandler.updateStatus("1", posting_id);
    }


    @Override
    public void onResume(){
        super.onResume();
        mSocket.connect();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mSocket.disconnect();

    }

}
