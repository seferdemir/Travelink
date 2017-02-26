package com.bitlink.travelink.fragment;

import android.os.Bundle;

import com.bitlink.travelink.activity.ProfileActivity;
import com.bitlink.travelink.util.AppContants;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import static com.bitlink.travelink.activity.MainAppActivity.getUid;
import static com.bitlink.travelink.activity.MainAppActivity.mDatabase;

public class PrivateChatFragment extends ChatFragment {

    private DatabaseReference mChatReference;
    private String mUserKey;

    public PrivateChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        // Get user key from intent
        mUserKey = getArguments().getString(ProfileActivity.EXTRA_USER_KEY);
        if (mUserKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_USER_KEY");
        }

        // Initialize Database
        mChatReference = mDatabase.child(AppContants.PRIVATE_MESSAGES_CHILD).child(mUserKey).child(getUid());
    }

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // All my posts
        return mChatReference;
    }
}
