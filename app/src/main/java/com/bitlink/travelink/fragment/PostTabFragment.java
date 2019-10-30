package com.bitlink.travelink.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;

import com.bitlink.travelink.R;
import com.bitlink.travelink.activity.ProfileActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import static com.bitlink.travelink.activity.MainAppActivity.mDatabase;

public class PostTabFragment extends PostListFragment {

    private DatabaseReference mPostReference;
    private String mUserKey;

    public PostTabFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get user key from intent
        mUserKey = getArguments().getString(ProfileActivity.EXTRA_USER_KEY);
        if (mUserKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_USER_KEY");
        }

        // Initialize Database
        mPostReference = mDatabase.child("user-posts").child(mUserKey);
    }

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // All my posts
        return mPostReference;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();

        inflater.inflate(R.menu.menu_profile, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
}
