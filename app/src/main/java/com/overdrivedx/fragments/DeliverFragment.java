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
import android.widget.EditText;
import android.widget.LinearLayout;

import com.overdrivedx.utils.DeliverInterface;
import com.overdrivedx.utils.Utils;
import com.overdrivedx.views.CaptureSignatureView;
import com.overdrivedx.zoomd.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link DeliverFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeliverFragment extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String FIRSTNAME = "first_name";
    private static final String LASTNAME = "last_name";
    private static final String POSTINGID = "posting_id";


    // TODO: Rename and change types of parameters
    private String first_name, last_name, posting_id;
    private DeliverInterface callback;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param first_name Parameter 1.
     * @return A new instance of fragment DeliverFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DeliverFragment newInstance(String first_name, String last_name, String posting_id) {
        DeliverFragment fragment = new DeliverFragment();
        Bundle args = new Bundle();
        args.putString(FIRSTNAME, first_name);
        args.putString(LASTNAME, last_name);
        args.putString(POSTINGID, posting_id);
        fragment.setArguments(args);
        return fragment;
    }

    public DeliverFragment() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            callback = (DeliverInterface) getActivity();
        }
        catch (ClassCastException e) {
            throw new ClassCastException("Calling fragment must implement DialogClickListener interface");
        }

        if (getArguments() != null) {
            posting_id = getArguments().getString(POSTINGID);
            last_name = getArguments().getString(LASTNAME);
            first_name = getArguments().getString(FIRSTNAME);
        }

        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_deliver, container, false);
        final EditText fname = (EditText)v.findViewById(R.id.deliver_first_name);
        final EditText lname = (EditText)v.findViewById(R.id.deliver_last_name);

        fname.setText(first_name);
        lname.setText(last_name);

        LinearLayout mContent = (LinearLayout) v.findViewById(R.id.linearLayout);
        final CaptureSignatureView mSig = new CaptureSignatureView(getActivity(), null);
        mContent.addView(mSig, LinearLayout.LayoutParams.MATCH_PARENT, 250);

        Button submit_recipient_signature = (Button)v.findViewById(R.id.submit_recipient_signature);

        submit_recipient_signature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap signature = mSig.getBitmap();
                String filename = posting_id + "_recipient";

                callback.deliverItem(
                        new Utils().saveImageLocally(signature, filename),
                        fname.getText().toString(),
                        lname.getText().toString());
                dismiss();
            }
        });
        return v;
    }




}
