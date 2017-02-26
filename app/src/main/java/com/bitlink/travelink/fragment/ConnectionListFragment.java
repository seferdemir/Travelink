package com.bitlink.travelink.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bitlink.travelink.R;
import com.bitlink.travelink.activity.ProfileActivity;
import com.bitlink.travelink.model.User;
import com.bitlink.travelink.util.AppContants;
import com.bitlink.travelink.viewholder.ConnectionViewHolder;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import static com.bitlink.travelink.activity.MainAppActivity.getUid;
import static com.bitlink.travelink.activity.MainAppActivity.mDatabase;
import static com.bitlink.travelink.activity.MainAppActivity.mUser;

public abstract class ConnectionListFragment extends Fragment {

    private final String TAG = ConnectionListFragment.class.getSimpleName();

    private FirebaseRecyclerAdapter<User, ConnectionViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;

    private DatabaseReference mUserReference;
    private String mUserKey;

    public ConnectionListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_all_connections, container, false);

//        final Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
//        final Toolbar mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
//        toolbar.setTitle(getString(R.string.home));

//        AppCompatActivity activity = (AppCompatActivity) getActivity();
//        activity.setSupportActionBar(mToolbar);

        mRecycler = (RecyclerView) rootView.findViewById(R.id.connection_list);
        mRecycler.setHasFixedSize(true);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Set up Layout Manager, reverse layout
        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        // Get user key from intent
        Bundle args = getArguments();
        mUserKey = args.getString(ProfileActivity.EXTRA_USER_KEY);
        if (mUserKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_USER_KEY");
        }
        // Get fragment key from intent
        int fragmentId = args.getInt(AppContants.ARG_CONNECTION_FRAGMENT, 0);

        final AppContants.ConnectionFragment fragment = AppContants.ConnectionFragment.values()[fragmentId];
        if (fragment == null) {
            throw new IllegalArgumentException("Must pass ARG_CONNECTION_FRAGMENT");
        }

        // Initialize Database
        mUserReference = mDatabase.child("users").child(mUserKey);

        // Set up FirebaseRecyclerAdapter with the Query
        Query usersQuery = getQuery(mDatabase);
        mAdapter = new FirebaseRecyclerAdapter<User, ConnectionViewHolder>(User.class, R.layout.item_connection,
                ConnectionViewHolder.class, usersQuery) {
            @Override
            protected void populateViewHolder(final ConnectionViewHolder viewHolder, final User user, final int position) {
                final DatabaseReference connRef = getRef(position);

                // Set click listener for the whole user view
                final String userKey = connRef.getKey();
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Launch PostDetailActivity
                        Intent intent = new Intent(getActivity(), ProfileActivity.class);
                        intent.putExtra(ProfileActivity.EXTRA_USER_KEY, userKey);
                        startActivity(intent);
                    }
                });

                if (user.photoUrl == null) {
                    viewHolder.photoView.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.mipmap.ic_account_circle));
                } else {
                    Glide.with(getActivity())
                            .load(user.photoUrl)
                            .into(viewHolder.photoView);
                }

                if (fragment.equals(AppContants.ConnectionFragment.Followers)) {
                    viewHolder.unfollowView.setVisibility(View.GONE);
                } else if (fragment.equals(AppContants.ConnectionFragment.Following)) {
                    viewHolder.followView.setVisibility(View.GONE);
                } else {
                    viewHolder.unfollowView.setVisibility(View.GONE);
                }

                mDatabase.child("user-followers").child(user.uid)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                // Filter User
                                if (dataSnapshot.hasChild(getUid())) {
                                    viewHolder.followView.setVisibility(View.GONE);
                                } else {
                                    viewHolder.followView.setVisibility(View.VISIBLE);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                            }
                        });

                // Bind User to ViewHolder, setting OnClickListener for the star button
                viewHolder.bindToUser(user, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Is the following user following back to the followed user
//                        mDatabase.child("user-following").child(user.uid) -> karşılıklı takip
                        mDatabase.child("user-followers").child(user.uid)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        // Filter User
                                        if (dataSnapshot.hasChild(getUid())) {
                                            Toast.makeText(getActivity(),
                                                    String.format("You are already following to %s",
                                                            user.username), Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getActivity(),
                                                    String.format("%s was followed",
                                                            user.username), Toast.LENGTH_SHORT).show();

                                            // Need to write to both places the user is stored
                                            DatabaseReference followerRef = mDatabase.child("users").child(user.uid);
                                            DatabaseReference followingRef = mDatabase.child("users").child(getUid());

                                            Map<String, Object> userValues = user.toMap();
                                            Map<String, Object> mUserValues = mUser.toMap();

                                            Map<String, Object> childUpdates = new HashMap<>();

                                            // Create new places at /user-followers/$userid and the last place of the user's simultaneously
                                            childUpdates.put("/user-following/" + getUid() + "/" + user.uid, userValues);
                                            childUpdates.put("/user-followers/" + user.uid + "/" + getUid(), mUserValues);

                                            mDatabase.updateChildren(childUpdates);
                                            // Run two transactions
                                            followingRef.runTransaction(new Transaction.Handler() {
                                                @Override
                                                public Transaction.Result doTransaction(MutableData mutableData) {
                                                    User u = mutableData.getValue(User.class);
                                                    if (u == null) {
                                                        return Transaction.success(mutableData);
                                                    }

                                                    u.followingCount = u.followingCount + 1;

                                                    // Set value and report transaction success
                                                    mutableData.setValue(u);
                                                    return Transaction.success(mutableData);
                                                }

                                                @Override
                                                public void onComplete(DatabaseError databaseError, boolean b,
                                                                       DataSnapshot dataSnapshot) {
                                                    // Transaction completed
                                                    Log.d(TAG, "postTransaction:onComplete:" + databaseError);
                                                }
                                            });

                                            followerRef.runTransaction(new Transaction.Handler() {
                                                @Override
                                                public Transaction.Result doTransaction(MutableData mutableData) {
                                                    User u = mutableData.getValue(User.class);
                                                    if (u == null) {
                                                        return Transaction.success(mutableData);
                                                    }

                                                    u.followerCount = u.followerCount + 1;

                                                    // Set value and report transaction success
                                                    mutableData.setValue(u);
                                                    return Transaction.success(mutableData);
                                                }

                                                @Override
                                                public void onComplete(DatabaseError databaseError, boolean b,
                                                                       DataSnapshot dataSnapshot) {
                                                    // Transaction completed
                                                    Log.d(TAG, "postTransaction:onComplete:" + databaseError);
                                                }
                                            });
                                        }
                                        viewHolder.followView.setEnabled(false);
                                        viewHolder.unfollowView.setEnabled(true);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                                    }
                                });
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Toast.makeText(getActivity(),
                                String.format("%s was removed from the following",
                                        user.username), Toast.LENGTH_SHORT).show();

                        // Need to write to both places the user is stored
                        DatabaseReference followerRef = mDatabase.child("users").child(user.uid);
                        DatabaseReference followingRef = mDatabase.child("users").child(getUid());

                        // Unfollow updates
                        mDatabase.child("user-followers").child(user.uid).child(getUid()).removeValue();
                        mDatabase.child("user-following").child(getUid()).child(user.uid).removeValue();

                        // Run two transactions
                        followingRef.runTransaction(new Transaction.Handler() {
                            @Override
                            public Transaction.Result doTransaction(MutableData mutableData) {
                                User u = mutableData.getValue(User.class);
                                if (u == null) {
                                    return Transaction.success(mutableData);
                                }

                                u.followingCount = u.followingCount - 1;

                                // Set value and report transaction success
                                mutableData.setValue(u);
                                return Transaction.success(mutableData);
                            }

                            @Override
                            public void onComplete(DatabaseError databaseError, boolean b,
                                                   DataSnapshot dataSnapshot) {
                                // Transaction completed
                                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
                            }
                        });

                        followerRef.runTransaction(new Transaction.Handler() {
                            @Override
                            public Transaction.Result doTransaction(MutableData mutableData) {
                                User u = mutableData.getValue(User.class);
                                if (u == null) {
                                    return Transaction.success(mutableData);
                                }

                                u.followerCount = u.followerCount - 1;

                                // Set value and report transaction success
                                mutableData.setValue(u);
                                return Transaction.success(mutableData);
                            }

                            @Override
                            public void onComplete(DatabaseError databaseError, boolean b,
                                                   DataSnapshot dataSnapshot) {
                                // Transaction completed
                                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
                            }
                        });

                        viewHolder.unfollowView.setEnabled(false);
                        viewHolder.followView.setEnabled(true);
                    }
                });
            }
        };
        mRecycler.setAdapter(mAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) {
            mAdapter.cleanup();
        }
    }

    public abstract Query getQuery(DatabaseReference databaseReference);

}
