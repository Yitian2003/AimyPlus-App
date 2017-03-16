// modified by Yitian Jan 16, 2017
package com.aimyplus.consumer.activity.user;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.content.Intent;
import android.app.AlertDialog;
import android.database.Cursor;
import android.provider.MediaStore;
import android.widget.Toast;


import com.aimyplus.consumer.R;
import com.aimyplus.consumer.app.GlobalApplication;
import com.aimyplus.consumer.base.BaseResponse;
import com.aimyplus.consumer.model.user.UserProfileModel;
import com.aimyplus.consumer.utils.ConnUtil;
import com.aimyplus.photopicker.PhotoPreview;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.aimyplus.photopicker.PhotoPicker;

import java.util.ArrayList;
import java.util.List;

public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = UserProfileActivity.class.getSimpleName();
    public static final String FIELD_FIRST_NAME = "Name";
    public static final String FIELD_LAST_NAME = "Last Name";
    public static final String FIELD_DATE_OF_BIRTH = "Date Of Birth";
    public static final String FIELD_GENDER = "Gender";
    public static final String FIELD_MOBILE = "Mobile";
    public static final String FIELD_LAND_LINE = "Landline";
    public static final String FIELD_EMAIL = "Email";
    public static final String FIELD_ADDRESS = "Address";
    public static final String FIELD_BILLING_ADDRESS = "Billing Address";
    public static final String FIELD_OSCAR = "Oscar";
    public static final String PARAM_KEY_MODEL = "Model";
    public static final String PARAM_KEY_FIELD ="FieldName";
    public static final String PARAM_VALUE_FIELD = "EditString";

    //final CharSequence[] genders = { "Male", "Female"};
    private ArrayList<String> selectedPhoto = new ArrayList<>();
    NetworkImageView ivProfileImage;
    ImageLoader imageLoader;

    UserProfileModel passingModel;

    TextView tvUsername;
    TextView tvFirstName;
    TextView tvLastName;
    TextView tvDoB;
    TextView tvGender;
    TextView tvMobile;
    TextView tvLandline;
    TextView tvEmail;
    TextView tvAddress;
    TextView tvBillingAddress;
    TextView tvOscar;

    String dateOfBirthString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        initActionBar();
        initView();

        setOnListener();
    }

    @Override
    public void onRestart()
    {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

    private void initView() {
        tvUsername       = (TextView) findViewById(R.id.tvUsername);
        tvFirstName      = (TextView) findViewById(R.id.tvFirstName);
        tvLastName       = (TextView) findViewById(R.id.tvLastName);
        tvDoB            = (TextView) findViewById(R.id.tvDateOfBirth);
        tvGender         = (TextView) findViewById(R.id.tvGender);
        tvMobile         = (TextView) findViewById(R.id.tvMobile);
        tvLandline       = (TextView) findViewById(R.id.tvLandline);
        tvEmail          = (TextView) findViewById(R.id.tvEmail);
        tvOscar          = (TextView) findViewById(R.id.tvOscar);
        tvAddress        = (TextView) findViewById(R.id.tvAddress);
        tvBillingAddress = (TextView) findViewById(R.id.tvBillingAddress);

        ivProfileImage   = (NetworkImageView) findViewById(R.id.ivProfileImage);
        imageLoader      = GlobalApplication.getInstance().getImageLoader();

        int userId = GlobalApplication.getInstance().getCurrentUser().getId();

        ConnUtil.getUserProfile(TAG, userId, new BaseResponse<UserProfileModel> (this) {

            @Override
            public void onSuccess(Object sender, UserProfileModel model) {
                super.onSuccess(sender, model);

                ivProfileImage.setDefaultImageResId(R.drawable.ic_action_profile);
                ivProfileImage.setImageUrl(model.getImage(), imageLoader);

                tvUsername.setText(model.getUsername());
                tvLastName.setText(model.getLastName());
                tvFirstName.setText(model.getFirstName());
                tvDoB.setText(model.getDateOfBirth());
                tvGender.setText(model.getGenderString());
                tvMobile.setText(model.getMobile());
                tvLandline.setText(model.getLandline());
                tvEmail.setText(model.getEmail());
                tvOscar.setText(model.getOscarNum());
                tvAddress.setText(model.getFullAddress());
                tvBillingAddress.setText(model.getFullBillingAddress());
                dateOfBirthString = model.getDateOfBirth();

                passingModel = new UserProfileModel();
                passingModel = model;
            }
        });
    }

    private void initActionBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(GlobalApplication.getResString(R.string.profile));
        }
    }

    @Override
    public void onClick(View v) {
        final Intent intent = new Intent(UserProfileActivity.this, EditProfileItemActivity.class);

        switch (v.getId()){
            case R.id.layoutProfileImage:
                PhotoPicker.builder()
                        .setPhotoCount(1)
                        .setShowCamera(true)
                        .setSelected(selectedPhoto) // store the selected in the selected photo list
                        .start(this);
                break;
            case R.id.layoutGender:
                AlertDialog.Builder dialogBuilder =  new AlertDialog.Builder(this);
                dialogBuilder.setTitle(R.string.gender);

                dialogBuilder.setSingleChoiceItems(R.array.genders, passingModel.getGenderId()-13, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            passingModel.setGenderId(13);
                            tvGender.setText(R.string.gender_male);
                        } else if (which == 1){
                            passingModel.setGenderId(14);
                            tvGender.setText(R.string.gender_female);
                        } else {
                            passingModel.setGenderId(0);
                            tvGender.setText(" ");
                        }
                        dialog.dismiss();
                        updateProfileGender();
                    }
                });
                AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();

                break;
            case R.id.layoutDateOfBirth:
                intent.putExtra(PARAM_KEY_FIELD, FIELD_DATE_OF_BIRTH);
                intent.putExtra(PARAM_VALUE_FIELD, tvDoB.getText().toString());
                intent.putExtra(PARAM_KEY_MODEL, passingModel);
                //this.finish();
                startActivity(intent);
                break;
            case R.id.layoutFirstName:
                intent.putExtra(PARAM_KEY_FIELD, FIELD_FIRST_NAME);
                intent.putExtra(PARAM_VALUE_FIELD, tvFirstName.getText().toString());
                intent.putExtra(PARAM_KEY_MODEL, passingModel);
                //this.finish();
                startActivity(intent);
                break;
            case R.id.layoutLastName:
                intent.putExtra(PARAM_KEY_FIELD, FIELD_LAST_NAME);
                intent.putExtra(PARAM_VALUE_FIELD, tvLastName.getText().toString());
                intent.putExtra(PARAM_KEY_MODEL, passingModel);
                //this.finish();
                startActivity(intent);
                break;
            case R.id.layoutEmail:
                intent.putExtra(PARAM_KEY_FIELD, FIELD_EMAIL);
                intent.putExtra(PARAM_VALUE_FIELD, tvEmail.getText().toString());
                intent.putExtra(PARAM_KEY_MODEL, passingModel);
                //this.finish();
                startActivity(intent);
                break;
            case R.id.layoutMobile:
                intent.putExtra(PARAM_KEY_FIELD, FIELD_MOBILE);
                intent.putExtra(PARAM_VALUE_FIELD, tvMobile.getText().toString());
                intent.putExtra(PARAM_KEY_MODEL, passingModel);
                //this.finish();
                startActivity(intent);
                break;
            case R.id.layoutLandline:
                intent.putExtra(PARAM_KEY_FIELD, FIELD_LAND_LINE);
                intent.putExtra(PARAM_VALUE_FIELD, tvLandline.getText().toString());
                intent.putExtra(PARAM_KEY_MODEL, passingModel);
                //this.finish();
                startActivity(intent);
                break;
            case R.id.layoutAddress:
                intent.putExtra(PARAM_KEY_FIELD, FIELD_ADDRESS);
                intent.putExtra(PARAM_VALUE_FIELD, tvAddress.getText().toString());
                intent.putExtra(PARAM_KEY_MODEL, passingModel);
                //this.finish();
                startActivity(intent);
                break;
            case R.id.layoutBillingAddress:
                intent.putExtra(PARAM_KEY_FIELD, FIELD_BILLING_ADDRESS);
                intent.putExtra(PARAM_VALUE_FIELD, tvBillingAddress.getText().toString());
                intent.putExtra(PARAM_KEY_MODEL, passingModel);
                //this.finish();
                startActivity(intent);
                break;
            case R.id.layoutOscar:
                intent.putExtra(PARAM_KEY_FIELD, FIELD_OSCAR);
                intent.putExtra(PARAM_VALUE_FIELD, tvOscar.getText().toString());
                intent.putExtra(PARAM_KEY_MODEL, passingModel);
                //this.finish();
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private void updateProfileGender() {
        ConnUtil.postUserProfile(TAG, passingModel, new BaseResponse<UserProfileModel>(this) {

            @Override
            public void onSuccess(Object sender, UserProfileModel response) {
                super.onSuccess(sender, response);
                Toast.makeText(UserProfileActivity.this, "update gender success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Object sender, String response) {
                super.onFailure(sender, response);
                Toast.makeText(UserProfileActivity.this, "update gender fail", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setOnListener() {
        this.findViewById(R.id.layoutProfileImage).setOnClickListener(this);
        this.findViewById(R.id.layoutLastName).setOnClickListener(this);
        this.findViewById(R.id.layoutFirstName).setOnClickListener(this);
        this.findViewById(R.id.layoutDateOfBirth).setOnClickListener(this);
        this.findViewById(R.id.layoutGender).setOnClickListener(this);
        this.findViewById(R.id.layoutEmail).setOnClickListener(this);
        this.findViewById(R.id.layoutMobile).setOnClickListener(this);
        this.findViewById(R.id.layoutLandline).setOnClickListener(this);
        this.findViewById(R.id.layoutAddress).setOnClickListener(this);
        this.findViewById(R.id.layoutBillingAddress).setOnClickListener(this);
        this.findViewById(R.id.layoutOscar).setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK &&
                (requestCode == PhotoPicker.REQUEST_CODE || requestCode == PhotoPreview.REQUEST_CODE)) {

            List<String> photos = null;
            if (data != null) {
                photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
            }
            selectedPhoto.clear();

            if (photos != null) {

                selectedPhoto.addAll(photos);
            }
            ivProfileImage.setImageUrl(selectedPhoto.get(0), imageLoader);
        }

    }
}
