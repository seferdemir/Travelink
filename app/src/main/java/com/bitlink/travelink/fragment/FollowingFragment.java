package com.bitlink.travelink.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;

import com.bitlink.travelink.activity.ProfileActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class FollowingFragment extends ConnectionListFragment {

    private String mUserKey;

    public FollowingFragment() {
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
        // All user's following
        return databaseReference.child("user-following")
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
