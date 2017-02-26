package com.bitlink.travelink.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.bitlink.travelink.model.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import static com.bitlink.travelink.util.AppContants.ARG_USER;

public class UsersFragment extends ConnectionListFragment {

    public UsersFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // All my posts
        return databaseReference.child("users");
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        menu.clear();
//
//        inflater.inflate(R.menu.menu_profile, menu);
//        super.onCreateOptionsMenu(menu, inflater);
//    }
}
