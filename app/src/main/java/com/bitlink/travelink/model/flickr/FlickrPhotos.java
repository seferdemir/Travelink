package com.bitlink.travelink.model.flickr;

import java.util.List;

/**
 * Created by Sefer on 1.01.2017.
 */

public class FlickrPhotos {

    public Photos photos;

    public static class Photos {

        public int page;
        public int pages;
        public int perpage;
        public int total;
        public List<Photo> photo;
    }
}