package com.bitlink.travelink.model.foursquare;

import java.util.ArrayList;
import java.util.List;

public class Search {

    private Meta meta;

    private List<Notification> notifications = new ArrayList<Notification>();

    private SearchResponse response;

    /**
     *
     * @return
     *     The meta
     */
    public Meta getMeta() {
        return meta;
    }

    /**
     *
     * @param meta
     *     The meta
     */
    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    /**
     *
     * @return
     *     The notifications
     */
    public List<Notification> getNotifications() {
        return notifications;
    }

    /**
     *
     * @param notifications
     *     The notifications
     */
    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    /**
     *
     * @return
     *     The response
     */
    public SearchResponse getResponse() {
        return response;
    }

    /**
     *
     * @param response
     *     The response
     */
    public void setResponse(SearchResponse response) {
        this.response = response;
    }

}
