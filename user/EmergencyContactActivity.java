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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aimyplus.consumer.R;
import com.aimyplus.consumer.app.GlobalApplication;
import com.aimyplus.consumer.base.BaseResponse;
import com.aimyplus.consumer.model.Constant.ContactTypeId;
import com.aimyplus.consumer.model.user.ContactModel;
import com.aimyplus.consumer.ui.CircularNetworkImageView;
import com.aimyplus.consumer.utils.ConnUtil;
import com.android.volley.toolbox.ImageLoader;

public class EmergencyContactActivity extends AppCompatActivity {

    private static final String TAG = SecondaryParentActivity.class.getSimpleName();
    public static final String PARAM_KEY_MODEL = "Model";
    public static final String PARAM_KEY_TYPE = "Type";

    ContactModel emergencyContactModel1;
    ContactModel emergencyContactModel2;
    ImageLoader imageLoader;
    CircularNetworkImageView profilePhoto1;
    CircularNetworkImageView profilePhoto2;
    TextView tvName1;
    TextView tvMobile1;
    TextView tvLandline1;
    TextView tvOffice1;
    TextView tvName2;
    TextView tvMobile2;
    TextView tvLandline2;
    TextView tvOffice2;
    TextView tvAdd1;
    TextView tvAdd2;
    ImageButton btnEdit1, btnEdit2;
    ImageButton btnDelete1, btnDelete2;
    LinearLayout layoutContact1, layoutContact2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contact);

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

    private void initialView() {

        emergencyContactModel1 = new ContactModel();
        imageLoader = GlobalApplication.getInstance().getImageLoader();
        profilePhoto1 = (CircularNetworkImageView) findViewById(R.id.profilePhoto1);
        tvName1 = (TextView) findViewById(R.id.tvName1);
        tvMobile1 = (TextView) findViewById(R.id.tvMobile1);
        tvLandline1 = (TextView) findViewById(R.id.tvLandline1);
        tvOffice1 = (TextView) findViewById(R.id.tvOffice1);
        btnDelete1 = (ImageButton) findViewById(R.id.btnDelete1);
        btnEdit1 = (ImageButton) findViewById(R.id.btnEdit1);
        tvAdd1 = (TextView) findViewById(R.id.tvAdd1);
        layoutContact1 = (LinearLayout) findViewById(R.id.layoutContact1);

        emergencyContactModel2 = new ContactModel();
        profilePhoto2 = (CircularNetworkImageView) findViewById(R.id.profilePhoto2);
        tvName2 = (TextView) findViewById(R.id.tvName2);
        tvMobile2 = (TextView) findViewById(R.id.tvMobile2);
        tvLandline2 = (TextView) findViewById(R.id.tvLandline2);
        tvOffice2 = (TextView) findViewById(R.id.tvOffice2);
        btnDelete2 = (ImageButton) findViewById(R.id.btnDelete2);
        btnEdit2 = (ImageButton) findViewById(R.id.btnEdit2);
        tvAdd2 = (TextView) findViewById(R.id.tvAdd2);
        layoutContact2 = (LinearLayout) findViewById(R.id.layoutContact2);

        ConnUtil.GetContact(TAG, new BaseResponse<ContactModel[]>(this) {

            @Override
            public void onSuccess(Object sender, ContactModel[] models) {
                super.onSuccess(sender, models);

                for (int i = 0; i < models.length; i++) {
                    if (models[i].getTypeId() == ContactTypeId.EmergencyContact1.getValue()) {
                        profilePhoto1.setImageUrl(models[i].getImageUrl(), imageLoader);
                        tvName1.setText(models[i].getFirstName() + " " + models[i].getLastName());
                        tvMobile1.setText(models[i].getMobile());
                        tvLandline1.setText(models[i].getLandline());
                        tvOffice1.setText(models[i].getOffice());
                        emergencyContactModel1 = models[i];
                    }
                    if (models[i].getTypeId() == ContactTypeId.EmergencyContact2.getValue()) {
                        profilePhoto2.setImageUrl(models[i].getImageUrl(), imageLoader);
                        tvName2.setText(models[i].getFirstName() + " " + models[i].getLastName());
                        tvMobile2.setText(models[i].getMobile());
                        tvLandline2.setText(models[i].getLandline());
                        tvOffice2.setText(models[i].getOffice());
                        emergencyContactModel2 = models[i];
                    }
                }

                if (emergencyContactModel1.getId() <= 0) {
                    tvAdd1.setVisibility(View.VISIBLE);
                    layoutContact1.setVisibility(View.GONE);

                    tvAdd1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(EmergencyContactActivity.this, EditContactActivity.class);
                            intent.putExtra(PARAM_KEY_TYPE, ContactTypeId.EmergencyContact1.getValue());
                            startActivity(intent);
                        }
                    });
                }

                if (emergencyContactModel2.getId() <= 0) {
                    tvAdd2.setVisibility(View.VISIBLE);
                    layoutContact2.setVisibility(View.GONE);

                    tvAdd2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(EmergencyContactActivity.this, EditContactActivity.class);
                            intent.putExtra(PARAM_KEY_TYPE, ContactTypeId.EmergencyContact2.getValue());
                            startActivity(intent);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Object sender, String response) {
                super.onFailure(sender, response);

                tvAdd1.setVisibility(View.VISIBLE);
                layoutContact1.setVisibility(View.GONE);
                tvAdd2.setVisibility(View.VISIBLE);
                layoutContact2.setVisibility(View.GONE);

                tvAdd1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(EmergencyContactActivity.this, EditContactActivity.class);
                        intent.putExtra(PARAM_KEY_TYPE, ContactTypeId.EmergencyContact1.getValue());
                        startActivity(intent);
                    }
                });

                tvAdd2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(EmergencyContactActivity.this, EditContactActivity.class);
                        intent.putExtra(PARAM_KEY_TYPE, ContactTypeId.EmergencyContact2.getValue());
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
            getSupportActionBar().setTitle(GlobalApplication.getResString(R.string.emergency_contact));
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

    private void setOnClickListen() {

        btnEdit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EmergencyContactActivity.this, EditContactActivity.class);
                intent.putExtra(PARAM_KEY_MODEL, emergencyContactModel1);
                startActivity(intent);
            }
        });

        btnDelete1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(EmergencyContactActivity.this)
                        .setTitle(R.string.delete_confirm)
                        .setMessage(R.string.delete_message)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                ConnUtil.DeleteFirstEmergency(TAG, emergencyContactModel1, new BaseResponse<ContactModel>(EmergencyContactActivity.this) {

                                    @Override
                                    public void onSuccess(Object sender, ContactModel response) {
                                        super.onSuccess(sender, response);
                                        Toast.makeText(EmergencyContactActivity.this, "delete FirstEmergency model success", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onFailure(Object sender, String response) {
                                        super.onFailure(sender, response);
                                        Toast.makeText(EmergencyContactActivity.this, "delete FirstEmergency model fail", Toast.LENGTH_SHORT).show();
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

        btnEdit2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EmergencyContactActivity.this, EditContactActivity.class);
                intent.putExtra(PARAM_KEY_MODEL, emergencyContactModel2);
                startActivity(intent);
            }
        });

        btnDelete2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(EmergencyContactActivity.this)
                        .setTitle(R.string.delete_confirm)
                        .setMessage(R.string.delete_message)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                ConnUtil.DeleteSecondEmergency(TAG, emergencyContactModel2, new BaseResponse<ContactModel>(EmergencyContactActivity.this) {

                                    @Override
                                    public void onSuccess(Object sender, ContactModel response) {
                                        super.onSuccess(sender, response);
                                        Toast.makeText(EmergencyContactActivity.this, "delete SecondEmergency model success", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onFailure(Object sender, String response) {
                                        super.onFailure(sender, response);
                                        Toast.makeText(EmergencyContactActivity.this, "delete SecondEmergency model fail", Toast.LENGTH_SHORT).show();
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
}
