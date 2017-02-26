package com.bitlink.travelink.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.bitlink.travelink.activity.ProfileActivity;
import com.bitlink.travelink.model.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import static com.bitlink.travelink.activity.MainAppActivity.mDatabase;
import static com.bitlink.travelink.util.AppContants.ARG_USER;

public class FollowersFragment extends ConnectionListFragment {

    private String mUserKey;

    public FollowersFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get user key from intent
        mUserKey = getArguments().getString(ProfileActivity.EXTRA_USER_KEY);
        if (mUserKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_USER_KEY");
        }
    }

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // All user's followers
        return databaseReference.child("user-followers")
                .child(mUserKey);
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        menu.clear();
//
//        inflater.inflate(R.menu.menu_profile, menu);
//        super.onCreateOptionsMenu(menu, inflater);
//    }
}
