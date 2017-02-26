package com.bitlink.travelink.model.flickr;

/**
 * Created by Sefer on 1.01.2017.
 */

public class FrobResponse {

    private Frob frob;

    private String stat;

    public void setFrob(Frob frob){
        this.frob = frob;
    }

    public Frob getFrob(){
        return this.frob;
    }

    public void setStat(String stat){
        this.stat = stat;
    }

    public String getStat(){
        return this.stat;
    }
}
