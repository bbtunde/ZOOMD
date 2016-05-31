package com.overdrivedx.zoomd;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.overdrivedx.adapter.ItemViewAdapter;
import com.overdrivedx.database.DatabaseHandler;
import com.overdrivedx.fragments.AcceptItemFragment;
import com.overdrivedx.fragments.CancelFragment;
import com.overdrivedx.fragments.DeliverFragment;
import com.overdrivedx.fragments.PickupFragment;
import com.overdrivedx.utils.AcceptItemInterface;
import com.overdrivedx.utils.AddActivityInterface;
import com.overdrivedx.utils.CancelInterface;
import com.overdrivedx.utils.Constants;
import com.overdrivedx.utils.DeliverInterface;
import com.overdrivedx.utils.PickUpInterface;
import com.overdrivedx.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;


public class ViewActivity extends AppCompatActivity implements CancelInterface, AcceptItemInterface,
        PickUpInterface, DeliverInterface, AddActivityInterface {

    private String posting_id;
    private String item_details;
    private String weight;
    private String size;
    private String sender_name;
    private String sender_phone;
    private String sender_address;
    private String sender_town;
    private String recipient_first_name;
    private String recipient_last_name;
    private String recipient_address;
    private String recipient_town;
    private String recipient_phone;
    private String date_posted;
    private String seller_id;
    private String seller_money;
    private String price;
    private String status;

    Dialog dialog_waiting;
    Utils utils;
    private static String MY_BUCKET ="boxment";
    private TransferUtility transferUtility;
    private TransferObserver observer;
    private Activity context;
    private DatabaseHandler databaseHandler;

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
        setContentView(R.layout.activity_view_acvitity);

        final ListView items_list = (ListView) findViewById(R.id.itemListView);
        Bundle extras = getIntent().getExtras();
        utils = new Utils();
        context = this;
        databaseHandler = new DatabaseHandler(this);

        dialog_waiting = utils.Waiting(this);
        transferUtility = utils.getTransferUtility(this);

        posting_id = extras.getString("id");
        item_details = extras.getString("item_details");
        weight = extras.getString("weight");
        size = extras.getString("size");


        Log.e(Constants.TAG, "user: " + utils.getUser(this));

        sender_name = extras.getString("sender_name");
        sender_phone = "+234" + extras.getString("sender_phone");

        sender_town = extras.getString("sender_town");
        sender_address = extras.getString("sender_address");

        recipient_first_name = extras.getString("recipient_first_name");
        recipient_last_name = extras.getString("recipient_last_name");
        recipient_phone = "+234" + extras.getString("recipient_phone");

        recipient_address = extras.getString("recipient_address");
        recipient_town = extras.getString("recipient_town");

        seller_id = extras.getString("seller_id");
        seller_money = extras.getString("seller_money");
        date_posted = extras.getString("date_posted");
        status = extras.getString("status");


        price = extras.getString("price");

        Button accept = (Button)findViewById(R.id.accept_button);
        Button pickup = (Button)findViewById(R.id.pick_up_button);
        Button deliver = (Button)findViewById(R.id.deliver_button);
        Button cancel = (Button)findViewById(R.id.cancel_button);

        if(status.equalsIgnoreCase("1")){
            accept.setVisibility(View.GONE);
        }
        else if(status.equalsIgnoreCase("2")){
            accept.setVisibility(View.GONE);
            pickup.setVisibility(View.GONE);
        }
        else if(status.equalsIgnoreCase("3")){
            accept.setVisibility(View.GONE);
            pickup.setVisibility(View.GONE);
            deliver.setVisibility(View.GONE);
        }

        String collect = "";


        if(seller_money == null || seller_money.equalsIgnoreCase("null")){
            collect = "Collect \u20a6" + extras.getString("price") + " from " + sender_name;

        }
        else{
            collect = "Collect \u20a6" + seller_money + " from " + recipient_first_name + " " + recipient_last_name;
        }

        String[] label = new String[] {
                "ID",
                "Item",
                "Amount",
                "Sender",
                "Address",
                "Town",
                "Phone",
                "Recipient",
                "Address",
                "Town",
                "Phone",

        };

        String[] field = new String[] {
                posting_id,
                item_details,
                collect,
                sender_name,
                sender_address,
                sender_town,
                sender_phone,
                recipient_first_name + " " + recipient_last_name,
                recipient_address,
                recipient_town,
                recipient_phone,

        };

        final ItemViewAdapter adapter = new ItemViewAdapter(this,label, field);
        items_list.setAdapter(adapter);

        mSocket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                dialog_waiting.dismiss();
                utils.onError("Cannot Connect To Server", context);
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

       // MenuInflater inflater = getMenuInflater();
        //inflater.inflate(R.menu.menu_view_acvitity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       /* if (id == R.id.action_activity) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);

            AddActivityFragment newFragment = AddActivityFragment.newInstance();
            newFragment.show(ft, "dialog");
            return true;
        }
        */
        return super.onOptionsItemSelected(item);
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


    public void Accept(View v){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        AcceptItemFragment newFragment = AcceptItemFragment.newInstance();
        newFragment.show(ft, "dialog");
    }

    public void PickUp(View v){

        if(seller_money == null || seller_money.equalsIgnoreCase("null")) {

            new AlertDialog.Builder(ViewActivity.this)
                    .setTitle("Collect \u20A6" + price + " From " + sender_name)
                    .setMessage("Have you collected the above amount?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            showPickUpSignatureBox();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        else{
            showPickUpSignatureBox();
        }

    }

    public void Deliver(View v){
        if(seller_money == null || seller_money.equalsIgnoreCase("null")) {
            showDeliverySignatureBox();
        }
        else{
            new AlertDialog.Builder(ViewActivity.this)
                    .setTitle("Collect \u20A6" + seller_money + " From " + recipient_first_name + " " + recipient_last_name)
                    .setMessage("Have you collected the above amount?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            showDeliverySignatureBox();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }


    }


    public void CancelItem(View v){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        CancelFragment newFragment = CancelFragment.newInstance(item_details);
        newFragment.show(ft, "dialog");

    }

    public void showPickUpSignatureBox(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        PickupFragment newFragment = PickupFragment.newInstance(posting_id);
        newFragment.show(ft, "dialog");
    }

    public void showDeliverySignatureBox(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        DeliverFragment newFragment = DeliverFragment.newInstance(recipient_first_name, recipient_last_name,posting_id);
        newFragment.show(ft, "dialog");
    }

    @Override
    public void acceptItem(int time) {
        mSocket.emit("accept_item", posting_id, time, sender_phone, sender_name, 1, seller_id, utils.getUser(context) );
        databaseHandler.updateStatus("1", posting_id);
    }

    @Override
    public void pickUpItem(File signature) {

        try {
            new UploadToS3Bucket().execute(signature).get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        mSocket.emit("pickup", posting_id, signature.getName(), recipient_phone, sender_name, 2, seller_id);
        databaseHandler.updateStatus("2", posting_id);

    }

    @Override
    public void deliverItem(File signature, String first_name, String last_name) {

        try {
            new UploadToS3Bucket().execute(signature).get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        mSocket.emit("deliver", posting_id, signature.getName(), sender_phone, first_name + " " + last_name, 3, seller_id);
        databaseHandler.updateStatus("3", posting_id);
    }

    @Override
    public void cancelItem(String reason) {
        mSocket.emit("cancel", posting_id, reason, sender_phone, 4, seller_id);
        databaseHandler.updateStatus("4", posting_id);
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

    @Override
    public void addStatus(String status) {
        Log.e(Constants.TAG, status);
    }


    private class UploadToS3Bucket extends AsyncTask<File, Void, String> {
        @Override
        protected String doInBackground(File... params) {
            final File file = params[0];

            final String[] signature = {null};

            runOnUiThread(new Runnable() {
                public void run() {

                    observer = transferUtility.upload(MY_BUCKET, file.getName(), file);
                    observer.setTransferListener(new TransferListener() {
                        @Override
                        public void onStateChanged(int i, TransferState transferState) {

                            if (transferState.COMPLETED.equals(observer.getState())) {
                                signature[0] = file.getName();
                                UploadEnded();
                            }

                        }

                        @Override
                        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

                        }

                        @Override
                        public void onError(int id, Exception e) {

                            UploadEnded();
                            new Utils().onError("Unable to save signature. Network error", context);
                            cancel(true);
                        }
                    });
                }
            });

            return signature[0];
        }

        @Override
        protected void onPreExecute() {
            if (isCancelled()){
                cancel(true);
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }

    }

    private void UploadEnded(){

        if(dialog_waiting != null) {
            dialog_waiting.dismiss();
        }


        if(observer != null){
            transferUtility.cancel(observer.getId());
        }
    }

}
