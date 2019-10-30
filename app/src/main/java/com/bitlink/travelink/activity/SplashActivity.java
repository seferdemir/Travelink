package com.bitlink.travelink.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.core.content.ContextCompat;
import android.widget.ImageView;

import com.bitlink.travelink.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends Activity {

    // Splash screen timer
    private final int SPLASH_TIME_OUT = 1000;

    @BindView(R.id.img_splash)
    ImageView imgSplash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ButterKnife.bind(this);

        imgSplash.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.logo_colorful));

        new Handler().postDelayed(new Runnable() {

            /* Showing splash screen with a timer. This will be useful when you want to show case your app logo / company */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app menu_main activity
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);

                // close this activity
                SplashActivity.this.finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
