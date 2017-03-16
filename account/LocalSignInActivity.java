// Yitian create on 27 Dec 2016

package com.aimyplus.consumer.activity.account;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aimyplus.consumer.R;
import com.aimyplus.consumer.activity.MainActivity;
import com.aimyplus.consumer.app.GlobalApplication;
import com.aimyplus.consumer.base.BaseResponse;
import com.aimyplus.consumer.firebase.NotificationHubHelper;
import com.aimyplus.consumer.model.account.LoginModel;
import com.aimyplus.consumer.model.account.LoginResponseModel;
import com.aimyplus.consumer.model.user.UserModel;
import com.aimyplus.consumer.utils.ConnUtil;
import com.aimyplus.consumer.utils.SpUtil;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

import com.facebook.FacebookSdk;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;

public class LocalSignInActivity extends AppCompatActivity {

    private static final String TAG = LocalSignInActivity.class.getName();
    private AutoCompleteTextView tvUsername;
    private EditText tvPassword;
    private LoginModel loginModel = new LoginModel();;
    private Button btnSignIn;
    private Button btnCancelSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_sign_in);

        btnCancelSignIn = (Button) findViewById(R.id.login_cancel);
        btnSignIn = (Button) findViewById(R.id.email_sign_in_button);

        btnCancelSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LocalSignInActivity.this, LoginActivity.class));
                LocalSignInActivity.this.finish();
            }
        });

        if(btnSignIn != null) {
            btnSignIn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    attemptLogin();
                }
            });
        }

        tvUsername = (AutoCompleteTextView) findViewById(R.id.email);
        tvPassword = (EditText) findViewById(R.id.password);
        tvPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
    }

    private void attemptLogin() {

        // Reset errors.
        tvUsername.setError(null);
        tvPassword.setError(null);

        // Store values at the time of the login attempt.
        String email = tvUsername.getText().toString();
        String password = tvPassword.getText().toString();
        //Login details
        loginModel.setUsername(email);
        loginModel.setPassword(password);

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            tvPassword.setError(getString(R.string.error_invalid_password));
            focusView = tvPassword;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            tvUsername.setError(getString(R.string.error_field_required));
            focusView = tvUsername;
            cancel = true;
        } else if (!isEmailValid(email)) {
            tvUsername.setError(getString(R.string.error_invalid_email));
            focusView = tvUsername;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            LoginRequest();
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }

    public void LoginRequest() {
        ConnUtil.login(TAG, loginModel, new BaseResponse<LoginResponseModel>(this) {

            @Override
            public void onSuccess(Object sender, LoginResponseModel response) {
                super.onSuccess(sender, response);

                Toast.makeText(getApplicationContext(),"success",Toast.LENGTH_SHORT).show();
                SpUtil.persist("AccessToken", response.getAccessToken());
                SpUtil.persist("RefreshToken", response.getRefreshToken());
                SpUtil.persistLong("TokenExpire", response.getLocalExpireDate());

                UserModel userModel = response.getUserModel();
                GlobalApplication.getInstance().setCurrentUser(userModel);
                SpUtil.persistUserModel(userModel);

                if (FirebaseInstanceId.getInstance().getToken() != null && !SpUtil.isNotificationHubInstalled()) {
                    Log.i("Test", "installation with AZURE NHUB....");
                    NotificationHubHelper.installWithNotificationHub(LocalSignInActivity.this, TAG);
                }
                startActivity(new Intent(LocalSignInActivity.this, MainActivity.class));
            }

            @Override
            public void onFailure(Object sender, String response) {
                super.onFailure(sender, response);
                Toast.makeText(getApplicationContext(),"fail " + response,Toast.LENGTH_SHORT).show();
                LoginManager.getInstance().logOut();
                btnSignIn.setEnabled(true);
            }
        });
    }
}
