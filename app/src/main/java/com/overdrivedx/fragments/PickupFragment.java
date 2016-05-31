package com.overdrivedx.fragments;

import android.app.Dialog;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

import com.overdrivedx.utils.PickUpInterface;
import com.overdrivedx.utils.Utils;
import com.overdrivedx.views.CaptureSignatureView;
import com.overdrivedx.zoomd.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link PickupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PickupFragment extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String POSTINGID = "posting_id";

    // TODO: Rename and change types of parameters

    private String posting_id;
    private PickUpInterface callback;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment PickupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PickupFragment newInstance(String posting_id) {
        PickupFragment fragment = new PickupFragment();
        Bundle args = new Bundle();
        args.putString(POSTINGID, posting_id);
        fragment.setArguments(args);
        return fragment;
    }

    public PickupFragment() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            callback = (PickUpInterface) getActivity();
        }
        catch (ClassCastException e) {
            throw new ClassCastException("Calling fragment must implement DialogClickListener interface");
        }

        if (getArguments() != null) {
            posting_id = getArguments().getString(POSTINGID);

        }
        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_pickup, container, false);


        LinearLayout mContent = (LinearLayout) v.findViewById(R.id.linearLayout);
        final CaptureSignatureView mSig = new CaptureSignatureView(getActivity(), null);
        mContent.addView(mSig, LinearLayout.LayoutParams.MATCH_PARENT, 250);

        Button submit_sender_signature = (Button)v.findViewById(R.id.submit_sender_signature);

        submit_sender_signature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap signature = mSig.getBitmap();
                String filename = posting_id + "_sender";

                callback.pickUpItem(new Utils().saveImageLocally(signature, filename));
                dismiss();

            }
        });
        return v;
    }





}
