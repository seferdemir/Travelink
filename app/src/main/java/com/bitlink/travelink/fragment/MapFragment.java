package com.bitlink.travelink.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bitlink.travelink.R;
import com.bitlink.travelink.adapter.PlacePagerAdapter;
import com.bitlink.travelink.api.foursquare.ApiClient;
import com.bitlink.travelink.api.foursquare.ApiInterface;
import com.bitlink.travelink.api.google.location.LocationProvider;
import com.bitlink.travelink.model.foursquare.Category;
import com.bitlink.travelink.model.foursquare.Explore;
import com.bitlink.travelink.model.foursquare.Item_;
import com.bitlink.travelink.model.foursquare.Venue;
import com.bitlink.travelink.util.AppContants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.yalantis.filter.adapter.FilterAdapter;
import com.yalantis.filter.listener.FilterListener;
import com.yalantis.filter.widget.Filter;
import com.yalantis.filter.widget.FilterItem;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bitlink.travelink.util.AppContants.ARG_LATITUDE;
import static com.bitlink.travelink.util.AppContants.ARG_LONGITUDE;
import static com.bitlink.travelink.util.AppContants.mSharedPreferences;

public class MapFragment extends Fragment implements GoogleMap.OnCameraIdleListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMyLocationButtonClickListener,
        OnMapReadyCallback,
        LocationProvider.LocationCallback,
        FilterListener<Category> {

    private final String TAG = MapFragment.class.getSimpleName();
    FragmentActivity mListener;
    View view;
    /* Binding UI Component with ButterKnife */
    @BindView(R.id.btn_search)
    Button searchButton;
    private Context mContext;
    private LatLng mCurrentLocation, locationOfScreenCenter;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private SharedPreferences.Editor mEditor;
    private Marker mMarker, mUserMarker;
    private LocationProvider mLocationProvider;
    private DatabaseReference mDatabase;
    private HashMap<Marker, Venue> mMarkersHashMap;
    private List<Marker> mMarkerList;
    private Marker prevMarker;
    private ViewPager mViewPager;
    private PlacePagerAdapter mAdapter;
    private Venue mVenueModel;
    private String[] mCategories;
    private String[] mCategoryIds;
    private int[] mCategoryColors;
    private List<Category> mCategoryList;
    private List<Item_> mItemList = new ArrayList<Item_>();
    private Filter<Category> mFilter;
    private Location previousLocation;
    private boolean flag = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();

        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle bundle) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_map, viewGroup, false);
        } catch (InflateException e) {
//            map is already there, just return view as it is
        }

        final Toolbar mToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);

        ViewGroup mapHost = (ViewGroup) view.findViewById(R.id.map_host);
        mapHost.requestTransparentRegion(mapHost);

        ButterKnife.bind(this, view);

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

        mViewPager = (ViewPager) view.findViewById(R.id.vp_details);

        mFilter = (Filter<Category>) view.findViewById(R.id.filter);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mContext = getActivity();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());
//
//        final double latitude = mSharedPreferences.getLong(ARG_LATITUDE, 0);
//        final double longitude = mSharedPreferences.getLong(ARG_LONGITUDE, 0);

//        mLatLng = new LatLng(latitude, longitude);
        mFilter.setAdapter(new Adapter(getTags()));
        mFilter.setListener(this);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            ViewCompat.setTranslationZ(mFilter, 10);
            ViewCompat.setTranslationZ(searchButton, 5);
        } else {
            mFilter.setTranslationZ(10);
            searchButton.setTranslationZ(5);
        }

        //the text to show when there's no selected items
        mFilter.setNoSelectedItemText(getString(R.string.all_selected));
        mFilter.build();

        mItemList = new ArrayList<Item_>();

        mLocationProvider = new LocationProvider(mContext, this);
        mLocationProvider.connect();

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mViewPager.setPadding(16, 0, 16, 0);
        mViewPager.setClipToPadding(false);
        mViewPager.setPageMargin(8);
        mViewPager.setOffscreenPageLimit(4);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (flag) {
                    flag = false;
                    String str = "";
                    final Venue temp = mItemList.get(position).getVenue();
                    LatLng newLatLng = new LatLng(temp.getLocation().getLat(), temp.getLocation().getLng());
                    // map.animateCenterZoom(newLatLng, 15);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(newLatLng));

                    // Zoom in, animating the camera.
                    mMap.animateCamera(CameraUpdateFactory.zoomOut());

                    // Zoom out to zoom level 10, animating with a duration of 1 seconds.
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 1000, null);

                    Marker marker = mMarkerList.get(position);

                    if (prevMarker != null) {
//                        prevMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
//                        prevMarker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_place_blue));
                    }

                    //leave Marker default color if re-click current Marker
                    if (!marker.equals(prevMarker)) {
//                        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
//                        marker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_place_pink));
                        prevMarker = marker;
                    }
                    prevMarker = marker;
                    flag = true;
                } else {
                    Log.i("", "" + mMarkerList);
                    Log.i("", "" + position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
//            }
//        }, DELAY_MILLIS);

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressWarnings("all")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

//        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setTiltGesturesEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(this);
        mMap.setOnCameraIdleListener(this);
        mMap.setMyLocationEnabled(true);
        mMap.setBuildingsEnabled(true);
        mMap.setIndoorEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        /*mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                //MapFragment.access$002(MapFragment.this, marker.getPosition());
                mMap.animateCamera(CameraUpdateFactory.newLatLng(markerLatLng));
                return true;
            }
        });*/

//        showLocation();
    }

    @Override
    public void onStop() {
        super.onStop();

        mLocationProvider.disconnect();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mListener = ((FragmentActivity) context);
    }

    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (this.mapFragment != null && getFragmentManager().findFragmentById(this.mapFragment.getId()) != null) {

            getFragmentManager().beginTransaction().remove(this.mapFragment).commit();
            this.mapFragment = null;
        }
    }

    private void showLocation() {
        // Add a marker in location
        mMap.addMarker(new MarkerOptions()
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                        .position(mCurrentLocation)
//                    .title(getResources().getString(R.string.your_location))
        );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mCurrentLocation, 15.0F));
    }


    @OnClick(R.id.btn_search)
    void SearchThisArea() {
        if (locationOfScreenCenter != null) {
            makeRequestForPlaceList(locationOfScreenCenter.latitude, locationOfScreenCenter.longitude);
            searchButton.setVisibility(View.GONE);
        }
    }

    /*
    *  Request for List of stores
    *  Add markers on google map
    *  Save data for future use
    */
    private void makeRequestForPlaceList(double lat, double lon) {

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        String categoryIds = getCategories();

        Call<Explore> call = apiService.getTopVenuesByCategory(AppContants.FOURSQUARE_CLIENT_ID, AppContants.FOURSQUARE_CLIENT_SECRET, AppContants.FOURSQUARE_VERSION, String.valueOf(lat) + "," + String.valueOf(lon), "10000", categoryIds);
        call.enqueue(new Callback<Explore>() {
            @Override
            public void onResponse(Call<Explore> call, Response<Explore> response) {
                Explore placeList = response.body();
                if (placeList != null) {
//                    mMarkersHashMap = new HashMap<>();
//                    if (mMarkerList == null)
                    mMarkerList = new ArrayList<>();
                    mItemList = new ArrayList<Item_>();
                    mItemList.addAll(placeList.getResponse().getGroups().get(0).getItems());
                    mMap.clear();

                    /*mUserMarker = mMap.addMarker(new MarkerOptions()
//                                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_user_blue))
                                    .position(mCurrentLocation)
                                    .snippet("0")
//                    .title(getResources().getString(R.string.your_location))
                    );*/

                    for (int i = 0; i < mItemList.size(); i++) {
                        Venue venue = mItemList.get(i).getVenue();

                        if (venue.getName().length() == 0)
                            mItemList.remove(i);

                        MarkerOptions marker = new MarkerOptions()
                                .position(new LatLng(venue.getLocation().getLat(), venue.getLocation().getLng()))
                                .title(venue.getName())
//                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_place_blue))
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                                .snippet(String.valueOf(i));

                        Marker currentMarker = mMap.addMarker(marker);
                        if (!mMarkerList.contains(currentMarker))
                            mMarkerList.add(currentMarker);
//                        mMarkersHashMap.put(currentMarker, venue);
                    }

//                    if (mAdapter == null) {
                    mAdapter = new PlacePagerAdapter(getFragmentManager(), mContext);
                    mAdapter.setItemList(mItemList);
                    mViewPager.setAdapter(mAdapter);
//                    } else
                    mViewPager.getAdapter().notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<Explore> call, Throwable t) {

            }
        });
    }

    private List<Category> getTags() {

        mCategoryList = new ArrayList<>();

        mCategoryIds = getResources().getStringArray(R.array.categoryIds);
        mCategories = getResources().getStringArray(R.array.categories);
        mCategoryColors = getResources().getIntArray(R.array.categoryColors);

        for (int i = 0; i < mCategories.length; i++) {
            Category category = new Category(mCategoryIds[i], mCategories[i], mCategoryColors[i]);
            mCategoryList.add(category);
        }

        return mCategoryList;
    }

    private String getCategories() {

        mCategoryIds = getResources().getStringArray(R.array.categoryIds);
        StringBuilder sb = new StringBuilder(mCategoryIds.length);

        for (String category : mCategoryIds) {
            sb.append(category + ",");
        }

        return sb.toString().substring(0, sb.length() - 1);
    }

    @Override
    public void handleNewLocation(Location location) {

        if (location != null) {
            double currentLatitude = location.getLatitude();
            double currentLongitude = location.getLongitude();
            mCurrentLocation = new LatLng(currentLatitude, currentLongitude);

            saveUserLocation(currentLatitude, currentLongitude);
        }
    }

    @Override
    public void handleFirstLocation(Location location) {
// Set location to current location
        double currentLatitude, currentLongitude;
        if (location != null) {
            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();

            saveUserLocation(currentLatitude, currentLongitude);
        } else {
            currentLatitude = Double.parseDouble(mSharedPreferences.getString(ARG_LATITUDE, "0"));
            currentLongitude = Double.parseDouble(mSharedPreferences.getString(ARG_LONGITUDE, "0"));
        }

        mCurrentLocation = new LatLng(currentLatitude, currentLongitude);
//            mCurrentLocation = new LatLng(41.007BitmapDescriptorFactory.HUE_RED4, 28.978196);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mCurrentLocation, 15.0F));
        makeRequestForPlaceList(mCurrentLocation.latitude, mCurrentLocation.longitude);
    }

    private void saveUserLocation(double lat, double lon) {
        String latStr = String.valueOf(lat);
        String longStr = String.valueOf(lon);
        mEditor = mSharedPreferences.edit();
        mEditor.putString(ARG_LATITUDE, latStr);
        mEditor.putString(ARG_LONGITUDE, longStr);
        mEditor.commit();
    }

    @Override
    public void onCameraIdle() {
        if (mCurrentLocation != null) {

            locationOfScreenCenter = mMap.getCameraPosition().target;

            Location currentLocation = new Location("current");
            currentLocation.setLatitude(mCurrentLocation.latitude);
            currentLocation.setLongitude(mCurrentLocation.longitude);

            Location centerOfScreen = new Location("center");
            centerOfScreen.setLatitude(locationOfScreenCenter.latitude);
            centerOfScreen.setLongitude(locationOfScreenCenter.longitude);

            if (previousLocation == null)
                previousLocation = currentLocation;

            float distanceCenter = previousLocation.distanceTo(centerOfScreen);
            if (distanceCenter > 2000) {
                if (distanceCenter > 10000)
                    mViewPager.setVisibility(View.GONE);
                searchButton.setVisibility(View.VISIBLE);
                previousLocation = centerOfScreen;
//                mViewPager.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public void onMapClick(LatLng latLng) {
        mViewPager.setVisibility(View.GONE);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (flag) {
            flag = false;
            mViewPager.setVisibility(View.VISIBLE);
            String str = "";
            final Venue temp = mItemList.get(Integer.parseInt(marker.getSnippet())).getVenue();
            LatLng newLatLng = new LatLng(temp.getLocation().getLat(), temp.getLocation().getLng());
            mViewPager.setCurrentItem(Integer.parseInt(marker.getSnippet()));

            if (prevMarker != null) {
//                prevMarker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_place_blue));
                BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
//                if (bitmapDescriptor != null)
//                    prevMarker.setIcon(bitmapDescriptor);
            }

            //leave Marker default color if re-click current Marker
            if (!marker.equals(prevMarker)) {
//                marker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_place_pink));
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                prevMarker = marker;
            }
            prevMarker = marker;

            // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(newLatLng)      // Sets the center of the map to marker
                    .zoom(20)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
//            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(20), 2000, null);

            flag = true;
        }

        return false;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }

    @Override
    public boolean onMyLocationButtonClick() {
//        Toast.makeText(mContext, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    private void drawCircle(LatLng location) {
        CircleOptions options = new CircleOptions();
        options.center(location);
        //Radius in meters
        options.radius(10);
        options.fillColor(getResources()
                .getColor(R.color.colorPrimary));
        options.strokeColor(getResources()
                .getColor(R.color.colorPrimaryDark));
        options.strokeWidth(10);
        mMap.addCircle(options);
    }

    private List<Item_> findByTags(List<Category> categories) {
        List<Item_> items = new ArrayList<>();

        for (Item_ item : mItemList) {
            for (Category category : categories) {
                if (item.hasTag(category.getId()) && !items.contains(item)) {
                    items.add(item);
                }
            }
        }

        return items;
    }

    @Override
    public void onNothingSelected() {
        if (mItemList != null && mAdapter != null) {
            mAdapter.setItemList(mItemList);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onFiltersSelected(@NotNull ArrayList<Category> filters) {
        List<Item_> newItem = findByTags(filters);
        List<Item_> oldItem = mAdapter.getItemList();
        mAdapter.setItemList(newItem);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFilterSelected(Category item) {
        if (item.getId().equals(mCategoryIds[0])) {
            mFilter.deselectAll();
            mFilter.collapse();
        }
    }

    @Override
    public void onFilterDeselected(Category item) {

    }

    class Adapter extends FilterAdapter<Category> {

        Adapter(@NotNull List<? extends Category> items) {
            super(items);
        }

        @NotNull
        @Override
        public FilterItem createView(int position, Category item) {
            FilterItem filterItem = new FilterItem(mContext);

            filterItem.setStrokeColor(mCategoryColors[0]);
            filterItem.setTextColor(mCategoryColors[0]);
            filterItem.setCheckedTextColor(ContextCompat.getColor(mContext, android.R.color.white));
            filterItem.setColor(ContextCompat.getColor(mContext, android.R.color.white));
            filterItem.setCheckedColor(mCategoryColors[position]);
            filterItem.setText(item.getText());
            filterItem.deselect();

            return filterItem;
        }
    }

}