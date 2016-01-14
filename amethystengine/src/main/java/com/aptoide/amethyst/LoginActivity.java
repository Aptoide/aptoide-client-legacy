package com.aptoide.amethyst;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aptoide.amethyst.analytics.Analytics;
import com.aptoide.amethyst.configuration.AptoideConfiguration;
import com.aptoide.amethyst.dialogs.AptoideDialog;
import com.aptoide.amethyst.events.BusProvider;
import com.aptoide.amethyst.events.OttoEvents;
import com.aptoide.amethyst.model.json.CheckUserCredentialsJson;
import com.aptoide.amethyst.model.json.OAuth;
import com.aptoide.amethyst.preferences.Preferences;
import com.aptoide.amethyst.preferences.SecurePreferences;
import com.aptoide.amethyst.services.RabbitMqService;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.amethyst.utils.Configs;
import com.aptoide.amethyst.utils.Filters;
import com.aptoide.amethyst.utils.Logger;
import com.aptoide.amethyst.webservices.CheckUserCredentialsRequest;
import com.aptoide.amethyst.webservices.CreateUserRequest;
import com.aptoide.amethyst.webservices.Errors;
import com.aptoide.amethyst.webservices.OAuth2AuthenticationRequest;
import com.aptoide.dataprovider.AptoideSpiceHttpsService;
import com.aptoide.dataprovider.webservices.models.Constants;
import com.aptoide.dataprovider.webservices.models.ErrorResponse;
import com.facebook.FacebookException;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

import lombok.Getter;
import retrofit.RetrofitError;

/**
 * Created by brutus on 09-12-2013.
 */
public class LoginActivity extends AccountAuthenticatorActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private ProgressDialog mConnectionProgressDialog;
    //    private PlusClient mPlusClient;
//    private ConnectionResult mConnectionResult;
    private static final int REQUEST_CODE_RESOLVE_ERR = 9000;
    private boolean showPassword = true;
    private CheckBox registerDevice;
    private boolean hasQueue;
    private CheckUserCredentialsRequest request;
    private boolean fromPreviousAptoideVersion;
    private Class signupClass = Aptoide.getConfiguration().getSignUpActivityClass();
    private boolean removeAccount;
    private Toolbar mToolbar;

    /* Client used to interact with Google APIs. */
    @Getter
    private GoogleApiClient mGoogleApiClient;

    /* A flag indicating that a PendingIntent is in progress and prevents
     * us from starting further intents.
     */
    private boolean mIntentInProgress;

    /* Store the connection result from onConnectionFailed callbacks so that we can
     * resolve them when the user clicks sign-in.
     */
    private ConnectionResult mConnectionResult;
    private EditText emailBox;

//    @Override
//    public void onConnected(Bundle bundle) {
//        Log.d("Aptoide-", "On Connected");
//
//        AptoideDialog.pleaseWaitDialog().show(getSupportFragmentManager(), "pleaseWaitDialog");

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//
//                    final String token = GoogleAuthUtil.getToken(Aptoide.getContext(), mPlusClient.getAccountName(), "oauth2:server:client_id:" + serverId + ":api_scope:" + Scopes.PLUS_LOGIN);
//
//
//                    final String username = mPlusClient.getAccountName();
//                    final String name = mPlusClient.getCurrentPerson().getDisplayName();
//
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            submit(Mode.GOOGLE, username, token, name);
//                            //Toast.makeText(Aptoide.getContext(), token, Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                    FlurryAgent.logEvent("Logged_In_With_Google_Plus");
//
//                }catch (UserRecoverableAuthException e) {
//                    startActivityForResult(e.getIntent(), 90);
//                }catch (Exception e) {
//
//                    if (mPlusClient != null && mPlusClient.isConnected()) {
//                        mPlusClient.clearDefaultAccount();
//                        mPlusClient.disconnect();
//                    }
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(Aptoide.getContext(), R.string.error_occured, Toast.LENGTH_LONG).show();
//
//                            android.support.v4.app.DialogFragment pd = (android.support.v4.app.DialogFragment) getSupportFragmentManager().findFragmentByTag("pleaseWaitDialog");
//                            if (pd != null) {
//                                pd.dismissAllowingStateLoss();
//                            }
//                        }
//                    });
//                }
//            }
//        }).start();
//
//
//        mConnectionProgressDialog.dismiss();
//    }

//    @Override
//    public void onDisconnected() {
//        android.support.v4.app.DialogFragment pd = (android.support.v4.app.DialogFragment) getSupportFragmentManager().findFragmentByTag("pleaseWaitDialog");
//        if (pd != null) {
//            pd.dismissAllowingStateLoss();
//        }
//    }

//    @Override
//    public void onConnectionFailed(ConnectionResult result) {
//        if (mConnectionProgressDialog.isShowing()) {
//            // The user clicked the sign-in button already. Start to resolve
//            // connection errors. Wait until onConnected() to dismiss the
//            // connection dialog.
//            if (result.hasResolution()) {
//                try {
//                    result.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
//                } catch (IntentSender.SendIntentException e) {
//                    mPlusClient.connect();
//                }
//            }else{
//                mConnectionProgressDialog.dismiss();
//            }
//        }
//
//        onDisconnected();
//        // Save the intent so that we can start an activity when the user clicks
//        // the sign-in button.
//        mConnectionResult = result;
//
//    }

//    @Override
//    public void onClick(View view) {
//        if (view.getId() == R.id.g_sign_in_button && !mPlusClient.isConnected()) {
//            if (mConnectionResult == null) {
//                mPlusClient.connect();
//                mConnectionProgressDialog.show();
//            } else {
//                try {
//                    mConnectionResult.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
//                } catch (IntentSender.SendIntentException e) {
//                    // Try connecting again.
//                    mConnectionResult = null;
//                    mPlusClient.connect();
//                }
//            }
//        }
//
//    }

//    @Override
//    public void onCancel() {
//        if (request != null) {
//            if(spiceManager.isStarted())spiceManager.cancel(request);
//        }
//
//    }

    public enum Mode {APTOIDE, GOOGLE, FACEBOOK}


    public final static String ARG_OPTIONS_BUNDLE = "BE";
    public final static String OPTIONS_LOGOUT_BOOL = "OLOUT";
    public final static String OPTIONS_FASTBOOK_BOOL = "OFASTBOOK";
    public final static String OPTIONS_EMAIL_STRING = "OEMAIL";
    public final static String OPTIONS_TOKEN_STRING = "OTOKEN";

    public final static String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public final static String ARG_AUTH_TYPE = "AUTH_TYPE";
    public final static String ARG_ACCOUNT_NAME = "ACCOUNT_NAME";
    public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";

    public static final String KEY_ERROR_MESSAGE = "ERR_MSG";

    public final static String PARAM_USER_PASS = "USER_PASS";
    public final static String PARAM_USER_AVATAR = "USER_AVATAR";

    private final int REQ_SIGNUP = 1;

    private final String TAG = "Login";

    private AccountManager mAccountManager;
    private String mAuthTokenType;
    private SpiceManager spiceManager = new SpiceManager(AptoideSpiceHttpsService.class);

    EditText password_box;
    CheckBox checkShowPass;
    private UiLifecycleHelper uiLifecycleHelper;
    private Session.StatusCallback statusCallback = new Session.StatusCallback() {
        @Override
        public void call(final Session session, SessionState state, Exception exception) {
            if (state.isOpened()) {
                AptoideDialog.pleaseWaitDialog().show(getSupportFragmentManager(), "pleaseWaitDialog");

                Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
                    @Override
                    public void onCompleted(final GraphUser user, Response response) {
                        if (session == Session.getActiveSession() && user != null) {

                            try {

                                if (removeAccount && mAccountManager.getAccountsByType(Aptoide.getConfiguration().getAccountType()).length > 0) {
                                    mAccountManager.removeAccount(mAccountManager.getAccountsByType(Aptoide.getConfiguration().getAccountType())[0], new AccountManagerCallback<Boolean>() {
                                        @Override
                                        public void run(AccountManagerFuture<Boolean> future) {
                                            submit(Mode.FACEBOOK, user.getProperty("email").toString(), session.getAccessToken(), null);
                                        }
                                    }, new Handler(Looper.getMainLooper()));
                                } else {
                                    //TODO show denied permissions
//                                    Toast.makeText(LoginActivity.this, session.getDeclinedPermissions().toString(), Toast.LENGTH_SHORT).show();
                                    submit(Mode.FACEBOOK, user.getProperty("email").toString(), session.getAccessToken(), null);
                                }

                            } catch (Exception e) {
                                Logger.printException(e);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(Aptoide.getContext(), R.string.error_occured, Toast.LENGTH_LONG).show();

                                        android.support.v4.app.DialogFragment pd = (android.support.v4.app.DialogFragment) getSupportFragmentManager().findFragmentByTag("pleaseWaitDialog");
                                        if (pd != null) {
                                            pd.dismissAllowingStateLoss();
                                        }

                                    }
                                });
                                session.closeAndClearTokenInformation();
                            }
                        } else {
                            session.closeAndClearTokenInformation();
                        }
                    }
                });
                request.executeAsync();
            }
        }
    };


    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Aptoide.getThemePicker().setAptoideTheme(this);

        String activityTitle = getString(R.string.login_or_register);

        Bundle b = getIntent().getBundleExtra(ARG_OPTIONS_BUNDLE);

        if (b != null && b.getBoolean(OPTIONS_FASTBOOK_BOOL, false)) {
            activityTitle = getString(R.string.social_timeline);

            if (b.getBoolean(OPTIONS_LOGOUT_BOOL, false)) {
                setContentView(R.layout.page_timeline_logout_and_login);
                removeAccount = true;
            } else {
                setContentView(R.layout.page_timeline_not_logged_in);
            }

        } else {
            if (!initLogin())
                return;
        }

        uiLifecycleHelper = new UiLifecycleHelper(this, statusCallback);
        uiLifecycleHelper.onCreate(savedInstanceState);
//
//        mPlusClient = new PlusClient.Builder(this, this, this).build();
//
        LoginButton fbButton = (LoginButton) findViewById(R.id.fb_login_button);
        fbButton.setReadPermissions(Arrays.asList("email", "user_friends"));
        fbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                FlurryAgent.logEvent("Login_Page_Clicked_On_Login_With_Facebook");
            }
        });
        fbButton.setOnErrorListener(new LoginButton.OnErrorListener() {
            @Override
            public void onError(FacebookException error) {
                error.printStackTrace();
                Toast.makeText(Aptoide.getContext(), R.string.error_occured, Toast.LENGTH_LONG).show();
            }
        });

        findViewById(R.id.g_sign_in_button).setOnClickListener(this);

        mAccountManager = AccountManager.get(getBaseContext());

        mAuthTokenType = getIntent().getStringExtra(ARG_AUTH_TYPE);
        if (mAuthTokenType == null)
            mAuthTokenType = AptoideConfiguration.AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS;

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(activityTitle);

        // Google
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();

    }

    @Override
    public void onClick(View v) {

        mGoogleApiClient.connect();
        if (v.getId() == R.id.g_sign_in_button && !mGoogleApiClient.isConnected()) {
            if (mConnectionResult == null) {
                mGoogleApiClient.connect();
            } else {
                try {
                    mConnectionResult.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
                } catch (IntentSender.SendIntentException e) {
                    // Try connecting again.
                    mConnectionResult = null;
                    mGoogleApiClient.connect();
                }
            }
        }
    }

    private boolean initLogin() {
        if (AptoideUtils.AccountUtils.isLoggedIn(this)) {
            finish();
            Toast.makeText(this, R.string.one_account_allowed, Toast.LENGTH_SHORT).show();
            return false;
        } else {

            setContentView(R.layout.form_login);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {

//                findViewById(R.id.g_sign_in_button).setOnClickListener(this);
//                mConnectionProgressDialog = new ProgressDialog(this);
//                mConnectionProgressDialog.setMessage(getString(R.string.signing_in));

                int val = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
                boolean play_installed = val == ConnectionResult.SUCCESS;

                SignInButton signInButton = (SignInButton) findViewById(R.id.g_sign_in_button);
                if (!play_installed) {
                    signInButton.setVisibility(View.GONE);
                }

            }

            String accountName = getIntent().getStringExtra(ARG_ACCOUNT_NAME);


            emailBox = (EditText) findViewById(R.id.username);
            if (accountName != null) {
                emailBox.setText(accountName);
            }

            if (PreferenceManager.getDefaultSharedPreferences(this).contains(Constants.LOGIN_USER_LOGIN)) {
                emailBox.setText(PreferenceManager.getDefaultSharedPreferences(this).getString(Constants.LOGIN_USER_LOGIN, ""));
                fromPreviousAptoideVersion = true;
            }
            password_box = (EditText) findViewById(R.id.password);
            password_box.setTransformationMethod(new PasswordTransformationMethod());

//            password_box.setCompoundDrawablesWithIntrinsicBounds(null, null, hidePasswordRes, null);
            Button button = (Button) findViewById(R.id.btn_show_hide_pass);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int cursorPosition = password_box.getSelectionStart();
                    if (showPassword) {
//                            FlurryAgent.logEvent("Login_Page_Clicked_On_Show_Password");
                        showPassword = false;
                        password_box.setTransformationMethod(null);

                        if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
                            v.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_open_eye));
                        } else {
                            v.setBackground(getResources().getDrawable(R.drawable.icon_open_eye));
                        }
//                        password_box.setCompoundDrawablesWithIntrinsicBounds(null, null, showPasswordRes, null);
                    } else {
//                            FlurryAgent.logEvent("Login_Page_Clicked_On_Hide_Password");
                        showPassword = true;
                        password_box.setTransformationMethod(new PasswordTransformationMethod());

                        if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
                            v.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_closed_eye));
                        } else {
                            v.setBackground(getResources().getDrawable(R.drawable.icon_closed_eye));
                        }
                    }

                    password_box.setSelection(cursorPosition);
                }
            });

            findViewById(R.id.button_login).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    FlurryAgent.logEvent("Login_Page_Clicked_On_Login_Button");

                    String username = ((EditText) findViewById(R.id.username)).getText().toString();
                    String password = ((EditText) findViewById(R.id.password)).getText().toString();

                    if (username.length() == 0 || password.length() == 0) {
                        Toast.makeText(getApplicationContext(), R.string.fields_cannot_empty, Toast.LENGTH_LONG).show();
                        return;
                    }
                    AptoideDialog.pleaseWaitDialog().show(getSupportFragmentManager(), "pleaseWaitDialog");

                    submit(Mode.APTOIDE, username, password, null);
                }
            });

//            TextView new_to_aptoide = (TextView) findViewById(R.id.new_to_aptoide);
//            SpannableString newToAptoideString = new SpannableString(AptoideUtils.StringUtils.getFormattedString(this, R.string.new_to_aptoide, Aptoide.getConfiguration().getMarketName()));
//            newToAptoideString.setSpan(new UnderlineSpan(), 0, newToAptoideString.length(), 0);
//            new_to_aptoide.setText(newToAptoideString);
//            new_to_aptoide.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
////                    FlurryAgent.logEvent("Login_Page_Clicked_On_New_To_Aptoide_Button");
//                    Intent signup = new Intent(LoginActivity.this, signupClass);
//                    startActivityForResult(signup, REQ_SIGNUP);
//                }
//            });

            Button registerButton = (Button) findViewById(R.id.button_register);
            registerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    createAccount();
                    Intent signup = new Intent(LoginActivity.this, signupClass);
                    startActivityForResult(signup, REQ_SIGNUP);
                }
            });

            registerDevice = (CheckBox) findViewById(R.id.link_my_device);

            TextView forgot_password = (TextView) findViewById(R.id.forgot_password);
            SpannableString forgetString = new SpannableString(getString(R.string.forgot_passwd));
            forgetString.setSpan(new UnderlineSpan(), 0, forgetString.length(), 0);
            forgot_password.setText(forgetString);
            forgot_password.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
//                    FlurryAgent.logEvent("Login_Page_Clicked_On_Forgot_Password");
                    Intent passwordRecovery = new Intent(Intent.ACTION_VIEW, Uri.parse("http://m.aptoide.com/account/password-recovery"));
                    startActivity(passwordRecovery);
                }
            });


        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (uiLifecycleHelper != null) {
            uiLifecycleHelper.onDestroy();
        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
//            if(uiLifecycleHelper!=null) uiLifecycleHelper.onDestroy();
//        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
//            uiLifecycleHelper.onSaveInstanceState(outState);
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        System.out.println("Debug: requestCode: " + requestCode);
        System.out.println("Debug: resultCode: " + resultCode);

        uiLifecycleHelper.onActivityResult(requestCode, resultCode, data);

        // Google Plus API
        if (requestCode == REQUEST_CODE_RESOLVE_ERR && resultCode == RESULT_OK) {
            mConnectionResult = null;
            mGoogleApiClient.connect();
        } else {
            if ((requestCode == 9001 || requestCode == 90) && resultCode == RESULT_OK) {
                mGoogleApiClient.connect();
            }
        }

//        if (requestCode == REQUEST_CODE_RESOLVE_ERR && resultCode == RESULT_OK) {
//            mConnectionResult = null;
////            mPlusClient.connect();
//        } else {
//            if (requestCode == 90 && resultCode == RESULT_OK) {
////                mPlusClient.connect();
//            }
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
//            if (uiLifecycleHelper != null) {
//                uiLifecycleHelper.onActivityResult(requestCode, resultCode, data);
//            }
//        }
        // The sign up activity returned that the user has successfully created an account
        if (requestCode == REQ_SIGNUP && resultCode == RESULT_OK) {
            hasQueue = true;
            finishLogin(data);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        spiceManager.start(getBaseContext());
//        FlurryAgent.onStartSession(this, "X89WPPSKWQB2FT6B8F3X");
    }

    @Override
    protected void onResume() {
        super.onResume();

        uiLifecycleHelper.onResume();

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
//            uiLifecycleHelper.onResume();
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        uiLifecycleHelper.onPause();

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
//            uiLifecycleHelper.onPause();
//        }
    }


    @Override
    protected void onStop() {
        super.onStop();
//        if (mPlusClient != null && mPlusClient.isConnected()) {
//            mPlusClient.disconnect();
//        }

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }

        spiceManager.shouldStop();
//        FlurryAgent.onEndSession(this);
    }


    public void submit(final Mode mode, final String username, final String passwordOrToken, final String nameForGoogle) {

        //final String userName = ((EditText) findViewById(R.id.username)).getAvatar().toString();
        //final String userPass = ((EditText) findViewById(R.id.password)).getAvatar().toString();

        final String accountType = getIntent().getStringExtra(ARG_ACCOUNT_TYPE);


        OAuth2AuthenticationRequest oAuth2AuthenticationRequest = new OAuth2AuthenticationRequest();

        oAuth2AuthenticationRequest.setPassword(passwordOrToken);
        oAuth2AuthenticationRequest.setUsername(username);
        oAuth2AuthenticationRequest.setMode(mode);
        oAuth2AuthenticationRequest.setNameForGoogle(nameForGoogle);

        spiceManager.execute(oAuth2AuthenticationRequest, new RequestListener<OAuth>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                if (spiceException != null && spiceException.getCause() != null) {
                    String error = null;
                    if (spiceException.getCause() instanceof RetrofitError) {
                        final RetrofitError cause = (RetrofitError) spiceException.getCause();
                        if (cause != null && cause.getResponse() != null && cause.getResponse().getStatus() == 400 || cause.getResponse().getStatus() == 401) {
                            error = getString(R.string.error_AUTH_1);
                        } else {
                            error = getString(R.string.error_occured);
                        }
                    } else {
                        error = getString(R.string.error_occured);
                    }

//                Session session = Session.getActiveSession();

//                if (session != null && session.isOpened()) {
//                    session.closeAndClearTokenInformation();
//                }

//                if (mPlusClient != null && mPlusClient.isConnected()) {
//                    mPlusClient.clearDefaultAccount();
//                    mPlusClient.disconnect();
//                }

                    Toast.makeText(getBaseContext(), error, Toast.LENGTH_SHORT).show();
                }

                android.support.v4.app.DialogFragment pd = (android.support.v4.app.DialogFragment) getSupportFragmentManager().findFragmentByTag("pleaseWaitDialog");
                if (pd != null) {
                    pd.dismissAllowingStateLoss();
                }


            }

            @Override
            public void onRequestSuccess(final OAuth oAuth) {

                if (oAuth.getStatus() != null && oAuth.getStatus().equals("FAIL")) {

                    AptoideUtils.UI.toastError(oAuth.getError());
//                    Session session = Session.getActiveSession();

//                    if (session != null && session.isOpened()) {
//                        session.closeAndClearTokenInformation();
//                    }
//
//                    if (mPlusClient != null && mPlusClient.isConnected()) {
//                        mPlusClient.clearDefaultAccount();
//                        mPlusClient.disconnect();
//                    }
                    android.support.v4.app.DialogFragment pd = (android.support.v4.app.DialogFragment) getSupportFragmentManager().findFragmentByTag("pleaseWaitDialog");

                    if (pd != null) {
                        pd.dismissAllowingStateLoss();
                    }

                } else {

//                    getUserInfo(oAuth, username, mode, accountType, passwordOrToken);
                    getUserInfo(oAuth, username, mode, accountType, passwordOrToken);
//                    Analytics.Login.login(username);

                }

            }
        });

    }

    private void getUserInfo(final OAuth oAuth, final String username, final Mode mode, final String accountType, final String passwordOrToken) {
        System.out.println("Debug: getUserInfo: " + oAuth.getAccess_token());
        request = CheckUserCredentialsRequest.buildDefaultRequest(this, oAuth.getAccess_token());
        request.setRegisterDevice(registerDevice != null && registerDevice.isChecked());
        spiceManager.execute(request, new RequestListener<CheckUserCredentialsJson>() {

            @Override
            public void onRequestFailure(SpiceException e) {

//                Session session = Session.getActiveSession();
//
//                if (session != null && session.isOpened()) {
//                    session.closeAndClearTokenInformation();
//                }
//
//                if (mPlusClient != null && mPlusClient.isConnected()) {
//                    mPlusClient.clearDefaultAccount();
//                    mPlusClient.disconnect();
//                }

                Toast.makeText(getBaseContext(), R.string.error_occured, Toast.LENGTH_SHORT).show();

                android.support.v4.app.DialogFragment pd = (android.support.v4.app.DialogFragment) getSupportFragmentManager().findFragmentByTag("pleaseWaitDialog");

                if (pd != null) {
                    pd.dismissAllowingStateLoss();
                }

            }

            @Override
            public void onRequestSuccess(CheckUserCredentialsJson checkUserCredentialsJson) {

                android.support.v4.app.DialogFragment pd = (android.support.v4.app.DialogFragment) getSupportFragmentManager().findFragmentByTag("pleaseWaitDialog");

                if (pd != null) {
                    pd.dismissAllowingStateLoss();
                }

                boolean booleano = updatePreferences(checkUserCredentialsJson, username, mode.name(), oAuth.getAccess_token());
                System.out.println("Debug: upadtePreferences: " + checkUserCredentialsJson.getStatus());
                System.out.println("Debug: upadtePreferences: " + booleano);
                System.out.println("Debug: upadtePreferences: " + checkUserCredentialsJson);
                System.out.println("Debug: upadtePreferences: " + username);
                System.out.println("Debug: upadtePreferences: " + mode.name());
                System.out.println("Debug: upadtePreferences: " + oAuth.getAccess_token());
                if (booleano) {
                    if (null != checkUserCredentialsJson.getQueue()) {
                        hasQueue = true;
                    }

                    Bundle data = new Bundle();
                    data.putString(AccountManager.KEY_ACCOUNT_NAME, username);
                    data.putString(AccountManager.KEY_ACCOUNT_TYPE, accountType);
                    data.putString(AccountManager.KEY_AUTHTOKEN, oAuth.getRefreshToken());
                    data.putString(PARAM_USER_PASS, passwordOrToken);

                    final Intent res = new Intent();
                    res.putExtras(data);
                    finishLogin(res);
                } else {
                    final HashMap<String, Integer> errorsMapConversion = Errors.getErrorsMap();
                    Integer stringId;
                    String message;
                    for (ErrorResponse error : checkUserCredentialsJson.getErrors()) {
                        stringId = errorsMapConversion.get(error.code);
                        if (stringId != null) {
                            message = getString(stringId);
                        } else {
                            message = error.msg;
                        }

                        Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void finishLogin(Intent intent) {

        Log.d("aptoide", TAG + "> finishLogin");

        String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        String accountPassword = intent.getStringExtra(PARAM_USER_PASS);
        final String accountType = intent.hasExtra(ARG_ACCOUNT_TYPE)
                ? intent.getStringExtra(ARG_ACCOUNT_TYPE)
                : Aptoide.getConfiguration().getAccountType();

        final Account account = new Account(accountName, accountType);

        if (getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false)) {
            Log.d("aptoide", TAG + "> finishLogin > addAccountExplicitly");
            String authtoken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
            String authtokenType = mAuthTokenType;


            // Creating the account on the device and setting the auth token we got
            // (Not setting the auth token will cause another call to the server to authenticate the user)
            mAccountManager.addAccountExplicitly(account, accountPassword, null);
            mAccountManager.setAuthToken(account, authtokenType, authtoken);


        } else {
            Log.d("aptoide", TAG + "> finishLogin > setPassword");
            mAccountManager.setPassword(account, accountPassword);
        }

        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);

        if (fromPreviousAptoideVersion) {
            PreferenceManager.getDefaultSharedPreferences(this).edit().remove(Constants.LOGIN_USER_LOGIN).commit();
        }

        /*
        ContentResolver wiResolver = getContentResolver();
        wiResolver.setIsSyncable(account, STUB_PROVIDER_AUTHORITY, 1);
        wiResolver.setSyncAutomatically(account, STUB_PROVIDER_AUTHORITY, true);

        if(Build.VERSION.SDK_INT >= 8) {
            wiResolver.addPeriodicSync(account, STUB_PROVIDER_AUTHORITY, new Bundle(), WEB_INSTALL_POLL_FREQUENCY);
        }
        */
        finish();
//        if(Build.VERSION.SDK_INT >= 10) {
//            if (registerDevice != null && registerDevice.isChecked()) {
//                FlurryAgent.logEvent("Login_Page_Linked_Account_With_WebInstall");
//            } else {
//                FlurryAgent.logEvent("Login_Page_Did_Not_Link_Account_With_WebInstall");
//            }
//        }
        if (registerDevice != null && registerDevice.isChecked() && hasQueue)
            startService(new Intent(this, RabbitMqService.class));
        ContentResolver.setSyncAutomatically(account, Aptoide.getConfiguration().getUpdatesSyncAdapterAuthority(), true);
        if (Build.VERSION.SDK_INT >= 8)
            ContentResolver.addPeriodicSync(account, Aptoide.getConfiguration().getUpdatesSyncAdapterAuthority(), new Bundle(), 43200);
        ContentResolver.setSyncAutomatically(account, Aptoide.getConfiguration().getAutoUpdatesSyncAdapterAuthority(), true);


    }

    protected View rootView;

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//
//        rootView = inflater.inflate(R.layout.form_login, container, false);
//
//        parent.findViewById(R.id.g_sign_in_button).setOnClickListener(this);
//
//        return super.onCreateView(parent, name, context, attrs);
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int i = item.getItemId();

        if (i == android.R.id.home || i == R.id.home) {
            finish();
        } else if (i == R.id.menu_SendFeedBack) {
            FeedBackActivity.screenshot(this);
            startActivity(new Intent(this, FeedBackActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    public static boolean updatePreferences(CheckUserCredentialsJson checkUserCredentialsJson,
                                            String username, String modeName, String token) {
        if ("OK".equals(checkUserCredentialsJson.getStatus())) {
            SharedPreferences.Editor preferences = PreferenceManager.getDefaultSharedPreferences(Aptoide.getContext()).edit();
            if (null != (checkUserCredentialsJson.getQueue())) {
                //hasQueue = true;
                preferences.putString("queueName", checkUserCredentialsJson.getQueue());
            }
            if (null != (checkUserCredentialsJson.getAvatar()) && !checkUserCredentialsJson.getAvatar().equals("")) {
                preferences.putString(Constants.USER_AVATAR, checkUserCredentialsJson.getAvatar());
            }

            if (null != (checkUserCredentialsJson.getRepo())) {
                preferences.putString("userRepo", checkUserCredentialsJson.getRepo());
            }
            if (null != (checkUserCredentialsJson.getUsername())) {
                preferences.putString("username", checkUserCredentialsJson.getUsername());
            }

            if (checkUserCredentialsJson.getSettings() != null) {
                boolean timeline = checkUserCredentialsJson.getSettings().getTimeline().equals("active");
                preferences.putBoolean(Preferences.TIMELINE_ACEPTED_BOOL, timeline);
                boolean matureswitch = checkUserCredentialsJson.getSettings().getMatureswitch().equals("active");
                preferences.putBoolean(Constants.MATURE_CHECK_BOX, matureswitch);

            }

            preferences.putString(Configs.LOGIN_USER_LOGIN, username);

            preferences.putString("loginType", modeName);
            preferences.apply();

            SharedPreferences.Editor securePreferences = SecurePreferences.getInstance().edit();
            securePreferences.putString("access_token", token);
            securePreferences.putInt("User_ID", checkUserCredentialsJson.getId());
            Log.d("pois", "updatePreferences, setting user id to " + checkUserCredentialsJson.getId());
            securePreferences.putString("devtoken", checkUserCredentialsJson.getToken());
            securePreferences.apply();
            BusProvider.getInstance().post(new OttoEvents.RedrawNavigationDrawer());

            return true;
        }
        return false;
    }

    @Override
    public void onConnected(final Bundle bundle) {
        Log.d("Aptoide-", "On Connected");

        final String serverId = getResources().getString(R.string.GOOGLE_PLUS_SERVER_ID);
        AptoideDialog.pleaseWaitDialog().show(getSupportFragmentManager(), "pleaseWaitDialog");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    final String token = GoogleAuthUtil.getToken(Aptoide.getContext(), Plus.AccountApi.getAccountName(mGoogleApiClient), "oauth2:server:client_id:" + serverId + ":api_scope:" + Scopes.PLUS_LOGIN);


                    final String username = Plus.AccountApi.getAccountName(mGoogleApiClient);
                    final String name = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient).getDisplayName();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            submit(Mode.GOOGLE, username, token, name);
                            //Toast.makeText(Aptoide.getContext(), token, Toast.LENGTH_SHORT).show();
                        }
                    });
//                    FlurryAgent.logEvent("Logged_In_With_Google_Plus");

                } catch (UserRecoverableAuthException e) {
                    startActivityForResult(e.getIntent(), 90);
                } catch (Exception e) {

                    if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                        // Check plz
                        mGoogleApiClient.clearDefaultAccountAndReconnect();
                        mGoogleApiClient.disconnect();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Aptoide.getContext(), R.string.error_occured, Toast.LENGTH_LONG).show();

                            android.support.v4.app.DialogFragment pd = (android.support.v4.app.DialogFragment) getSupportFragmentManager().findFragmentByTag("pleaseWaitDialog");
                            if (pd != null) {
                                pd.dismissAllowingStateLoss();
                            }
                        }
                    });
                }
            }
        }).start();


//        mConnectionProgressDialog.dismiss();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!mIntentInProgress && result.hasResolution()) {
            try {
                mIntentInProgress = true;
                result.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
            } catch (IntentSender.SendIntentException e) {
                // The intent was canceled before it was sent.  Return to the default
                // state and attempt to connect to get an updated ConnectionResult.
                mIntentInProgress = false;
//                mGoogleApiClient.connect();
            }
        }
    }

    private void createAccount() {

        // Validation!

        if (emailBox.getText().toString().length() == 0 || password_box.getText().toString().length() == 0) {
            Toast.makeText(getApplicationContext(), R.string.fields_cannot_empty, Toast.LENGTH_LONG).show();
            return;
        }
        String pass = password_box.getText().toString();
        if (pass.length() < 8 || !hasValidChars(pass)) {
            Toast.makeText(getApplicationContext(), R.string.password_validation_text, Toast.LENGTH_LONG).show();
            return;
        }

        CreateUserRequest createUserRequest = new CreateUserRequest();


        createUserRequest.setEmail(emailBox.getText().toString());
//        createUserRequest.setName(nameBox.getAvatar().toString());
        createUserRequest.setPass(password_box.getText().toString());

        AptoideDialog.pleaseWaitDialog().show(getSupportFragmentManager(), "pleaseWaitDialog");
        spiceManager.execute(createUserRequest, new RequestListener<OAuth>() {
            @Override
            public void onRequestFailure(SpiceException e) {
                Toast.makeText(Aptoide.getContext(), R.string.error_occured, Toast.LENGTH_LONG).show();
                DialogFragment pd = (DialogFragment) getSupportFragmentManager().findFragmentByTag("pleaseWaitDialog");
                if (pd != null) pd.dismiss();
            }

            @Override
            public void onRequestSuccess(final OAuth oAuth) {


                CheckUserCredentialsRequest request = new CheckUserCredentialsRequest();
                String errorMessages = AptoideUtils.ServerConnectionUtils.getErrorCodeFromErrorList(LoginActivity.this, oAuth.errors);
                if (!TextUtils.isEmpty(errorMessages)) {
                    Toast.makeText(LoginActivity.this, errorMessages, Toast.LENGTH_SHORT).show();
                    android.support.v4.app.DialogFragment pd = (android.support.v4.app.DialogFragment) getSupportFragmentManager().findFragmentByTag("pleaseWaitDialog");
                    if (pd != null) {
                        pd.dismiss();
                    }
                    return;
                }

                String deviceId = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
                request.setRegisterDevice(true);
                request.setSdk(String.valueOf(AptoideUtils.HWSpecifications.getSdkVer()));
                request.setDeviceId(deviceId);
                request.setCpu(AptoideUtils.HWSpecifications.getAbis());
                request.setDensity(String.valueOf(AptoideUtils.HWSpecifications.getNumericScreenSize(LoginActivity.this)));
                request.setOpenGl(String.valueOf(AptoideUtils.HWSpecifications.getGlEsVer(LoginActivity.this)));
                request.setModel(Build.MODEL);
                request.setScreenSize(Filters.Screen.values()[AptoideUtils.HWSpecifications.getScreenSize(LoginActivity.this)].name().toLowerCase(Locale.ENGLISH));

                request.setToken(oAuth.getAccess_token());

                spiceManager.execute(request, new RequestListener<CheckUserCredentialsJson>() {

                    @Override
                    public void onRequestFailure(SpiceException e) {

                        Toast.makeText(getBaseContext(), R.string.error_occured, Toast.LENGTH_SHORT).show();

                        android.support.v4.app.DialogFragment pd = (android.support.v4.app.DialogFragment) getSupportFragmentManager().findFragmentByTag("pleaseWaitDialog");
                        if (pd != null) {
                            pd.dismissAllowingStateLoss();
                        }
                    }

                    @Override
                    public void onRequestSuccess(CheckUserCredentialsJson checkUserCredentialsJson) {

                        android.support.v4.app.DialogFragment pd = (android.support.v4.app.DialogFragment) getSupportFragmentManager().findFragmentByTag("pleaseWaitDialog");

                        if (pd != null) {
                            pd.dismissAllowingStateLoss();
                        }


                        if ("OK".equals(checkUserCredentialsJson.getStatus())) {
                            SharedPreferences.Editor editor = PreferenceManager
                                    .getDefaultSharedPreferences(LoginActivity.this)
                                    .edit();
                            if (null != checkUserCredentialsJson.getQueue()) {
                                //hasQueue = true;
                                editor.putString(Constants.USER_QUEUE_NAME, checkUserCredentialsJson.getQueue());
                            }
                            if (null != (checkUserCredentialsJson.getAvatar()) && !checkUserCredentialsJson.getAvatar().equals("")) {
                                editor.putString(Constants.USER_AVATAR, checkUserCredentialsJson.getAvatar());
                            }
                            if (null != (checkUserCredentialsJson.getRepo())) {
                                editor.putString(Constants.USER_REPO, checkUserCredentialsJson.getRepo());
                            }

                            if (null != checkUserCredentialsJson.getUsername()) {
                                editor.putString(Constants.USER_NAME, checkUserCredentialsJson.getUsername());
                            }

                            editor.putString(Configs.LOGIN_USER_LOGIN, emailBox.getText().toString());
                            editor.putString(Constants.USER_LOGIN_TYPE, LoginActivity.Mode.APTOIDE.name());
                            editor.commit();
                            BusProvider.getInstance().post(new OttoEvents.RedrawNavigationDrawer());

                            SharedPreferences preferences = SecurePreferences.getInstance();
                            preferences.edit().putString("access_token", oAuth.getAccess_token()).commit();
                            preferences.edit().putString("devtoken", checkUserCredentialsJson.getToken()).commit();


                            Bundle data = new Bundle();
                            data.putString(AccountManager.KEY_ACCOUNT_NAME, emailBox.getText().toString());
                            data.putString(AccountManager.KEY_ACCOUNT_TYPE, Aptoide.getConfiguration().getAccountType());
                            data.putString(AccountManager.KEY_AUTHTOKEN, oAuth.getRefreshToken());
                            data.putString(PARAM_USER_PASS, password_box.getText().toString());


                            final Intent res = new Intent();
                            res.putExtras(data);
                            setResult(RESULT_OK, res);
                            Analytics.UserRegister.registered();
                            hasQueue = true;

                            finishLogin(res);
                        } else {
                            final HashMap<String, Integer> errorsMapConversion = Errors.getErrorsMap();
                            Integer stringId;
                            String message;
                            for (ErrorResponse error : checkUserCredentialsJson.getErrors()) {
                                stringId = errorsMapConversion.get(error.code);
                                if (stringId != null) {
                                    message = getString(stringId);
                                } else {
                                    message = error.msg;
                                }
                                Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                });
            }
        });
    }

    private boolean hasValidChars(String pass) {

        return has1number1letter(pass);

    }

    private boolean has1number1letter(String pass) {
        boolean hasLetter = false;
        boolean hasNumber = false;

        for (char c : pass.toCharArray()) {
            if (!hasLetter && Character.isLetter(c)) {
                if (hasNumber)
                    return true;
                hasLetter = true;
            } else if (!hasNumber && Character.isDigit(c)) {
                if (hasLetter)
                    return true;
                hasNumber = true;
            }
        }
        if (pass.contains("!") || pass.contains("@") || pass.contains("#") || pass.contains("$") || pass.contains("#") || pass.contains("*")) {
            hasNumber = true;
        }

        return hasNumber && hasLetter;
    }
}



