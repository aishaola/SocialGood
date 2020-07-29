package com.example.socialgood.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.parceler.Parcel;

@Parcel(analyze ={Fundraiser.class})
@ParseClassName("Fundraiser")
public class Fundraiser extends ParseObject {
    public Fundraiser(){
        super();
    }
}
