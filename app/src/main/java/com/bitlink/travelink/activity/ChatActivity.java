package com.bitlink.travelink.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.bitlink.travelink.R;
import com.bitlink.travelink.fragment.PrivateChatFragment;
import com.bitlink.travelink.model.User;
import com.bitlink.travelink.util.AppContants;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import static com.bitlink.travelink.activity.MainAppActivity.mDatabase;

public class ChatActivity extends AppCompatActivity {

    private final String TAG = ChatActivity.class.getSimpleName();

    private DatabaseReference mUserReference;
    private ValueEventListener mUserListener;
    private String mUserKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_connection);

        final Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add_connection);
        fab.setVisibility(View.GONE);

        // Get user key from intent
        mUserKey = getIntent().getStringExtra(ProfileActivity.EXTRA_USER_KEY);
        if (mUserKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_USER_KEY");
        }

        // Initialize Database
        mUserReference = mDatabase.child("users").child(mUserKey);

        mUserReference.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        User user = dataSnapshot.getValue(User.class);

                        if (user != null) {
                            // Set user's name text to the toolbar
                            mToolbar.setTitle(user.getUsername());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container) != null) {

            Bundle args = getIntent().getExtras();
            args.putString(ProfileActivity.EXTRA_USER_KEY, mUserKey);

            // Get fragment key from intent
            int chatTypeId = getIntent().getIntExtra(AppContants.ARG_CHAT_TYPE, 1);

            AppContants.ChatType chatType = AppContants.ChatType.values()[chatTypeId];
            if (chatType == null) {
                throw new IllegalArgumentException("Must pass ARG_CONNECTION_FRAGMENT");
            }

            if (chatType.equals(AppContants.ChatType.Private)) {
                // Set following text to the toolbar
                mToolbar.setTitle(getResources().getString(R.string.followers));

                // Create a new Fragment to be placed in the activity layout
                PrivateChatFragment chatFragment = new PrivateChatFragment();
                args.putString(ProfileActivity.EXTRA_USER_KEY, mUserKey);
                chatFragment.setArguments(args);

                // Add the fragment to the 'fragment_container' FrameLayout
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, chatFragment).commit();
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