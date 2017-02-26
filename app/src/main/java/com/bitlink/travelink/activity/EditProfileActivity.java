package com.bitlink.travelink.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bitlink.travelink.R;
import com.bitlink.travelink.util.DateTimeUtils;
import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bitlink.travelink.activity.LoginActivity.writeNewUser;
import static com.bitlink.travelink.activity.MainAppActivity.mUser;

public class EditProfileActivity extends AppCompatActivity {

    @BindView(R.id.img_person_image)
    ImageView imgPerson;
    @BindView(R.id.input_gender)
    Spinner genderSpinner;
    @BindView(R.id.input_birthday)
    EditText birtDayText;
    @BindView(R.id.input_fullname)
    EditText nameText;
    @BindView(R.id.input_email)
    EditText emailText;
    @BindView(R.id.fab)
    FloatingActionButton photoButton;
    View focusView = null;
    // Pick image request
    private int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        final Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ButterKnife.bind(this);

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
                text.setTextColor(Color.BLACK);
                text.setTextSize(18);

                return view;
            }
        };

        genderSpinner.setAdapter(adapter);
        genderSpinner.setPrompt(getResources().getString(R.string.gender));
        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                mUser.gender = parent.getItemAtPosition(pos).equals("Male") ? 0 : 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        Glide.with(this)
                .load(mUser.photoUrl)
                .into(imgPerson);

        genderSpinner.setId(mUser.gender);

        birtDayText.setText(DateTimeUtils.parseDateTime(mUser.birthday, "yyyyMMdd", "dd MMMM yyyy EEEE"));
        nameText.setText(mUser.username);
        emailText.setText(mUser.email);
    }

    @OnClick(R.id.fab)
    void PickPhoto() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture)), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                imgPerson.setImageBitmap(bitmap);
                setPhoto(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_profile, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save_profile) {

            if (!validate()) {
                focusView.requestFocus();
                return false;
            }

            mUser.username = nameText.getText().toString();
            mUser.email = emailText.getText().toString();
            mUser.gender = (int) genderSpinner.getSelectedItemId();

            writeNewUser(mUser);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean validate() {

        String email = emailText.getText().toString();
        String fullname = nameText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showText(getResources().getString(R.string.enter_valid_email));
            focusView = emailText;
            return false;
        }

        if (fullname.isEmpty() || fullname.length() < 3 || fullname.length() > 25) {
            showText(getResources().getString(R.string.enter_name));
            focusView = nameText;
            return false;
        }

        return true;
    }

    public void setBirthDay(View view) {
        Calendar newCalendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                EditProfileActivity.this, R.style.MyDialogTheme, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);

                birtDayText.setText(new SimpleDateFormat("dd MMMM yyyy EEEE").format(newDate.getTime()));
                mUser.birthday = new SimpleDateFormat("yyyyMMdd").format(newDate.getTime());
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        Calendar minCal = Calendar.getInstance();
        minCal.set(1987, 0, 1);

        datePickerDialog.getDatePicker().setMinDate(minCal.getTimeInMillis());
        datePickerDialog.getDatePicker().setMaxDate(newCalendar.getTimeInMillis());

        datePickerDialog.show();
    }

    private void setPhoto(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        mUser.photoUrl = Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public void showText(String text) {
        Toast.makeText(getBaseContext(), text, Toast.LENGTH_LONG).show();
    }
}

