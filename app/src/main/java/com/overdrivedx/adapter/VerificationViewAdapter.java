package com.overdrivedx.adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.overdrivedx.zoomd.R;

/**
 * Created by babatundedennis on 10/13/15.
 */
public class VerificationViewAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] field;
    private final String[] label;
    private
    static class ViewHolder {
        public TextView tVlabel;
        public TextView tVfield;
    }

    public VerificationViewAdapter(Activity context, String[] labels, String[] field) {
        super(context, R.layout.activity_view_acvitity, field);
        this.context = context;
        this.field = field;
        this.label= labels;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.view_item_list, null);
            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.tVlabel = (TextView) rowView.findViewById(R.id.label);

            viewHolder.tVfield = (TextView) rowView.findViewById(R.id.field);
            rowView.setTag(viewHolder);
        }

        ViewHolder holder = (ViewHolder) rowView.getTag();
        holder.tVlabel.setText(label[position]);
        holder.tVfield.setText(field[position]);

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position == 5 || position == 9) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", field[position], null));
                    context.startActivity(intent);
                }
            }
        });


        return rowView;
    }
}
