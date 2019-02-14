package com.app.sampleapp.network;

import com.google.gson.annotations.SerializedName;

public class ResponseModel {

    @SerializedName("location")
    String location;

    public void setLocation(String location){

        this.location = location;
    }

    public String getLocation(){

        return location;
    }
}
