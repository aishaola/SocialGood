package com.example.socialgood.fragments;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.socialgood.R;
// ...

public class LinkEntryDialogFragment extends DialogFragment implements View.OnClickListener {

    private EditText etLinkTitle;
    private EditText etLinkUrl;
    private Button btnAddLink;

    public LinkEntryDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static LinkEntryDialogFragment newInstance(String title) {
        LinkEntryDialogFragment frag = new LinkEntryDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_link_entry, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        etLinkTitle = view.findViewById(R.id.etLinkTitle);
        etLinkUrl = view.findViewById(R.id.etLinkUrl);
        btnAddLink = view.findViewById(R.id.btnAddLink);
        btnAddLink.setOnClickListener(this);
        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Enter Name");
        getDialog().setTitle(title);
        // Show soft keyboard automatically and request focus to field
        etLinkTitle.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);


    }

    public interface LinkEntryDialogListener {
        void onFinishEditDialog(String title, String url);
    }

    @Override
    public void onClick(View view) {
        sendBackResult();
    }

    // Call this method to send the data back to the parent fragment
    public void sendBackResult() {
        // Notice the use of `getTargetFragment` which will be set when the dialog is displayed
        LinkEntryDialogListener listener = (LinkEntryDialogListener) getTargetFragment();
        listener.onFinishEditDialog(etLinkTitle.getText().toString(), etLinkUrl.getText().toString());
        dismiss();
    }
}