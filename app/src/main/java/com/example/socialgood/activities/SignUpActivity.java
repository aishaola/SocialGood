package com.example.socialgood.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.socialgood.R;
import com.example.socialgood.models.ParseUserSocial;
import com.parse.ParseException;
import com.parse.SignUpCallback;

import java.util.Arrays;
import java.util.List;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    Button btnSignUp;

    EditText etUsername;
    EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);


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

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if(e!=null){
                    Log.e(TAG, "SignUp error: issue with signing up!", e);
                    Toast.makeText(SignUpActivity.this, "Issue with signing up", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent i = new Intent(SignUpActivity.this, CategoriesActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}
