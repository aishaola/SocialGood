package com.example.socialgood.models;

import android.graphics.Bitmap;

import com.parse.ParseFile;

import java.util.List;

public class Page {
    private int position;
    private String type;
    private List<Link> links;
    private String imageUrl;
    private Bitmap bitmap;
    public static final String TYPE_BITMAP = "bitmap";

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

    // This constructor will be for when showing Bitmaps of photos on the add image screen
    public Page(int position, String type, Bitmap contents){
        this.position = position;
        this.type = type;
        this.bitmap = contents;
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

    public Bitmap getBitmap(){
        return bitmap;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

}
