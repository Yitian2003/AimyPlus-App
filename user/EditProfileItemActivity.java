// created by Yitian Jan 14， 2017
package com.aimyplus.consumer.activity.user;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.EditText;
import android.content.Intent;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.text.TextWatcher;
import android.text.Editable;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import com.aimyplus.consumer.R;
import com.aimyplus.consumer.app.GlobalApplication;
import com.aimyplus.consumer.base.BaseResponse;
import com.aimyplus.consumer.base.IResponse;
import com.aimyplus.consumer.model.user.UserProfileModel;
import com.aimyplus.consumer.utils.ConnUtil;
import com.aimyplus.consumer.utils.DateTimeUtil;
import com.aimyplus.consumer.utils.UIUtil;
import com.aimyplus.consumer.adapter.PlaceArrayAdapter;

public class EditProfileItemActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,GoogleApiClient.ConnectionCallbacks {

    public static final String FIELD_FIRST_NAME = "Name";
    public static final String FIELD_LAST_NAME = "Last Name";
    public static final String FIELD_DATE_OF_BIRTH = "Date Of Birth";
    public static final String FIELD_MOBILE = "Mobile";
    public static final String FIELD_LAND_LINE = "Landline";
    public static final String FIELD_EMAIL = "Email";
    public static final String FIELD_ADDRESS = "Address";
    public static final String FIELD_BILLING_ADDRESS = "Billing Address";
    public static final String FIELD_OSCAR = "Oscar";
    public static final String PARAM_KEY_MODEL = "Model";
    public static final String PARAM_KEY_FIELD ="FieldName";
    public static final String PARAM_VALUE_FIELD = "EditString";

// Log tag
    private static final String TAG = EditProfileItemActivity.class.getSimpleName();
    private Context context;

    private static final int GOOGLE_API_CLIENT_ID = 0;
    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(-45.9, 166.0), new LatLng(-35.0, 175.0)); // latLng close to New Zealand

    private String fieldName;

    EditText editTextName;
    EditText editTextLastName;
    EditText editTextUserName;
    EditText editTextEmail;
    EditText editTextMobile;
    EditText editTextCountryCode;
    EditText editTextLandline;
    EditText editTextLandlineCountryCode;
    EditText editTextOscar;
    TextView textViewPlusSign;
    TextView textViewPlusSignLandline;
    DatePicker datePickerDB;
    //EditText editTextLandlineAreaCode;
    AutoCompleteTextView editTextAddress;
    AutoCompleteTextView editTextBillingAddress;

    MenuItem btnSave;

    UserProfileModel userProfileModel;
    String toolBarTitle = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_item);

        initView();
        initActionBar();
        setTextChangeListener();
    }

    private void initView() {
        editTextName                = (EditText) findViewById(R.id.editName);
        editTextUserName            = (EditText) findViewById(R.id.editUsername);
        editTextLastName            = (EditText) findViewById(R.id.editLastName);
        editTextEmail               = (EditText) findViewById(R.id.editEmail);
        datePickerDB                = (DatePicker) findViewById(R.id.editDB);
        editTextLandline            = (EditText) findViewById(R.id.editPhone);
        textViewPlusSignLandline    = (TextView) findViewById(R.id.textViewPlusSighLandline);
        editTextMobile              = (EditText) findViewById(R.id.editMobile);
        editTextLandlineCountryCode = (EditText) findViewById(R.id.editLandlineCountryCode);
        //editTextLandlineAreaCode  = (EditText) findViewById(R.id.editLandlineAreaCode);
        textViewPlusSign            = (TextView) findViewById(R.id.textViewPlusSigh);
        editTextCountryCode         = (EditText) findViewById(R.id.editMobileCountryCode);
        editTextOscar               = (EditText) findViewById(R.id.editOscar);
        editTextAddress             = (AutoCompleteTextView) findViewById(R.id.editAddress);
        editTextBillingAddress      = (AutoCompleteTextView) findViewById(R.id.editBillingAddress);

        googlePlaceAutoCompleteInit();

        // get data from last activity
        Intent intent = getIntent();
        if (intent != null) {
            fieldName = intent.getStringExtra(PARAM_KEY_FIELD);
            String editString = intent.getStringExtra(PARAM_VALUE_FIELD);
            userProfileModel = (UserProfileModel) intent.getSerializableExtra(PARAM_KEY_MODEL);

            switch (fieldName) {

                case FIELD_FIRST_NAME:
                    setEditText(editString, editTextName);
                    toolBarTitle = "Change First Name";
                    break;
                case FIELD_LAST_NAME:
                    setEditText(editString, editTextLastName);
                    toolBarTitle = "Change Last Name";
                    break;
                case FIELD_DATE_OF_BIRTH:
                    if (editString.isEmpty() || editString == null) {
                        datePickerDB.init(1988, 06, 15, new OnDateChangedListener() {
                            @Override
                            public void onDateChanged(DatePicker view, int year, int month, int day) {
                                btnSave.setEnabled(true);
                            }
                        });
                    } else {
                        String[] parts = editString.split("-");
                        int monthInit = Integer.parseInt(parts[1]) - 1;
                        int dayInit = Integer.parseInt(parts[2].substring(0, 2));
                        int yearInit = Integer.parseInt(parts[0]);
                        datePickerDB.init(yearInit, monthInit, dayInit, new OnDateChangedListener() {
                            @Override
                            public void onDateChanged(DatePicker view, int year, int month, int day) {
                                btnSave.setEnabled(true);
                            }
                        });
                    }
                    datePickerDB.setVisibility(View.VISIBLE);
                    toolBarTitle = "Change Date of Birth";
                    break;
                case FIELD_EMAIL:
                    setEditText(editString, editTextEmail);
                    toolBarTitle = "Change Email";
                    break;
                case FIELD_MOBILE:

                    if (editString.isEmpty()) {
                        editTextCountryCode.setText(setCountryCode());
                    } else {
                        String[] parts = editString.split("-");
                        editTextMobile.setText(parts[1]);
                        editTextCountryCode.setText(parts[0].toCharArray(), 1, parts[0].length() - 1);
                    }
                    textViewPlusSign.setVisibility(View.VISIBLE);
                    editTextCountryCode.setVisibility(View.VISIBLE);
                    editTextMobile.setVisibility(View.VISIBLE);
                    editTextMobile.requestFocus();
                    editTextMobile.setSelection(editTextMobile.getText().length());
                    toolBarTitle = "Change Mobile";
                    break;
                case FIELD_LAND_LINE:

                    if (editString.isEmpty()) {

                        editTextLandlineCountryCode.setText(setCountryCode());
                    } else {
                        String[] parts = editString.split("-");
                        editTextLandline.setText(parts[parts.length - 1]);
                        //editTextLandlineAreaCode.setText(parts[1]);
                        editTextLandlineCountryCode.setText(parts[0].toCharArray(), 1, parts[0].length() - 1);
                    }
                    textViewPlusSignLandline.setVisibility(View.VISIBLE);
                    editTextLandlineCountryCode.setVisibility(View.VISIBLE);
                    //editTextLandlineAreaCode.setVisibility(View.VISIBLE);
                    editTextLandline.setVisibility(View.VISIBLE);
                    editTextLandline.requestFocus();
                    editTextLandline.setSelection(editTextLandline.getText().length());
                    toolBarTitle = "Change Landline";
                    break;
                case FIELD_ADDRESS:
                    editTextAddress.setText(editString);
                    editTextAddress.setVisibility(View.VISIBLE);
                    editTextAddress.setSelection(editTextAddress.getText().length());
                    toolBarTitle = "Change Address";
                    break;
                case FIELD_BILLING_ADDRESS:
                    editTextBillingAddress.setText(editString);
                    editTextBillingAddress.setVisibility(View.VISIBLE);
                    editTextBillingAddress.setSelection(editTextBillingAddress.getText().length());
                    toolBarTitle = "Change Billing Address";
                    break;
                case FIELD_OSCAR:
                    setEditText(editString, editTextOscar);
                    toolBarTitle = "Change Oscar Number";
                    break;
                default:
                    break;
            }
        }
    }

    private void setEditText(String editString, EditText editText) {
        editText.setText(editString);
        editText.setVisibility(View.VISIBLE);
        editText.setSelection(editText.getText().length());
    }

    private void googlePlaceAutoCompleteInit() {
        // connect to google place service, place autocomplete
        mGoogleApiClient = new GoogleApiClient.Builder(EditProfileItemActivity.this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .addApi(AppIndex.API).build();
        editTextAddress.setThreshold(3);
        editTextBillingAddress.setThreshold(3);
        editTextAddress.setOnItemClickListener(mAutocompleteClickListener);
        editTextBillingAddress.setOnItemClickListener(mAutocompleteClickListener);
        mPlaceArrayAdapter = new PlaceArrayAdapter(this, android.R.layout.simple_list_item_1,
                BOUNDS_MOUNTAIN_VIEW, null);
        editTextAddress.setAdapter(mPlaceArrayAdapter);
        editTextBillingAddress.setAdapter(mPlaceArrayAdapter);
    }

    private void initActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(toolBarTitle);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_save_profile, menu);
        btnSave = menu.findItem(R.id.action_profile_save);
        btnSave.setEnabled(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // final Intent intent = new Intent(EditProfileItemActivity.this, UserProfileActivity.class);

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.action_profile_save:
                btnSave.setEnabled(false);

                if (!isEditTextValidate()) {
                    btnSave.setEnabled(false);
                } else {
                    // save model and back to previous activity
                    btnSave.setEnabled(true);
                    switch (fieldName) {
                        case FIELD_FIRST_NAME:
                            userProfileModel.setFirstName(editTextName.getText().toString());

                            break;
                        case FIELD_LAST_NAME:
                            userProfileModel.setLastName(editTextLastName.getText().toString());
                            break;
                        case FIELD_DATE_OF_BIRTH:
                            String date = "";
                            date = datePickerDB.getYear() + "-" + (datePickerDB.getMonth() + 1) + "-" + datePickerDB.getDayOfMonth()
                                    + " 00:00:00";
                            userProfileModel.setDateOfBirth(date);

                            break;
                        case FIELD_MOBILE:
                            String mobile = "+" + editTextCountryCode.getText().toString()
                                    + "-" + editTextMobile.getText().toString();
                            userProfileModel.setMobile(mobile);
                            break;
                        case FIELD_LAND_LINE:
                            String landline = "+" + editTextLandlineCountryCode.getText().toString()
                                    + "-" + /*editTextLandlineAreaCode.getText().toString()
                                    + "-" +*/ editTextLandline.getText().toString();
                            userProfileModel.setLandline(landline);
                            break;
                        case FIELD_ADDRESS:
                            userProfileModel.setAddress(editTextAddress.getText().toString());
                            break;
                        case FIELD_BILLING_ADDRESS:
                            userProfileModel.setBillingAddress(editTextBillingAddress.getText().toString());
                            break;
                        case FIELD_OSCAR:
                            userProfileModel.setOscarNum(editTextOscar.getText().toString());
                            break;
                        default:
                            break;
                    }

                    ConnUtil.postUserProfile(TAG, userProfileModel, new BaseResponse<UserProfileModel>(this) {

                        @Override
                        public void onSuccess(Object sender, UserProfileModel response) {
                            super.onSuccess(sender, response);
                            Toast.makeText(EditProfileItemActivity.this, "pass model success", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Object sender, String response) {
                            super.onFailure(sender, response);
                            Toast.makeText(EditProfileItemActivity.this, "pass model fail", Toast.LENGTH_SHORT).show();
                        }
                    });
                    //
                    finish();
                    //startActivity(new Intent(EditProfileItemActivity.this, UserProfileActivity.class));
                    return true;
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isFirstNameValid(String firstName) {
        return firstName.length() >= 2;
    }

    private boolean isLastNameValid(String lastName) {
        return lastName.length() >= 2;
    }

    // enable save button when text changed
    private void setTextChangeListener() {
        editTextName.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnSave.setEnabled(true);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        editTextLastName.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnSave.setEnabled(true);
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        editTextCountryCode.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnSave.setEnabled(true);
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        editTextMobile.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnSave.setEnabled(true);
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        editTextLandline.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnSave.setEnabled(true);
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        /*editTextLandlineAreaCode.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnSave.setEnabled(true);
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });*/

        editTextLandlineCountryCode.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnSave.setEnabled(true);
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        editTextEmail.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnSave.setEnabled(true);
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        editTextAddress.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnSave.setEnabled(true);

            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });


        editTextBillingAddress.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnSave.setEnabled(true);
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        editTextOscar.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnSave.setEnabled(true);
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
    }

    private boolean isEditTextValidate(){
        switch (fieldName) {

            case FIELD_FIRST_NAME:
                if (!isFirstNameValid(editTextName.getText().toString())) {
                    editTextName.setError(getString(R.string.check_valid_first_name));
                    return false;
                } else {
                    return true;
                }
            case FIELD_LAST_NAME:
                if (!isLastNameValid(editTextLastName.getText().toString())) {
                    editTextLastName.setError(getString(R.string.check_valid_last_name));
                    //btnSave.setEnabled(false);
                    return false;
                } else {
                    return true;
                }
            // Check for a valid email address.
            case FIELD_EMAIL:
                if (TextUtils.isEmpty(editTextEmail.getText().toString())) {
                    editTextEmail.setError(getString(R.string.error_field_required));
                    return false;
                } else if (!isEmailValid(editTextEmail.getText().toString())) {
                    editTextEmail.setError(getString(R.string.error_invalid_email));
                    return false;
                } else {
                    return true;
                }
            case FIELD_MOBILE:
                return true;
            default:
                return true;
        }
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);
            CharSequence attributions = places.getAttributions();

            if (attributions != null) {
            }
        }
    };

   @Override
    public void onConnected(Bundle bundle) {
        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this,
                "Google Places API connection failed with error code:" +
                        connectionResult.getErrorCode(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mPlaceArrayAdapter.setGoogleApiClient(null);
    }

    private String setCountryCode() {
        String countryCodeString = "";
        String countryCodesNum="";

        TelephonyManager tm = (TelephonyManager)this.getSystemService(this.TELEPHONY_SERVICE);
        countryCodeString = tm.getNetworkCountryIso().toUpperCase();
        if (countryCodeString.isEmpty()) {
            countryCodeString = getApplicationContext().getResources().getConfiguration().locale.getCountry();
        }

        String[] countryCodeArray=this.getResources().getStringArray(R.array.CountryCodes);
        for(int i=0;i<countryCodeArray.length;i++){
            String[] countryCodes=countryCodeArray[i].split(",");
            if(countryCodes[1].trim().equals(countryCodeString.trim())){
                countryCodesNum=countryCodes[0];
                break;
            }
        }
        return  countryCodesNum;
    }

    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("EditProfileItem Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mGoogleApiClient.connect();
        AppIndex.AppIndexApi.start(mGoogleApiClient, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(mGoogleApiClient, getIndexApiAction());
        mGoogleApiClient.disconnect();
    }
}


