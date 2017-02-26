package com.bitlink.travelink.fragment;

import android.os.Bundle;

import com.bitlink.travelink.util.AppContants;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import static com.bitlink.travelink.activity.MainAppActivity.mDatabase;

public class CommonChatFragment extends ChatFragment {

    private DatabaseReference mChatReference;

    public CommonChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        // Initialize Database
        mChatReference = mDatabase.child(AppContants.MESSAGES_CHILD);
    }

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // All my posts
        return mChatReference;
    }
}
