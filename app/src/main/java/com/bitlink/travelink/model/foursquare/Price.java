package com.bitlink.travelink.model.foursquare;

import java.util.HashMap;
import java.util.Map;

public class Price {

    private Integer tier;

    private String message;

    private String currency;

    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The tier
     */
    public Integer getTier() {
        return tier;
    }

    /**
     * 
     * @param tier
     *     The tier
     */
    public void setTier(Integer tier) {
        this.tier = tier;
    }

    /**
     * 
     * @return
     *     The placeName
     */
    public String getMessage() {
        return message;
    }

    /**
     * 
     * @param message
     *     The placeName
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 
     * @return
     *     The currency
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * 
     * @param currency
     *     The currency
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
