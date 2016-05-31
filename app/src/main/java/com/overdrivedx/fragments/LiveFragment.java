package com.overdrivedx.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.overdrivedx.adapter.FeedListAdapter;
import com.overdrivedx.database.DatabaseHandler;
import com.overdrivedx.model.Item;
import com.overdrivedx.utils.Constants;
import com.overdrivedx.utils.Utils;
import com.overdrivedx.zoomd.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LiveFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LiveFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LiveFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Activity activity;
    private OnFragmentInteractionListener mListener;
    Utils util;
    private FeedListAdapter listAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<Item> feedItems;
    private ListView listView;
    private View waiting;
    private TextView splash_message;
    ProgressBar pd;
    DatabaseHandler dh;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LiveFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LiveFragment newInstance(String param1, String param2) {
        LiveFragment fragment = new LiveFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public LiveFragment() {
        // Required empty public constructor
    }

    @Override
    public void onSaveInstanceState(Bundle icicle) {
        super.onSaveInstanceState(icicle);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        dh = new DatabaseHandler(getActivity());
        feedItems = new ArrayList<Item>();
        util = new Utils();

        new GetFeed().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

       View v = inflater.inflate(R.layout.fragment_live, container, false);
       listView = (ListView) v.findViewById(R.id.listfeedView);
       swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
       waiting = v.findViewById(R.id.waiting);
       splash_message = (TextView) v.findViewById(R.id.splash_message);
       pd = (ProgressBar)v.findViewById(R.id.progressBar);

       return v;
    }

    @Override
    public void onRefresh() {
        feedItems = null;
        feedItems = new ArrayList<Item>();
        new GetFeed().execute();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onLiveFragmentInteraction(Uri uri);
    }

    private class GetFeed extends AsyncTask<Void, String, Void> {
        @Override
        protected void onPreExecute() {

            if (isCancelled()) {
                cancel(true);
            }
        }

        @Override
        protected Void doInBackground(Void... params) {

            RequestQueue rq = Volley.newRequestQueue(activity);

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
                    Log.e(Constants.TAG, error.getMessage());
                    onXError("Unable to fetch feed local.");
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

                String status = feedObj.getString("status");
                String date_posted = feedObj.getString("date_posted");
                /*
                Item item = new Item();
                item.setID(id);
                item.setItemDetails(item_details);
                item.setSenderName(sender_name);
                item.setSenderPhone(sender_phone);
                item.setSenderAddress(sender_address);
                item.setSenderTown(sender_town);
                item.setRecipientFirstName(recipient_first_name);
                item.setRecipientLastName(recipient_last_name);
                item.setRecipientAddress(recipient_address);
                item.setRecipientPhone(recipient_phone);
                item.setRecipientTown(recipient_town);

                item.setSellerMoney(seller_money);
                item.setSellerID(seller_id);
                item.setWeight(weight);
                item.setSize(size);
                item.setPrice(price);
                item.setStatus(status);
                item.setDatePosted(date_posted);
                */

                Item item = new Item(id,item_details, sender_name, sender_phone, sender_town, sender_address,
                        recipient_address, recipient_first_name, recipient_last_name, recipient_phone, recipient_town, seller_id, seller_money, weight,
                        price, size, date_posted, status);

                feedItems.add(item);


            }


            listAdapter = new FeedListAdapter(activity, feedItems);
            listView.setAdapter(listAdapter);

            waiting.setVisibility(View.INVISIBLE);
            swipeRefreshLayout.setVisibility(View.VISIBLE);

            listAdapter = new FeedListAdapter(activity, feedItems);

            listView.setAdapter(listAdapter);

        }
        else{


        }


    }

    public void onXError(String s) {
        util.onError(s, activity);
    }
}
