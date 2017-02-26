package com.bitlink.travelink.model.flickr;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sefer on 5.01.2017.
 */

public class Photos {

    private int page;

    private int pages;

    private int perpage;

    private int total;

    private List<Photo> photo;

    public void setPage(int page){
        this.page = page;
    }

    public int getPage(){
        return this.page;
    }

    public void setPages(int pages){
        this.pages = pages;
    }

    public int getPages(){
        return this.pages;
    }

    public void setPerpage(int perpage){
        this.perpage = perpage;
    }

    public int getPerpage(){
        return this.perpage;
    }

    public void setTotal(int total){
        this.total = total;
    }

    public int getTotal(){
        return this.total;
    }

    public void setPhoto(List<Photo> photo){
        this.photo = photo;
    }

    public List<Photo> getPhoto(){
        return this.photo;
    }

}
