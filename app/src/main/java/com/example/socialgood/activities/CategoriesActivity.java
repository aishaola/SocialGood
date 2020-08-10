package com.example.socialgood.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.example.socialgood.R;
import com.example.socialgood.SocialGoodHelpers;
import com.example.socialgood.models.ParseUserSocial;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CategoriesActivity extends AppCompatActivity {
    CheckBox cb1;
    CheckBox cb2;
    CheckBox cb3;
    List<CheckBox> checkBoxes;
    List<CheckBox> checkBoxes1;
    Button btnSaveCategories;
    LinearLayout llCheckboxes;
    ParseUserSocial helper;
    ParseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        user = ParseUser.getCurrentUser();
        helper = new ParseUserSocial(user);

        // Initialize linear layout to add buttons to
        llCheckboxes = findViewById(R.id.llCheckboxes);
        checkBoxes = new ArrayList<>();

        // List of categories User can choose to associate account with, add a checkbox to each view
        List<String> categories = SocialGoodHelpers.SG_CATEGORIES;

        // For each category, add a checkbox to LinearLayout View
        for (String cat: categories) {
            CheckBox cb = new CheckBox(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams
                    (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            cb.setLayoutParams(lp);
            lp.topMargin = 10;
            cb.setText(cat);
            llCheckboxes.addView(cb);
            checkBoxes.add(cb);
        }

        btnSaveCategories = findViewById(R.id.btnSaveCategories);
        btnSaveCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePickedCategories();
                startActivity(new Intent(CategoriesActivity.this, MainActivity.class));
            }
        });
    }

    private void savePickedCategories(){
        for (int i = 0; i < checkBoxes.size(); i++) {
            CheckBox cb = checkBoxes.get(i);
            if(cb.isChecked())
                helper.addCategory(cb.getText().toString());
        }
        helper.saveCategories();
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null)
                    Log.e("saveCat", "done: categories not saving", e);
            }
        });
    }
}