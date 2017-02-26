package com.bitlink.travelink.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bitlink.travelink.R;
import com.bitlink.travelink.model.Place;
import com.bitlink.travelink.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bitlink.travelink.activity.LoginActivity.writeNewUser;
import static com.bitlink.travelink.activity.MainAppActivity.mAuth;
import static com.bitlink.travelink.util.AppContants.ARG_LATITUDE;
import static com.bitlink.travelink.util.AppContants.ARG_LONGITUDE;
import static com.bitlink.travelink.util.AppContants.mSharedPreferences;

public class SignupActivity extends AppCompatActivity {

    View focusView = null;
    /* User model */
    User model;
    @BindView(R.id.input_name)
    EditText nameText;
    @BindView(R.id.input_email)
    EditText emailText;
    @BindView(R.id.input_password)
    EditText passwordText;
    @BindView(R.id.input_birthday)
    EditText birtDayText;
    @BindView(R.id.btn_create_account)
    Button createAccountButton;
    @BindView(R.id.link_signin)
    TextView linkSignin;
    @BindView(R.id.input_gender)
    Spinner genderSpinner;
    private MaterialDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        ButterKnife.bind(this);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplication().getApplicationContext());

        model = new User();

        String[] spinnerList = getResources().getStringArray(R.array.gender_arrays);
        List<String> list = Arrays.asList(spinnerList);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, list) {

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {

                View view = super.getView(position, convertView, parent);

                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));

                return view;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View view = super.getView(position, convertView, parent);

                TextView text = (TextView) view.findViewById(android.R.id.text1);
//                text.setTextColor(Color.WHITE);
                text.setTextSize(18);

                return view;
            }
        };

        genderSpinner.setAdapter(adapter);
        genderSpinner.setPrompt(getResources().getString(R.string.gender));
        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                model.gender = parent.getItemAtPosition(pos).equals("Male") ? 0 : 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    @OnClick(R.id.link_signin)
    void SignIn() {
        // Finish the registration screen and return to the Login activity
        finish();
    }

    @OnClick(R.id.btn_create_account)
    void CreateAccount() {
        if (!validate()) {
            focusView.requestFocus();
            return;
        }

        String name = nameText.getText().toString();
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        model.username = name;
        model.email = email;

        createAccountButton.setEnabled(false);

        progressDialog = new MaterialDialog.Builder(SignupActivity.this)
                .content(getResources().getString(R.string.create_account_text))
                .progress(true, 0)
                .cancelable(false)
                .build();
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull final Task<AuthResult> task) {

                if (!task.isSuccessful()) {
                    onSignupFailed();
                } else {
                    new android.os.Handler().postDelayed(
                            new Runnable() {
                                public void run() {
                                    model.uid = task.getResult().getUser().getUid();
                                    model.lastLocation = new Place();
                                    model.lastLocation.latitude = mSharedPreferences.getString(ARG_LATITUDE, "0");
                                    model.lastLocation.longitude = mSharedPreferences.getString(ARG_LONGITUDE, "0");

                                    Calendar calendar = Calendar.getInstance();
                                    Date date = calendar.getTime();
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
                                    model.lastLocation.checkinAt = dateFormat.format(date);
                                    model.lastLocation.name = getResources().getString(R.string.unknown);

                                    onSignupSuccess();
                                    progressDialog.dismiss();
                                }
                            }, 3000);
                }
            }
        });
    }

    public void onSignupSuccess() {
        writeNewUser(model);
        showText(getResources().getString(R.string.create_successful));
        createAccountButton.setEnabled(true);
        setResult(RESULT_OK, null);

        Intent intent = new Intent(this, MainAppActivity.class);
        startActivity(intent);
        finish();
    }

    public void onSignupFailed() {
        showText(getResources().getString(R.string.signup_failed));
        createAccountButton.setEnabled(true);
        progressDialog.dismiss();
    }

    public boolean validate() {

        String name = nameText.getText().toString();
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            showText(getResources().getString(R.string.enter_valid_name));
            focusView = nameText;
            return false;
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showText(getResources().getString(R.string.enter_valid_email));
            focusView = emailText;
            return false;
        }

        if (password.isEmpty() || password.length() < 6 || password.length() > 10) {
            showText(getResources().getString(R.string.enter_valid_password));
            focusView = passwordText;
            return false;
        }

        return true;
    }

    public void showText(String text) {
        Toast.makeText(getBaseContext(), text, Toast.LENGTH_LONG).show();
    }

    public void setBirthDay(View view) {
        Calendar newCalendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                SignupActivity.this, R.style.MyDialogTheme, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);

                birtDayText.setText(new SimpleDateFormat("dd MMMM yyyy EEEE").format(newDate.getTime()));
                model.birthday = new SimpleDateFormat("yyyyMMdd").format(newDate.getTime());
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        Calendar minCal = Calendar.getInstance();
        minCal.set(1987, 0, 1);

        datePickerDialog.getDatePicker().setMinDate(minCal.getTimeInMillis());
        datePickerDialog.getDatePicker().setMaxDate(newCalendar.getTimeInMillis());

        datePickerDialog.show();
    }
}
