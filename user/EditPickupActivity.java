package com.aimyplus.consumer.activity.user;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.aimyplus.consumer.R;
import com.aimyplus.consumer.app.GlobalApplication;
import com.aimyplus.consumer.base.BaseResponse;
import com.aimyplus.consumer.model.Constant.ContactTypeId;
import com.aimyplus.consumer.model.user.ConditionModel;
import com.aimyplus.consumer.model.user.ContactModel;
import com.aimyplus.consumer.ui.CircularNetworkImageView;
import com.aimyplus.consumer.utils.ConnUtil;
import com.android.volley.toolbox.ImageLoader;

public class EditPickupActivity extends AppCompatActivity {

    private static final String TAG = EditPickupActivity.class.getSimpleName();
    public String PARAM_KEY_MODEL = "Model";

    private ImageLoader imageLoader;
    MenuItem btnSave;
    CircularNetworkImageView profilePhoto;
    TextView tvName;
    TextView tvType;
    TextView tvNote;
    ToggleButton btnAuth;
    ToggleButton btnActive;
    EditText editRelationship;
    EditText editNote;
    LinearLayout layoutActive;
    LinearLayout layoutAuth;
    LinearLayout layoutRelation;
    LinearLayout layoutNote;
    ContactModel contactModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pickup);

        initialView();
        initActionBar();
        setOnTextChangeListen();
    }

    private void initActionBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(GlobalApplication.getResString(R.string.manage_pickup));
        }
    }

    private void initialView(){
        profilePhoto =(CircularNetworkImageView) findViewById(R.id.profilePhoto);
        tvName = (TextView) findViewById(R.id.tvName);
        tvType = (TextView) findViewById(R.id.tvType);
        tvNote = (TextView) findViewById(R.id.tvNote);
        btnAuth = (ToggleButton) findViewById(R.id.btnAuth);
        btnActive = (ToggleButton) findViewById(R.id.btnActive);
        editRelationship = (EditText) findViewById(R.id.editRelationship);
        editNote = (EditText) findViewById(R.id.editNote);
        layoutActive = (LinearLayout) findViewById(R.id.layoutActive);
        layoutAuth = (LinearLayout)findViewById(R.id.layoutAuth);
        layoutRelation = (LinearLayout)findViewById(R.id.layoutRelation);
        layoutNote = (LinearLayout) findViewById(R.id.layoutNote);
        imageLoader = GlobalApplication.getInstance().getImageLoader();
        profilePhoto.setDefaultImageResId(R.drawable.ic_perm_identity_black_24dp);

        Intent intent = getIntent();
        contactModel = (ContactModel)intent.getSerializableExtra(PARAM_KEY_MODEL);

        profilePhoto.setImageUrl(contactModel.getImageUrl(), imageLoader);
        tvName.setText(contactModel.getFirstName() + " " + contactModel.getLastName());
        if (contactModel.getRelation() != null) {
            editRelationship.setText(contactModel.getRelation());
            editRelationship.requestFocus();
            editRelationship.setSelection(editRelationship.getText().length());
        }
        if (contactModel.getActionOnAppear() != null) {
            editNote.setText(contactModel.getActionOnAppear());
        }


        if (contactModel.getCanPickup() == null) {
            tvType.setText(" ");
        } else if(!contactModel.getCanPickup()) {
            tvType.setText(R.string.non_auth_pickup);
        } else {
            tvType.setText(R.string.auth_pickup);
        }

        if (contactModel.getCanPickup() == null) {
            layoutAuth.setVisibility(View.GONE);
            layoutNote.setVisibility(View.GONE);
            layoutRelation.setVisibility(View.GONE);
        } else {
            btnActive.setChecked(true);
            if (!contactModel.getCanPickup()) {
                btnAuth.setChecked(false);
                tvNote.setText(R.string.action_on_arrival);
            } else {
                btnAuth.setChecked(true);
                tvNote.setText(R.string.note);
            }
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
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_profile_save:
                btnSave.setEnabled(false);

                updatePickup();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updatePickup() {

        if (!btnActive.isChecked()){
            contactModel.setCanPickup(null);
        } else if (btnAuth.isChecked()) {
            contactModel.setCanPickup(true);
        } else {
            contactModel.setCanPickup(false);
        }

        contactModel.setRelation(editRelationship.getText().toString());
        contactModel.setActionOnAppear(editNote.getText().toString());

        ConnUtil.postContact(TAG, contactModel, new BaseResponse<ContactModel>(this) {

            @Override
            public void onSuccess(Object sender, ContactModel response) {
                super.onSuccess(sender, response);
                Toast.makeText(EditPickupActivity.this, "pass pickup model success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Object sender, String response) {
                super.onFailure(sender, response);
                Toast.makeText(EditPickupActivity.this, "pass pickup model fail", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setOnTextChangeListen() {
        editNote.addTextChangedListener(new TextWatcher() {

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

        editRelationship.addTextChangedListener(new TextWatcher() {

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

        btnAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSave.setEnabled(true);
                if (btnAuth.isChecked()) {
                    tvNote.setText(R.string.note);
                } else {
                    tvNote.setText(R.string.action_on_arrival);
                }
            }
        });

        btnActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (layoutAuth.getVisibility() == View.GONE) {
                    layoutAuth.setVisibility(View.VISIBLE);
                    layoutNote.setVisibility(View.VISIBLE);
                    layoutRelation.setVisibility(View.VISIBLE);
                } else {
                    layoutAuth.setVisibility(View.GONE);
                    layoutNote.setVisibility(View.GONE);
                    layoutRelation.setVisibility(View.GONE);
                }

                btnSave.setEnabled(true);
            }
        });
    }
}
