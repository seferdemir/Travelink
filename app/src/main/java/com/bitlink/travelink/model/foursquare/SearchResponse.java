package com.bitlink.travelink.model.foursquare;

import java.util.ArrayList;
import java.util.List;


public class SearchResponse {

    private List<Venue> venues = new ArrayList<Venue>();

    /**
     *
     * @return
     *     The venues
     */
    public List<Venue> getVenues() {
        return venues;
    }

    /**
     *
     * @param venues
     *     The venues
     */
    public void setVenues(List<Venue> venues) {
        this.venues = venues;
    }
}
