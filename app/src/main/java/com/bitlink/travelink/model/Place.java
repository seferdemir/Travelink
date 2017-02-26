package com.bitlink.travelink.model;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Place implements Serializable {

    public String name;
    public String latitude;
    public String longitude;
    public String checkinAt;

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("latitude", latitude);
        result.put("longitude", longitude);
        result.put("checkinAt", checkinAt);

        return result;
    }
}
