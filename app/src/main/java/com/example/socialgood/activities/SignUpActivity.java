package com.example.socialgood.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.socialgood.R;
import com.example.socialgood.SocialGoodHelpers;
import com.example.socialgood.models.ParseUserSocial;
import com.google.android.material.textfield.TextInputLayout;
import com.parse.ParseException;
import com.parse.SignUpCallback;

import java.util.Arrays;
import java.util.List;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    Button btnSignUp;
    EditText etUsername;
    EditText etPassword;
    EditText etConfirmPassword;
    RelativeLayout rlSignUp;
    TextInputLayout etUsernameLayout;
    TextInputLayout etPasswordLayout;
    TextInputLayout etConfirmPasswordLayout;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etUsernameLayout = findViewById(R.id.etUsernameLayout);
        etPasswordLayout = findViewById(R.id.etPasswordLayout);
        etConfirmPasswordLayout = findViewById(R.id.etConfirmPasswordLayout);
        btnSignUp = findViewById(R.id.btnSignUp);

        rlSignUp = findViewById(R.id.rlSignUp);

        SocialGoodHelpers.initHideKeyboardLayout(rlSignUp, this);
        SocialGoodHelpers.removeErrorWhenClicked(etUsername, etUsernameLayout);
        SocialGoodHelpers.removeErrorWhenClicked(etPassword, etPasswordLayout);
        SocialGoodHelpers.removeErrorWhenClicked(etConfirmPassword, etConfirmPasswordLayout);

        // Show Progress dialog when posts are first loading
        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCancelable(false);


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                String confirmPassword = etConfirmPassword.getText().toString();
                if(!checkForError(username, password, confirmPassword))
                    signUp(username, password);
                else {
                    YoYo.with(Techniques.Shake).playOn(btnSignUp);
                }
            }
        });


    }

    public boolean checkForError(String username, String password, String confirmPassword){
        boolean error = false;
        if(username.isEmpty()) {
            etUsernameLayout.setError(getString(R.string.fieldRequired));
            error = true;
        }
        if(password.isEmpty()){
            etPasswordLayout.setError(getString(R.string.fieldRequired));
            error = true;
        }
        if(confirmPassword.isEmpty()){
            etConfirmPasswordLayout.setError(getString(R.string.fieldRequired));
            error = true;
        }

        if(!password.equals(confirmPassword)){
            etConfirmPasswordLayout.setError("Passwords do not match");
            error = true;
        }
        return error;

    }

    private void signUp(String username, final String password) {
        pd.show();
        ParseUserSocial user = new ParseUserSocial();
        user.setUsername(username);
        user.setPassword(password);
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if(e!=null){
                    if(e.getCode() == 202){
                        etUsernameLayout.setError("Username is already taken");
                    } else {
                        Log.e(TAG, "done: Error Signing up", e);
                        Toast.makeText(SignUpActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                    }
                    pd.dismiss();
                    YoYo.with(Techniques.Shake).playOn(btnSignUp);
                    return;
                }
                pd.dismiss();
                Intent i = new Intent(SignUpActivity.this, CategoriesActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}
