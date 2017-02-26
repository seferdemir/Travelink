package com.bitlink.travelink.model.flickr;

/**
 * Created by Sefer on 1.01.2017.
 */

public class AuthUser {

    private Auth auth;

    private String stat;

    public void setAuth(Auth auth){
        this.auth = auth;
    }

    public Auth getAuth(){
        return this.auth;
    }

    public void setStat(String stat){
        this.stat = stat;
    }

    public String getStat(){
        return this.stat;
    }

}