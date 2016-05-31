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

import com.overdrivedx.utils.AcceptItemInterface;
import com.overdrivedx.zoomd.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link AcceptItemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AcceptItemFragment extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match


    // TODO: Rename and change types of parameters

    private AcceptItemInterface callback;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AcceptItemFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AcceptItemFragment newInstance() {
        AcceptItemFragment fragment = new AcceptItemFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    public AcceptItemFragment() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            callback = (AcceptItemInterface) getActivity();
        }
        catch (ClassCastException e) {
            throw new ClassCastException("Calling fragment must implement DialogClickListener interface");
        }


        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_accept_item, container, false);
        Button m30 = (Button)v.findViewById(R.id.m30);

        m30.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.acceptItem(0);
                dismiss();
            }
        });

        Button m1 = (Button)v.findViewById(R.id.m1);

        m1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.acceptItem(1);
                dismiss();
            }
        });

        Button m3 = (Button)v.findViewById(R.id.m3);
        m3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.acceptItem(2);
                dismiss();
            }
        });
        return v;
    }




}
