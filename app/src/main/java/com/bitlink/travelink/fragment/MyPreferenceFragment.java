package com.bitlink.travelink.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bitlink.travelink.R;
import com.bitlink.travelink.activity.UserTransactionActivity;
import com.bitlink.travelink.util.AppContants;

public class MyPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

    SharedPreferences sharedPreferences;

    Preference changeEmailPref, changePasswordPref;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Toolbar mToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);

        addPreferencesFromResource(R.xml.preferences);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
//        final SharedPreferences.Editor editor = sharedPreferences.edit();

        changeEmailPref = findPreference("ChangeEmail");
        changePasswordPref = findPreference("ChangePassword");

        changeEmailPref.setOnPreferenceClickListener(this);
        changePasswordPref.setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {

        Intent intent = new Intent(getActivity(), UserTransactionActivity.class);

        if (preference.equals(changeEmailPref))
            intent.putExtra(AppContants.ARG_TRANSACTION, 0);
        else
            intent.putExtra(AppContants.ARG_TRANSACTION, 1);

        startActivity(intent);

        return true;
    }
}