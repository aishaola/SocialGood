package com.example.socialgood.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.socialgood.R;
import com.parse.ParseUser;

public class IntroActivity extends AppCompatActivity {
    Button btnSignUp;
    TextView tvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        // If user is logged in go to main activity
        if(ParseUser.getCurrentUser() != null){
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        btnSignUp = findViewById(R.id.btnSignUp);
        tvLogin = findViewById(R.id.tvLogin);

        YoYo.with(Techniques.Pulse).pivot(200, 100).repeat(10000).playOn(btnSignUp);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(IntroActivity.this, SignUpActivity.class);
                startActivity(i);
            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(IntroActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });
    }
}