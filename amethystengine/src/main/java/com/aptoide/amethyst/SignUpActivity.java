package com.aptoide.amethyst;

import android.accounts.AccountManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.aptoide.amethyst.analytics.Analytics;
import com.aptoide.amethyst.dialogs.AptoideDialog;
import com.aptoide.amethyst.events.BusProvider;
import com.aptoide.amethyst.events.OttoEvents;
import com.aptoide.amethyst.model.json.CheckUserCredentialsJson;
import com.aptoide.amethyst.model.json.OAuth;
import com.aptoide.amethyst.preferences.SecurePreferences;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.amethyst.utils.Configs;
import com.aptoide.amethyst.utils.Filters;
import com.aptoide.amethyst.webservices.CheckUserCredentialsRequest;
import com.aptoide.amethyst.webservices.CreateUserRequest;
import com.aptoide.amethyst.webservices.Errors;
import com.aptoide.dataprovider.AptoideSpiceHttpService;
import com.aptoide.dataprovider.webservices.models.Constants;
import com.aptoide.dataprovider.webservices.models.ErrorResponse;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.HashMap;
import java.util.Locale;


/**
 * Created by rmateus on 30-12-2013.
 */
public class SignUpActivity extends AptoideBaseActivity {
    /*  private String TAG = "SignUp";
        private String mAccountType;*/
    private SpiceManager spiceManager = new SpiceManager(AptoideSpiceHttpService.class);
    private boolean showPassword = true;
    private EditText password_box;
    private EditText emailBox;
    private String activityTitle = "Register";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Aptoide.getThemePicker().setAptoideTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_create_user);


        findViewById(R.id.submitCreateUser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
//                FlurryAgent.logEvent("My_Account_Clicked_On_Sign_Up_Button");
            }
        });

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar!= null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.register));
        }
        emailBox = (EditText) findViewById(R.id.username);
        password_box = (EditText) findViewById(R.id.password);
        password_box.setTransformationMethod(new PasswordTransformationMethod());

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
//        passBox.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//
//                if (hasFocus) {
//                    showPassword = false;
//                    passBox.setTransformationMethod(null);
//                    passBox.setCompoundDrawablesWithIntrinsicBounds(null, null, showPasswordRes, null);
//                } else {
//                    showPassword = true;
//                    passBox.setTransformationMethod(new PasswordTransformationMethod());
//                    passBox.setCompoundDrawablesWithIntrinsicBounds(null, null, hidePasswordRes, null);
//                }
//            }
//        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        spiceManager.start(this);
//        FlurryAgent.onStartSession(this, getResources().getString(R.string.FLURRY_KEY));
    }

    @Override
    protected void onStop() {
        super.onStop();
        spiceManager.shouldStop();
//        FlurryAgent.onEndSession(this);
    }

    public final static String PARAM_USER_PASS = "USER_PASS";


    private void createAccount() {

        // Validation!

        if (emailBox.getText().toString().length() == 0 && password_box.getText().toString().length() == 0) {
            Toast.makeText(getApplicationContext(), R.string.no_email_and_pass_error_message, Toast.LENGTH_LONG).show();
            return;
        } else if (password_box.getText().toString().length() == 0) {
            Toast.makeText(getApplicationContext(), R.string.no_pass_error_message, Toast.LENGTH_LONG).show();
            return;
        } else if (emailBox.getText().toString().length() == 0) {
            Toast.makeText(getApplicationContext(), R.string.no_email_error_message, Toast.LENGTH_LONG).show();
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
                String errorMessages = AptoideUtils.ServerConnectionUtils.getErrorCodeFromErrorList(SignUpActivity.this, oAuth.errors);
                if (!TextUtils.isEmpty(errorMessages)) {
                    Toast.makeText(SignUpActivity.this, errorMessages, Toast.LENGTH_SHORT).show();
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
                request.setDensity(String.valueOf(AptoideUtils.HWSpecifications.getNumericScreenSize(SignUpActivity.this)));
                request.setOpenGl(String.valueOf(AptoideUtils.HWSpecifications.getGlEsVer(SignUpActivity.this)));
                request.setModel(Build.MODEL);
                request.setScreenSize(Filters.Screen.values()[AptoideUtils.HWSpecifications.getScreenSize(SignUpActivity.this)].name().toLowerCase(Locale.ENGLISH));

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
                                    .getDefaultSharedPreferences(SignUpActivity.this)
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
                            finish();
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


//        CheckUserCredentialsRequest request = new CheckUserCredentialsRequest();
//
//        request.setUser(userName);
//        try {
//            request.setPassword(AptoideUtils.Algorithms.computeSHA1sum(userPass));
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//
//
//        spiceManager.execute(request, new RequestListener<CheckUserCredentialsJson>() {
//            @Override
//            public void onRequestFailure(SpiceException e) {
//                Toast.makeText(getBaseContext(), "An error ocurred.", Toast.LENGTH_SHORT).show();
//
//            }
//
//            @Override
//            public void onRequestSuccess(CheckUserCredentialsJson checkUserCredentialsJson) {
//                Bundle data = new Bundle();
//                data.putString(AccountManager.KEY_ACCOUNT_NAME, userName);
//                data.putString(AccountManager.KEY_ACCOUNT_TYPE, accountType);
//                data.putString(AccountManager.KEY_AUTHTOKEN, checkUserCredentialsJson.getToken());
//                data.putString(PARAM_USER_PASS, userPass);
//                final Intent res = new Intent();
//                res.putExtras(data);
//                finishLogin(res);
//
//
//            }
//        });
//
//
//        new AsyncTask<String, Void, Intent>() {
//
//            String name = ((TextView) findViewById(R.id.name)).getAvatar().toString().trim();
//            String accountName = ((TextView) findViewById(R.id.accountName)).getAvatar().toString().trim();
//            String accountPassword = ((TextView) findViewById(R.id.accountPassword)).getAvatar().toString().trim();
//
//            @Override
//            protected Intent doInBackground(String... params) {
//
//                Log.d("udinic", TAG + "> Started authenticating");
//
//                String authtoken = null;
//                Bundle data = new Bundle();
//                try {
//                    authtoken = sServerAuthenticate.userSignUp(name, accountName, accountPassword, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS);
//
//                    data.putString(AccountManager.KEY_ACCOUNT_NAME, accountName);
//                    data.putString(AccountManager.KEY_ACCOUNT_TYPE, mAccountType);
//                    data.putString(AccountManager.KEY_AUTHTOKEN, authtoken);
//                    data.putString(PARAM_USER_PASS, accountPassword);
//                } catch (Exception e) {
//                    data.putString(KEY_ERROR_MESSAGE, e.getMessage());
//                }
//
//                final Intent res = new Intent();
//                res.putExtras(data);
//                return res;
//            }
//
//            @Override
//            protected void onPostExecute(Intent intent) {
//                if (intent.hasExtra(KEY_ERROR_MESSAGE)) {
//                    Toast.makeText(getBaseContext(), intent.getStringExtra(KEY_ERROR_MESSAGE), Toast.LENGTH_SHORT).show();
//                } else {
//                    setResult(RESULT_OK, intent);
//                    finish();
//                }
//            }
//        }.execute();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();

        if (i == android.R.id.home || i == R.id.home) {
            setResult(RESULT_CANCELED);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected String getScreenName() {
        return "Sign Up";
    }
}
