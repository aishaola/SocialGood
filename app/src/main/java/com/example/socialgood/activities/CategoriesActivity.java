package com.example.socialgood.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.example.socialgood.R;
import com.example.socialgood.models.ParseUserSocial;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Arrays;
import java.util.List;

public class CategoriesActivity extends AppCompatActivity {
    CheckBox cb1;
    CheckBox cb2;
    CheckBox cb3;
    List<CheckBox> checkBoxes;
    Button btnSaveCategories;
    ParseUserSocial user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        user = ParseUserSocial.getCurrentUser();

        // Add the checkboxes into a list so they can be accessed easily
        cb1 = findViewById(R.id.cbCat1);
        cb2 = findViewById(R.id.cbCat2);
        cb3 = findViewById(R.id.cbCat3);
        checkBoxes = Arrays.asList(cb1, cb2, cb3);

        // List of categories User can choose to associate account with
        List<String> categories = Arrays.asList("Racial Justice", "Yemen Crisis", "General", "Global Warming");

        // Sets the text of each of the checkboxes to a category
        for (int i = 0; i < checkBoxes.size(); i++) {
            checkBoxes.get(i).setText(categories.get(i));
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
                user.addCategory(cb.getText().toString());
        }
        user.saveCategories();
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null)
                    Log.e("saveCat", "done: categories not saving", e);
            }
        });
    }
}