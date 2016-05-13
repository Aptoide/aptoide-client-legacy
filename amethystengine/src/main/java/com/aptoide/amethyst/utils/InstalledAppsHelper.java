package com.aptoide.amethyst.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;

import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.database.AptoideDatabase;
import com.aptoide.dataprovider.webservices.models.UpdatesApi;
import com.aptoide.models.InstalledPackage;

import java.util.List;
import java.util.Locale;

import com.aptoide.amethyst.services.UpdatesService;

/**
 * Created by hsousa on 18/12/15.
 */
public class InstalledAppsHelper {


    public static void syncInstalledApps(Context context) {
        Logger.d("Aptoide-InstalledSync", "Syncing");
        long startTime = System.currentTimeMillis();

        InstalledAppsHelper.sync(Aptoide.getDb());

        context.startService(new Intent(context, UpdatesService.class));
        Logger.d("Aptoide-InstalledSync", "Sync complete in " + (System.currentTimeMillis() - startTime) + "ms");
    }


    private static void sync(SQLiteDatabase database) {
        AptoideDatabase db = new AptoideDatabase(database);

        try {

            db.getDatabase().beginTransaction();

            List<InstalledPackage> databaseInstalledList = db.getStartupInstalled();

            PackageManager packageManager = Aptoide.getContext().getPackageManager();
            List<PackageInfo> installedPackages = packageManager.getInstalledPackages(PackageManager.GET_SIGNATURES);

            for (PackageInfo anInstalledPackage : installedPackages) {

                try {

                    UpdatesApi.Package aPackage = new UpdatesApi.Package();
                    aPackage.signature = AptoideUtils.Algorithms.computeSHA1sumFromBytes(anInstalledPackage.signatures[0].toByteArray()).toUpperCase(Locale.ENGLISH);
                    aPackage.vercode = anInstalledPackage.versionCode;
                    aPackage.packageName = anInstalledPackage.packageName;

                    InstalledPackage apk = new InstalledPackage(
                            "",
                            anInstalledPackage.packageName,
                            anInstalledPackage.versionCode,
                            anInstalledPackage.versionName,
                            aPackage.signature);

                    if (!databaseInstalledList.contains(apk)) {
                        Logger.d("Aptoide-InstalledSync", "Adding " + apk.getPackage_name() + "-" + apk.getVersion_name());
                        db.insertInstalled(aPackage);
                    } else {
                        databaseInstalledList.remove(apk);
                        Logger.d("Aptoide-InstalledSync", "Removing from list" + apk.getPackage_name() + "-" + apk.getVersion_name());
                    }


                } catch (Exception e) {
                    Logger.printException(e);
                }

            }

            if (!databaseInstalledList.isEmpty()) {
                for (InstalledPackage installedPackage : databaseInstalledList) {
                    db.deleteInstalledApk(installedPackage.getPackage_name());
                    Logger.d("Aptoide-InstalledSync", "Removing from database" + installedPackage.getPackage_name() + "-" + installedPackage.getVersion_name());
                }
            }

        } catch (Exception e) {
            Logger.printException(e);
        } finally {
            db.getDatabase().setTransactionSuccessful();
            db.getDatabase().endTransaction();
        }
    }

}
