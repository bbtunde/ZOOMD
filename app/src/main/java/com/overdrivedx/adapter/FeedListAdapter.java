package com.overdrivedx.adapter;

/**
 * Created by babatundedennis on 8/24/15.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.overdrivedx.model.Item;
import com.overdrivedx.zoomd.R;
import com.overdrivedx.zoomd.ViewActivity;
import com.overdrivedx.zoomd.ViewVerificationActivity;

import java.util.List;

public class FeedListAdapter extends BaseAdapter{
    private Activity activity;
    private LayoutInflater inflater;
    private List<Item> feedItems;

    public FeedListAdapter(Activity activity, List<Item> feedItems) {
        this.activity = activity;
        this.feedItems = feedItems;

    }

    @Override
    public int getCount() {
        return feedItems.size();
    }

    @Override
    public Object getItem(int location) {
        return feedItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
            convertView = inflater.inflate(R.layout.feed_item, null);



        TextView item_details = (TextView) convertView.findViewById(R.id.item_details);
        TextView pickup_deliver_town = (TextView) convertView.findViewById(R.id.pickup_deliver_town);

        final Item item = feedItems.get(position);

        item_details.setText(item.getItemDetails());

        if(item.getType().equalsIgnoreCase("1") || item.getType() == "1"){
            pickup_deliver_town.setText(item.getSenderTown());

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, ViewVerificationActivity.class);
                    Bundle args = new Bundle();
                    args.putString("id", item.getID());
                    args.putString("item_details", item.getItemDetails());

                    args.putString("sender_name", item.getSenderName());
                    args.putString("sender_phone", item.getSenderPhone());
                    args.putString("sender_town", item.getSenderTown());
                    args.putString("sender_address", item.getSenderAddress());

                    args.putString("seller_id", item.getSellerID());
                    args.putString("date_posted", item.getDatePosted());
                    args.putString("status", item.getStatus());

                    intent.putExtras(args);
                    activity.startActivity(intent);
                }
            });

            convertView.setBackgroundColor(Color.parseColor("#B5EDFF"));
        }
        else {
            pickup_deliver_town.setText(item.getSenderTown() + " - " + item.getRecipientTown());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, ViewActivity.class);
                    Bundle args = new Bundle();
                    args.putString("id", item.getID());
                    args.putString("item_details", item.getItemDetails());
                    args.putString("weight", item.getWeight());
                    args.putString("size", item.getSize());

                    args.putString("sender_name", item.getSenderName());
                    args.putString("sender_phone", item.getSenderPhone());
                    args.putString("sender_town", item.getSenderTown());
                    args.putString("sender_address", item.getSenderAddress());

                    args.putString("recipient_first_name", item.getRecipientFirstName());
                    args.putString("recipient_last_name", item.getRecipientLastName());
                    args.putString("recipient_phone", item.getRecipientPhone());
                    args.putString("recipient_address", item.getRecipientAddress());
                    args.putString("recipient_town", item.getRecipientTown());

                    args.putString("seller_id", item.getSellerID());
                    args.putString("seller_money", item.getSellerMoney());
                    args.putString("date_posted", item.getDatePosted());
                    args.putString("status", item.getStatus());
                    args.putString("price", item.getPrice());

                    intent.putExtras(args);
                    activity.startActivity(intent);
                }
            });

            convertView.setBackgroundColor(Color.parseColor("#BDE6B5"));

        }



        return convertView;
    }



}
