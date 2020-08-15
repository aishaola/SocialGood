package com.example.socialgood.models;

import com.parse.ParseFile;

import java.util.List;

public class Page {
    private int position;
    private String type;
    private List<Link> links;
    private String imageUrl;

    public Page(int position, String type, List<Link> contents){
        this.position = position;
        this.type = type;
        this.links = contents;
    }

    public Page(int position, String type, String contents){
        this.position = position;
        this.type = type;
        this.imageUrl = contents;
    }

    public int getPosition() {
        return position;
    }

    public String getType() {
        return type;
    }

    public String getImageUrl(){
        return imageUrl;
    }
}
