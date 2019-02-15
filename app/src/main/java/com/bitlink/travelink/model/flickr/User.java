package com.bitlink.travelink.model.flickr;

/**
 * Created by Sefer on 31.12.2016.
 */

public class User {

    private String nsid;

    private String name;

    private String fullname;

    public void setNsid(String nsid){
        this.nsid = nsid;
    }

    public String getNsid(){
        return this.nsid;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public void setFullname(String fullname){
        this.fullname = fullname;
    }

    public String getFullname(){
        return this.fullname;
    }

}