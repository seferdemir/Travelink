package com.bitlink.travelink.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bitlink.travelink.MyApplication;
import com.bitlink.travelink.R;
import com.bitlink.travelink.adapter.TabViewAdapter;
import com.bitlink.travelink.fragment.CommonChatFragment;
import com.bitlink.travelink.fragment.MapFragment;
import com.bitlink.travelink.fragment.ProfileFragment;
import com.bitlink.travelink.fragment.RecentPostsFragment;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;

import static com.bitlink.travelink.activity.MainAppActivity.getUid;
import static com.bitlink.travelink.activity.MainAppActivity.mAuth;
import static com.bitlink.travelink.activity.MainAppActivity.mAuthCurrentUser;
import static com.bitlink.travelink.activity.MainAppActivity.mAuthStateListener;
import static com.bitlink.travelink.activity.MainAppActivity.mUser;

public class MainActivity extends AppCompatActivity {

    private final String TAG = MainActivity.class.getSimpleName();
    private final int PROFILE_SETTING = 100000;
    private final int HOME = 2;
    private final int LOGIN = 1;
    private final int SIGN_OUT = 3;
    private final int SETTINGS = 4;
    private final int INVITE_FRIENDS = 5;
    private final int HELP_AND_FEEDBACK = 6;
    // Create a few sample profile
    IProfile profile;
    private Fragment mFragment;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private AccountHeader headerResult = null;
    private Drawer result = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appBar);

        MainAppActivity.setContext(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }

        final int selectedColor = ContextCompat.getColor(this, R.color.colorPrimary);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_new_post);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user == null || user.isAnonymous()) {
                    MainAppActivity.showText("You must sign-in to post.");
                    return;
                }
                startActivity(new Intent(MainActivity.this, NewPostActivity.class)); // PostShareActivity
            }
        });

        viewPager = (ViewPager) findViewById(R.id.viewpager);

        ProfileFragment profileFragment = new ProfileFragment();
        CommonChatFragment chatFragment = new CommonChatFragment();

        Bundle args = new Bundle();
        args.putString(ProfileActivity.EXTRA_USER_KEY, getUid());
        chatFragment.setArguments(args);
        profileFragment.setArguments(args);

        TabViewAdapter adapter = new TabViewAdapter(getSupportFragmentManager());
        adapter.addFrag(new RecentPostsFragment(), getString(R.string.home));
        adapter.addFrag(new MapFragment(), getString(R.string.explore));
        adapter.addFrag(chatFragment, getString(R.string.chats));
        adapter.addFrag(profileFragment, getString(R.string.profile));
        viewPager.setOffscreenPageLimit(4);
        viewPager.setAdapter(adapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        int[] tabIcons = {
                R.mipmap.ic_home,
                R.mipmap.ic_explore,
                R.mipmap.ic_chat,
                R.mipmap.ic_person
        };

        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons[3]);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                if (tab.getPosition() == 0) {
                    toolbar.setTitle(getString(R.string.home));
                    fab.setVisibility(View.VISIBLE);
                } else if (tab.getPosition() == 1) {
                    appBarLayout.setExpanded(false, true);
                    toolbar.setTitle(getString(R.string.explore));
                    fab.setVisibility(View.GONE);
                } else if (tab.getPosition() == 2) {
                    appBarLayout.setExpanded(false, true);
                    toolbar.setTitle(getString(R.string.chats));
                    fab.setVisibility(View.GONE);
                } else if (tab.getPosition() == 3) {
                    toolbar.setTitle(getString(R.string.profile));
                    fab.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // Create the AccountHeader
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(true)
                .withHeaderBackground(R.drawable.header)
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
                        //sample usage of the onProfileChanged listener
                        //if the clicked item has the identifier 1 add a new profile ;)
                        Intent intent = null;
                        if (profile instanceof IDrawerItem && profile.getIdentifier() == PROFILE_SETTING) {
                            intent = new Intent(MainActivity.this, EditProfileActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            MainActivity.this.startActivity(intent);
                        } else if (profile instanceof IDrawerItem && profile.getIdentifier() == SIGN_OUT) {
                            mAuth.signOut();
                            headerResult.clear();

//                            finish();
                        }

                        //false if you have not consumed the event and it should close the drawer
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();

        //initialize and create the image loader logic
        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                Glide.with(imageView.getContext()).load(uri).placeholder(placeholder).into(imageView);
            }

            @Override
            public void cancel(ImageView imageView) {
                Glide.clear(imageView);
            }
        });

        //Create the drawer
        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withHasStableIds(true)
//                .withItemAnimator(new AlphaCrossFadeAnimator())
                .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
                .addDrawerItems(
                        new PrimaryDrawerItem()
                                .withName(R.string.home)
                                .withIcon(R.mipmap.ic_home)
                                .withIconColor(selectedColor)
                                .withIconTintingEnabled(true)
                                .withTextColor(selectedColor)
                                .withIdentifier(HOME)
                                .withSelectable(true),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem()
                                .withName(R.string.invite_friends)
                                .withIcon(R.mipmap.ic_person_add)
                                .withIconColor(selectedColor)
                                .withIconTintingEnabled(true)
                                .withTextColor(selectedColor)
                                .withIdentifier(INVITE_FRIENDS)
                                .withSelectable(true),
                        new PrimaryDrawerItem()
                                .withName(R.string.action_settings)
                                .withIcon(R.mipmap.ic_settings)
                                .withIconColor(selectedColor)
                                .withIconTintingEnabled(true)
                                .withTextColor(selectedColor)
                                .withIdentifier(SETTINGS)
                                .withSelectable(true),
                        new PrimaryDrawerItem()
                                .withName(R.string.help_and_feedback)
                                .withIcon(R.mipmap.ic_help)
                                .withIconColor(selectedColor)
                                .withIconTintingEnabled(true)
                                .withTextColor(selectedColor)
                                .withIdentifier(HELP_AND_FEEDBACK)
                                .withSelectable(true)
                ) // add the items we want to use with our Drawer
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                        if (drawerItem != null) {
                            Intent intent = null;
                            if (drawerItem.getIdentifier() == LOGIN) {
                                intent = new Intent(MainActivity.this, LoginActivity.class);
                            } else if (drawerItem.getIdentifier() == SETTINGS) {
                                intent = new Intent(MainActivity.this, MyPreferencesActivity.class);
                            }
                            if (intent != null) {
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                MainActivity.this.startActivity(intent);
                            }
                        }

                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(true)
                .build();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                mAuthCurrentUser = firebaseAuth.getCurrentUser();
                if (mAuthCurrentUser != null) {
                    Log.d(TAG, mAuthCurrentUser.getUid() + " - " + mAuthCurrentUser.getDisplayName());

                    // Create a few sample profile
                    if (headerResult.getActiveProfile() == null) {
                        profile = new ProfileDrawerItem()
                                .withName(mUser.getUsername())
                                .withEmail(mUser.getEmail())
                                .withIdentifier(1);

                        if (mUser.getPhotoUrl() != null)
                            profile.withIcon(mUser.getPhotoUrl());
                        else
                            profile.withIcon(ContextCompat.getDrawable(MainActivity.this, R.mipmap.ic_account_circle));

                        headerResult.addProfiles(profile,
                                new ProfileSettingDrawerItem()
                                        .withName(getResources().getString(R.string.manage_account))
                                        .withIcon(getResources().getDrawable(R.mipmap.ic_settings))
                                        .withIconColor(selectedColor)
                                        .withIconTinted(true)
                                        .withTextColor(selectedColor)
                                        .withIdentifier(PROFILE_SETTING),
                                new ProfileSettingDrawerItem()
                                        .withName(getResources().getString(R.string.sign_out))
                                        .withIcon(getResources().getDrawable(R.mipmap.ic_exit_to_app))
                                        .withIconColor(selectedColor)
                                        .withIconTinted(true)
                                        .withTextColor(selectedColor)
                                        .withIdentifier(SIGN_OUT)
                        );

                        headerResult.updateProfile(profile);
                        // for coloring
                        result.getAdapter().withPositionBasedStateManagement(false);

                        if (result.getDrawerItems() != null && result.getDrawerItem(1) != null &&
                                result.getDrawerItem(1).getIdentifier() == LOGIN)
                            result.removeItem(1);
                    }
                } else {
                    if (result.getDrawerItems() == null || result.getDrawerItem(1) == null || result.getDrawerItem(1) == null) {
                        result.addItemsAtPosition(1,
                                new PrimaryDrawerItem()
                                        .withName(R.string.login)
                                        .withIcon(R.mipmap.ic_account_circle)
                                        .withIconColor(selectedColor)
                                        .withIdentifier(LOGIN)
                                        .withSelectable(false));
                    }
                }
            }
        };

        //only set the active selection or active profile if we do not recreate the activity
        if (savedInstanceState == null) {
            // set the selection to the item with the identifier 1
//            result.setSelection(1, false);

            //set the active profile
            headerResult.setActiveProfile(profile);
            // Do first time initialization -- add initial fragment.
//            mFragment = new RecentPostsFragment();

//            getSupportFragmentManager().beginTransaction().replace(R.id.flContent, mFragment).commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthStateListener);

        // Check auth on Activity start
        if (mAuth.getCurrentUser() == null)
            result.openDrawer();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mAuthStateListener != null) {
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    public void onBackPressed() {
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, MyPreferencesActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean canAccessLocation() {
        return (hasPermission(Manifest.permission.ACCESS_FINE_LOCATION) || hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION));
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean hasPermission(String perm) {
        return (PackageManager.PERMISSION_GRANTED == checkSelfPermission(perm));
    }
}
