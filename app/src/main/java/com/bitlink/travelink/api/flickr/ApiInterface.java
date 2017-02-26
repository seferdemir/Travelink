package com.bitlink.travelink.api.flickr;

import com.bitlink.travelink.model.flickr.AuthUser;
import com.bitlink.travelink.model.flickr.FlickrPhotos;
import com.bitlink.travelink.model.flickr.FrobResponse;
import com.bitlink.travelink.model.flickr.PhotoResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Sefer on 31.12.2016.
 */

public interface ApiInterface {

    @GET("?method=flickr.auth.getFrob")
    Call<FrobResponse> getFrob(
            @Query("api_key") String apiKey,
            @Query("format") String format,
            @Query("nojsoncallback") String nojsoncallback,
            @Query("api_sig") String apiSig
    );

    @GET("?method=flickr.auth.getToken")
    Call<AuthUser> getToken(
            @Query("api_key") String apiKey,
            @Query("format") String format,
            @Query("frob") String frob,
            @Query("nojsoncallback") String nojsoncallback,
            @Query("api_sig") String apiSig
    );

    @GET("?method=flickr.photos.search&format=json&nojsoncallback=1")
    Call<FlickrPhotos> getPhotosByGeo(
            @Query("api_key") String apiKey,
            @Query("lat") double latitude,
            @Query("lon") double longitude
    );

    @GET("?method=flickr.photos.search")
    Call<PhotoResponse> getPhotos(
            @Query("api_key") String apiKey,
            @Query("user_id") String userId,
            @Query("extras") String extras,
            @Query("format") String format,
            @Query("nojsoncallback") String nojsoncallback
    );

}
