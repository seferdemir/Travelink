package com.bitlink.travelink.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bitlink.travelink.R;
import com.bitlink.travelink.activity.ChatActivity;
import com.bitlink.travelink.activity.ConnectionActivity;
import com.bitlink.travelink.activity.ProfileActivity;
import com.bitlink.travelink.adapter.ProfileTabViewAdapter;
import com.bitlink.travelink.model.User;
import com.bitlink.travelink.util.AppContants;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.bitlink.travelink.activity.MainAppActivity.getUid;
import static com.bitlink.travelink.activity.MainAppActivity.mDatabase;
import static com.bitlink.travelink.activity.MainAppActivity.mUser;

/**
 * Created by Sefer on 2.03.2017.
 */

public class ProfileFragment extends Fragment {

    private final String TAG = ProfileFragment.class.getSimpleName();
    @BindView(R.id.tv_user_fullname)
    TextView userFullname;
    @BindView(R.id.profile_image)
    CircleImageView profileImage;
    @BindView(R.id.cover_image)
    ImageView coverImage;
    @BindView(R.id.tv_followers)
    TextView tvFollowerCount;
    @BindView(R.id.tv_following)
    TextView tvFollowingCount;
    @BindView(R.id.tv_posts)
    TextView tvPostCount;
    @BindView(R.id.user_location_view)
    TextView tvLocation;
    @BindView(R.id.btn_follow)
    Button followButton;
    @BindView(R.id.btn_send_message)
    Button sendMessageButton;
    private DatabaseReference mUserReference;
    private ValueEventListener mUserListener;
    private String mUserKey;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        // Get user key from intent
        mUserKey = getArguments().getString(ProfileActivity.EXTRA_USER_KEY);
        if (mUserKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_USER_KEY");
        }

        // Initialize Database
        mUserReference = mDatabase.child("users").child(mUserKey);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_profile, container, false);

//        final Toolbar mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
//        mToolbar.setTitle("");
        final Toolbar mToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);

        ButterKnife.bind(this, view);

        mUserReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get user value
                final User user = dataSnapshot.getValue(User.class);

                if (user != null) {

                    userFullname.setText(user.username);

                    if (user.photoUrl == null) {
                        profileImage.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.mipmap.ic_account_circle));
                    } else {
                        Glide.with(getContext())
                                .load(user.photoUrl)
                                .centerCrop()
                                .thumbnail(0.5f)
                                .crossFade()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(profileImage);
                    }

                    Glide.with(getContext())
                            .load(R.drawable.cover)
                            .crossFade()
                            .into(coverImage);

                    tvFollowerCount.setText(user.followerCount == null ? "0" : String.valueOf(user.followerCount));
                    tvFollowingCount.setText(user.followingCount == null ? "0" : String.valueOf(user.followingCount));
                    tvPostCount.setText(user.postCount == null ? "0" : String.valueOf(user.postCount));

                    tvLocation.setText(user.lastLocation == null ? "" : user.lastLocation.name);

                    mDatabase.child("user-followers").child(user.uid)
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    // Filter User
                                    if (dataSnapshot.hasChild(getUid())) {
                                        followButton.setVisibility(View.GONE);
                                        sendMessageButton.setVisibility(View.VISIBLE);
                                    } else {
                                        if (mUserKey.equals(getUid())) {
                                            followButton.setVisibility(View.GONE);
                                            sendMessageButton.setVisibility(View.GONE);
                                            tvLocation.setSingleLine(false);
                                        } else {
                                            followButton.setVisibility(View.VISIBLE);
                                            sendMessageButton.setVisibility(View.GONE);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                                }
                            });

                    followButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Is the following user following back to the followed user
//                          mDatabase.child("user-following").child(user.uid) -> karşılıklı takip
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
                                            followButton.setVisibility(View.GONE);
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                                        }
                                    });
                        }
                    });

                    sendMessageButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity(), ChatActivity.class);
                            intent.putExtra(ProfileActivity.EXTRA_USER_KEY, mUserKey);
                            intent.putExtra(AppContants.ARG_CHAT_TYPE, 1); // Private
                            startActivity(intent);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "getUser:onCancelled", databaseError.toException());
            }
        });
//        }
        final TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Posts"));
        tabLayout.addTab(tabLayout.newTab().setText("Places"));
        tabLayout.addTab(tabLayout.newTab().setText("Info"));

        final ViewPager viewPager = (ViewPager) view.findViewById(R.id.view_pager);
        final ProfileTabViewAdapter adapter = new ProfileTabViewAdapter(getChildFragmentManager(), mUserKey);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        LinearLayout postLayout = (LinearLayout) view.findViewById(R.id.post_layout);
        postLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(0);
            }
        });

        LinearLayout followerLayout = (LinearLayout) view.findViewById(R.id.follower_layout);
        followerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ConnectionActivity.class);
                intent.putExtra(ProfileActivity.EXTRA_USER_KEY, mUserKey);
                intent.putExtra(AppContants.ARG_CONNECTION_FRAGMENT, 0);
                startActivity(intent);
            }
        });

        LinearLayout followingLayout = (LinearLayout) view.findViewById(R.id.following_layout);
        followingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ConnectionActivity.class);
                intent.putExtra(ProfileActivity.EXTRA_USER_KEY, mUserKey);
                intent.putExtra(AppContants.ARG_CONNECTION_FRAGMENT, 1);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if (!mUserKey.equals(getUid()))
            menu.findItem(R.id.action_edit_profile).setVisible(false);

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();

        inflater.inflate(R.menu.menu_profile, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

}
