package com.example.socialgood.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Link {
    private String title;
    private String url;

    public Link(String title, String url){
        this.title = title;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public JSONObject toJSON(){
        JSONObject linkJson = new JSONObject();

        try {
            linkJson.put("type", "link");
            linkJson.put("title", title);
            linkJson.put("url", url);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return linkJson;
    }

    public static JSONObject linkToJSON(Link link){
        JSONObject linkJson = new JSONObject();

        try {
            linkJson.put("type", "link");
            linkJson.put("title", link.getTitle());
            linkJson.put("url", link.getUrl());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return linkJson;
    }
}