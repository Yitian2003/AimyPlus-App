// modified by Yitian Jan 21, 2017
package com.aimyplus.consumer.activity.user;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aimyplus.consumer.Manifest;
import com.aimyplus.consumer.R;
import com.aimyplus.consumer.app.GlobalApplication;
import com.aimyplus.consumer.base.BaseResponse;
import com.aimyplus.consumer.model.user.ChildModel;
import com.aimyplus.consumer.ui.CircularNetworkImageView;
import com.aimyplus.consumer.utils.ConnUtil;
import com.aimyplus.consumer.utils.DateTimeUtil;
import com.aimyplus.photopicker.PhotoPicker;
import com.aimyplus.photopicker.PhotoPreview;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.List;

public class ChildProfileActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String PARAM_KEY_MODEL = "Model";
    public static final String PARAM_KEY_CHILDE_ID = "Child Id";
    public static final String PARAM_VALUE_FIELD = "EditString";

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 329;
    private int REQUEST_CODE_FOR_PHOTOPICKER = 455;
    private int REQUEST_CODE_FOR_DATEPICKER = 456;

    private static final String TAG = ChildProfileActivity.class.getSimpleName();
    private int id = -1;
    String dateOfBirthString;

    ImageLoader imageLoader;
    CircularNetworkImageView ivProfileImage;

    EditText editKnownName;
    EditText editFirstName;
    EditText editLastName;
    TextView tvDoB;
    TextView tvGender;

    MenuItem btnDone;

    private ArrayList<String> selectedPhoto = new ArrayList<>();
    ChildModel childModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_profile);

        editKnownName    = (EditText) findViewById(R.id.editKnownName);
        editFirstName    = (EditText) findViewById(R.id.editFirstName);
        editLastName     = (EditText) findViewById(R.id.editLastName);
        tvDoB          = (TextView) findViewById(R.id.tvDateOfBirth);
        tvGender       = (TextView) findViewById(R.id.tvGender);

        ivProfileImage = (CircularNetworkImageView) findViewById(R.id.ivProfileImage);
        imageLoader    = GlobalApplication.getInstance().getImageLoader();

        Intent intent = getIntent();
        if (intent != null) {
            childModel = new ChildModel();
            childModel = (ChildModel) intent.getSerializableExtra(PARAM_KEY_MODEL);
            if (childModel != null) {
                id = childModel.getId();
                initialView();
            }
            else {
                initialNewChildView();
            }
        }

        /*if (childModel != null) {
            initialView();

        } else {
            initialNewChildView();
        }*/
        setOnListener();
        setTextChange();
    }

    private void initialNewChildView()
    {
        childModel = new ChildModel();
        initialAppBar(getString(R.string.new_child));
        // hide photo holder
        findViewById(R.id.layoutProfileImage).setVisibility(View.GONE);
        findViewById(R.id.divider).setVisibility(View.GONE);
        findViewById(R.id.btnMedicalCondition).setVisibility((View.GONE));
    }

    private void initialView() {

        initialAppBar(childModel.getFirstName());

        ivProfileImage.setDefaultImageResId(R.drawable.ic_action_profile);
        ivProfileImage.setImageUrl(childModel.getImageUrl(), imageLoader);

        editKnownName.setText(childModel.getKnownName());
        editLastName.setText(childModel.getLastName());
        editFirstName.setText(childModel.getFirstName());
        tvDoB.setText(childModel.getDateOfBirth());
        tvGender.setText(childModel.getGenderString());
        dateOfBirthString = childModel.getDateOfBirth();

        editLastName.requestFocus();
        editLastName.setSelection(editLastName.getText().length());
    }

    private void initialAppBar(String title) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_done:
                if (isEditTextValidate(R.id.editFirstName)&& isEditTextValidate(R.id.editLastName)
                        && isEditTextValidate(R.id.editKnownName)) {
                    if (id < 0) {
                        // this is new profile activity
                        //pass child model and create a new child id
                        childModel.setFirstName(editFirstName.getText().toString());
                        childModel.setLastName(editLastName.getText().toString());
                        childModel.setKnownName(editKnownName.getText().toString());

                        updateChildProfile();
                        finish();
                        Intent intent = new Intent(ChildProfileActivity.this, ChooseConditionPhotoActivity.class);
                        startActivity(intent);

                    } else {
                        // this is edit profile activity
                        // passing child model
                        childModel.setFirstName(editFirstName.getText().toString());
                        childModel.setLastName(editLastName.getText().toString());
                        childModel.setKnownName(editKnownName.getText().toString());

                        updateChildProfile();
                        finish();
                    }

                } else {
                    btnDone.setEnabled(false);
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        final Intent intent = new Intent(ChildProfileActivity.this, EditChildProfileItemActivity.class);

        switch (v.getId()){

            case R.id.layoutProfileImage:
                checkPermission();  // dynamically check the user permission to the access of the external storage
                PhotoPicker.builder()
                        .setPhotoCount(1)
                        .setShowCamera(true)
                        .setSelected(selectedPhoto) // store the selected in the selected photo list
                        .start(this, REQUEST_CODE_FOR_PHOTOPICKER);
                btnDone.setEnabled(true);
                break;

            case R.id.layoutGender:
                AlertDialog.Builder dialogBuilder =  new AlertDialog.Builder(this);
                dialogBuilder.setTitle(R.string.gender);

                dialogBuilder.setSingleChoiceItems(R.array.genders, childModel.getGenderId()-13, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            childModel.setGenderId(13);
                            tvGender.setText(R.string.gender_male);
                        } else if(which == 1) {
                            childModel.setGenderId(14);
                            tvGender.setText(R.string.gender_female);
                        } else {
                            childModel.setGenderId(0);
                            tvGender.setText(" ");
                        }
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();
                btnDone.setEnabled(true);
                break;

            case R.id.layoutDateOfBirth:
                intent.putExtra("FieldName", "Date Of Birth");
                intent.putExtra(PARAM_VALUE_FIELD, dateOfBirthString);
                intent.putExtra(PARAM_KEY_MODEL, childModel);
                startActivityForResult(intent, REQUEST_CODE_FOR_DATEPICKER);
                btnDone.setEnabled(true);
                break;

            case R.id.btnMedicalCondition:
                if (btnDone.isEnabled()) {
                    // alert user save profile first
                } else {
                    Intent intentCondtion = new Intent(ChildProfileActivity.this, ChildConditionActivity.class);
                    intentCondtion.putExtra("Child Id", id);
                    startActivity(intentCondtion);
                }
                break;

            default:
                break;
        }
    }

    private void updateChildProfile() {
        ConnUtil.postChildProfile(TAG, childModel, new BaseResponse<ChildModel>(this) {

            @Override
            public void onSuccess(Object sender, ChildModel response) {
                super.onSuccess(sender, response);
                Toast.makeText(ChildProfileActivity.this, "pass child model success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Object sender, String response) {
                super.onFailure(sender, response);
                Toast.makeText(ChildProfileActivity.this, "pass child model fail", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setTextChange() {
        editFirstName.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (btnDone != null) {
                    btnDone.setEnabled(true);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        editLastName.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (btnDone != null) {
                    btnDone.setEnabled(true);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        editKnownName.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (btnDone != null) {
                    btnDone.setEnabled(true);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setOnListener() {
        this.findViewById(R.id.layoutProfileImage).setOnClickListener(this);
        this.findViewById(R.id.layoutDateOfBirth).setOnClickListener(this);
        this.findViewById(R.id.layoutGender).setOnClickListener(this);
        this.findViewById(R.id.btnMedicalCondition).setOnClickListener(this);
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

            Bitmap bmImg = BitmapFactory.decodeFile(selectedPhoto.get(0));
            ivProfileImage.setImageBitmap(bmImg);
            //ivProfileImage.setImageURI(Uri.parse("file://" + selectedPhoto.get(0)));//, imageLoader);
        }

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_FOR_DATEPICKER) {
            String dateOfBirth = DateTimeUtil.getLocalDateString(GlobalApplication.getContext(), data.getStringExtra(PARAM_VALUE_FIELD));
            childModel.setDateOfBirth(dateOfBirth);
            tvDoB.setText(dateOfBirth);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.done_menu, menu);
        btnDone = menu.findItem(R.id.action_done);
        btnDone.setEnabled(false);
        return true;
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

    private boolean isEditTextValidate(int editTextId){
        switch (editTextId) {
            case R.id.editFirstName:
                if (!isFirstNameValid(editFirstName.getText().toString())) {
                    editFirstName.requestFocus();
                    editFirstName.setError(getString(R.string.check_valid_first_name));
                    return false;
                } else {
                    return true;
                }
            case R.id.editLastName:
                if (!isLastNameValid(editLastName.getText().toString())) {
                    editLastName.requestFocus();
                    editLastName.setError(getString(R.string.check_valid_last_name));
                    return false;
                } else {
                    return true;
                }
            default:
                return true;
        }
    }

    private boolean isFirstNameValid(String firstName) {
        return firstName.length() >= 2;
    }

    private boolean isLastNameValid(String lastName) {
        return lastName.length() >= 2;
    }
}
