package com.bitlink.travelink.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User implements Parcelable {

    private String username;
    private String email;
    private String photoUrl;
    private String birthday;
    private Integer gender;
    private Place lastLocation;
    private Integer followerCount;
    private Integer followingCount;
    private Integer postCount;
    private String uid;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.uid);
        dest.writeString(this.username);
        dest.writeString(this.email);
        dest.writeString(this.photoUrl);
        dest.writeInt(this.gender);
        dest.writeString(this.birthday);
        dest.writeInt(this.followerCount);
        dest.writeInt(this.followingCount);
        dest.writeInt(this.postCount);
        dest.writeSerializable(this.lastLocation);
    }

    public User() {
        this.followerCount = 0;
        this.followingCount = 0;
        this.postCount = 0;
        this.lastLocation = new Place();
    }

    public User(String uid, String username, String email) {
        this.uid = uid;
        this.username = username;
        this.email = email;
        this.followerCount = 0;
        this.followingCount = 0;
        this.postCount = 0;
        this.lastLocation = new Place();
    }

    protected User(Parcel in) {
        this.uid = in.readString();
        this.username = in.readString();
        this.email = in.readString();
        this.photoUrl = in.readString();
        this.gender = in.readInt();
        this.birthday = in.readString();
        this.followerCount = in.readInt();
        this.followingCount = in.readInt();
        this.postCount = in.readInt();
        this.lastLocation = (Place) in.readSerializable();
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("username", username);
        result.put("email", email);
        result.put("photoUrl", photoUrl);
        result.put("gender", gender);
        result.put("birthday", birthday);
        result.put("lastLocation", lastLocation);
        result.put("followerCount", followerCount);
        result.put("followingCount", followingCount);
        result.put("postCount", postCount);

        return result;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public Place getLastLocation() {
        return lastLocation;
    }

    public void setLastLocation(Place lastLocation) {
        this.lastLocation = lastLocation;
    }

    public Integer getFollowerCount() {
        return followerCount;
    }

    public void setFollowerCount(Integer followerCount) {
        this.followerCount = followerCount;
    }

    public Integer getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(Integer followingCount) {
        this.followingCount = followingCount;
    }

    public Integer getPostCount() {
        return postCount;
    }

    public void setPostCount(Integer postCount) {
        this.postCount = postCount;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
