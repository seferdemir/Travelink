package com.bitlink.travelink.model.flickr;

/**
 * Created by Sefer on 1.01.2017.
 */

public class Auth {

    private Token token;

    private Perms perms;

    private User user;

    public void setToken(Token token){
        this.token = token;
    }

    public Token getToken(){
        return this.token;
    }

    public void setPerms(Perms perms){
        this.perms = perms;
    }

    public Perms getPerms(){
        return this.perms;
    }

    public void setUser(User user){
        this.user = user;
    }

    public User getUser(){
        return this.user;
    }

}