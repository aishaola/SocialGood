package com.example.parsegram;

import android.text.format.DateUtils;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;


@ParseClassName("Post")
public class Post extends ParseObject {
    public static final String KEY_CATEGORIES = "categoriesPost";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_LINK = "linkPost";
    public static final String KEY_CAPTION = "caption";
    public static final String KEY_USER = "user";

    public JSONArray jsonArrayCategories;

    public String getCategories(){
        JSONArray json = getJSONArray(KEY_CATEGORIES);
        String categories = "";
        for (int i = 0; i < json.length(); i++) {
            try {
                categories += json.getJSONObject(i).getString("category") + ",";
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return categories;
    }

    public Post(){
        super();
        jsonArrayCategories = new JSONArray();
    }

    public void addCategory(String category){
        JSONObject categoriesObj = new JSONObject();
        try {
            categoriesObj.put("category", category);
            jsonArrayCategories.put(categoriesObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void saveCategories(){
        put(KEY_CATEGORIES, jsonArrayCategories);
    }

    public String getCaption(){
        return getString(KEY_CAPTION);
    }
    public void setCaption(String caption){
        put(KEY_CAPTION, caption);
    }

    public ParseFile getImage(){
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile image){
        put(KEY_IMAGE, image);
    }

    public String getLink(){
        JSONObject linkPost = getJSONObject(KEY_LINK);
        String title = "";
        String url = "";
        try {
            title = linkPost.getString("title");
            url = linkPost.getString("url");
        } catch(JSONException e){
            e.printStackTrace();
        }
        return title + ", " + url;
    }

    public ParseUser getUser(){
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user){
        put(KEY_USER, user);
    }


    public String getRelativeTimeAgo() {
        String rawJsonDate = getCreatedAt().toString();
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }


}