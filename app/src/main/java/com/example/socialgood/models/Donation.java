package com.example.socialgood.models;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

@ParseClassName("Donation")
public class Donation extends ParseObject {
    public static final String KEY_USER = "userDonating";
    public static final String KEY_FUNDRAISER = "fundraiser";
    public static final String KEY_AMOUNT = "amountDonated";
    public static final String KEY_MESSAGE = "message";


    public Donation(){
        super();
        try {
            fetchIfNeeded();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    public Donation(ParseUser user, Fundraiser fundraiser, Double amountDonated){
        super();
        put(KEY_USER, user);
        put(KEY_FUNDRAISER, fundraiser);
        put(KEY_AMOUNT, amountDonated);
    }

    public void getUser(){
        getParseUser(KEY_USER);
    }

    public Fundraiser getFundraiser(){
        return (Fundraiser) getParseObject(KEY_FUNDRAISER);
    }

    public double getAmountDonated(){
        return getDouble(KEY_AMOUNT);
    }
}
