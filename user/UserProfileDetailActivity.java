package com.aimyplus.consumer.activity.user;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aimyplus.consumer.R;
import com.aimyplus.consumer.base.BaseResponse;
import com.aimyplus.consumer.model.user.UserProfileModel;
import com.aimyplus.consumer.utils.ConnUtil;
import com.aimyplus.consumer.utils.ContactUtil;
import com.aimyplus.consumer.utils.UIUtil;
import com.bumptech.glide.Glide;

/**
 * Created by SoK on 3/11/2016.
 */

public class UserProfileDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = UserProfileDetailActivity.class.getSimpleName();

    public static final String ID_TAG = "USER_PROFILE_TAG";

    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;

    private RelativeLayout landLineLayout;
    private RelativeLayout mobileLayout;
    private RelativeLayout emailLayout;
    private RelativeLayout layoutAddress;

    private TextView userLandline;

    private TextView userMobile;
    private TextView userEmail;
    private TextView userAddress;
    private ImageView profileImage;

    private ImageView userMobileImage;
    private ImageView userMobileMessageImage;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.user_profile_detail_activity);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        toolbar = (Toolbar) findViewById(R.id.user_profile_detail_toolbar);

        landLineLayout = (RelativeLayout) findViewById(R.id.layoutHomeline);
        mobileLayout = (RelativeLayout) findViewById(R.id.layoutMobile);
        emailLayout = (RelativeLayout) findViewById(R.id.layoutEmail);
        layoutAddress = (RelativeLayout) findViewById(R.id.layoutAddress);

        userLandline = (TextView) findViewById(R.id.userLandline);
        userMobile = (TextView) findViewById(R.id.userMobile);
        userEmail = (TextView) findViewById(R.id.userEmail);
        userAddress = (TextView) findViewById(R.id.userAddress);
        profileImage = (ImageView) findViewById(R.id.user_profile_image);

        userMobileImage = (ImageView) findViewById(R.id.userMobileImage);
        userMobileMessageImage = (ImageView) findViewById(R.id.userMobileMessageImage);

        setSupportActionBar(toolbar);


        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // get the current id
        int userId = getIntent().getIntExtra(ID_TAG,0);

        ConnUtil.getUserProfile(TAG, userId, new BaseResponse<UserProfileModel>(this,true) {
            @Override
            public void onSuccess(Object sender, UserProfileModel response) {
                super.onSuccess(sender, response);

                if (!TextUtils.isEmpty(response.getImageUrl())) {
                    Glide
                            .with(UserProfileDetailActivity.this)
                            .load("http://www.sheffield.com/wp-content/uploads/2013/06/placeholder.png")
                            .centerCrop()
                            .crossFade()
                            .into(profileImage);
                } else {
                    //if (response.getG)
                    profileImage.setImageDrawable(UIUtil.getDrawable(R.drawable.ic_male_placeholder));
                }

                // initialize home land line
                if (!TextUtils.isEmpty(response.getLandline())) {
                    userLandline.setText(response.getLandline());
                    landLineLayout.setOnClickListener(UserProfileDetailActivity.this);
                } else {
                    landLineLayout.setVisibility(View.GONE);
                }

                // initialize mobile
                if (!TextUtils.isEmpty(response.getMobile())) {
                    userMobile.setText(response.getMobile());
                    mobileLayout.setOnClickListener(UserProfileDetailActivity.this);
                    userMobileImage.setOnClickListener(UserProfileDetailActivity.this);
                    userMobileMessageImage.setOnClickListener(UserProfileDetailActivity.this);
                } else {
                    mobileLayout.setVisibility(View.GONE);
                }

                // initialize email
                if (!TextUtils.isEmpty(response.getEmail())) {
                    userEmail.setText(response.getEmail());
                    emailLayout.setOnClickListener(UserProfileDetailActivity.this);
                } else {
                    emailLayout.setVisibility(View.GONE);
                }

                if (!TextUtils.isEmpty(response.getFullAddress())) {
                    userAddress.setText(response.getFullAddress());
                    layoutAddress.setOnClickListener(UserProfileDetailActivity.this);
                } else {
                    layoutAddress.setVisibility(View.GONE);
                }

                collapsingToolbarLayout.setTitle(String.format("%s %s", response.getFirstName(), response.getLastName()));

            }

            @Override
            public void onFailure(Object sender, String response) {
                super.onFailure(sender, response);
            }
        });

        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);


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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layoutHomeline: {
                ContactUtil.Call(this,userLandline.getText().toString());
                break;
            }
            case R.id.layoutMobile: {
                ContactUtil.Call(this,userMobile.getText().toString());
                break;
            }
            case R.id.userMobileImage: {
                ContactUtil.Call(this,userMobile.getText().toString());
                break;
            }
            case R.id.userMobileMessageImage: {
                ContactUtil.Message(this,userMobile.getText().toString());
                break;
            }

            case R.id.layoutEmail: {
                ContactUtil.Email(this,userEmail.getText().toString());
                break;
            }

            case R.id.layoutAddress: {
                ContactUtil.ShowAddress(this,userAddress.getText().toString());
                break;
            }

        }
    }
}
