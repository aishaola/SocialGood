package com.example.socialgood;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.example.socialgood.models.ParseUserSocial;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.Arrays;
import java.util.List;

public class Signup extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    Button btnSignUp;
    CheckBox cb1;
    CheckBox cb2;
    CheckBox cb3;
    EditText etUsername;
    EditText etPassword;
    List<CheckBox> checkBoxes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);

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

        btnSignUp = findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp(etUsername.getText().toString(), etPassword.getText().toString());
            }
        });


    }

    private void signUp(String username, String password) {
        ParseUserSocial user = new ParseUserSocial();
        user.setUsername(username);
        user.setPassword(password);

        for (int i = 0; i < checkBoxes.size(); i++) {
            CheckBox cb = checkBoxes.get(i);
            if(cb.isChecked())
                user.addCategory(cb.getText().toString());
        }
        user.saveCategories();
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if(e!=null){
                    Log.e(TAG, "SignUp error: issue with signing up!", e);
                    Toast.makeText(Signup.this, "Issue with signing up", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent i = new Intent(Signup.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}
