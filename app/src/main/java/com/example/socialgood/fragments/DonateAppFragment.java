package com.example.socialgood.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.socialgood.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DonateAppFragment} factory method to
 * create an instance of this fragment.
 */
public class DonateAppFragment extends Fragment {
    private static final String ARG_PARAM2 = "param2";

    private EditText etDonation;
    private Button btnDonate;

    public DonateAppFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_donate_app, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Donate to App");

        etDonation = view.findViewById(R.id.etDonation);
        btnDonate = view.findViewById(R.id.btnDonate);

        btnDonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double donation = Double.parseDouble(etDonation.getText().toString());

            }
        });
    }
}