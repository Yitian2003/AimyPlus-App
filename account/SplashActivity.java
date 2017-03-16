package com.aimyplus.consumer.activity.account;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.aimyplus.consumer.R;
import com.aimyplus.consumer.activity.MainActivity;
import com.aimyplus.consumer.base.BaseResponse;
import com.aimyplus.consumer.model.account.AccessTokenRefreshModel;
import com.aimyplus.consumer.model.account.AccessTokenRefreshResponseModel;
import com.aimyplus.consumer.utils.ConnUtil;
import com.aimyplus.consumer.utils.SpUtil;


public class SplashActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 1500;
    AccessTokenRefreshModel accessRefreshModel = new AccessTokenRefreshModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String refreshToken = SpUtil.getRefreshToken();
                accessRefreshModel.setRefreshToken(refreshToken);
                String accessToken = SpUtil.getAccessToken();

                //Check if there is a access_token in shared preferences.
                if (accessToken.length() > 0) {

                    //Server POST refreshing accessToken
                ConnUtil.refreshAccessToken(SplashActivity.class.getName(), accessRefreshModel, new BaseResponse<AccessTokenRefreshResponseModel>(SplashActivity.this) {

                        @Override
                        public void onSuccess(Object sender, AccessTokenRefreshResponseModel response) {
                            //Delete tokens in shared preferences and save new tokens.
                            SpUtil.deleteAccessToken();
                            SpUtil.persist("AccessToken", response.getNewAccessToken());
                            Toast.makeText(getApplicationContext(), "Success AccessToken Refresh", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(SplashActivity.this, MainActivity.class));
                            SplashActivity.this.finish();
                        }

                        @Override
                        public void onFailure(Object sender, String response) {
                            Toast.makeText(getApplicationContext(), "Connection Error", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                            SplashActivity.this.finish();
                        }
                    });
                } else {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    SplashActivity.this.finish();
                }
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}