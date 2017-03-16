/**
 * Created by Yitian on 22/02/2017.
 */

package com.aimyplus.consumer.activity.user;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aimyplus.consumer.R;
import com.aimyplus.consumer.app.GlobalApplication;
import com.aimyplus.consumer.base.BaseResponse;
import com.aimyplus.consumer.model.Constant.ContactTypeId;
import com.aimyplus.consumer.model.user.ConditionModel;
import com.aimyplus.consumer.model.user.ContactModel;
import com.aimyplus.consumer.ui.CircularNetworkImageView;
import com.aimyplus.consumer.utils.ConnUtil;
import com.android.volley.toolbox.ImageLoader;

public class SecondaryParentActivity extends AppCompatActivity {

    private static final String TAG = SecondaryParentActivity.class.getSimpleName();
    public static final String PARAM_KEY_MODEL = "Model";
    public static final String PARAM_KEY_TYPE = "Type";

    ContactModel secondaryParentModel;
    ImageLoader imageLoader;
    CircularNetworkImageView profilePhoto;
    TextView tvName;
    TextView tvMobile;
    TextView tvLandline;
    TextView tvOffice;
    ImageButton btnEdit;
    ImageButton btnDelete;
    TextView tvAdd;
    LinearLayout layoutContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secondary_parent);

        initialView();
        initialAppBar();
        setOnClickListen();
    }

    @Override
    public void onRestart()
    {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

    private void setOnClickListen() {
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SecondaryParentActivity.this, EditContactActivity.class);
                intent.putExtra(PARAM_KEY_MODEL, secondaryParentModel);
                startActivity(intent);
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(SecondaryParentActivity.this)
                        .setTitle(R.string.delete_confirm)
                        .setMessage(R.string.delete_message)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                ConnUtil.DeleteSecondParent(TAG, secondaryParentModel, new BaseResponse<ContactModel>(SecondaryParentActivity.this) {

                                    @Override
                                    public void onSuccess(Object sender, ContactModel response) {
                                        super.onSuccess(sender, response);
                                        Toast.makeText(SecondaryParentActivity.this, "delete secondparent model success", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onFailure(Object sender, String response) {
                                        super.onFailure(sender, response);
                                        Toast.makeText(SecondaryParentActivity.this, "delete secondparent model fail", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                Intent intent = getIntent();
                                finish();
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setIcon(R.drawable.ic_delete_alert)
                        .show();
            }
        });
    }

    private void initialView(){

        secondaryParentModel = new ContactModel();
        profilePhoto = (CircularNetworkImageView) findViewById(R.id.profilePhoto);
        imageLoader = GlobalApplication.getInstance().getImageLoader();
        tvName = (TextView) findViewById(R.id.tvName);
        tvMobile = (TextView) findViewById(R.id.tvMobile);
        tvLandline = (TextView) findViewById(R.id.tvLandline);
        tvOffice = (TextView) findViewById(R.id.tvOffice);
        btnDelete = (ImageButton)findViewById(R.id.btnDelete);
        btnEdit = (ImageButton) findViewById(R.id.btnEdit);
        tvAdd = (TextView) findViewById(R.id.tvAdd);
        layoutContact = (LinearLayout) findViewById(R.id.layoutContact);

        ConnUtil.GetContact(TAG, new BaseResponse<ContactModel[]>(this) {

            @Override
            public void onSuccess(Object sender, ContactModel[] models) {
                super.onSuccess(sender, models);

                for (int i = 0; i < models.length; i++) {
                    if (models[i].getTypeId() == ContactTypeId.SecondaryGuardian.getValue()) {
                        secondaryParentModel = models[i];
                    }
                }
                if (secondaryParentModel.getId() <= 0) {
                    tvAdd.setVisibility(View.VISIBLE);
                    layoutContact.setVisibility(View.GONE);

                    tvAdd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(SecondaryParentActivity.this, EditContactActivity.class);
                            intent.putExtra(PARAM_KEY_TYPE, ContactTypeId.SecondaryGuardian.getValue());
                            startActivity(intent);
                        }
                    });
                } else {
                    profilePhoto.setImageUrl(secondaryParentModel.getImageUrl(), imageLoader);
                    tvName.setText(secondaryParentModel.getFirstName() + " " + secondaryParentModel.getLastName());
                    tvMobile.setText(secondaryParentModel.getMobile());
                    tvLandline.setText(secondaryParentModel.getLandline());
                    tvOffice.setText(secondaryParentModel.getOffice());
                }

            }

            @Override
            public void onFailure(Object sender, String response) {
                super.onFailure(sender, response);

                tvAdd.setVisibility(View.VISIBLE);
                layoutContact.setVisibility(View.GONE);

                tvAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(SecondaryParentActivity.this, EditContactActivity.class);
                        intent.putExtra(PARAM_KEY_TYPE, ContactTypeId.SecondaryGuardian.getValue());
                        startActivity(intent);
                    }
                });
            }
        });
    }

    private void initialAppBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(GlobalApplication.getResString(R.string.secondary_parent));
        }
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
}
