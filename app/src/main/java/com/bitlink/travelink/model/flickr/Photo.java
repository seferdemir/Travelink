package com.bitlink.travelink.model.flickr;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Sefer on 1.01.2017.
 */

public class Photo implements Parcelable {

    private String id;

    private String owner;

    private String secret;

    private String server;

    private int farm;

    private String title;

    private int ispublic;

    private int isfriend;

    private int isfamily;

    private String url_m;

    private String height_m;

    private String width_m;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getOwner() {
        return this.owner;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getSecret() {
        return this.secret;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getServer() {
        return this.server;
    }

    public void setFarm(int farm) {
        this.farm = farm;
    }

    public int getFarm() {
        return this.farm;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }

    public void setIspublic(int ispublic) {
        this.ispublic = ispublic;
    }

    public int getIspublic() {
        return this.ispublic;
    }

    public void setIsfriend(int isfriend) {
        this.isfriend = isfriend;
    }

    public int getIsfriend() {
        return this.isfriend;
    }

    public void setIsfamily(int isfamily) {
        this.isfamily = isfamily;
    }

    public int getIsfamily() {
        return this.isfamily;
    }

    public void setUrl_m(String url_m){
        this.url_m = url_m;
    }

    public String getUrl_m(){
        return this.url_m;
    }

    public void setHeight_m(String height_m){
        this.height_m = height_m;
    }

    public String getHeight_m(){
        return this.height_m;
    }

    public void setWidth_m(String width_m){
        this.width_m = width_m;
    }

    public String getWidth_m(){
        return this.width_m;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.owner);
        dest.writeString(this.secret);
        dest.writeString(this.server);
        dest.writeInt(this.farm);
        dest.writeString(this.title);
        dest.writeInt(this.ispublic);
        dest.writeInt(this.isfriend);
        dest.writeInt(this.isfamily);
        dest.writeString(this.url_m);
        dest.writeString(this.height_m);
        dest.writeString(this.width_m);
    }

    public Photo() {
    }

    protected Photo(Parcel in) {
        this.id = in.readString();
        this.owner = in.readString();
        this.secret = in.readString();
        this.server = in.readString();
        this.farm = in.readInt();
        this.title = in.readString();
        this.ispublic = in.readInt();
        this.isfriend = in.readInt();
        this.isfamily = in.readInt();
        this.url_m = in.readString();
        this.height_m = in.readString();
        this.width_m = in.readString();
    }

    public static final Creator<Photo> CREATOR = new Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel source) {
            return new Photo(source);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };
}
