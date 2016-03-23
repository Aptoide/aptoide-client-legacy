package com.aptoide.amethyst.downloadmanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.analytics.Analytics;
import com.aptoide.amethyst.database.AptoideDatabase;
import com.aptoide.amethyst.downloadmanager.model.FinishedApk;
import com.aptoide.amethyst.preferences.AptoidePreferences;
import com.aptoide.amethyst.preferences.SecurePreferences;
import com.aptoide.amethyst.ui.PermissionsActivity;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.amethyst.utils.Logger;
import com.aptoide.amethyst.webservices.OauthErrorHandler;
import com.aptoide.models.CpiAptwordsResponse;
import com.aptoide.models.RollBackItem;
import com.google.gson.Gson;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.converter.JacksonConverter;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created with IntelliJ IDEA.
 * User: rmateus
 * Date: 08-07-2013
 * Time: 14:49
 * To change this template use File | Settings | File Templates.
 */
public class DownloadExecutor implements Serializable {


    private final FinishedApk apk;
    private final String path;

    public DownloadExecutor(FinishedApk apk) {
        this.apk = apk;
        this.path = apk.getPath();
    }

    public FinishedApk getApk() {
        return apk;
    }

    public interface RegisterUserApkInstall{

        @POST("/3/registerUserApkInstall")
        @FormUrlEncoded
        Object call(@FieldMap HashMap<String, String> map);

    }

    public void execute() {

        final Context context = Aptoide.getContext();
        boolean isUpdate = false;
        AptoideDatabase db = new AptoideDatabase(Aptoide.getDb());

        try {

            PackageInfo pkgInfo = context.getPackageManager().getPackageInfo(apk.getApkid(), 0);
            isUpdate = true;
            // Update
            File apkFile = new File(pkgInfo.applicationInfo.sourceDir);
            String md5_sum = AptoideUtils.Algorithms.md5Calc(apkFile);

            db.insertRollbackAction(new RollBackItem(apk.getName(), apk.getApkid(), apk.getVersion(), pkgInfo.versionName, apk.getIconPath(), null, md5_sum, RollBackItem.Action.UPDATING, apk.getRepoName()));

        } catch (PackageManager.NameNotFoundException e) {

            // Check if its a downgrade
            if (!db.updateDowngradingAction(apk.getApkid())) {
                // New Installation
                db.insertRollbackAction(new RollBackItem(apk.getName(), apk.getApkid(), apk.getVersion(), null, apk.getIconPath(), null, null, RollBackItem.Action.INSTALLING.setReferrer(apk.getReferrer()), apk.getRepoName()));
            }
        }

        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(Aptoide.getContext());

        if(apk.getCpiUrl() != null) {

            new Thread(new Runnable() {
                @Override
                public void run() {

                    OkHttpClient client = new OkHttpClient();


                    FormEncodingBuilder formBody = new FormEncodingBuilder();
                    String oemid = Aptoide.getConfiguration().getExtraId();

                    formBody.add("dummyproperty", "dummyvalue");

                    if(!TextUtils.isEmpty(oemid)){
                        formBody.add("oemid", oemid);
                    }

                    com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder().post(formBody.build()).url(apk.getCpiUrl()).build();

                    //RegisterAdRequest registerAdRequest = new RegisterAdRequest(context, apk.getCpiUrl());

                    //registerAdRequest.setHttpRequestFactory(AndroidHttp.newCompatibleTransport().createRequestFactory());
                    try {
                        Response execute = client.newCall(request).execute();

                        String response = execute.body().string();

                        CpiAptwordsResponse cpiAptwordsResponse = new Gson().fromJson(response, CpiAptwordsResponse.class);

                        Analytics.LTV.cpi(apk.getApkid(), cpiAptwordsResponse.getRevenue());

                        //registerAdRequest.loadDataFromNetwork();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    HashMap<String, String> map = new HashMap<String, String>();

                    map.put("referrer", String.valueOf(apk.getReferrer() != null));

//                    FlurryAgent.logEvent("CPI_URL_REFERRER", map);
                    apk.setCpiUrl(null);
                }
            }).start();

        }

        if(sPref.getBoolean(AptoidePreferences.SHARE_TIMELINE_DOWNLOAD_BOOL, false) && apk.getId() > 0 && !isUpdate) {

            try {

                RestAdapter adapter = new RestAdapter.Builder().setConverter(new JacksonConverter()).setEndpoint("http://webservices.aptoide.com/webservices").build();
                HashMap<String, String> parameters = new HashMap<String, String>();

                parameters.put("access_token", SecurePreferences.getInstance().getString("access_token", null));
                parameters.put("appid", String.valueOf(apk.getId()));

                try {
                    adapter.create(RegisterUserApkInstall.class).call(parameters);

                } catch (RetrofitError e) {
                    OauthErrorHandler.handle(e);
                    Logger.printException(e);
                    try {
                        adapter.create(RegisterUserApkInstall.class).call(parameters);
                    } catch (Exception e1) {
                        Logger.printException(e1);
                    }

                } catch (Exception e) {
                    Logger.printException(e);
                }
            } catch (Exception e) {
                Logger.printException(e);
            }

        }

        if (Aptoide.IS_SYSTEM || (sPref.getBoolean("allowRoot", true) && DownloadUtils.canRunRootCommands() && !apk.getApkid().equals(context.getPackageName()))) {

            Intent i = new Intent(context, PermissionsActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            i.putExtra("apk", (Parcelable) apk);
            i.putStringArrayListExtra("permissions", apk.getPermissionsList());
            context.startActivity(i);

        } else {

            File file = new File(path);
            if (path.contains(Aptoide.getContext().getFilesDir().getPath())) {
                file.setReadable(true, false);
                Aptoide.getConfiguration().resetPathCacheApks();
            }
            Intent install = new Intent(Intent.ACTION_VIEW);
            install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= 14)
                install.putExtra(Intent.EXTRA_INSTALLER_PACKAGE_NAME, context.getPackageName());
            install.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            Logger.d("Aptoide", "Installing app: " + path);
            context.startActivity(install);
        }
    }

}