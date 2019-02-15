package com.bitlink.travelink.model.timeline;

import android.os.Parcel;
import android.os.Parcelable;

import com.bitlink.travelink.model.Place;
import com.google.android.gms.maps.model.LatLng;

public class TimeLine implements Parcelable {

    private Place place;
    private OrderStatus status;

    public enum OrderStatus {
        COMPLETED,
        ACTIVE,
        INACTIVE
    }

    public TimeLine() {
    }

    public TimeLine(Place place, OrderStatus status) {
        this.place = place;
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(place);
        dest.writeInt(status == null ? -1 : status.ordinal());
    }

    protected TimeLine(Parcel in) {
        this.place = in.readParcelable(LatLng.class.getClassLoader());
        int tmpMStatus = in.readInt();
        this.status = tmpMStatus == -1 ? null : OrderStatus.values()[tmpMStatus];
    }

    public static final Parcelable.Creator<TimeLine> CREATOR = new Parcelable.Creator<TimeLine>() {
        @Override
        public TimeLine createFromParcel(Parcel source) {
            return new TimeLine(source);
        }

        @Override
        public TimeLine[] newArray(int size) {
            return new TimeLine[size];
        }
    };

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}