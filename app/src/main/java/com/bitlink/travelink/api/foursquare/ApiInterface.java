package com.bitlink.travelink.api.foursquare;

/**
 * Created by Sefer on 14.11.2016.
 */

import com.bitlink.travelink.model.foursquare.Explore;
import com.bitlink.travelink.model.foursquare.Search;
import com.bitlink.travelink.model.foursquare.Venue;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("venues/explore")
    Call<Explore> getTopVenues(
            @Query("client_id") String client_id,
            @Query("client_secret") String client_secret,
            @Query("v") String v,
            @Query("ll") String ll,
            @Query("radius") String radius
    );

    @GET("venues/explore")
    Call<Explore> getTopVenuesByCategory(
            @Query("client_id") String client_id,
            @Query("client_secret") String client_secret,
            @Query("v") String v,
            @Query("ll") String ll,
            @Query("radius") String radius,
            @Query("categoryId") String categoryId
    );

    @GET("venues/{id}/")
    Call<Venue> getVenueDetails(
            @Path("id") String id,
            @Query("client_id") String client_id,
            @Query("client_secret") String client_secret,
            @Query("v") String v
    );

    @GET("venues/search")
    Call<Search> searchVenue(
            @Query("client_id") String client_id,
            @Query("client_secret") String client_secret,
            @Query("v") String v,
            @Query("query") String query,
            @Query("intent") String intent
    );

}