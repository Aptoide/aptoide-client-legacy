package com.aptoide.amethyst.ui;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.AptoideBaseActivity;
import com.aptoide.amethyst.BuildConfig;
import com.aptoide.amethyst.R;
import com.aptoide.amethyst.configuration.AptoideConfiguration;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

/**
 * Created by rmateus on 31-12-2013.
 */
public class MyAccountActivity extends AptoideBaseActivity implements GoogleApiClient.ConnectionCallbacks , GoogleApiClient.OnConnectionFailedListener{
    public static final String WEBINSTALL_SYNC_AUTHORITY = BuildConfig.APPLICATION_ID + ".StubProvider";

    private Toolbar mToolbar;
    private AccountManager mAccountManager;
    private UiLifecycleHelper uiLifecycleHelper;
    private Session.StatusCallback statusCallback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {

        }
    };
    private View mLogout;
    private GoogleApiClient googleApiClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Aptoide.getThemePicker().setAptoideTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_logout);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_login);
        mLogout = findViewById(R.id.button_logout);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            uiLifecycleHelper = new UiLifecycleHelper(this, statusCallback);
            uiLifecycleHelper.onCreate(savedInstanceState);
        }

        final String accountType = Aptoide.getConfiguration().getAccountType();
        mAccountManager = AccountManager.get(this);
        if (mAccountManager.getAccountsByType(accountType).length <= 0) {
            addAccount();
            finish();
            return;
        }

        final Account account = mAccountManager.getAccountsByType(accountType)[0];
        ((TextView) findViewById(R.id.username)).setText(account.name);

        final int connectionResult = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        final Collection<Integer> badResults = Arrays.asList(ConnectionResult.SERVICE_MISSING, ConnectionResult.SERVICE_DISABLED);
        final boolean gmsAvailable = BuildConfig.GMS_CONFIGURED && !badResults.contains(connectionResult);
        if (gmsAvailable) {
            mLogout.setEnabled(false);
            final GoogleSignInOptions gso = new GoogleSignInOptions
                    .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestServerAuthCode(BuildConfig.GMS_SERVER_ID)
                    .build();
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .enableAutoManage(this, this)
                    .build();
            googleApiClient.connect();
        }

        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                singOutGoogle();
                singOutFacebook();
                removeAccount(account);
            }
        });
    }

    private void singOutGoogle() {
        if (googleApiClient != null && googleApiClient.isConnected()) {
            Auth.GoogleSignInApi.signOut(googleApiClient);
        }
    }

    private void singOutFacebook() {
        if (Build.VERSION.SDK_INT >= 8) {
            Session session = new Session(MyAccountActivity.this);
            Session.setActiveSession(session);
            if (Session.getActiveSession() != null) {
                Session.getActiveSession().closeAndClearTokenInformation();
            }
        }
    }

    private void removeAccount(final Account account) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sharedPreferences.edit().remove("queueName").apply();
        ContentResolver.setIsSyncable(account, WEBINSTALL_SYNC_AUTHORITY, 0);
        ContentResolver.setSyncAutomatically(account, MyAccountActivity.WEBINSTALL_SYNC_AUTHORITY, false);
        if(Build.VERSION.SDK_INT>=8){
            ContentResolver.removePeriodicSync(account, MyAccountActivity.WEBINSTALL_SYNC_AUTHORITY, new Bundle());
        }
        mAccountManager.removeAccount(account, new AccountManagerCallback<Boolean>() {
            @Override
            public void run(AccountManagerFuture<Boolean> future) {
                addAccount();
                finish();
            }
        }, null);
    }

    private void addAccount() {
        mAccountManager.addAccount(Aptoide.getConfiguration().getAccountType(), AptoideConfiguration.AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, null, null, this, new AccountManagerCallback<Bundle>() {
            @Override
            public void run(AccountManagerFuture<Bundle> future) {
                try {
                    Bundle bnd = future.getResult();
                    if (bnd.containsKey(AccountManager.KEY_AUTHTOKEN)) {
                        setContentView(R.layout.form_logout);
                    } else {
                        finish();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            uiLifecycleHelper.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            uiLifecycleHelper.onPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            uiLifecycleHelper.onDestroy();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            uiLifecycleHelper.onSaveInstanceState(outState);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int i = item.getItemId();

        if (i == android.R.id.home) {
            finish();
        } else if (i == R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLogout.setEnabled(true);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mLogout.setEnabled(false);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        new Properties().getProperty("");
    }

    @Override
    protected String getScreenName() {
        return "My Account";
    }

}