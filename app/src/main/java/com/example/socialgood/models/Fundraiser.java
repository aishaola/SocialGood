package com.example.socialgood.models;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcel;

import java.io.File;

@Parcel(analyze ={Fundraiser.class})
@ParseClassName("Fundraiser")
public class Fundraiser extends ParseObject {
    public static final String KEY_OWNER = "owner";
    public static final String KEY_TITLE = "title";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_TARGET = "amountTarget";
    public static final String KEY_AMOUNT_RAISED = "amountRaised";
    private static final String TAG = Fundraiser.class.getSimpleName();


    public Fundraiser(){
        super();
    }

    public Fundraiser(ParseUser owner, String title, String description){
        super();
        put(KEY_OWNER, owner);
        put(KEY_TITLE, title);
        put(KEY_DESCRIPTION, description);
    }

    public ParseUser getOwner(){
        return getParseUser(KEY_OWNER);
    }

    public String getTitle(){
        return getString(KEY_TITLE);
    }

    public String getDescription(){
        return getString(KEY_DESCRIPTION);
    }

    public double getAmountRaised(){
        return getDouble(KEY_AMOUNT_RAISED);
    }

    public void addToFunds(double donationAmount){
        put(KEY_AMOUNT_RAISED, getAmountRaised() + donationAmount);
        saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null){
                    Log.e(TAG, "Couldn't add funds to fundraiser", e);
                    return;
                }
                Log.i(TAG, "Saved Donation Post");
            }
        });
    }
}
