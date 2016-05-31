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

import com.overdrivedx.database.DatabaseHandler;
import com.overdrivedx.utils.Utils;
import com.overdrivedx.zoomd.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link AcceptItemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddUserFragment extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match

    private DatabaseHandler dh;
    // TODO: Rename and change types of parameters

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AcceptItemFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddUserFragment newInstance() {
        AddUserFragment fragment = new AddUserFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    public AddUserFragment() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dh = new DatabaseHandler(getActivity());
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_add_user, container, false);
        final EditText user_name = (EditText)v.findViewById(R.id.user_name);
        Button add_user = (Button)v.findViewById(R.id.add_user);

        add_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usern = user_name.getText().toString();
                new Utils().saveUser(usern, getActivity());
                dismiss();
            }
        });

        return v;
    }




}
