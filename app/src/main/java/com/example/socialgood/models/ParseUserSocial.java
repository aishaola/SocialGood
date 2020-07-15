package com.example.socialgood.models;

import android.os.FileUtils;

import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

@ParseClassName("User")
public class ParseUserSocial extends ParseUser {

    public static final String KEY_CATEGORIES = "categories";
    public static final String KEY_PROFILE_PIC = "profilePic";
    public static final String JSON_KEY_CATEGORY = "category";
    public JSONArray jsonArrayCategories;
    public ParseFile profilePic;

    public ParseUserSocial(){
        super();
        jsonArrayCategories = new JSONArray();
    }

    // Initialize ParseUserSocial with existing user, allows for qu
    public ParseUserSocial(ParseUser user){
        super();
        jsonArrayCategories = user.getJSONArray(KEY_CATEGORIES);
        profilePic = user.getParseFile(KEY_PROFILE_PIC);
    }

    public static ParseUserSocial getCurrentUser(){
        ParseUser user = ParseUser.getCurrentUser();
        if(user == null)
            return null;
        return new ParseUserSocial(user);
    }

    public void setProfilePic(File file){
        ParseFile pf = new ParseFile(file);
        put(KEY_PROFILE_PIC, pf);
    }

    public ParseFile getProfilePic(){
        return profilePic;
    }

    public void addCategory(String category){
        JSONObject categoriesObj = new JSONObject();
        try {
            categoriesObj.put(JSON_KEY_CATEGORY, category);
            jsonArrayCategories.put(categoriesObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void saveCategories(){
        put(KEY_CATEGORIES, jsonArrayCategories);
    }

    public String getCategories(){
        JSONArray json = jsonArrayCategories;
        String categories = "";
        if(json == null)
            return categories;
        for (int i = 0; i < json.length(); i++) {
            try {
                categories += json.getJSONObject(i).getString(JSON_KEY_CATEGORY) + ",";
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return categories;
    }
}
