package com.bitlink.travelink.model.flickr;

/**
 * Created by Sefer on 5.01.2017.
 */

public class PhotoResponse {

    private Photos photos;

    private String stat;

    private int code;

    private String message;

    public void setPhotos(Photos photos) {
        this.photos = photos;
    }

    public Photos getPhotos() {
        return this.photos;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }

    public String getStat() {
        return this.stat;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

