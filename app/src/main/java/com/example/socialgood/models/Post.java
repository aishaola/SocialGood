package com.example.socialgood.models;
import android.text.format.DateUtils;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Parcel(analyze ={Post.class})
@ParseClassName("Post")
public class Post extends ParseObject {
    public static final String KEY_CATEGORIES = "categoriesPost";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_LINK = "linkPost";
    public static final String KEY_CAPTION = "caption";
    public static final String KEY_USER = "user";

    public List<String> listCategories;

    public Post(){
        super();
        listCategories = new ArrayList<>();
    }

    public String getCategoriesDisplay(){
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

    public List<String> getListCategories(){
        JSONArray json = getJSONArray(KEY_CATEGORIES);
        List<String> listCat = new ArrayList<>();
        for (int i = 0; i < json.length(); i++) {
            try {
                listCat.add(json.getJSONObject(i).getString("category"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return listCat;
    }

    public void addCategory(String category){
        listCategories.add(category);
    }

    public void saveCategories(){
        JSONArray jsonArrayCategories = new JSONArray();
        for (int i = 0; i < listCategories.size(); i++) {
            String category = listCategories.get(i);

            JSONObject categoriesObj = new JSONObject();
            try {
                categoriesObj.put("category", category);
                jsonArrayCategories.put(categoriesObj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
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

    public void setLink(JSONObject link){
        put(KEY_LINK, link);
    }

    public String[] getLink(){
        JSONObject linkPost = getJSONObject(KEY_LINK);
        if(linkPost == null)
            return null;
        String title = "";
        String url = "";
        try {
            title = linkPost.getString("title");
            url = linkPost.getString("url");
        } catch(JSONException e){
            e.printStackTrace();
        }
        String[] link = {title, url};
        return link;
    }

    public ParseUser getUser(){
        return getParseUser(KEY_USER);
    }

    public ParseUserSocial getUserSocial(){ return new ParseUserSocial(getUser());}

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