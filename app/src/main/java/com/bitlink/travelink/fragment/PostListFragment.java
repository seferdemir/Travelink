package com.bitlink.travelink.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bitlink.travelink.R;
import com.bitlink.travelink.activity.PostDetailActivity;
import com.bitlink.travelink.model.Post;
import com.bitlink.travelink.viewholder.PostViewHolder;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;

import static com.bitlink.travelink.activity.MainAppActivity.getUid;
import static com.bitlink.travelink.activity.MainAppActivity.mDatabase;

public abstract class PostListFragment extends Fragment {

    private final String TAG = PostListFragment.class.getSimpleName();

    private FirebaseRecyclerAdapter<Post, PostViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;

    public PostListFragment() {}

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_all_posts, container, false);

        final Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        final Toolbar mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.home));

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);

        mRecycler = (RecyclerView) rootView.findViewById(R.id.post_list);
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

        if (!getUid().equals("")) {
            // Set up FirebaseRecyclerAdapter with the Query
            Query postsQuery = getQuery(mDatabase);
            mAdapter = new FirebaseRecyclerAdapter<Post, PostViewHolder>(Post.class, R.layout.item_post,
                    PostViewHolder.class, postsQuery) {
                @Override
                protected void populateViewHolder(final PostViewHolder viewHolder, final Post model, final int position) {
                    final DatabaseReference postRef = getRef(position);

                    // Set click listener for the whole post view
                    final String postKey = postRef.getKey();
                    viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Launch PostDetailActivity
                            Intent intent = new Intent(getActivity(), PostDetailActivity.class);
                            intent.putExtra(PostDetailActivity.EXTRA_POST_KEY, postKey);
                            startActivity(intent);
                        }
                    });

                    if (model.getPhotoUrl() == null) {
                        viewHolder.photoView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.ic_account_circle));
                    } else {
                        Glide.with(getActivity())
                                .load(model.getPhotoUrl())
                                .into(viewHolder.photoView);
                    }

                    // Determine if the current user has liked this post and set UI accordingly
                    if (model.getStars().containsKey(getUid())) {
                        viewHolder.starView.setImageResource(R.mipmap.ic_star);
                    } else {
                        viewHolder.starView.setImageResource(R.mipmap.ic_star_border);
                    }

                    // Bind Post to ViewHolder, setting OnClickListener for the star button
                    viewHolder.bindToPost(model, new View.OnClickListener() {
                        @Override
                        public void onClick(View starView) {
                            // Need to write to both places the post is stored
                            DatabaseReference globalPostRef = mDatabase.child("posts").child(postRef.getKey());
                            DatabaseReference userPostRef = mDatabase.child("user-posts").child(model.getUid()).child(postRef.getKey());

                            // Run two transactions
                            onStarClicked(globalPostRef);
                            onStarClicked(userPostRef);
                        }
                    });
                }
            };
            mRecycler.setAdapter(mAdapter);
        }
    }

    // [START post_stars_transaction]
    private void onStarClicked(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Post p = mutableData.getValue(Post.class);
                if (p == null) {
                    return Transaction.success(mutableData);
                }

                if (p.getStars().containsKey(getUid())) {
                    // Unstar the post and remove self from stars
                    p.setStarCount(p.getStarCount() - 1);
                    p.getStars().remove(getUid());
                } else {
                    // Star the post and add self to stars
                    p.setStarCount(p.getStarCount() + 1);
                    p.getStars().put(getUid(), true);
                }

                // Set value and report transaction success
                mutableData.setValue(p);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) {
            mAdapter.cleanup();
        }
    }

    public abstract Query getQuery(DatabaseReference databaseReference);

}
