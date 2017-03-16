// create by Yitian Dec 28, 2016
package com.aimyplus.consumer.activity.account;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aimyplus.consumer.R;
import com.aimyplus.consumer.activity.MainActivity;
import com.aimyplus.consumer.base.BaseResponse;
import com.aimyplus.consumer.model.JsonResponseModel;
import com.aimyplus.consumer.model.account.LoginModel;
import com.aimyplus.consumer.model.account.LoginResponseModel;
import com.aimyplus.consumer.model.account.RegistrationExternalModel;
import com.aimyplus.consumer.model.account.RegistrationModel;
import com.aimyplus.consumer.utils.ConnUtil;
import com.aimyplus.consumer.utils.SpUtil;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class RegisterFromSocial extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private View mProgressView;
    private View mLoginFormView;
    private Button btnCancelRegistration;
    private Button btnSubmitRegistration;
    private LoginButton btnFacebookRegistration;
    private EditText mEmail;
    private EditText mPassword;
    private EditText mPasswordConfirm;

    private static final int RC_SIGN_IN = 9001;
    private String accessToken;
    private String deviceId;
    private String deviceosVersion;
    private String deviceModel;
    private String deviceBrand;
    private String deviceManufacturer;

    private String email;
    private String provider;
    private String firstName;
    private String lastName;

    GoogleApiClient mGoogleApiClient;
    RegistrationModel registrationModel = new RegistrationModel();
    RegistrationExternalModel registrationExternalModel = new RegistrationExternalModel();
    LoginModel loginModel = new LoginModel();
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize facebook and its callback
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_register_from_social);

        // get email address and provider
        Bundle extras = getIntent().getExtras();
        email = extras.getString("Email");
        provider = extras.getString("Provider");
        firstName = extras.getString("FirstName");
        lastName = extras.getString("LastName");
        accessToken = extras.getString("Token");

        mLoginFormView = findViewById(R.id.registration_form);
        mProgressView = findViewById(R.id.login_progress);

        //Get phone information
        deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        deviceosVersion = Build.VERSION.RELEASE;
        deviceModel = android.os.Build.MODEL;
        deviceBrand = android.os.Build.BRAND;
        deviceManufacturer = android.os.Build.MANUFACTURER;

        loginModel.setDeviceId(deviceId);
        loginModel.setDeviceosVersion(deviceosVersion);
        loginModel.setDeviceModel(deviceModel);
        loginModel.setDeviceBrand(deviceBrand);
        loginModel.setDeviceManufacturer(deviceManufacturer);

        mEmail = (EditText) findViewById(R.id.email);
        mEmail.setText(email);
        mEmail.setEnabled(false);
        mPassword = (EditText) findViewById(R.id.password);
        mPasswordConfirm = (EditText) findViewById(R.id.password_confirm);

        btnSubmitRegistration = (Button) findViewById(R.id.registration_submit);

        //Aimy registration button listener
        btnSubmitRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //disable button
                btnSubmitRegistration.setEnabled(false);

                // Check for valid password
                if (!isPasswordValid(mPassword.getText().toString())) {
                    mPassword.requestFocus();
                    mPassword.setError("Password must be at least 6 characters.");
                    btnSubmitRegistration.setEnabled(true);
                } else if (!isPasswordMatch(mPassword.getText().toString(), mPasswordConfirm.getText().toString())) {
                    mPasswordConfirm.requestFocus();
                    mPasswordConfirm.setError("Passwords must match");
                    btnSubmitRegistration.setEnabled(true);
                } else if( !provider.isEmpty()){

                    // Social account register
                    loginModel.setAccessToken(accessToken);
                    loginModel.setProvider(provider);
                    registrationModel.setUsername(mEmail.getText().toString());
                    registrationModel.setEmail(mEmail.getText().toString());
                    registrationModel.setPassword(mPassword.getText().toString());
                    registrationModel.setPasswordConfirm(mPasswordConfirm.getText().toString());
                    registrationModel.setFirstName(firstName);
                    registrationModel.setLastName(lastName);
                    RegistrationRequest();
                }
            }
        });
    }

    public void RegistrationRequest() {
        ConnUtil.register(RegistrationActivity.class.getName(), registrationModel, new BaseResponse<JsonResponseModel> (this) {

            @Override
            public void onSuccess(Object sender, JsonResponseModel model) {
                if (model.getSuccess() == "false") {
                    Toast.makeText(RegisterFromSocial.this, model.getMessage(), Toast.LENGTH_SHORT).show();
                    btnSubmitRegistration.setEnabled(true);
                } else if (model.getSuccess() == "true") {

                    loginModel.setUsername(registrationModel.getUsername());
                    loginModel.setPassword(registrationModel.getPasswordConfirm());

                    ConnUtil.login(RegistrationActivity.class.getName(), loginModel, new BaseResponse<LoginResponseModel>(RegisterFromSocial.this) {

                        @Override
                        public void onSuccess(Object sender, LoginResponseModel response) {
                            //Save tokens to shared preference file.
                            String accessToken = response.getAccessToken();
                            String refreshToken = response.getRefreshToken();
                            String expiryDate = response.getExpires();
                            SpUtil.persist("AccessToken", accessToken);
                            SpUtil.persist("RefreshToken", refreshToken);
                            SpUtil.persist("ExpiryDate", expiryDate);
                            startActivity(new Intent(RegisterFromSocial.this, MainActivity.class));
                        }

                        @Override
                        public void onFailure(Object sender, String response) {
                            super.onFailure(sender, response);
                            btnSubmitRegistration.setEnabled(true);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Object sender, String response) {
                Toast.makeText(RegisterFromSocial.this, "Connection Error", Toast.LENGTH_SHORT).show();
                btnSubmitRegistration.setEnabled(true);
            }
        });
    }

    public void RegistrationExternalRequest() {
        ConnUtil.registerWithExternal(RegistrationActivity.class.getName(), registrationExternalModel, new BaseResponse<JsonResponseModel> (this, true) {

            @Override
            public void onSuccess(Object sender, JsonResponseModel model) {
                if (model.getSuccess() == "false") {
                    Toast.makeText(RegisterFromSocial.this, model.getMessage(), Toast.LENGTH_SHORT).show();
                    btnSubmitRegistration.setEnabled(true);
                } else if (model.getSuccess() == "true") {

                    ConnUtil.login(RegistrationActivity.class.getName(), loginModel, new BaseResponse<LoginResponseModel>(RegisterFromSocial.this) {

                        @Override
                        public void onBegin(Object sender) {
                        }

                        @Override
                        public void onSuccess(Object sender, LoginResponseModel response) {
                            //Save tokens to shared preference file.
                            String accessToken = response.getAccessToken();
                            String refreshToken = response.getRefreshToken();
                            String expiryDate = response.getExpires();
                            SpUtil.persist("AccessToken", accessToken);
                            SpUtil.persist("RefreshToken", refreshToken);
                            SpUtil.persist("ExpiryDate", expiryDate);
                            startActivity(new Intent(RegisterFromSocial.this, MainActivity.class));
                        }

                        @Override
                        public void onFailure(Object sender, String response) {
                            btnSubmitRegistration.setEnabled(true);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Object sender, String response) {
                super.onFailure(sender, response);
                btnSubmitRegistration.setEnabled(true);
            }
        });
    }

    //Google Plus sign in.
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    //Google plus/Facebook sign in result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) { //Google
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        } else { //Facebook
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    //Google plus result handler
    private void handleSignInResult(GoogleSignInResult result) {

        if (result.isSuccess()) {
            final GoogleSignInAccount acct = result.getSignInAccount();
            String mAccountName = acct.getEmail();
            registrationExternalModel.setUsername(mAccountName);
            registrationExternalModel.setEmail(mAccountName);
            String mDisplayName = acct.getDisplayName();
            String[] names = mDisplayName.split(" ");
            registrationExternalModel.setFirstName(names[0]);
            registrationExternalModel.setLastName(names[1]);
            new RetrieveTokenTask().execute(mAccountName);

        } else {
            Toast.makeText(RegisterFromSocial.this, "Google Authentication failed.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    //Get google plus access_token
    private class RetrieveTokenTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String accountName = params[0];
            String scope = "oauth2:" + Scopes.PROFILE;
            Context context = getApplicationContext();
            try {
                //This process is slow at the moment as the accessToken was not being
                //refreshed unless it was cleared first, so two calls are made to Google.
                accessToken = GoogleAuthUtil.getToken(context, accountName, scope);
                GoogleAuthUtil.clearToken(context, accessToken);
                accessToken = GoogleAuthUtil.getToken(context, accountName, scope);
            } catch (IOException e) {
                //TODO add error handling
            } catch (UserRecoverableAuthException e) {
                //TODO add error handling
                Log.d("RetrieveTokenTask", "Error");
            } catch (GoogleAuthException e) {
                //TODO add error handling
            }
            return accessToken;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //showProgress(true);
            loginModel.setProvider("Google");
            loginModel.setAccessToken(accessToken);
            RegistrationExternalRequest();
        }
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() >= 6;
    }

    private boolean isPasswordMatch(String password, String passwordConfirm) {
        return (password.equals(passwordConfirm));
    }
}
