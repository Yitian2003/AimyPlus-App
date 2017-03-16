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

public class RegistrationActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private View mProgressView;
    private View mLoginFormView;
    private Button btnCancelRegistration;
    private Button btnSubmitRegistration;
    private LoginButton btnFacebookRegistration;
    private EditText mEmail;
    private EditText mPassword;
    private EditText mPasswordConfirm;
    private EditText mFirstName;
    private EditText mLastName;
    private static final int RC_SIGN_IN = 9001;
    private String accessToken;
    private String deviceId;
    private String deviceosVersion;
    private String deviceModel;
    private String deviceBrand;
    private String deviceManufacturer;
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

        setContentView(R.layout.activity_registration);

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
        mPassword = (EditText) findViewById(R.id.password);
        mPasswordConfirm = (EditText) findViewById(R.id.password_confirm);
        mFirstName = (EditText) findViewById(R.id.first_name);
        mLastName = (EditText) findViewById(R.id.last_name);

        btnCancelRegistration = (Button) findViewById(R.id.registration_cancel);
        btnSubmitRegistration = (Button) findViewById(R.id.registration_submit);

//        btnFacebookRegistration = (LoginButton) findViewById(R.id.registration_facebook);
//        btnFacebookRegistration.setReadPermissions("email", "public_profile");
//
//        SignInButton mGoogleSignInButton = (SignInButton) findViewById(R.id.registration_google);
//        setGooglePlusButtonText(mGoogleSignInButton,"Register using Google");

        //Cancel button listener
        btnCancelRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                RegistrationActivity.this.finish();
            }
        });

        //Aimy registration button listener
        btnSubmitRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //disable button
                btnSubmitRegistration.setEnabled(false);

                // Check for a valid email address.
                if (TextUtils.isEmpty(mEmail.getText().toString())) {
                    mEmail.requestFocus();
                    mEmail.setError(getString(R.string.error_field_required));
                    btnSubmitRegistration.setEnabled(true);
                } else if (!isEmailValid(mEmail.getText().toString())) {
                    mEmail.requestFocus();
                    mEmail.setError(getString(R.string.error_invalid_email));
                    btnSubmitRegistration.setEnabled(true);
                }

                // Check for valid password
                else if (!isPasswordValid(mPassword.getText().toString())) {
                    mPassword.requestFocus();
                    mPassword.setError("Password must be at least 6 characters.");
                    btnSubmitRegistration.setEnabled(true);
                } else if (!isPasswordMatch(mPassword.getText().toString(), mPasswordConfirm.getText().toString())) {
                    mPasswordConfirm.requestFocus();
                    mPasswordConfirm.setError("Passwords must match");
                    btnSubmitRegistration.setEnabled(true);
                } else if (!isFirstNameValid(mFirstName.getText().toString())) {
                    mFirstName.requestFocus();
                    mFirstName.setError("First name must be at least 2 characters");
                    btnSubmitRegistration.setEnabled(true);
                } else if (!isLastNameValid(mLastName.getText().toString())) {
                    mLastName.requestFocus();
                    mLastName.setError("Last name must be at least 2 characters");
                    btnSubmitRegistration.setEnabled(true);
                } else {
                    //set model
                    registrationModel.setUsername(mEmail.getText().toString());
                    registrationModel.setEmail(mEmail.getText().toString());
                    registrationModel.setPassword(mPassword.getText().toString());
                    registrationModel.setPasswordConfirm(mPasswordConfirm.getText().toString());
                    registrationModel.setFirstName(mFirstName.getText().toString());
                    registrationModel.setLastName(mLastName.getText().toString());
                    //showProgress(true);
                    RegistrationRequest();
                }
            }
        });

        //Facebook button listener
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        //showProgress(true);
                        AccessToken token = AccessToken.getCurrentAccessToken();
                        // Facebook Email address
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(
                                            JSONObject object,
                                            GraphResponse response) {

                                        try {
                                            registrationExternalModel.setEmail(object.getString("email"));
                                            registrationExternalModel.setUsername(object.getString("email"));
                                            registrationExternalModel.setFirstName(object.getString("first_name"));
                                            registrationExternalModel.setLastName(object.getString("last_name"));

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email,gender,first_name,last_name");
                        request.setParameters(parameters);
                        request.executeAsync();
                        loginModel.setAccessToken(token.getToken());
                        loginModel.setProvider("Facebook");
                        RegistrationExternalRequest();
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(RegistrationActivity.this, "Facebook authentication failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void RegistrationRequest() {
        ConnUtil.register(RegistrationActivity.class.getName(), registrationModel, new BaseResponse<JsonResponseModel> (this) {

            @Override
            public void onSuccess(Object sender, JsonResponseModel model) {
                if (model.getSuccess() == "false") {
                    Toast.makeText(RegistrationActivity.this, model.getMessage(), Toast.LENGTH_SHORT).show();
                    btnSubmitRegistration.setEnabled(true);
                } else if (model.getSuccess() == "true") {

                    loginModel.setUsername(registrationModel.getUsername());
                    loginModel.setPassword(registrationModel.getPasswordConfirm());

                    ConnUtil.login(RegistrationActivity.class.getName(), loginModel, new BaseResponse<LoginResponseModel>(RegistrationActivity.this) {

                        @Override
                        public void onSuccess(Object sender, LoginResponseModel response) {
                            //Save tokens to shared preference file.
                            String accessToken = response.getAccessToken();
                            String refreshToken = response.getRefreshToken();
                            String expiryDate = response.getExpires();
                            SpUtil.persist("AccessToken", accessToken);
                            SpUtil.persist("RefreshToken", refreshToken);
                            SpUtil.persist("ExpiryDate", expiryDate);
                            startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
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
                Toast.makeText(RegistrationActivity.this, "Connection Error", Toast.LENGTH_SHORT).show();
                btnSubmitRegistration.setEnabled(true);
            }
        });
    }

    public void RegistrationExternalRequest() {
        ConnUtil.registerWithExternal(RegistrationActivity.class.getName(), registrationExternalModel, new BaseResponse<JsonResponseModel> (this, true) {

            @Override
            public void onSuccess(Object sender, JsonResponseModel model) {
                if (model.getSuccess() == "false") {
                    Toast.makeText(RegistrationActivity.this, model.getMessage(), Toast.LENGTH_SHORT).show();
                    btnSubmitRegistration.setEnabled(true);
                } else if (model.getSuccess() == "true") {

                    ConnUtil.login(RegistrationActivity.class.getName(), loginModel, new BaseResponse<LoginResponseModel>(RegistrationActivity.this) {

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
                            startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
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
            Toast.makeText(RegistrationActivity.this, "Google Authentication failed.", Toast.LENGTH_SHORT).show();
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

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() >= 6;
    }

    private boolean isPasswordMatch(String password, String passwordConfirm) {
        return (password.equals(passwordConfirm));
    }

    private boolean isFirstNameValid(String firstName) {
        return firstName.length() >= 2;
    }

    private boolean isLastNameValid(String lastName) {
        return lastName.length() >= 2;
    }

    protected void setGooglePlusButtonText(SignInButton signInButton,
                                           String buttonText) {
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setTextSize(15);
                //tv.setGravity(1);
                tv.setPadding(0, 0,35,0);
                tv.setTypeface(null, Typeface.NORMAL);
                tv.setText(buttonText);
                return;
            }
        }
    }
}