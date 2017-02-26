package com.bitlink.travelink.adapter;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;

import com.bitlink.travelink.fragment.PlaceViewPagerFragment;
import com.bitlink.travelink.model.Tag;
import com.bitlink.travelink.model.foursquare.Category;
import com.bitlink.travelink.model.foursquare.Item_;
import com.bitlink.travelink.model.foursquare.Venue;

import java.util.List;

public class PlacePagerAdapter extends FragmentStatePagerAdapter {

    private LayoutInflater mLayoutInflater;
    private List<Item_> mItemList;
    private int position;

    public PlacePagerAdapter(FragmentManager fragmentManager, Context context) {
        super(fragmentManager);

        this.mLayoutInflater = LayoutInflater.from(context);
    }

    public List<Item_> getItemList() {
        return mItemList;
    }

    public void setItemList(List<Item_> itemList) {
        this.mItemList = itemList;
    }

    @Override
    public Fragment getItem(int position) {
        this.position = position;
        return new PlaceViewPagerFragment(mItemList.get(position));
    }

    @Override
    public int getItemPosition(Object object) {
        /* Returning POSITION_NONE means the current data does not matches the data this fragment is showing right now.
         * Returning POSITION_NONE constant will force the fragment to redraw its view layout all over again and show new data. */
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return this.mItemList.size();
    }

    @Override
    public float getPageWidth(int position) {
        return 0.45f;
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

}
