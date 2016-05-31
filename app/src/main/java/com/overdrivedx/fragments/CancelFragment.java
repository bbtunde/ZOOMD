package com.overdrivedx.fragments;

import android.app.Dialog;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.overdrivedx.utils.CancelInterface;
import com.overdrivedx.zoomd.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link CancelFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CancelFragment extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private static final String ITEMDETAILS = "item_details";

    // TODO: Rename and change types of parameters
    private String item_details;
    private CancelInterface callback;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *


     * @return A new instance of fragment CancelFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CancelFragment newInstance(String item_details) {
        CancelFragment fragment = new CancelFragment();
        Bundle args = new Bundle();


        args.putString(ITEMDETAILS, item_details);

        fragment.setArguments(args);
        return fragment;
    }

    public CancelFragment() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            callback = (CancelInterface) getActivity();
        }
        catch (ClassCastException e) {
            throw new ClassCastException("Calling fragment must implement DialogClickListener interface");
        }

        if (getArguments() != null) {
            item_details = getArguments().getString(ITEMDETAILS);

        }
        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_cancel, container, false);
        TextView item_details_field = (TextView)v.findViewById(R.id.item_details);
        item_details_field.setText(item_details);

        final EditText reason_field = (EditText)v.findViewById(R.id.reason);
        Button cancel_btn = (Button) v.findViewById(R.id.cancel_btn);

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reason = reason_field.getText().toString();
                callback.cancelItem(reason);
                dismiss();

            }
        });
        return v;
    }


}
