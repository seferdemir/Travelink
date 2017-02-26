package com.bitlink.travelink.fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;

import com.bitlink.travelink.R;
import com.bitlink.travelink.activity.ProfileActivity;
import com.bitlink.travelink.model.User;
import com.bitlink.travelink.util.DateTimeUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bitlink.travelink.activity.MainAppActivity.mDatabase;
import static com.bitlink.travelink.util.AppContants.ARG_USER;

/**
 * A simple {@link Fragment} subclass.
 */
public class InfoTabFragment extends Fragment implements OnMapReadyCallback {

    private final String TAG = InfoTabFragment.class.getSimpleName();

    private DatabaseReference mUserReference;
    private ValueEventListener mUserListener;
    private String mUserKey;

    private SupportMapFragment mapFragment;
    private GoogleMap mMap;

    private LatLng mLastKnownLocation;

    private String mUserFullname;

    @BindView(R.id.tv_fullname)
    TextView textViewFullname;

    @BindView(R.id.tv_gender)
    TextView textViewGender;

    @BindView(R.id.tv_birthday)
    TextView textViewBirthday;

    @BindView(R.id.tv_email)
    TextView textViewEMail;

    public InfoTabFragment() {
    }

    public static InfoTabFragment newInstance(User user) {
        InfoTabFragment fragmentDemo = new InfoTabFragment();
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

        // Initialize Database
        mUserReference = mDatabase.child("users").child(mUserKey);

        mUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get User object and use the values to update the UI
                User user = dataSnapshot.getValue(User.class);

                if (user != null) {
                    mUserFullname = user.username;
                    textViewFullname.setText(mUserFullname);
                    if (user.gender != null)
                        textViewGender.setText(user.gender == 0 ? getResources().getString(R.string.male) : getResources().getString(R.string.female));
                    if (user.birthday != null)
                        textViewBirthday.setText(DateTimeUtils.parseDateTime(user.birthday, "yyyyMMdd", "dd MMMM yyyy"));
                    textViewEMail.setText(user.email);
                    if (user.lastLocation != null && user.lastLocation.latitude != null && user.lastLocation.longitude != null)
                        mLastKnownLocation = new LatLng(Double.valueOf(user.lastLocation.latitude),
                                Double.valueOf(user.lastLocation.longitude));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a placeName
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(getContext(), "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
            }
        });
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
        View view = inflater.inflate(R.layout.fragment_tab_info, container, false);

        ButterKnife.bind(this, view);

        // Get the map and register for the ready callback
        if (mapFragment == null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.lite_map);
                if (mapFragment == null)
                    mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.lite_map);
            } else {
                mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.lite_map);
            }
        }
        mapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (mLastKnownLocation != null) {
            mMap.addMarker(new MarkerOptions()
                    .position(mLastKnownLocation)
                    .title(mUserFullname)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

            final View mapView = mapFragment.getView();
            if (mapView.getViewTreeObserver().isAlive()) {
                mapView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @SuppressWarnings("deprecation") // We use the new method when supported
                    @SuppressLint("NewApi") // We check which build version we are using.
                    @Override
                    public void onGlobalLayout() {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                            mapView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        } else {
                            mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                        // Move camera to show all markers and locations
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLastKnownLocation, 20));
                    }
                });
            }
        }
    }
}
