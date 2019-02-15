package com.bitlink.travelink.model;

import com.google.firebase.database.Exclude;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Message {

    private long timestamp;
    private String id;
    private String text;
    private String author;
    private String photoUrl;
    private String senderUid;
    private String receiverUid;

    public Message() {
    }

    public Message(String text, String author, String photoUrl, String senderUid) {
        this.text = text;
        this.author = author;
        this.photoUrl = photoUrl;
        this.timestamp = new Date().getTime();
        this.senderUid = senderUid;
    }

    public Message(String text, String author, String photoUrl, String senderUid, String receiverUid) {
        this.text = text;
        this.author = author;
        this.photoUrl = photoUrl;
        this.timestamp = new Date().getTime();
        this.senderUid = senderUid;
        this.receiverUid = receiverUid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSenderUid() {
        return senderUid;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }

    public String getReceiverUid() {
        return receiverUid;
    }

    public void setReceiverUid(String receiverUid) {
        this.receiverUid = receiverUid;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp() {
        this.timestamp = new Date().getTime();
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("text", text);
        result.put("author", author);
        result.put("photoUrl", photoUrl);
        result.put("timestamp", timestamp);
        result.put("senderUid", senderUid);
        result.put("receiverUid", receiverUid);

        return result;
    }
}