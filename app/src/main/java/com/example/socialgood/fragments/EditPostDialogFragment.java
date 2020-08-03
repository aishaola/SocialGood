package com.example.socialgood.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.socialgood.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditPostDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditPostDialogFragment extends DialogFragment {

    Button btnDelete;

    public EditPostDialogFragment() {
        // Required empty public constructor
    }


    public static EditPostDialogFragment newInstance(String title) {
        EditPostDialogFragment frag = new EditPostDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
            btnDelete = view.findViewById(R.id.btnDelete);
    }
}