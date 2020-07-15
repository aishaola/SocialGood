package com.example.socialgood;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        cb1 = findViewById(R.id.cbCat1);
        cb2 = findViewById(R.id.cbCat2);
        cb3 = findViewById(R.id.cbCat3);

        List<String> categories = Arrays.asList("Racial Justice", "Yemen Crisis", "General");

        cb1.setText(categories.get(0));
        cb2.setText(categories.get(1));
        cb3.setText(categories.get(2));

        btnSignUp = findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp(etUsername.getText().toString(), etPassword.getText().toString());
            }
        });


    }

    private void signUp(String username, String password) {
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if(e!=null){
                    Log.e(TAG, "Login error: issue with signing up!", e);
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
