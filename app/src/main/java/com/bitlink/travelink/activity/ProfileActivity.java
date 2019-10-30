package com.bitlink.travelink.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.bitlink.travelink.R;
import com.bitlink.travelink.fragment.ProfileFragment;
import com.bitlink.travelink.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import static com.bitlink.travelink.activity.MainAppActivity.mDatabase;

public class ProfileActivity extends AppCompatActivity {

    public static final String EXTRA_USER_KEY = "user_key";
    private static final String TAG = ProfileActivity.class.getSimpleName();
    private DatabaseReference mUserReference;
    private String mUserKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Get user key from intent
        mUserKey = getIntent().getStringExtra(EXTRA_USER_KEY);
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

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            } else {
                // Create a new Fragment to be placed in the activity layout
                ProfileFragment profileFragment = new ProfileFragment();

                Bundle newArgs = new Bundle();
                newArgs.putString(ProfileActivity.EXTRA_USER_KEY, mUserKey);
                profileFragment.setArguments(newArgs);

                // Add the fragment to the 'fragment_container' FrameLayout
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, profileFragment).commit();
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
