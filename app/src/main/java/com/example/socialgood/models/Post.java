package com.example.socialgood.models;
import android.media.audiofx.DynamicsProcessing;
import android.nfc.Tag;
import android.text.format.DateUtils;
import android.util.Log;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

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

import kotlin.reflect.KType;

@Parcel(analyze ={Post.class})
@ParseClassName("Post")
public class Post extends ParseObject {
    public static final String KEY_CATEGORIES = "categoriesPost";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_CAPTION = "caption";
    public static final String KEY_USER = "user";
    public static final String KEY_POST_RESHARED = "postReshared";
    public static final String KEY_TYPE = "type";
    public static final String KEY_DONATION = "donation";
    public static final String KEY_LINK_LIST = "linkList";

    public static final String DONATION_TYPE = "donation";
    public static final String RESHARE_TYPE = "reshare";
    public static final String LINK_TYPE = "link";
    public static final String IMAGE_TYPE = "image";


    public List<String> listCategories;
    public boolean userFollowsCat;
    public ParseUser userReshared;

    public Post(){
        super();
        listCategories = new ArrayList<>();
        userFollowsCat = false;
        userReshared = null;
    }

    public boolean isPostCurrUsers(){
        return getUser().getObjectId().equals(ParseUser.getCurrentUser().getObjectId());
    }

    public boolean isPostReshare(){
        return getType() != null && getType().equals(Post.RESHARE_TYPE);
    }

    public boolean isLink() {
        return getType() != null && getType().equals(Post.LINK_TYPE);
    }

    public boolean isImage(){
        return getType() != null && getType().equals(Post.IMAGE_TYPE);
    }

    public Post getPostReshared(){
        return (Post) getParseObject(KEY_POST_RESHARED);
    }

    public String getCategoriesDisplay(){
        JSONArray json = getJSONArray(KEY_CATEGORIES);
        String categories = "";
        if(json == null)
            return categories;
        for (int i = 0; i < json.length(); i++) {
            try {
                if(i != 0)
                    categories += ", ";

                categories += json.getJSONObject(i).getString("category");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return categories;
    }

    public String getTempCategoriesDisplay(){
        String categories = "";
        for (int i = 0; i < listCategories.size(); i++) {
            if(i != 0)
                categories += ", ";
            categories += listCategories.get(i);
        }
        return categories;
    }

    public List<String> getListCategories(){
        JSONArray json = getJSONArray(KEY_CATEGORIES);
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


    public void setUserFollowsCat(boolean userFollowsCat) {
        this.userFollowsCat = userFollowsCat;
    }

    public boolean isUserFollowsCat() {
        return userFollowsCat;
    }

    public void addCategory(String category){
        listCategories.add(category);
    }

    public void addCategories(List<String> categories){
        listCategories.addAll(categories);
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

    public void setImage(ParseFile image) {
        image.saveInBackground();
        put(KEY_IMAGE, image);
    }

    public ParseUser getUser(){
        return getParseUser(KEY_USER);
    }

    public ParseUserSocial getUserSocial(){ return new ParseUserSocial(getUser());}

    public void setUser(ParseUser user){
        put(KEY_USER, user);
    }

    public static Post reshare(ParseUser otherUser, Post postToReshare){
        Post post = new Post();
        post.setUser(otherUser);
        post.put(KEY_POST_RESHARED, postToReshare);
        post.setType(Post.RESHARE_TYPE);
        return post;
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

    public void setDonation(Donation donation) {
        put(KEY_DONATION, donation);
    }

    public Donation getDonation() {
        return (Donation) getParseObject (KEY_DONATION);
    }

    public void setType(String type) {
        put(KEY_TYPE, type);
    }
    public String getType() {
        return getString(KEY_TYPE);
    }

    public void setLinks(List<Link> links) {
        JSONArray linkJsonArray = new JSONArray();
        for(Link link: links){
            linkJsonArray.put(link.toJSON());
        }
        put(KEY_LINK_LIST, linkJsonArray);
    }

    public List<Link> getLinks() {
        JSONArray jsonLinks = getJSONArray(KEY_LINK_LIST);
        List<Link> links = new ArrayList<>();
        if(jsonLinks == null)
            return links;
        for (int i = 0; i < jsonLinks.length(); i++) {
            try {
                links.add(Link.fromJSON(jsonLinks.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return links;
    }


}