package com.example.socialgood.models;

import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

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
}
