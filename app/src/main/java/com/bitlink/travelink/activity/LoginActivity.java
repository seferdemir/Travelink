package com.bitlink.travelink.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bitlink.travelink.R;
import com.bitlink.travelink.model.User;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bitlink.travelink.activity.MainAppActivity.getUid;
import static com.bitlink.travelink.activity.MainAppActivity.mAuth;
import static com.bitlink.travelink.activity.MainAppActivity.mAuthCurrentUser;
import static com.bitlink.travelink.activity.MainAppActivity.mAuthStateListener;
import static com.bitlink.travelink.activity.MainAppActivity.mDatabase;
import static com.bitlink.travelink.util.AppContants.mSharedPreferences;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class LoginActivity extends AppCompatActivity {

    /* Context */
    static Context mContext;
    private final String TAG = LoginActivity.class.getSimpleName();
    private final int REQUEST_SIGNUP = 0;
    @BindView(R.id.input_email)
    EditText emailText;

    @BindView(R.id.input_password)
    EditText passwordText;

    @BindView(R.id.btn_login)
    Button loginButton;

//    @BindView(R.id.link_skip)
//    TextView linkSkip;
    /* User model */
    User model = new User();
    /* The login button for Facebook
    private LoginButton mFacebookLoginButton; */
    @BindView(R.id.login_with_facebook)
    LoginButton mFacebookLoginButton;
    View focusView = null;
    private ProgressDialog progressDialog;
    /* A dialog that is presented until the Firebase authentication finished. */
    private ProgressDialog mAuthProgressDialog;
    /* The callback manager for Facebook */
    private CallbackManager mFacebookCallbackManager;

    public static void writeNewUser(final User user) {

        mDatabase.child("users").child(getUid()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        User u = dataSnapshot.getValue(User.class);

                        if (u != null) {
                            // might be added the last login time
                            u.setUsername(user.getUsername());
                            u.setBirthday(user.getBirthday());
                            u.setEmail(user.getEmail());
                            u.setGender(user.getGender());
                            u.setPhotoUrl(user.getPhotoUrl());

                            Map<String, Object> userValues = u.toMap();

                            Map<String, Object> childUpdates = new HashMap<>();
                            childUpdates.put("/users/" + getUid() + "/", userValues);

                            mDatabase.updateChildren(childUpdates);

                        } else {
                            mDatabase.child("users").child(getUid()).setValue(user);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
//                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());

        AppEventsLogger.activateApp(this);

        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplication().getApplicationContext());

        mDatabase.child("users").child(getUid()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        User u = dataSnapshot.getValue(User.class);

                        if (u != null)
                            model = u;
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
//                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                mAuthCurrentUser = firebaseAuth.getCurrentUser();
                if (mAuthCurrentUser != null) {
                    Log.d(TAG, mAuthCurrentUser.getUid() + " - " + mAuthCurrentUser.getDisplayName());
//                    mAuth.signOut();
//                    finish();
                } else {
                    LoginManager.getInstance().logOut();
                }
            }
        };

        /* Load the Facebook login button and set up the tracker to monitor access token changes */
        mFacebookCallbackManager = CallbackManager.Factory.create();
        mFacebookLoginButton.setReadPermissions("email", "public_profile");
        mFacebookLoginButton.registerCallback(mFacebookCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);

                /*GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {

                        Place lastLocation = new Place();

                        Log.i("LoginActivity", response.toString());
                        // Get facebook data from login

                        try {
                            String id = object.getString("id");
                            model.setPhotoUrl(new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=200").toString());
                            model.setGender(object.getString("gender").equals("male") ? 0 : 1);
                            if (object.getString("birthday").equals(null)) {
                                Date date = Calendar.getInstance().getTime();
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                                model.setBirthday(dateFormat.format(date));
                            } else
                                model.setBirthday(DateTimeUtils.parseDateTime(object.getString("birthday"), "MM/dd/yyyy", "yyyyMMdd"));

                            lastLocation.setLatitude(object.getJSONObject("location") != null ? object.getJSONObject("location").getString("latitude") :
                                    mSharedPreferences.getString(ARG_LATITUDE, "0"));
                            lastLocation.setLongitude(object.getJSONObject("location") != null ? object.getJSONObject("location").getString("longitude") :
                                    mSharedPreferences.getString(ARG_LONGITUDE, "0"));
                            lastLocation.setName(String.format("%s",
                                    object.getJSONObject("location") != null ? object.getJSONObject("location").getString("name") :
                                            mSharedPreferences.getString(ARG_LOCATION_NAME, getResources().getString(R.string.unknown))));
                        } catch (JSONException e) {
                            lastLocation.setLatitude(mSharedPreferences.getString(ARG_LATITUDE, "0"));
                            lastLocation.setLongitude(mSharedPreferences.getString(ARG_LONGITUDE, "0"));
                            lastLocation.setName(mSharedPreferences.getString(ARG_LOCATION_NAME, getResources().getString(R.string.unknown)));
                            e.printStackTrace();
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                        model.setLastLocation(lastLocation);
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, first_name, last_name, email, gender, birthday, location");
                request.setParameters(parameters);
                request.executeAsync();*/

                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // ...
            }
        });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a placeName to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            MainAppActivity.showText(getResources().getString(R.string.authentication_failed));
                        } else {
                            model.setUid(getUid());
                            model.setUsername(mAuthCurrentUser.getDisplayName());
                            model.setEmail(mAuthCurrentUser.getEmail());

                            onLoginSuccess();
                        }
                    }
                });
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        float fbIconScale = 1.45F;
        Drawable drawable = getResources().getDrawable(com.facebook.R.drawable.com_facebook_button_icon);
        drawable.setBounds(0, 0, (int) (drawable.getIntrinsicWidth() * fbIconScale),
                (int) (drawable.getIntrinsicHeight() * fbIconScale));
        mFacebookLoginButton.setCompoundDrawables(drawable, null, null, null);
        mFacebookLoginButton.setCompoundDrawablePadding(getResources().getDimensionPixelSize(R.dimen.fb_margin_override_textpadding));
        mFacebookLoginButton.setPadding(getResources().getDimensionPixelSize(R.dimen.fb_margin_override_lr), getResources().getDimensionPixelSize(R.dimen.fb_margin_override_top), 0, getResources().getDimensionPixelSize(R.dimen.fb_margin_override_bottom));
    }

    /*@OnClick(R.id.link_skip)
    void SignInAnonymously() {

        if (mAuthCurrentUser == null) {
            mAuth.signInAnonymously()
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "signInAnonymously:onComplete:" + task.isSuccessful());

                            // If sign in fails, display a placeName to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "signInAnonymously", task.getException());
                                MainAppActivity.showText(getResources().getString(R.string.authentication_failed));
                            } else {
                                finish();
                            }
                        }
                    });
        } else {
            finish();
        }
    }*/

    @OnClick(R.id.btn_login)
    void Login() {

        if (!validate()) {
            focusView.requestFocus();
            return;
        }

        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        loginButton.setEnabled(false);

        final MaterialDialog progressDialog = new MaterialDialog.Builder(LoginActivity.this)
                .content(getResources().getString(R.string.authenticate_text))
                .progress(true, 0)
                .cancelable(false)
                .build();
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "signInWithEmailAndPassword:onComplete:" + task.isSuccessful());

                // If sign in fails, display a placeName to the user. If sign in succeeds
                // the auth state listener will be notified and logic to handle the
                // signed in user can be handled in the listener.
                if (!task.isSuccessful()) {
                    Log.w(TAG, "signInWithEmailAndPassword", task.getException());
                    progressDialog.dismiss();
                    onLoginFailed();
                } else {
                    new android.os.Handler().postDelayed(
                            new Runnable() {
                                public void run() {
                                    model.setUid(getUid());
                                    model.setUsername(mAuthCurrentUser.getDisplayName());
                                    model.setEmail(mAuthCurrentUser.getEmail());

                                    onLoginSuccess();
                                    progressDialog.dismiss();
                                }
                            }, 2000);
                }
            }
        });
    }

    @OnClick(R.id.btn_signup)
    void SignUp() {
        // Start the Signup activity
        Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
        startActivityForResult(intent, REQUEST_SIGNUP);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthStateListener != null) {
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                this.finish();
            }
        } else {
            /* Otherwise, it's probably the request by the Facebook login button, keep track of the session */
            mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
//        super.onBackPressed();
    }

    private boolean validate() {

        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            MainAppActivity.showText(getResources().getString(R.string.enter_valid_email));
            focusView = emailText;
            return false;
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            MainAppActivity.showText(getResources().getString(R.string.enter_valid_password));
            focusView = passwordText;
            return false;
        }

        return true;
    }

    public void onLoginSuccess() {
        writeNewUser(model);
        loginButton.setEnabled(true);
        finish();
    }

    public void onLoginFailed() {
        MainAppActivity.showText(getResources().getString(R.string.login_failed));

        loginButton.setEnabled(true);
    }
}
