package com.bitlink.travelink.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bitlink.travelink.R;
import com.bitlink.travelink.activity.StreetViewActivity;
import com.bitlink.travelink.model.foursquare.Item_;
import com.bitlink.travelink.model.foursquare.Tip;
import com.bitlink.travelink.model.foursquare.Venue;
import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bitlink.travelink.util.AppContants.ARG_LATITUDE;
import static com.bitlink.travelink.util.AppContants.ARG_LOCATION_NAME;
import static com.bitlink.travelink.util.AppContants.ARG_LONGITUDE;

public class PlaceViewPagerFragment extends Fragment {

    private Item_ mItem;
    private Typeface mTypeface;

    @BindView(R.id.tv_venue_name)
    TextView tvTitle;

    @BindView(R.id.img_thumbnail)
    ImageView imgThumbnail;

    public PlaceViewPagerFragment() {

    }

    @SuppressLint("ValidFragment")
    public PlaceViewPagerFragment(Item_ item) {
        this.mItem = item;
    }

    public Item_ getItem() {
        return mItem;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTypeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Segoe-UI-Light.ttf");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.place_card, container, false);

        ButterKnife.bind(this, rootView);

        try {

            if (getItem() != null) {
                final Venue venue = getItem().getVenue();

                if (getItem().getTips().size() > 0) {
                    Tip tip = getItem().getTips().get(0);

                    Glide.with(this.getContext())
                            .load(tip.getPhotourl())
                            .centerCrop()
                            .crossFade()
                            .into(imgThumbnail);
                }

                tvTitle.setTypeface(mTypeface);
                tvTitle.setText(venue.getName());

                String distance = "";
                Integer venueDistance = venue.getLocation().getDistance();
                if (venueDistance > 1000) {
                    distance = String.format("%.2f", Double.parseDouble(String.valueOf(venueDistance)) / 1000) + " km";
                } else {
                    distance = String.valueOf(venueDistance) + " m";
                }
//            tvDescription.setText(String.valueOf(venue.getUrl()));

                rootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), StreetViewActivity.class);

                        Bundle args = new Bundle();
                        args.putDouble(ARG_LATITUDE, venue.getLocation().getLat());
                        args.putDouble(ARG_LONGITUDE, venue.getLocation().getLng());
                        args.putString(ARG_LOCATION_NAME, venue.getName());
                        intent.putExtras(args);

                        startActivity(intent);
                    }
                });
            }

            return rootView;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("PlacesMapFragment:" + e.getStackTrace());
        }
    }

}
