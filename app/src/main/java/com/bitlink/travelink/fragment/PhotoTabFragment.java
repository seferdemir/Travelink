package com.bitlink.travelink.fragment;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bitlink.travelink.R;
import com.bitlink.travelink.adapter.PhotoAdapter;
import com.bitlink.travelink.model.flickr.AuthUser;
import com.bitlink.travelink.model.flickr.FrobResponse;
import com.bitlink.travelink.model.flickr.PhotoResponse;
import com.bitlink.travelink.model.flickr.Photos;
import com.bitlink.travelink.util.AppContants;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bitlink.travelink.util.AppContants.AUTH_REQUEST_CODE;
import static com.bitlink.travelink.util.AppContants.mSharedPreferences;

/**
 * A simple {@link Fragment} subclass.
 */
public class PhotoTabFragment extends Fragment {

    private SharedPreferences.Editor mEditor;

    com.bitlink.travelink.api.flickr.ApiInterface apiService;

    final String flickrUserNsid = "flickrUserNsid";
    final String flickrToken = "token";

    private Photos mPhotos;

    private String frob;

    private RecyclerView mRecyclerView;

    private PhotoAdapter mAdapter;

    public PhotoTabFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        apiService = com.bitlink.travelink.api.flickr.ApiClient.getClient().create(com.bitlink.travelink.api.flickr.ApiInterface.class);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getActivity().getApplicationContext());

        String nSid = mSharedPreferences.getString(flickrUserNsid, null);
        if (nSid == null) {
            sendRequestForAuthToken();
        } else {
            sendRequestForUserPhotos("150082343@N02");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tab_photo, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_photos);

        return view;
    }

    private void sendRequestForAuthToken() {

        Call<FrobResponse> callFrob = apiService.getFrob(AppContants.FLICKR_API_KEY, "json", "1", getApiSig(AppContants.FLICKR_GET_FROB_ACTION));
        callFrob.enqueue(new Callback<FrobResponse>() {
            @Override
            public void onResponse(Call<FrobResponse> call, Response<FrobResponse> response) {
                final FrobResponse frobResponse = response.body();
                if (frobResponse != null && !frobResponse.getStat().contains("fail")) {
                    frob = frobResponse.getFrob().get_content();

                    onAuth();
                }
            }

            @Override
            public void onFailure(Call<FrobResponse> call, Throwable t) {

            }
        });

    }

    private void sendRequestForUserPhotos(String flickrUserNsid) {

        Call<PhotoResponse> callPhoto = apiService.getPhotos(AppContants.FLICKR_API_KEY, flickrUserNsid, "url_m", "json", "1");
        callPhoto.enqueue(new Callback<PhotoResponse>() {
            @Override
            public void onResponse(Call<PhotoResponse> call, Response<PhotoResponse> response) {
                PhotoResponse photoResponse = response.body();
                if (photoResponse != null && !photoResponse.getStat().contains("fail")) {
                    mPhotos = photoResponse.getPhotos();

                    mAdapter = new PhotoAdapter(getActivity(), mPhotos.getPhoto());
                    mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                    mRecyclerView.setAdapter(mAdapter);
                }
            }

            @Override
            public void onFailure(Call<PhotoResponse> call, Throwable t) {

            }
        });

    }

    public void onAuth() {
//        startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.flickr.com/services/auth/?api_key=" + AppContants.FLICKR_API_KEY + "&api_sig=" + getApiSig(AppContants.FLICKR_AUTH_ACTION) + "&frob=" + frob + "&perms=write")), AUTH_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == AUTH_REQUEST_CODE) {

            Call<AuthUser> callToken = apiService.getToken(AppContants.FLICKR_API_KEY, "json", frob, "1", getApiSig(AppContants.FLICKR_GET_TOKEN_ACTION));
            callToken.enqueue(new Callback<AuthUser>() {
                @Override
                public void onResponse(Call<AuthUser> call, Response<AuthUser> response) {
                    AuthUser authUser = response.body();
                    if (authUser != null && !authUser.getStat().contains("fail")) {
                        com.bitlink.travelink.model.flickr.User flickrUser = authUser.getAuth().getUser();

                        mEditor = mSharedPreferences.edit();
                        mEditor.putString(flickrUserNsid, flickrUser.getNsid());
                        mEditor.putString(flickrToken, authUser.getAuth().getToken().get_content());
                        mEditor.commit();

                        sendRequestForUserPhotos(flickrUser.getNsid());
                    }
                }

                @Override
                public void onFailure(Call<AuthUser> call, Throwable t) {

                }
            });
        }
    }

    private String getApiSig(String requestType) {
        String apiSig = "";

        if (requestType.equals(AppContants.FLICKR_GET_TOKEN_ACTION)) {
            apiSig = new String(Hex.encodeHex(DigestUtils.md5(AppContants.FLICKR_API_SECRET + "api_key" + AppContants.FLICKR_API_KEY + "formatjsonfrob" + frob + "methodflickr.auth.getTokennojsoncallback1")));
        } else if (requestType.equals(AppContants.FLICKR_GET_FROB_ACTION)) {
            apiSig = new String(Hex.encodeHex(DigestUtils.md5(AppContants.FLICKR_API_SECRET + "api_key" + AppContants.FLICKR_API_KEY + "formatjsonmethodflickr.auth.getFrobnojsoncallback1")));
        } else if (requestType.equals(AppContants.FLICKR_AUTH_ACTION)) {
            apiSig = new String(Hex.encodeHex(DigestUtils.md5(AppContants.FLICKR_API_SECRET + "api_key" + AppContants.FLICKR_API_KEY + "frob" + frob + "permswrite")));
        } else if (requestType.equals(AppContants.FLICKR_GET_PHOTO_ACTION)) {
            apiSig = new String(Hex.encodeHex(DigestUtils.md5(AppContants.FLICKR_API_SECRET + "api_key" + AppContants.FLICKR_API_KEY + "formatjsonmethodflickr.photos.searchnojsoncallback1")));
        }

        return apiSig;
    }

}
