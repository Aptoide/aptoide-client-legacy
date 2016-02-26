package com.aptoide.amethyst;

import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.aptoide.amethyst.configuration.AptoideConfiguration;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.amethyst.utils.IconSizeUtils;
import com.aptoide.amethyst.utils.Logger;
import com.aptoide.amethyst.webservices.json.UploadAppToRepoJson;
import com.aptoide.dataprovider.AptoideSpiceHttpService;
import com.aptoide.dataprovider.webservices.json.GenericResponseV2;
import com.aptoide.dataprovider.webservices.models.ErrorResponse;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.aptoide.amethyst.fragments.Md5CalculatorFragmentTask;
import com.aptoide.amethyst.webservices.CreateRepoRequest;
import com.aptoide.amethyst.webservices.GetApkInfoRequestFromMd5;
import com.aptoide.amethyst.webservices.UploadAppToRepoRequest;
import com.aptoide.amethyst.webservices.json.GetApkInfoJson;

/**
 * Created by fabio on 22-10-2015.
 */
public class UploadApkActivity extends AptoideBaseActivity implements Md5CalculatorFragmentTask.Callback {
    Toolbar mToolbar;
    SpiceManager spiceManager = new SpiceManager(AptoideSpiceHttpService.class);
    private String packageName;

    Md5CalculatorFragmentTask mMd5TaskFragment;
    private String repoName;
    private boolean continueLoading;
    private SharedPreferences defaultSharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Aptoide.getThemePicker().setAptoideTheme(this);
        super.onCreate(savedInstanceState);

        setContentView(getContentView());
        bindViews();

        packageName = getIntent().getStringExtra("package_name");

        mToolbar.setCollapsible(false);

        setSupportActionBar(mToolbar);


        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(R.string.make_review_title);
        AccountManager accountManager = AccountManager.get(this);
        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(Aptoide.getContext());

        if (accountManager.getAccountsByType(Aptoide.getConfiguration().getAccountType()).length > 0) {
            mMd5TaskFragment = (Md5CalculatorFragmentTask) getSupportFragmentManager().findFragmentByTag("md5calc");

            if (savedInstanceState == null) {


                if (defaultSharedPreferences.contains("userRepo")) {

                    mMd5TaskFragment = new Md5CalculatorFragmentTask();

                    Bundle bundle = new Bundle();
                    bundle.putString("package_name", packageName);
                    bundle.putString("repo_name", defaultSharedPreferences.getString("userRepo", "null"));
                    mMd5TaskFragment.setArguments(bundle);

                    getSupportFragmentManager().beginTransaction().add(mMd5TaskFragment, "md5calc").commit();

                } else {

                    setupCreateRepo();


                }


            }
        } else {
            accountManager.addAccount(Aptoide.getConfiguration().getAccountType(), AptoideConfiguration.AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, null, null, this, new AccountManagerCallback<Bundle>() {
                @Override
                public void run(AccountManagerFuture<Bundle> future) {


                    try {
                        Bundle bundle = future.getResult();

                        if (defaultSharedPreferences.contains("userRepo")) {

                            continueLoading = true;


                        } else {

                            setupCreateRepo();

                        }
                    } catch (OperationCanceledException | IOException | AuthenticatorException e) {
                        e.printStackTrace();
                        finish();
                    }


                }
            }, null);
        }


    }

    protected int getContentView() {
        return R.layout.repo_create;
    }

    protected void bindViews() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    private void setupCreateRepo() {
        findViewById(R.id.please_wait).setVisibility(View.GONE);
        findViewById(R.id.content).setVisibility(View.VISIBLE);

        EditText repository = (EditText) findViewById(R.id.repository);
        EditText repoUsername = (EditText) findViewById(R.id.repo_username);
        EditText repoPassword = (EditText) findViewById(R.id.repo_password);

        findViewById(R.id.create_store).setOnClickListener(new CreateStoreListener(repository, repoUsername, repoPassword));
    }

    private void hideContent() {
        findViewById(R.id.please_wait).setVisibility(View.VISIBLE);
        findViewById(R.id.content).setVisibility(View.GONE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        spiceManager.shouldStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        spiceManager.start(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (continueLoading) {
            mMd5TaskFragment = new Md5CalculatorFragmentTask();

            Bundle bundle = new Bundle();
            bundle.putString("package_name", packageName);
            bundle.putString("repo_name", defaultSharedPreferences.getString("userRepo", "null"));

            mMd5TaskFragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction().add(mMd5TaskFragment, "md5calc").commit();
            continueLoading = false;

        }
    }

//    @Override
//    protected String getScreenName() {
//        return "Upload Apk";
//    }


    public void createRepo(final String repository, String username, String password) {

        CreateRepoRequest request = new CreateRepoRequest(repository, username, password);

        hideContent();

        spiceManager.execute(request, new RequestListener<GenericResponseV2>() {

            @Override
            public void onRequestFailure(SpiceException spiceException) {
                AptoideUtils.UI.toast(getString(R.string.error_occured));
                Logger.printException(spiceException);
                finish();
            }

            @Override
            public void onRequestSuccess(GenericResponseV2 genericResponseV2) {

                if (genericResponseV2.getErrors() != null && !genericResponseV2.getErrors().isEmpty()) {

                    for (ErrorResponse error : genericResponseV2.getErrors()) {
                        AptoideUtils.UI.toast(error.msg);
                    }
                    finish();

                } else if ("OK".equals(genericResponseV2.getStatus())) {
                    defaultSharedPreferences.edit().putString("userRepo", repository).apply();
                    mMd5TaskFragment = new Md5CalculatorFragmentTask();

                    Bundle bundle = new Bundle();
                    bundle.putString("package_name", packageName);
                    bundle.putString("repo_name", repository);

                    mMd5TaskFragment.setArguments(bundle);

                    getSupportFragmentManager().beginTransaction().add(mMd5TaskFragment, "md5calc").commit();
                    continueLoading = false;


                }

            }

        });

    }

    public static class BackupAppsDialog extends DialogFragment {

        PackageManager packageManager = Aptoide.getContext().getPackageManager();

        boolean backupappsAvailable = false;

        DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {


                    if (backupappsAvailable) {
                        Intent intent = packageManager.getLaunchIntentForPackage("pt.aptoide.backupapps");
                        AptoideUtils.UI.toast(getString(R.string.error_occured_uploading));
                        startActivity(intent);
                    } else {

                        Intent i = new Intent(getActivity(), AppViewActivity.class);
                        i.putExtra("getBackupApps", true);
                        startActivity(i);
                    }
                }

                getActivity().finish();

            }
        };

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {


            try {
                packageManager.getApplicationInfo("pt.aptoide.backupapps", 0);
                backupappsAvailable = true;
            } catch (Exception e) {
                backupappsAvailable = false;

            }

            String message;

            if (backupappsAvailable) {
                message = Aptoide.getContext().getString(R.string.upload_app_backup_apps_installed);
            } else {
                message = Aptoide.getContext().getString(R.string.upload_app_backup_apps_not_installed);
            }


            return new AlertDialog.Builder(getActivity())
                    .setIcon(R.drawable.ic_action_backup_custom)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, clickListener)
                    .setTitle(R.string.upload_app)
                    .setNegativeButton(android.R.string.cancel, clickListener)
                    .create();
        }
    }

    public class UploadAppToRepoListener implements RequestListener<UploadAppToRepoJson> {

        private String md5sum;

        public UploadAppToRepoListener(String md5sum) {

            this.md5sum = md5sum;
        }

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            AptoideUtils.UI.toast(getString(R.string.error_occured));
            finish();
        }

        @Override
        public void onRequestSuccess(UploadAppToRepoJson uploadAppToRepoJson) {
            List<ErrorResponse> errors = uploadAppToRepoJson.errors;

            if (errors != null && !errors.isEmpty() && errors.get(0).code.equals("APK-5")) {

                new BackupAppsDialog().show(getSupportFragmentManager(), "backupAppsDialog");

            } else {

                GetApkInfoRequestFromMd5 md5 = new GetApkInfoRequestFromMd5(Aptoide.getContext());
                md5.setMd5Sum(md5sum);
                //md5.setRepoName(defaultSharedPreferences.getString("userRepo", "null"));
                spiceManager.execute(md5, new RequestListener<GetApkInfoJson>() {
                    @Override
                    public void onRequestFailure(SpiceException spiceException) {
                        AptoideUtils.UI.toast(getString(R.string.error_occured));
                        finish();
                    }

                    @Override
                    public void onRequestSuccess(GetApkInfoJson getApkInfoJson) {
                        Intent intent = new Intent(Aptoide.getContext(), MakeReviewActivity.class);
                        intent.putExtra(MakeReviewActivity.EXTRA_PACKAGE, getApkInfoJson.getApk().packageName);
                        intent.putExtra(MakeReviewActivity.EXTRA_APP_NAME, getApkInfoJson.getMeta().getTitle());

                        String icon = getApkInfoJson.getApk().icon_hd;

                        if (icon != null) {
                            if (icon.contains("_icon")) {
                                String[] splittedUrl = icon.split("\\.(?=[^\\.]+$)");
                                icon = splittedUrl[0] + "_" + IconSizeUtils.generateSizeString(Aptoide.getContext()) + "." + splittedUrl[1];
                            }
                        } else {
                            icon = getApkInfoJson.getApk().getIcon();
                        }

                        ArrayList<String> screenshotsUrl = new ArrayList<String>();

                        for (GetApkInfoJson.Media.Screenshots screenshots : getApkInfoJson.getMedia().sshots_hd) {
                            String imagePath = AptoideUtils.UI.screenshotToThumb(Aptoide.getContext(), screenshots.path, screenshots.orient);
                            screenshotsUrl.add(imagePath);
                        }

                        intent.putExtra(MakeReviewActivity.EXTRA_SCREENSHOTS_URL, screenshotsUrl);

                        intent.putExtra(MakeReviewActivity.EXTRA_ICON, icon);
                        intent.putExtra(MakeReviewActivity.EXTRA_DOWNLOADS, getApkInfoJson.getMeta().getDownloads());
                        intent.putExtra(MakeReviewActivity.EXTRA_SIZE, getApkInfoJson.getApk().getSize());
                        intent.putExtra(MakeReviewActivity.EXTRA_STARS, getApkInfoJson.getMeta().getLikevotes().rating.floatValue());
                        intent.putExtra(MakeReviewActivity.EXTRA_REPO, defaultSharedPreferences.getString("userRepo", "null"));

                        startActivity(intent);
                        finish();

                    }
                });
            }
        }
    }


    @Override
    public void onPostExecute(String md5Sum) {

        getSupportFragmentManager().beginTransaction().remove(mMd5TaskFragment).commit();
        UploadAppToRepoRequest repoRequest = new UploadAppToRepoRequest();
        repoRequest.md5Sum = md5Sum;
        repoRequest.repo = defaultSharedPreferences.getString("userRepo", "null");

        spiceManager.execute(repoRequest, new UploadAppToRepoListener(md5Sum));

    }


    private class CreateStoreListener implements View.OnClickListener {
        private final EditText repository;
        private final EditText username;
        private final EditText password;

        public CreateStoreListener(EditText repository, EditText username, EditText password) {


            this.repository = repository;
            this.username = username;
            this.password = password;
        }

        @Override
        public void onClick(View v) {

            createRepo(repository.getText().toString(), username.getText().toString(), password.getText().toString());

        }

    }

    @Override
    protected String getScreenName() {
        return "Upload Apk";
    }
}
