package com.example.socialgood.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

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


    public Fundraiser(){
        super();
    }

    public Fundraiser(ParseUser owner, String title, String description){
        super();
        put(KEY_OWNER, owner);
        put(KEY_TITLE, title);
        put(KEY_DESCRIPTION, description);
    }

    public void getOwner(){
        getParseUser(KEY_OWNER);
    }

    public void getTitle(){
        getParseObject(KEY_TITLE);
    }

    public void getDescription(){
        getString(KEY_DESCRIPTION);
    }
}
