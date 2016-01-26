package com.aptoide.amethyst.ui;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.AptoideBaseActivity;
import com.aptoide.amethyst.FeedBackActivity;
import com.aptoide.amethyst.R;
import com.aptoide.amethyst.configuration.AptoideConfiguration;
import com.aptoide.dataprovider.webservices.models.Constants;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.util.Properties;

/**
 * Created by rmateus on 31-12-2013.
 */
public class MyAccountActivity extends AptoideBaseActivity implements GoogleApiClient.ConnectionCallbacks , GoogleApiClient.OnConnectionFailedListener{

    private Toolbar mToolbar;
    private AccountManager mAccountManager;
    private UiLifecycleHelper uiLifecycleHelper;
    private Session.StatusCallback statusCallback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {

        }
    };
    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Aptoide.getThemePicker().setAptoideTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_logout);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_login);

        setSupportActionBar(mToolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
//        getSupportActionBar().setTitle(getString(R.string.sign_out));
//        getSupportActionBar().setDisplayShowTitleEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            uiLifecycleHelper = new UiLifecycleHelper(this, statusCallback);
            uiLifecycleHelper.onCreate(savedInstanceState);
        }
        final GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this , this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addConnectionCallbacks(this)
                .build();

        mAccountManager = AccountManager.get(this);

        if (mAccountManager.getAccountsByType(Aptoide.getConfiguration().getAccountType()).length > 0) {

            final Account account = mAccountManager.getAccountsByType(Aptoide.getConfiguration().getAccountType())[0];

            ((TextView) findViewById(R.id.username)).setText(account.name);

            findViewById(R.id.button_logout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Build.VERSION.SDK_INT >= 8) {
                        Session session = new Session(MyAccountActivity.this);
                        Session.setActiveSession(session);
                        if (Session.getActiveSession() != null) {
                            Session.getActiveSession().closeAndClearTokenInformation();
                        }
                    }
//
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(final Status status) {
                        }
                    });

                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    sharedPreferences.edit().remove("queueName").apply();
                    ContentResolver.setIsSyncable(account, Constants.WEBINSTALL_SYNC_AUTHORITY, 0);
                    ContentResolver.setSyncAutomatically(account, Constants.WEBINSTALL_SYNC_AUTHORITY, false);
                    if(Build.VERSION.SDK_INT>=8){
                        ContentResolver.removePeriodicSync(account, Constants.WEBINSTALL_SYNC_AUTHORITY, new Bundle());
                    }
                    mAccountManager.removeAccount(account, new AccountManagerCallback<Boolean>() {
                        @Override
                        public void run(AccountManagerFuture<Boolean> future) {
                            addAccount();
                            finish();
                        }
                    }, null);

                }
            });

        } else {
            addAccount();
            finish();
        }
    }

    private void addAccount() {
        final AccountManagerFuture<Bundle> future = mAccountManager.addAccount(Aptoide.getConfiguration().getAccountType(), AptoideConfiguration.AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, null, null, this, new AccountManagerCallback<Bundle>() {
            @Override
            public void run(AccountManagerFuture<Bundle> future) {
                try {
                    Bundle bnd = future.getResult();
                    //showMessage("Account was created");
                    if (bnd.containsKey(AccountManager.KEY_AUTHTOKEN)) {
                        setContentView(R.layout.form_logout);
//                        Log.d("udinic", "AddNewAccount Bundle is " + bnd);
                    } else {
                        finish();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    //showMessage(e.getMessage());
                }
            }
        }, null);
    }

    @Override
    protected void onStart() {
        super.onStart();
//        if (Build.VERSION.SDK_INT >= 8) mPlusClient.connect();
        if (Build.VERSION.SDK_INT >= 8) mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        if (Build.VERSION.SDK_INT >= 8) mPlusClient.disconnect();
        if (Build.VERSION.SDK_INT >= 8) mGoogleApiClient.disconnect();
//        FlurryAgent.onEndSession(this);
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

    }

    @Override
    public void onConnectionSuspended(int i) {

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