package com.example.socialgood.models;

import android.text.format.DateUtils;

import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

import java.text.SimpleDateFormat;
import java.util.Locale;

@Parcel(analyze ={Comment.class})
@ParseClassName("Comment")
public class Comment extends ParseObject {
    public static final String KEY_USER = "user";
    public static final String KEY_USER_COMMENT = "userComment";
    public static final String KEY_POST = "post";

    public Comment(){
        super();
    }

    public Comment(ParseUser user, String comment, Post post){
        super();
        setUser(user);
        setComment(comment);
        setPost(post);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setComment(String comment){
        put(KEY_USER_COMMENT, comment);
    }

    public String getComment(){
        return getString(KEY_USER_COMMENT);
    }

    public void setPost(Post post){
        put(KEY_POST, post);
    }

    public Post getPost(){
        return (Post) getParseObject(KEY_POST);
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
        relativeDate = relativeDate.replace(" days ago", "d");
        relativeDate = relativeDate.replace(" day ago", "d");
        relativeDate = relativeDate.replace(" hours ago", "h");
        relativeDate = relativeDate.replace(" hour ago", "h");
        relativeDate = relativeDate.replace(" weeks ago", "w");
        relativeDate = relativeDate.replace(" week ago", "w");

        return relativeDate;
    }

}
