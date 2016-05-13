package com.aptoide.amethyst.fragments.timeline;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.LoginActivity;
import com.aptoide.amethyst.R;
import com.aptoide.amethyst.analytics.Analytics;
import com.aptoide.amethyst.configuration.AptoideConfiguration;
import com.aptoide.amethyst.model.json.CheckUserCredentialsJson;
import com.aptoide.amethyst.model.json.OAuth;
import com.aptoide.amethyst.services.RabbitMqService;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.amethyst.utils.Filters;
import com.aptoide.amethyst.webservices.CheckUserCredentialsRequest;
import com.aptoide.amethyst.webservices.OAuth2AuthenticationRequest;
import com.aptoide.dataprovider.AptoideSpiceHttpService;
import com.aptoide.dataprovider.webservices.Errors;
import com.aptoide.dataprovider.webservices.models.ErrorResponse;
import com.facebook.Session;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;


import java.util.HashMap;
import java.util.Locale;


import com.aptoide.amethyst.webservices.exceptions.InvalidGrantSpiceException;

/**
 * Created by fabio on 13-10-2015.
 */
public class FragmentSignIn extends Fragment {

    public static final String LOGIN_MODE_ARG = "loginMode";
    public static final String LOGIN_USERNAME_ARG = "loginUsername";
    public static final String LOGIN_PASSWORD_OR_TOKEN_ARG = "loginPasswordOrToken";
    SpiceManager spiceManager = new SpiceManager(AptoideSpiceHttpService.class);


    LoginActivity.Mode mode;
    private String username;
    private String password;
    private CheckUserCredentialsRequest request;
    private SignInCallback callback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mode = LoginActivity.Mode.values()[getArguments().getInt(LOGIN_MODE_ARG, 0)];
        username = getArguments().getString(LOGIN_USERNAME_ARG);
        password = getArguments().getString(LOGIN_PASSWORD_OR_TOKEN_ARG);

    }

    @Override
    public void onResume() {
        super.onResume();
        submit(mode, username, password, "");
    }

    @Override
    public void onStart() {
        super.onStart();
        spiceManager.start(getActivity());
    }

    @Override
    public void onStop() {
        super.onStop();
        spiceManager.shouldStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page_signing_in, container, false);
    }

    public void submit(final LoginActivity.Mode mode, final String username, final String passwordOrToken, final String nameForGoogle) {

        //final String userName = ((EditText) findViewById(R.id.username)).getAvatar().toString();
        //final String userPass = ((EditText) findViewById(R.id.password)).getAvatar().toString();

        final String accountType = Aptoide.getConfiguration().getAccountType();

        OAuth2AuthenticationRequest oAuth2AuthenticationRequest = new OAuth2AuthenticationRequest();

        oAuth2AuthenticationRequest.setPassword(passwordOrToken);
        oAuth2AuthenticationRequest.setUsername(username);
        oAuth2AuthenticationRequest.setMode(mode);
        oAuth2AuthenticationRequest.setNameForGoogle(nameForGoogle);



        spiceManager.execute(oAuth2AuthenticationRequest, new RequestListener<OAuth>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {

                String error;

                if(spiceException.getCause() instanceof InvalidGrantSpiceException && spiceException.getCause().getMessage().equals("Invalid username and password combination")){
                    error = getString(R.string.error_AUTH_1);
                } else {
                    error = getString(R.string.error_occured);
                }

                Session session = Session.getActiveSession();

                if (session != null && session.isOpened()) {
                    session.closeAndClearTokenInformation();
                }

                Toast.makeText(Aptoide.getContext(), error, Toast.LENGTH_SHORT).show();
                onError();

                android.support.v4.app.DialogFragment pd = (android.support.v4.app.DialogFragment) getFragmentManager().findFragmentByTag("pleaseWaitDialog");
                if (pd != null) {
                    pd.dismissAllowingStateLoss();
                }
            }

            @Override
            public void onRequestSuccess(final OAuth oAuth) {


                if(oAuth.getStatus() != null && oAuth.getStatus().equals("FAIL")){

                    AptoideUtils.UI.toastError(oAuth.getError());
                    Session session = Session.getActiveSession();

                    if (session != null && session.isOpened()) {
                        session.closeAndClearTokenInformation();
                    }

                    onError();

                }else{
                    getUserInfo(oAuth, username, mode, accountType, passwordOrToken);
                    Analytics.Login.login(username, mode);
                }

            }
        });

    }

    private void getUserInfo(final OAuth oAuth, final String username, final LoginActivity.Mode mode, final String accountType, final String passwordOrToken) {
        request = new CheckUserCredentialsRequest();


        String deviceId = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);

        request.setRegisterDevice(true);

        request.setSdk(String.valueOf(AptoideUtils.HWSpecifications.getSdkVer()));
        request.setDeviceId(deviceId);
        request.setCpu(AptoideUtils.HWSpecifications.getAbis());
        request.setDensity(String.valueOf(AptoideUtils.HWSpecifications.getNumericScreenSize(getActivity())));
        request.setOpenGl(String.valueOf(AptoideUtils.HWSpecifications.getGlEsVer(getActivity())));
        request.setModel(Build.MODEL);
        request.setScreenSize(Filters.Screen.values()[AptoideUtils.HWSpecifications.getScreenSize(getActivity())].name().toLowerCase(Locale.ENGLISH));

        request.setToken(oAuth.getAccess_token());

        spiceManager.execute(request, new RequestListener<CheckUserCredentialsJson>() {

            @Override
            public void onRequestFailure(SpiceException e) {

                Session session = Session.getActiveSession();

                if (session != null && session.isOpened()) {
                    session.closeAndClearTokenInformation();
                }

                Toast.makeText(Aptoide.getContext(), R.string.error_occured, Toast.LENGTH_SHORT).show();
                onError();

                android.support.v4.app.DialogFragment pd = (android.support.v4.app.DialogFragment) getFragmentManager().findFragmentByTag("pleaseWaitDialog");
                if (pd != null) {
                    pd.dismissAllowingStateLoss();
                }
            }

            @Override
            public void onRequestSuccess(CheckUserCredentialsJson checkUserCredentialsJson) {

                android.support.v4.app.DialogFragment pd = (android.support.v4.app.DialogFragment) getFragmentManager().findFragmentByTag("pleaseWaitDialog");

                if (pd != null) {
                    pd.dismissAllowingStateLoss();
                }


                if ("OK".equals(checkUserCredentialsJson.getStatus())) {
                    LoginActivity.updatePreferences(checkUserCredentialsJson, username, mode.name(), oAuth.getAccess_token());
                    Bundle data = new Bundle();
                    data.putString(AccountManager.KEY_ACCOUNT_NAME, username);
                    data.putString(AccountManager.KEY_ACCOUNT_TYPE, accountType);
                    data.putString(AccountManager.KEY_AUTHTOKEN, oAuth.getRefreshToken());
                    data.putString(AccountManager.KEY_PASSWORD, passwordOrToken);

                    final Intent res = new Intent();
                    res.putExtras(data);
                    finishLogin(res);
                } else {
                    final HashMap<String, Integer> errorsMapConversion = Errors.getErrorsMap();
                    Integer stringId;
                    String message;
                    for (ErrorResponse error : checkUserCredentialsJson.getErrors()) {
                        stringId = errorsMapConversion.get( error.code );
                        if(stringId != null) {
                            message = getString( stringId );
                        } else {
                            message = error.msg;
                        }
                        onError();

                        Toast.makeText(Aptoide.getContext(), message, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void onError() {
        callback = (SignInCallback) getParentFragment();
        callback.loginError();
    }


    private void finishLogin(Intent intent) {

        String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        String accountPassword = intent.getStringExtra(AccountManager.KEY_PASSWORD);
        String token = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);

        final Account account = new Account(accountName, intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));

        String accountType = Aptoide.getConfiguration().getAccountType();
        String authTokenType = AptoideConfiguration.AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS;
        final Activity activity = getActivity();
        AccountManager.get(getActivity()).addAccount(accountType, authTokenType, new String[]{"timelineLogin"}, intent.getExtras(), getActivity(), new AccountManagerCallback<Bundle>() {
            @Override
            public void run(AccountManagerFuture<Bundle> future) {
                if (activity != null) {
                    activity.startService(new Intent(activity, RabbitMqService.class));
                }

                ContentResolver.setSyncAutomatically(account, Aptoide.getConfiguration().getUpdatesSyncAdapterAuthority(), true);
                ContentResolver.addPeriodicSync(account, Aptoide.getConfiguration().getUpdatesSyncAdapterAuthority(), new Bundle(), 43200);
                ContentResolver.setSyncAutomatically(account, Aptoide.getConfiguration(). getAutoUpdatesSyncAdapterAuthority(), true);
                callback = (SignInCallback) getParentFragment();
                if (callback != null) callback.loginEnded();

            }
        }, new Handler(Looper.getMainLooper()));


    }

    public interface SignInCallback {
        void loginEnded();
        void loginError();
    }
}
