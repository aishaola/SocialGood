package com.example.socialgood.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.socialgood.R;
import com.example.socialgood.SocialGoodHelpers;
import com.google.android.material.textfield.TextInputLayout;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    Button btnLogin;
    EditText etUsername;
    EditText etPassword;
    RelativeLayout rlLogin;
    TextInputLayout etUsernameLayout;
    TextInputLayout etPasswordLayout;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etUsernameLayout = findViewById(R.id.etUsernameLayout);
        etPasswordLayout = findViewById(R.id.etPasswordLayout);
        rlLogin = findViewById(R.id.rlLogin);
        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login(etUsername.getText().toString(), etPassword.getText().toString());
            }
        });

        // Show Progress dialog when posts are first loading
        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCancelable(false);

        SocialGoodHelpers.initHideKeyboardLayout(rlLogin, this);
        SocialGoodHelpers.removeErrorWhenClicked(etUsername, etUsernameLayout);
        SocialGoodHelpers.removeErrorWhenClicked(etPassword, etPasswordLayout);

    }

    private void login(String username, String password) {
        if(username.isEmpty()){
            etUsernameLayout.setError(getString(R.string.fieldRequired));
            YoYo.with(Techniques.Shake).playOn(btnLogin);
            return;
        } else if(password.isEmpty()){
            etPasswordLayout.setError(getString(R.string.fieldRequired));
            YoYo.with(Techniques.Shake).playOn(btnLogin);
            return;
        }
        pd.show();
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if(e != null){
                    if(e.getCode()==101){
                        etUsernameLayout.setError("Username or password wrong");
                        etPasswordLayout.setError("Username or password wrong");
                    } else
                        Toast.makeText(LoginActivity.this, "Network error", Toast.LENGTH_SHORT).show();

                    YoYo.with(Techniques.Shake).playOn(btnLogin);
                    Log.e(TAG, "login error: issue with logging in", e);
                    pd.dismiss();
                    return;
                }
                pd.dismiss();
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}