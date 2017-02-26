package com.bitlink.travelink.util;

import android.content.SharedPreferences;

public class AppContants {

    public final static String FOURSQUARE_CLIENT_ID = "JRTI0KZGEB5E3D11PXFBMYE2RBZAVVVZLMSFEXAXEOOWIFK1";
    public final static String FOURSQUARE_CLIENT_SECRET = "54WOMCIBGY1EGP2O1PXXDFXU11ONLTFSLHLQJSULQE0JEJQ3";
    public final static String FOURSQUARE_VERSION = "20161010";
    public final static String FLICKR_API_KEY = "20a65a01ad5d715c998738e5a66db95a";
    public final static String FLICKR_API_SECRET = "c087bb2a7913afb3";
    public final static String FLICKR_AUTH_ACTION = "auth";
    public final static String FLICKR_GET_FROB_ACTION = "frob";
    public final static String FLICKR_GET_TOKEN_ACTION = "gettoken";
    public final static String FLICKR_GET_PHOTO_ACTION = "getphotos";
    public final static String FLICKR_PHOTO_REVEAL_CREDENTIAL = "532-862-960";

    public final static int AUTH_REQUEST_CODE = 1;

    public static final String REQUIRED = "Required";

    public static final String ARG_USER = "user";

    public static final String ARG_TRANSACTION = "user_transaction";
    public static final String ARG_CONNECTION_FRAGMENT = "connection_fragment";
    public static final String ARG_CHAT_TYPE = "chat_type";

    public static final String ARG_LATITUDE = "latitude";
    public static final String ARG_LONGITUDE = "longitude";
    public static final String ARG_LOCATION_NAME = "location";
    public static final String MESSAGES_CHILD = "messages";
    public static final String PRIVATE_MESSAGES_CHILD = "private-messages";
    public static SharedPreferences mSharedPreferences;

    public enum ConnectionFragment {
        Followers,
        Following,
        AllUsers
    }

    public enum UserTransactions {
        ChangeEmail,
        ChangePassword,
        ResetPassword
    }

    public enum ChatType {
        Common,
        Private
    }
}
