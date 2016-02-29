package com.aptoide.amethyst;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
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
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
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
import com.aptoide.amethyst.utils.Logger;
import com.aptoide.amethyst.webservices.CheckUserCredentialsRequest;
import com.aptoide.amethyst.webservices.Errors;
import com.aptoide.amethyst.webservices.OAuth2AuthenticationRequest;
import com.aptoide.dataprovider.AptoideSpiceHttpsService;
import com.aptoide.dataprovider.webservices.models.Constants;
import com.aptoide.dataprovider.webservices.models.ErrorResponse;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import retrofit.RetrofitError;

/**
 * Created by brutus on 09-12-2013.
 */
public class LoginActivity extends AccountAuthenticatorActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private static final String TAG_PROGRESS = "progressDialog";
    private static final int REQUEST_CODE_RESOLVE_ERR = 9000;

    private CheckBox registerDevice;
    private boolean hasQueue;
    private CheckUserCredentialsRequest request;
    private boolean fromPreviousAptoideVersion;
    private Class signupClass = Aptoide.getConfiguration().getSignUpActivityClass();
    private boolean removeAccount;

    /* A flag indicating that a PendingIntent is in progress and prevents
     * us from starting further intents.
     */
    private boolean mIntentInProgress;

    /* Store the connection result from onConnectionFailed callbacks so that we can
     * resolve them when the user clicks sign-in.
     */
    private ConnectionResult mConnectionResult;
    private EditText emailBox;

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
    private final int REQ_SIGN_IN_GOOGLE = 2;

    private AccountManager mAccountManager;
    private String mAuthTokenType;
    private SpiceManager spiceManager = new SpiceManager(AptoideSpiceHttpsService.class);

    EditText password_box;
    CheckBox checkShowPass;
    private UiLifecycleHelper uiLifecycleHelper;
    private Session.StatusCallback statusCallback = new Session.StatusCallback() {
        @Override
        public void call(final Session session, SessionState state, Exception exception) {
            if (!state.isOpened()) {
                return;
            }
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
    };


    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (AptoideUtils.AccountUtils.isLoggedIn(this)) {
            finish();
            Toast.makeText(this, R.string.one_account_allowed, Toast.LENGTH_SHORT).show();
            return;
        }

        Aptoide.getThemePicker().setAptoideTheme(this);

        final String activityTitle;
        final Bundle options = getIntent().getBundleExtra(ARG_OPTIONS_BUNDLE);
        if (options != null && options.getBoolean(OPTIONS_FASTBOOK_BOOL, false)) {
            activityTitle = getString(R.string.social_timeline);
            if (options.getBoolean(OPTIONS_LOGOUT_BOOL, false)) {
                setContentView(R.layout.page_timeline_logout_and_login);
                removeAccount = true;
            } else {
                setContentView(R.layout.page_timeline_not_logged_in);
            }
        } else {
            activityTitle = getString(R.string.login_or_register);
            setContentView(R.layout.form_login);
            setUpLogin();
            setUpGoogle();
        }
        setUpFacebook(savedInstanceState);

        final Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(activityTitle);

        mAccountManager = AccountManager.get(getBaseContext());
        mAuthTokenType = getIntent().getStringExtra(ARG_AUTH_TYPE);
        if (mAuthTokenType == null) {
            mAuthTokenType = AptoideConfiguration.AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS;
        }

        spiceManager.start(this);
    }

    private void setUpLogin() {
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

        Button button = (Button) findViewById(R.id.btn_show_hide_pass);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int cursorPosition = password_box.getSelectionStart();
                final boolean passwordShown = password_box.getTransformationMethod() == null;
                v.setBackgroundResource(passwordShown ? R.drawable.icon_closed_eye : R.drawable.icon_open_eye);
                password_box.setTransformationMethod(passwordShown ? new PasswordTransformationMethod() : null);
                password_box.setSelection(cursorPosition);
            }
        });

        findViewById(R.id.button_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = ((EditText) findViewById(R.id.username)).getText().toString();
                String password = ((EditText) findViewById(R.id.password)).getText().toString();

                if (username.length() == 0 || password.length() == 0) {
                    Toast.makeText(getApplicationContext(), R.string.fields_cannot_empty, Toast.LENGTH_LONG).show();
                    return;
                }

                submit(Mode.APTOIDE, username, password, null);
            }
        });

        Button registerButton = (Button) findViewById(R.id.button_register);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                Intent passwordRecovery = new Intent(Intent.ACTION_VIEW, Uri.parse("http://m.aptoide.com/account/password-recovery"));
                startActivity(passwordRecovery);
            }
        });
    }

    private void setUpFacebook(final Bundle savedInstanceState) {
        uiLifecycleHelper = new UiLifecycleHelper(this, statusCallback);
        uiLifecycleHelper.onCreate(savedInstanceState);

        LoginButton fbButton = (LoginButton) findViewById(R.id.fb_login_button);
        fbButton.setReadPermissions(Arrays.asList("email", "user_friends"));
    }

    private void setUpGoogle() {
        final View googleSignIn = findViewById(R.id.g_sign_in_button);
        final int connectionResult = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        final Collection<Integer> badResults = Arrays.asList(ConnectionResult.SERVICE_MISSING, ConnectionResult.SERVICE_DISABLED);
        final boolean gmsAvailable = BuildConfig.GMS_CONFIGURED && !badResults.contains(connectionResult);
        if (!gmsAvailable) {
            googleSignIn.setVisibility(View.GONE);
            return;
        }
        final GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestServerAuthCode(BuildConfig.GMS_SERVER_ID)
                .build();
        final GoogleApiClient client = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        googleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(client);
                startActivityForResult(signInIntent, REQ_SIGN_IN_GOOGLE);
            }
        });
    }

    // Google
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        if (result.hasResolution()) {
            try {
                result.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
            } catch (IntentSender.SendIntentException ignore) {
                // The intent was canceled before it was sent.
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == android.R.id.home || i == R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        uiLifecycleHelper.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        uiLifecycleHelper.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        uiLifecycleHelper.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (uiLifecycleHelper != null) {
            uiLifecycleHelper.onDestroy();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiLifecycleHelper.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_SIGN_IN_GOOGLE) {
            final GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        } else if (requestCode == REQ_SIGNUP && resultCode == RESULT_OK) {
            hasQueue = true;
            finishLogin(data);
        }
    }

    public void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "GoogleSignInResult. status: " + result.getStatus());
        final GoogleSignInAccount account;
        if (result.isSuccess() && (account = result.getSignInAccount()) != null) {
            final String userName = account.getEmail();
            final String token = account.getServerAuthCode();
            final String name = account.getDisplayName();
            submit(Mode.GOOGLE, userName, token, name);
        } else {
            Toast.makeText(Aptoide.getContext(), R.string.error_occured, Toast.LENGTH_SHORT).show();
        }
    }

    public void setShowProgress(final boolean showProgress) {
        final DialogFragment progress = (DialogFragment) getSupportFragmentManager().findFragmentByTag(TAG_PROGRESS);
        if (progress == null && showProgress) {
            try {
                AptoideDialog.pleaseWaitDialog().show(getSupportFragmentManager(), TAG_PROGRESS);
            // https://code.google.com/p/android/issues/detail?id=23761
            } catch (IllegalStateException ignore) { }
        } else if (progress != null && !showProgress) {
            progress.dismissAllowingStateLoss();
        }
    }


    public void submit(final Mode mode, final String userName, final String passwordOrToken, final String nameForGoogle) {
        Log.d(TAG, "Submitting. mode: " + mode.name() +", userName: " + userName + ", nameForGoogle: " + nameForGoogle);
        final String accountType = getIntent().getStringExtra(ARG_ACCOUNT_TYPE);

        OAuth2AuthenticationRequest oAuth2AuthenticationRequest = new OAuth2AuthenticationRequest();
        oAuth2AuthenticationRequest.setPassword(passwordOrToken);
        oAuth2AuthenticationRequest.setUsername(userName);
        oAuth2AuthenticationRequest.setMode(mode);
        oAuth2AuthenticationRequest.setNameForGoogle(nameForGoogle);

        setShowProgress(true);
        spiceManager.execute(oAuth2AuthenticationRequest, new RequestListener<OAuth>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                Log.d(TAG, "OAuth filed: " + spiceException.getMessage());
                final Throwable cause = spiceException.getCause();
                if (cause != null) {
                    final String error;
                    if (cause instanceof RetrofitError) {
                        final RetrofitError retrofitError = (RetrofitError) cause;
                        final retrofit.client.Response response = retrofitError.getResponse();
                        if (response != null && (response.getStatus() == 400 || response.getStatus() == 401)) {
                            error = getString(R.string.error_AUTH_1);
                        } else {
                            error = getString(R.string.error_occured);
                        }
                    } else {
                        error = getString(R.string.error_occured);
                    }
                    Toast.makeText(getBaseContext(), error, Toast.LENGTH_SHORT).show();
                }
                setShowProgress(false);
            }

            @Override
            public void onRequestSuccess(final OAuth oAuth) {
                if (oAuth.getStatus() != null && oAuth.getStatus().equals("FAIL")) {
                    Log.d(TAG, "OAuth filed: " + oAuth.getError_description());
                    AptoideUtils.UI.toastError(oAuth.getError());
                    setShowProgress(false);
                } else {
                    getUserInfo(oAuth, userName, mode, accountType, passwordOrToken);
                    Analytics.Login.login(userName, mode);
                }
            }
        });
    }

    private void getUserInfo(final OAuth oAuth, final String userName, final Mode mode, final String accountType, final String passwordOrToken) {
        Log.d(TAG, "Loading user info.");
        request = CheckUserCredentialsRequest.buildDefaultRequest(this, oAuth.getAccess_token());
        request.setRegisterDevice(registerDevice != null && registerDevice.isChecked());
        setShowProgress(true);
        spiceManager.execute(request, new RequestListener<CheckUserCredentialsJson>() {
            @Override
            public void onRequestFailure(SpiceException e) {
                Log.d(TAG, "Loading user info failed. " + e.getMessage());
                Toast.makeText(getBaseContext(), R.string.error_occured, Toast.LENGTH_SHORT).show();
                setShowProgress(false);
            }

            @Override
            public void onRequestSuccess(CheckUserCredentialsJson checkUserCredentialsJson) {
                Log.d(TAG, "User info loaded. status: " + checkUserCredentialsJson.getStatus() + ", userName: " + checkUserCredentialsJson.getUsername());
                if ("OK".equals(checkUserCredentialsJson.getStatus())) {
                    updatePreferences(checkUserCredentialsJson, userName, mode.name(), oAuth.getAccess_token());
                    if (null != checkUserCredentialsJson.getQueue()) {
                        hasQueue = true;
                    }

                    Bundle data = new Bundle();
                    data.putString(AccountManager.KEY_ACCOUNT_NAME, userName);
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

                setShowProgress(false);
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
        finish();
        if (registerDevice != null && registerDevice.isChecked() && hasQueue)
            startService(new Intent(this, RabbitMqService.class));
        ContentResolver.setSyncAutomatically(account, Aptoide.getConfiguration().getUpdatesSyncAdapterAuthority(), true);
        if (Build.VERSION.SDK_INT >= 8)
            ContentResolver.addPeriodicSync(account, Aptoide.getConfiguration().getUpdatesSyncAdapterAuthority(), new Bundle(), 43200);
        ContentResolver.setSyncAutomatically(account, Aptoide.getConfiguration().getAutoUpdatesSyncAdapterAuthority(), true);
    }


    public static void updatePreferences(CheckUserCredentialsJson checkUserCredentialsJson,
                                            String username, String modeName, String token) {
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
    }
}



