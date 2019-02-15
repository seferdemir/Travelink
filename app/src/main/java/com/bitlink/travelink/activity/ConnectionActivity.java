package com.bitlink.travelink.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bitlink.travelink.R;
import com.bitlink.travelink.fragment.FollowersFragment;
import com.bitlink.travelink.fragment.FollowingFragment;
import com.bitlink.travelink.fragment.UsersFragment;
import com.bitlink.travelink.util.AppContants;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import static com.bitlink.travelink.activity.MainAppActivity.mAuthCurrentUser;
import static com.bitlink.travelink.activity.MainAppActivity.mDatabase;
import static com.bitlink.travelink.util.AppContants.ARG_CONNECTION_FRAGMENT;

public class ConnectionActivity extends AppCompatActivity {

    private final String TAG = ConnectionActivity.class.getSimpleName();

    private DatabaseReference mUserReference;
    private ValueEventListener mUserListener;
    private String mUserKey;

    public static FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_connection);

        final Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Get user key from intent
        mUserKey = getIntent().getStringExtra(ProfileActivity.EXTRA_USER_KEY);
        if (mUserKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_USER_KEY");
        }

        // Initialize Database
        mUserReference = mDatabase.child("users").child(mUserKey);

        fab = (FloatingActionButton) findViewById(R.id.fab_add_connection);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAuthCurrentUser == null || mAuthCurrentUser.isAnonymous()) {
                    MainAppActivity.showText("You must sign-in to post.");
                    return;
                }

                Intent intent = new Intent(ConnectionActivity.this, ConnectionActivity.class);
                intent.putExtra(ProfileActivity.EXTRA_USER_KEY, mUserKey);
                intent.putExtra(ARG_CONNECTION_FRAGMENT, 2);
                startActivity(intent);
            }
        });

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container) != null) {

            Bundle args = getIntent().getExtras();
            args.putString(ProfileActivity.EXTRA_USER_KEY, mUserKey);

            // Get fragment key from intent
            int fragmentId = getIntent().getIntExtra(AppContants.ARG_CONNECTION_FRAGMENT, 2);

            AppContants.ConnectionFragment fragment = AppContants.ConnectionFragment.values()[fragmentId];
            if (fragment == null) {
                throw new IllegalArgumentException("Must pass ARG_CONNECTION_FRAGMENT");
            }

            if (fragment.equals(AppContants.ConnectionFragment.Followers)) {
                // Set following text to the toolbar
                mToolbar.setTitle(getResources().getString(R.string.followers));

                fab.setVisibility(View.GONE);

                // Create a new Fragment to be placed in the activity layout
                FollowersFragment followersFragment = new FollowersFragment();
                args.putInt(AppContants.ARG_CONNECTION_FRAGMENT, 0);
                followersFragment.setArguments(args);

                // Add the fragment to the 'fragment_container' FrameLayout
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, followersFragment).commit();
            } else if (fragment.equals(AppContants.ConnectionFragment.Following)) {
                // Set following text to the toolbar
                mToolbar.setTitle(getResources().getString(R.string.following));

                fab.setVisibility(View.VISIBLE);

                // Create a new Fragment to be placed in the activity layout
                FollowingFragment followingFragment = new FollowingFragment();
                args.putInt(AppContants.ARG_CONNECTION_FRAGMENT, 1);
                followingFragment.setArguments(args);

                // Add the fragment to the 'fragment_container' FrameLayout
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, followingFragment).commit();
            } else {
                // Set following text to the toolbar
                mToolbar.setTitle(getResources().getString(R.string.add_friend));

                fab.setVisibility(View.GONE);

                // Create a new Fragment to be placed in the activity layout
                UsersFragment usersFragment = new UsersFragment();
                args.putInt(AppContants.ARG_CONNECTION_FRAGMENT, 2);
                usersFragment.setArguments(args);

                // Add the fragment to the 'fragment_container' FrameLayout
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, usersFragment).commit();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
            }
        }
        return super.onOptionsItemSelected(item);
    }
}