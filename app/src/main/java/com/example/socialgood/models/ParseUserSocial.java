package com.example.socialgood.models;

import android.os.FileUtils;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseDecoder;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ParseClassName("User")
public class ParseUserSocial extends ParseUser {

    public static final String KEY_CATEGORIES = "categories";
    public static final String KEY_PROFILE_PIC = "profilePic";
    public static final String JSON_KEY_CATEGORY = "category";
    public static final String KEY_FOLLOWING = "profilesFollowing";
    public JSONArray jsonArrayCategories;
    public ParseFile profilePic;
    public ParseUser user;

    public ParseUserSocial(){
        super();
        user = new ParseUser();
        jsonArrayCategories = new JSONArray();
    }

    // Initialize ParseUserSocial with existing user, allows for qu
    public ParseUserSocial(ParseUser user){
        super();
        this.user = user;
        jsonArrayCategories = user.getJSONArray(KEY_CATEGORIES);
        profilePic = user.getParseFile(KEY_PROFILE_PIC);
    }

    public static ParseUserSocial getCurrentUser(){
        return new ParseUserSocial(ParseUser.getCurrentUser());
    }

    public void setProfilePic(ParseFile file){
        user.put(KEY_PROFILE_PIC, file);
    }

    public ParseFile getProfilePic(){
        return user.getParseFile(KEY_PROFILE_PIC);
    }

    public List<ParseUser> getProfilesFollowing(){
        JSONArray jsonUsersFollowing = user.getJSONArray(KEY_FOLLOWING);
        List<ParseUser> usersFollowing = new ArrayList<>();

        // return empty list if users does not follow any other profile
        if(jsonUsersFollowing == null)
            return usersFollowing;

        for (int i = 0; i < jsonUsersFollowing.length(); i++) {
            try {
                ParseUser user = ParseUser.fromJSON(jsonUsersFollowing.getJSONObject(i), "User", ParseDecoder.get());
                usersFollowing.add(user);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return usersFollowing;
    }

    public boolean userIsFollowing(ParseUser otherUser){
        for (ParseUser parseUser: getProfilesFollowing()) {
            if(otherUser.getObjectId().equals(parseUser.getObjectId()))
                return true;
        }
        return false;
    }

    public void addProfileFollowing(ParseUser otherUser){
        user.add(KEY_FOLLOWING, otherUser);
    }

    public void removeProfileFollowing(ParseUser otherUser){
        user.removeAll(KEY_FOLLOWING, Collections.singletonList(otherUser));
    }

    public void addCategory(String category){
        if(jsonArrayCategories == null)
            jsonArrayCategories = new JSONArray();

        JSONObject categoriesObj = new JSONObject();
        try {
            categoriesObj.put(JSON_KEY_CATEGORY, category);
            jsonArrayCategories.put(categoriesObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addNewListOfCategories(List<String> categories){
        jsonArrayCategories = null;
        for (String cat: categories) {
            addCategory(cat);
        }
        saveCategories();
    }

    public void saveCategories(){
        user.put(KEY_CATEGORIES, jsonArrayCategories);
    }

    public String getCategories(){
        JSONArray json = jsonArrayCategories;
        String categories = "";
        if(json == null)
            return categories;
        for (int i = 0; i < json.length(); i++) {
            try {
                if(i != 0)
                    categories += ", ";
                categories += json.getJSONObject(i).getString(JSON_KEY_CATEGORY);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return categories;
    }

    public List<String> getCategoriesList(){
        JSONArray json = jsonArrayCategories;
        List<String> listCat = new ArrayList<>();

        if(json == null)
            return listCat;

        for (int i = 0; i < json.length(); i++) {
            try {
                listCat.add(json.getJSONObject(i).getString("category"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return listCat;
    }



    public void saveToDatabase(){
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null){
                    Log.e(ParseUserSocial.class.getSimpleName(), "ERROR SAVING", e);
                }
            }
        });
    }
}
