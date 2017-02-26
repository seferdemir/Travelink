package com.bitlink.travelink.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.bitlink.travelink.activity.ProfileActivity;
import com.bitlink.travelink.fragment.PostTabFragment;
import com.bitlink.travelink.fragment.PhotoTabFragment;
import com.bitlink.travelink.fragment.InfoTabFragment;
import com.bitlink.travelink.fragment.TimelineTabFragment;
import com.bitlink.travelink.model.User;
import com.bitlink.travelink.model.flickr.Photos;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import static com.bitlink.travelink.activity.MainAppActivity.getUid;
import static com.bitlink.travelink.activity.MainAppActivity.mDatabase;
import static com.bitlink.travelink.util.AppContants.ARG_USER;

public class ProfileTabViewAdapter extends FragmentStatePagerAdapter {

    private final String TAG = ProfileTabViewAdapter.class.getSimpleName();

    private String mUserKey;

    public ProfileTabViewAdapter(FragmentManager fragmentManager, String userKey) {
        super(fragmentManager);

        mUserKey = userKey;
    }

    @Override
    public Fragment getItem(int position) {

        Bundle args = new Bundle();
        args.putString(ProfileActivity.EXTRA_USER_KEY, mUserKey);

        switch (position) {
            case 0:
                PostTabFragment tab1 = new PostTabFragment();
                tab1.setArguments(args);
                return tab1;
            case 1:
                TimelineTabFragment tab2 = new TimelineTabFragment();
                tab2.setArguments(args);
                return tab2;
            case 2:
                InfoTabFragment tab3 = new InfoTabFragment();
                tab3.setArguments(args);
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}