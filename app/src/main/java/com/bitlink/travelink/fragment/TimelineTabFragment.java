package com.bitlink.travelink.fragment;


import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bitlink.travelink.R;
import com.bitlink.travelink.activity.MainAppActivity;
import com.bitlink.travelink.activity.ProfileActivity;
import com.bitlink.travelink.model.Place;
import com.bitlink.travelink.model.User;
import com.bitlink.travelink.model.timeline.TimeLine;
import com.bitlink.travelink.util.DateTimeUtils;
import com.bitlink.travelink.util.VectorDrawableUtils;
import com.bitlink.travelink.viewholder.TimeLineViewHolder;
import com.github.vipulasri.timelineview.TimelineView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.bitlink.travelink.activity.MainAppActivity.getUid;
import static com.bitlink.travelink.activity.MainAppActivity.mDatabase;
import static com.bitlink.travelink.model.timeline.TimeLine.OrderStatus;
import static com.bitlink.travelink.util.AppContants.ARG_USER;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimelineTabFragment extends Fragment implements OnMapReadyCallback {

    private final String TAG = TimelineTabFragment.class.getSimpleName();

    private DatabaseReference mPlacesReference;
    private String mUserKey;

    private ValueEventListener mPlaceListener;

    private TimeLineAdapter mAdapter;

    private RecyclerView mRecyclerView;

    private List<TimeLine> mTimeLineList;

    private SupportMapFragment mapFragment;
    private GoogleMap mMap;

    public TimelineTabFragment() {
        // Required empty public constructor
    }

    public static TimelineTabFragment newInstance(User user) {
        TimelineTabFragment fragmentDemo = new TimelineTabFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_USER, user);
        fragmentDemo.setArguments(args);
        return fragmentDemo;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get user key from intent
        mUserKey = getArguments().getString(ProfileActivity.EXTRA_USER_KEY);
        if (mUserKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_USER_KEY");
        }

        mTimeLineList = new ArrayList<>();

        // Initialize Database
        mPlacesReference = mDatabase.child("user-places").child(mUserKey);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();

        inflater.inflate(R.menu.menu_profile, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_tab_timeline, container, false);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        if (mapFragment == null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map);
                if (mapFragment == null)
                    mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
            } else {
                mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
            }
        }
        mapFragment.getMapAsync(this);

        mRecyclerView = (RecyclerView) itemView.findViewById(R.id.timeline_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setHasFixedSize(true);

        initView();

        return itemView;
    }

    private void initView() {
        mAdapter = new TimeLineAdapter(this.getContext(), mPlacesReference);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Menu
        switch (item.getItemId()) {
            //When home is clicked
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("all")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

//        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setTiltGesturesEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.setMyLocationEnabled(false);
        mMap.setBuildingsEnabled(false);
        mMap.setIndoorEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(true);
    }

    @Override
    public void onStop() {
        super.onStop();

        // Clean up comments listener
        mAdapter.cleanupListener();
    }

    private class TimeLineAdapter extends RecyclerView.Adapter<TimeLineViewHolder> implements View.OnClickListener {

        private Context mContext;
        private DatabaseReference mDatabaseReference;
        private ChildEventListener mChildEventListener;

        private List<String> mTimeLineIds = new ArrayList<>();
        private List<TimeLine> mTimeLines = new ArrayList<>();

        public TimeLineAdapter(final Context context, DatabaseReference ref) {
            mContext = context;
            mDatabaseReference = ref;

            // Create child event listener
            ChildEventListener childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                    // A new timeLine has been added, add it to the displayed list
                    Place place = dataSnapshot.getValue(Place.class);

                    if (place != null) {
                        TimeLine timeLine = new TimeLine(place, OrderStatus.COMPLETED);

                        // Update RecyclerView
                        mTimeLineIds.add(dataSnapshot.getKey());
                        mTimeLines.add(timeLine);
                        notifyItemInserted(mTimeLines.size() - 1);
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                    // A timeLine has changed, use the key to determine if we are displaying this
                    // timeLine and if so displayed the changed timeLine.
                    TimeLine newTimeLine = dataSnapshot.getValue(TimeLine.class);
                    String timeLineKey = dataSnapshot.getKey();

                    int timeLineIndex = mTimeLineIds.indexOf(timeLineKey);
                    if (timeLineIndex > -1) {
                        // Replace with the new data
                        mTimeLines.set(timeLineIndex, newTimeLine);

                        // Update the RecyclerView
                        notifyItemChanged(timeLineIndex);
                    } else {
                        Log.w(TAG, "onChildChanged:unknown_child:" + timeLineKey);
                    }
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                    // A timeLine has changed, use the key to determine if we are displaying this
                    // timeLine and if so remove it.
                    String timeLineKey = dataSnapshot.getKey();

                    int timeLineIndex = mTimeLineIds.indexOf(timeLineKey);
                    if (timeLineIndex > -1) {
                        // Remove data from the list
                        mTimeLineIds.remove(timeLineIndex);
                        mTimeLines.remove(timeLineIndex);

                        // Update the RecyclerView
                        notifyItemRemoved(timeLineIndex);
                    } else {
                        Log.w(TAG, "onChildRemoved:unknown_child:" + timeLineKey);
                    }
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

                    // A timeLine has changed position, use the key to determine if we are
                    // displaying this timeLine and if so move it.
                    TimeLine movedTimeLine = dataSnapshot.getValue(TimeLine.class);
                    String timeLineKey = dataSnapshot.getKey();

                    // ...
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "postTimeLines:onCancelled", databaseError.toException());
                    MainAppActivity.showText("Failed to load timeLines.");
                }
            };
            ref.addChildEventListener(childEventListener);

            // Store reference to listener so it can be removed on app stop
            mChildEventListener = childEventListener;
        }

        @Override
        public int getItemViewType(int position) {
            return TimelineView.getTimeLineViewType(position, getItemCount());
        }

        @Override
        public TimeLineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.item_timeline, parent, false);
            view.setOnClickListener(this);

            return new TimeLineViewHolder(view, viewType);
        }

        @Override
        public void onBindViewHolder(TimeLineViewHolder holder, int position) {
            TimeLine model = mTimeLines.get(position);

            if (model != null) {
                if (model.getStatus() == OrderStatus.INACTIVE) {
                    holder.timelineView.setMarker(VectorDrawableUtils.getDrawable(mContext, R.drawable.ic_marker_inactive, R.color.colorAccent));
                } else if (model.getStatus() == OrderStatus.ACTIVE) {
                    holder.timelineView.setMarker(VectorDrawableUtils.getDrawable(mContext, R.drawable.ic_marker_active, R.color.colorAccent));
                } else {
                    holder.timelineView.setMarker(ContextCompat.getDrawable(mContext, R.drawable.ic_marker), ContextCompat.getColor(mContext, R.color.colorAccent));
                }

                if (model.getPlace().getCheckinAt() != null && !model.getPlace().getCheckinAt().isEmpty()) {
                    holder.dateView.setVisibility(View.VISIBLE);
                    holder.dateView.setText(DateTimeUtils.parseDateTime(model.getPlace().getCheckinAt(), "yyyyMMddHHmmssSSS", "dd MMMM yyyy"));
                } else
                    holder.dateView.setVisibility(View.GONE);

                holder.messageView.setText(model.getPlace().getName());
            }
        }

        @Override
        public int getItemCount() {
            return mTimeLines.size();
        }

        public void cleanupListener() {
            if (mChildEventListener != null) {
                mDatabaseReference.removeEventListener(mChildEventListener);
            }
        }

        @Override
        public void onClick(View view) {
            int position = mRecyclerView.getChildAdapterPosition(view);
            TimeLine timeLine = mTimeLines.get(position);

            LatLng latLng = new LatLng(Double.valueOf(timeLine.getPlace().getLatitude()), Double.valueOf(timeLine.getPlace().getLongitude()));

            mMap.clear();
            // Add a marker in location
            mMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                            .position(latLng)
            );
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13.0F), 1000, null);
        }
    }
}
