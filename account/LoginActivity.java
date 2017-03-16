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
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;

import com.facebook.FacebookSdk;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements OnConnectionFailedListener, View.OnClickListener {

    private static final String TAG = LoginActivity.class.getName();
    // UI references.
    private AutoCompleteTextView tvUsername;
    private EditText tvPassword;
    LoginButton btnFacebook ;
    SignInButton btnGoogle;
    private TextView tvSignIn;

    // Sign in variables
    GoogleApiClient googleApiClient;
    private LoginModel loginModel;
    private static final int RC_SIGN_IN = 9001;
    private String accessToken;
    private String firstName;
    private String lastName;
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_login);

        this.findViewById(R.id.facebook_sign_in_button).setOnClickListener(this);
        this.findViewById(R.id.google_sign_in_button).setOnClickListener(this);

        btnFacebook = (LoginButton) findViewById(R.id.facebook_sign_in_button);
        btnGoogle = (SignInButton) findViewById(R.id.google_sign_in_button);

        initialDeviceInfo();
        initialLocalLogin();
        initialGoogleLogin();
        initialFacebookLogin();
        initialRego();
    }

    private void initialDeviceInfo() {

        loginModel = new LoginModel();
        //Get phone information
        String deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        String deviceosVersion = Build.VERSION.RELEASE;
        String deviceModel = android.os.Build.MODEL;
        String deviceBrand = android.os.Build.BRAND;
        String deviceManufacturer = android.os.Build.MANUFACTURER;

        loginModel.setDeviceId(deviceId);
        loginModel.setDeviceosVersion(deviceosVersion);
        loginModel.setDeviceModel(deviceModel);
        loginModel.setDeviceBrand(deviceBrand);
        loginModel.setDeviceManufacturer(deviceManufacturer);
    }

    private void initialLocalLogin() {
        tvSignIn = (TextView) findViewById(R.id.link_signin);
        if(tvSignIn != null) {
            tvSignIn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), LocalSignInActivity.class);
                    startActivity(intent);
                    finish();
//                    attemptLogin();
                }
            });
        }
    }

    private void initialGoogleLogin() {
        //Google login

        btnGoogle.setSize(SignInButton.SIZE_ICON_ONLY);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                //.requestIdToken(getString(R.string.default_web_client_id))
                .requestProfile()
                .requestEmail()
                .build();
        try {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    //.addScope(Plus.SCOPE_PLUS_LOGIN)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initialFacebookLogin() {
        //Facebook Login

        if (btnFacebook != null) {
            btnFacebook.setReadPermissions("email");
        }

        // Callback registration
        if (btnFacebook != null) {
            btnFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    AccessToken token = AccessToken.getCurrentAccessToken();
                    accessToken = token.getToken();
                    loginModel.setAccessToken(accessToken);
                    loginModel.setProvider("Facebook");

                    // get Facebook Email address
                    GraphRequest request = GraphRequest.newMeRequest(
                            loginResult.getAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(
                                        JSONObject object,
                                        GraphResponse response) {

                                    try {
                                        loginModel.setUsername(object.getString("email"));
                                        firstName = object.getString("first_name");
                                        lastName = object.getString("last_name");
                                        LoginRequest();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id,name,email,gender,first_name,last_name");
                    request.setParameters(parameters);
                    request.executeAsync();

                }

                @Override
                public void onCancel() {
                }

                @Override
                public void onError(FacebookException exception) {
                    Toast.makeText(LoginActivity.this, "Facebook access error", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fbBtn) {
            btnFacebook.performClick();
        }
        if (v.getId() == R.id.gpBtn) {
            SignInWithGoogle();
        }
    }

    private void initialRego() {
        Button btnRegistration = (Button) findViewById(R.id.registration);
        if (btnRegistration != null) {
            btnRegistration.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
                    LoginActivity.this.finish();
                }
            });
        }
    }

    public void LoginRequest() {
        ConnUtil.login(TAG, loginModel, new BaseResponse<LoginResponseModel>(this) {

            @Override
            public void onSuccess(Object sender, LoginResponseModel response) {
                super.onSuccess(sender, response);

                UserModel userModel = response.getUserModel();

                Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_SHORT).show();
                SpUtil.persist("AccessToken", response.getAccessToken());
                SpUtil.persist("RefreshToken", response.getRefreshToken());
                SpUtil.persistLong("TokenExpire", response.getLocalExpireDate());

                GlobalApplication.getInstance().setCurrentUser(userModel);
                SpUtil.persistUserModel(userModel);

                if (FirebaseInstanceId.getInstance().getToken() != null && !SpUtil.isNotificationHubInstalled()) {
                    Log.i("Test", "installation with AZURE NHUB....");
                    NotificationHubHelper.installWithNotificationHub(LoginActivity.this, TAG);
                }
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }

            @Override
            public void onFailure(Object sender, String response) {
                super.onFailure(sender, response);
                if (loginModel.getProvider() == null) {
                    Toast.makeText(getApplicationContext(), "fail " + response, Toast.LENGTH_SHORT).show();
                    LoginManager.getInstance().logOut();
                    tvSignIn.setEnabled(true);
                }
                else {
                    String email = loginModel.getUsername();
                    String provider = loginModel.getProvider();
                    Intent intent = new Intent(LoginActivity.this, RegisterFromSocial.class);
                    intent.putExtra("Email", email);
                    intent.putExtra("Provider", provider);
                    intent.putExtra("FirstName", firstName);
                    intent.putExtra("LastName", lastName);
                    intent.putExtra("Token", accessToken);
                    startActivity(intent);
                }
            }
        });
    }

     //Google Plus sign in.
    private void SignInWithGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    //Google plus/Facebook sign in result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) { //Google
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Log.d(TAG, "----handleSignInResult:" + result.getStatus().toString());
            handleSignInResult(result);
        } else { //Facebook
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    //Google plus result handler
    private void handleSignInResult(GoogleSignInResult result) {

        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            String mAccountName = null;
            if (acct != null) {
                mAccountName = acct.getEmail();
                loginModel.setUsername(mAccountName);
                //String mDisplayName = acct..getDisplayName();
                //String[] names = mDisplayName.split(" ");
                firstName = acct.getGivenName();
                lastName = acct.getFamilyName();
            }
            new RetrieveTokenTask().execute(mAccountName);

        } else {
            //Toast.makeText(LoginActivity.this, "Google Authentication failed.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

//    private interface ProfileQuery {
//        String[] PROJECTION = {
//                ContactsContract.CommonDataKinds.Email.ADDRESS,
//                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
//        };
//        int ADDRESS = 0;
//        int IS_PRIMARY = 1;
//    }

    //Get google plus access_token
    private class RetrieveTokenTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String accountName = params[0];
            String scope = "oauth2:" + Scopes.PROFILE;
            //String scope = "oauth2:" + Scopes.EMAIL;
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
            loginModel.setProvider("Google");
            loginModel.setAccessToken(accessToken);
            LoginRequest();
        }
    }
}

