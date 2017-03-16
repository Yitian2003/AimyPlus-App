// create by Yitian Jan 29, 2017

package com.aimyplus.consumer.activity.user;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aimyplus.consumer.Manifest;
import com.aimyplus.consumer.R;
import com.aimyplus.consumer.app.GlobalApplication;
import com.aimyplus.consumer.base.BaseResponse;
import com.aimyplus.consumer.model.user.ChildModel;
import com.aimyplus.consumer.model.user.ContactModel;
import com.aimyplus.consumer.utils.ConnUtil;
import com.aimyplus.photopicker.PhotoPicker;
import com.aimyplus.photopicker.PhotoPreview;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.List;

public class EditContactActivity extends AppCompatActivity {

    public static final String PARAM_KEY_MODEL = "Model";
    public static final String PARAM_KEY_TYPE = "Type";
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 329;
    private int REQUEST_CODE_FOR_PHOTOPICKER = 455;

    private static final String TAG = EditContactActivity.class.getSimpleName();
    private ArrayList<String> selectedPhoto = new ArrayList<>();
    private int id;
    MenuItem btnSave;
    ContactModel contact;
    private ImageLoader imageLoader;
    private int typeId;

    NetworkImageView profilePhoto;
    TextView tvType;
    EditText editFirstName;
    EditText editLastName;
    EditText editMobileCountryCode;
    EditText editMobile;
    EditText editLandlineCountryCode;
    EditText editLandline;
    EditText editOfficeCountryCode;
    EditText editOffice;
    LinearLayout layoutProfileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);

        Intent intent = getIntent();
        contact = (ContactModel) intent.getSerializableExtra(PARAM_KEY_MODEL);
        if (contact == null) {
            typeId = intent.getIntExtra(PARAM_KEY_TYPE, 0);
        } else {
            typeId = contact.getTypeId();
        }

        profilePhoto = (NetworkImageView) findViewById(R.id.profilePhoto);
        tvType = (TextView) findViewById(R.id.tvType);
        editFirstName = (EditText) findViewById(R.id.editFirstName);
        editLastName = (EditText) findViewById(R.id.editLastName);
        editMobileCountryCode = (EditText) findViewById(R.id.editMobileCountryCode);
        editMobile = (EditText) findViewById(R.id.editMobile);
        editLandlineCountryCode = (EditText) findViewById(R.id.editLandlineCountryCode);
        editLandline = (EditText) findViewById(R.id.editLandline);
        editOfficeCountryCode = (EditText) findViewById(R.id.editOfficeCountryCode);
        editOffice = (EditText) findViewById(R.id.editOffice);
        imageLoader = GlobalApplication.getInstance().getImageLoader();
        layoutProfileImage = (LinearLayout) findViewById(R.id.layoutProfileImage);

        profilePhoto.setDefaultImageResId(R.drawable.ic_action_profile);

        if (contact == null) {
            contact = new ContactModel();
            initNewActionBar();
            initialNewView();
        } else {
            initActionBar();
            initialView();
        }

        setOnClickListen();
        setTextChangeListener();
    }

    private void initNewActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(GlobalApplication.getResString(R.string.new_contact));
        }
    }

    private void initialView() {

        profilePhoto.setImageUrl(contact.getImageUrl(), imageLoader);
        editFirstName.setText(contact.getFirstName());
        editLastName.setText(contact.getLastName());
        tvType.setText(contact.getTypeString());

        setupPhoneField(contact.getMobile(), editMobileCountryCode, editMobile);
        setupPhoneField(contact.getLandline(), editLandlineCountryCode, editLandline);
        setupPhoneField(contact.getOffice(), editOfficeCountryCode, editOffice);

        editFirstName.setSelection(editFirstName.getText().length());
    }

    private void initialNewView() {

        switch (typeId) {
            case 10:
                tvType.setText(R.string.SecondaryGuardian);
                break;
            case 11:
                tvType.setText(R.string.contact_emergency_contact_1);
                break;
            case 12:
                tvType.setText(R.string.contact_emergency_contact_2);
                break;
            default:
                tvType.setText(R.string.Pickup);
                break;
        }

        setupPhoneField(null, editMobileCountryCode, editMobile);
        setupPhoneField(null, editLandlineCountryCode, editLandline);
        setupPhoneField(null, editOfficeCountryCode, editOffice);
    }

    private void setOnClickListen() {
        layoutProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();  // dynamically check the user permission to the access of the external storage
                PhotoPicker.builder()
                        .setPhotoCount(1)
                        .setShowCamera(true)
                        .setSelected(selectedPhoto) // store the selected in the selected photo list
                        .start(EditContactActivity.this, REQUEST_CODE_FOR_PHOTOPICKER);
            }
        });
    }

    private void setupPhoneField(String phoneString, EditText editTextCode, EditText editTextPhone) {

        if (TextUtils.isEmpty(phoneString)) {
            editTextCode.setText(setCountryCode());
        } else {
            String[] parts = phoneString.split("-");
            if (parts.length == 1) {
                editTextCode.setText(parts[0].toCharArray(), 1, parts[0].length() - 1);
            } else if (parts.length == 2) {
                editTextPhone.setText(parts[1]);
                editTextCode.setText(parts[0].toCharArray(), 1, parts[0].length() - 1);
            }
        }
    }

    private void initActionBar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(GlobalApplication.getResString(R.string.edit_contact));
        }
    }

    private String setCountryCode() {
        String countryCodeString = "";
        String countryCodesNum = "";

        TelephonyManager tm = (TelephonyManager) this.getSystemService(this.TELEPHONY_SERVICE);
        countryCodeString = tm.getNetworkCountryIso().toUpperCase();
        if (countryCodeString.isEmpty()) {
            countryCodeString = getApplicationContext().getResources().getConfiguration().locale.getCountry();
        }

        String[] countryCodeArray = this.getResources().getStringArray(R.array.CountryCodes);
        for (int i = 0; i < countryCodeArray.length; i++) {
            String[] countryCodes = countryCodeArray[i].split(",");
            if (countryCodes[1].trim().equals(countryCodeString.trim())) {
                countryCodesNum = countryCodes[0];
                break;
            }
        }
        return countryCodesNum;
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
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.action_profile_save:
                btnSave.setEnabled(false);

                /*if (contact == null) {
                    contact.setChildId();
                }*/
                contact.setTypeId(typeId);
                contact.setFirstName(editFirstName.getText().toString());
                contact.setLastName(editLastName.getText().toString());
                contact.setMobile("+" + editMobileCountryCode.getText().toString()
                        + "-" + editMobile.getText().toString());
                contact.setLandline("+" + editLandlineCountryCode.getText().toString()
                        + "-" + editLandline.getText().toString());
                contact.setOffice("+" + editOfficeCountryCode.getText().toString()
                        + "-" + editOffice.getText().toString());

                //contact.setChildId(contact.getChildId());

                updateContact();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateContact() {
        ConnUtil.postContact(TAG, contact, new BaseResponse<ContactModel>(this) {

            @Override
            public void onSuccess(Object sender, ContactModel response) {
                super.onSuccess(sender, response);
                Toast.makeText(EditContactActivity.this, "pass contact model success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Object sender, String response) {
                super.onFailure(sender, response);
                Toast.makeText(EditContactActivity.this, "pass contact model fail", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
            }

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            return;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK &&
                (requestCode == REQUEST_CODE_FOR_PHOTOPICKER || requestCode == PhotoPreview.REQUEST_CODE)) {

            List<String> photos = null;
            if (data != null) {
                photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
            }
            selectedPhoto.clear();

            if (photos != null) {

                selectedPhoto.addAll(photos);
            }
            btnSave.setEnabled(true);

            Bitmap bmImg = BitmapFactory.decodeFile(selectedPhoto.get(0));
            profilePhoto.setImageBitmap(bmImg);
            //ivProfileImage.setImageURI(Uri.parse("file://" + selectedPhoto.get(0)));//, imageLoader);
        }
    }

    private void setTextChangeListener() {
        editFirstName.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (btnSave != null) {
                    btnSave.setEnabled(true);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        editLastName.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (btnSave != null) {
                    btnSave.setEnabled(true);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        editMobileCountryCode.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (btnSave != null) {
                    btnSave.setEnabled(true);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
        editMobile.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (btnSave != null) {
                    btnSave.setEnabled(true);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        editLandlineCountryCode.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (btnSave != null) {
                    btnSave.setEnabled(true);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        editLandline.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (btnSave != null) {
                    btnSave.setEnabled(true);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        editOffice.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (btnSave != null) {
                    btnSave.setEnabled(true);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        editOfficeCountryCode.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (btnSave != null) {
                    btnSave.setEnabled(true);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
    }

}
