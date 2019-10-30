package com.bitlink.travelink;

import android.app.Application;
import android.content.Context;
import androidx.multidex.MultiDex;

import com.google.firebase.database.FirebaseDatabase;

public class MyApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

//        FirebaseApp.initializeApp(this);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}