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

import com.overdrivedx.utils.AddActivityInterface;
import com.overdrivedx.zoomd.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link AcceptItemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddActivityFragment extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match


    // TODO: Rename and change types of parameters

    private AddActivityInterface callback;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AcceptItemFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddActivityFragment newInstance() {
        AddActivityFragment fragment = new AddActivityFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    public AddActivityFragment() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            callback = (AddActivityInterface) getActivity();
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
        View v = inflater.inflate(R.layout.fragment_add_activity, container, false);
        Button m30 = (Button)v.findViewById(R.id.activity_add_status);
        final EditText status = (EditText) v.findViewById(R.id.activity_text);

        m30.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.addStatus(status.getText().toString());
                dismiss();
            }
        });


        return v;
    }




}
