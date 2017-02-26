package com.bitlink.travelink.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.bitlink.travelink.R;
import com.bitlink.travelink.model.User;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainAppActivity extends Activity {

    private final String TAG = MainAppActivity.class.getSimpleName();

    /*  A reference to the Firebase */
    public static DatabaseReference mDatabase;

    /* Data from the authenticated user */
    public static FirebaseAuth mAuth;

    /* Listener for Firebase session changes */
    public static FirebaseAuth.AuthStateListener mAuthStateListener;

    /* Firebase user */
    public static FirebaseUser mAuthCurrentUser;

    public static User mUser;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.keepSynced(true);

        //Get Firebase auth instance
        mAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                Intent activityIntent;

                mAuthCurrentUser = firebaseAuth.getCurrentUser();
                if (mAuthCurrentUser != null) {
                    Log.d(TAG, mAuthCurrentUser.getUid() + " - " + mAuthCurrentUser.getDisplayName());

                    mDatabase.child("users").child(getUid()).addListenerForSingleValueEvent(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    // Get user value
                                    User user = dataSnapshot.getValue(User.class);

                                    if (user != null) {
                                        mUser = user;
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                                }
                            });

                    activityIntent = new Intent(mContext, MainActivity.class);
                } else {
                    activityIntent = new Intent(mContext, LoginActivity.class);
                }

                startActivity(activityIntent);
                finish();
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthStateListener != null) {
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    public static String getUid() {
        FirebaseUser user = mAuth.getCurrentUser();
        return user != null ? user.getUid() : "";
    }
}
