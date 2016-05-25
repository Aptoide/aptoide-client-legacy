package com.aptoide.amethyst.database;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Toast;

import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.R;
import com.aptoide.amethyst.analytics.Analytics;
import com.aptoide.amethyst.database.provider.DatabaseProvider;
import com.aptoide.amethyst.database.schema.Schema;
import com.aptoide.amethyst.utils.Logger;
import com.aptoide.dataprovider.webservices.models.UpdatesApi;
import com.aptoide.dataprovider.webservices.models.UpdatesResponse;
import com.aptoide.dataprovider.webservices.models.v7.GetAppMeta;
import com.aptoide.models.InstalledPackage;
import com.aptoide.models.RollBackItem;
import com.aptoide.models.ScheduledDownloadItem;
import com.aptoide.models.stores.Login;
import com.aptoide.models.stores.Store;
import com.aptoide.models.displayables.UpdateRow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by rmateus on 17/06/15.
 */
public class AptoideDatabase {

    private SQLiteDatabase database;


    public AptoideDatabase(SQLiteDatabase database) {
        this.database = database;
    }

    public String getRollbackRepo(String packageName){
        String repo = null;

        Cursor c = database.rawQuery("select reponame from rollbacktbl where package_name = ? and action='Updated' or action='Installed'", new String[]{ packageName });

        if(c.moveToFirst()){
            repo = c.getString(c.getColumnIndex("reponame"));
        }

        c.close();
        return repo;
    }

    public List<UpdatesApi.Package> getUpdates(int i) {

        final int ONE_DAY_MILIS = 86400000;
        long yesterday = System.currentTimeMillis() - ONE_DAY_MILIS;

        Cursor cursor = database.rawQuery(
                "select * from updates where timestamp < ? and updates.package_name not in (select package_name from excluded) order by timestamp asc limit ?",
                new String[]{String.valueOf(yesterday), String.valueOf(i)});

        ArrayList<UpdatesApi.Package> list = new ArrayList<>();

        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
            UpdatesApi.Package aPackage = new UpdatesApi.Package();
            aPackage.signature = cursor.getString(cursor.getColumnIndex("signature"));
            aPackage.vercode = cursor.getInt(cursor.getColumnIndex("version_code"));
            aPackage.packageName = cursor.getString(cursor.getColumnIndex("package_name"));
            list.add(aPackage);
        }

        cursor.close();

        return list;

    }

    public List<UpdatesResponse.UpdateApk> getAvailableUpdates(final Context context) {
        final PackageManager pm = context.getPackageManager();

        final ArrayList<UpdatesResponse.UpdateApk> updateApks = new ArrayList<>();
        final ArrayList<String> excludedPackages = getExcludedApksAsList();
        final Cursor cursor = getUpdatesTabList();

        final int path = cursor.getColumnIndex(Schema.Updates.COLUMN_URL);
        final int packageName = cursor.getColumnIndex(Schema.Updates.COLUMN_PACKAGE);
        final int versionName = cursor.getColumnIndex(Schema.Updates.COLUMN_UPDATE_VERNAME);
        final int md5sum = cursor.getColumnIndex(Schema.Updates.COLUMN_MD5);
        final int icon = cursor.getColumnIndex(Schema.Updates.COLUMN_ICON);
        final int fileSize = cursor.getColumnIndex(Schema.Updates.COLUMN_FILESIZE);
        final int path_alt = cursor.getColumnIndex(Schema.Updates.COLUMN_ALT_URL);

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            final String packageNameValue = cursor.getString(packageName);
            final String nameValue;
            try {
                final ApplicationInfo info = pm.getPackageInfo(packageNameValue, 0).applicationInfo;
                nameValue = info.loadLabel(pm).toString();
            } catch (PackageManager.NameNotFoundException e) {
                continue;
            }
            final String pathValue = cursor.getString(path);
            if (excludedPackages.contains(packageNameValue) || pathValue == null) {
                continue;
            }
            final UpdatesResponse.UpdateApk updateApk = new UpdatesResponse.UpdateApk();
            updateApk.id = 0;
            updateApk.name = nameValue;
            updateApk.packageName = packageNameValue;
            updateApk.versionName = cursor.getString(versionName);
            updateApk.md5sum = cursor.getString(md5sum);
            updateApk.icon = cursor.getString(icon);
            updateApk.size = cursor.getLong(fileSize);

            updateApk.apk = new UpdatesResponse.UpdateApk.Apk();
            updateApk.apk.path = pathValue;
            updateApk.apk.path_alt = cursor.getString(path_alt);
            updateApk.apk.filesize = updateApk.size;

            updateApks.add(updateApk);
        }

        cursor.close();
        return updateApks;
    }

    public Cursor getUpdatesTabList() {

        Cursor cursor = database.rawQuery("select * from updates order by url desc", null);
        cursor.getCount(); //probably safe to delete this line
        return cursor;

    }

    public ArrayList<String> getExcludedApksAsList() {

        Cursor c = getExcludedApks();
        ArrayList<String> excludedPackages = new ArrayList<>();

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            excludedPackages.add(c.getString(c.getColumnIndex(Schema.Excluded.COLUMN_PACKAGE_NAME)));
        }

        c.close();

        return excludedPackages;
    }

    public Cursor getExcludedApks() {
        return database.query(Schema.Excluded.getName(), null, null, null, null, null, null);
    }

    public void deleteFromExcludeUpdate(String apkid, int vercode) {
        database.delete(Schema.Excluded.getName(),
                "package_name = ? and vercode = ?",
                new String[]{apkid, vercode + ""});
    }

    public void deleteFromExcludeUpdate(List<String[]> excludedList) {

        database.beginTransaction();
        try {

            for (String[] strings : excludedList) {

                database.delete(Schema.Excluded.getName(),
                        "package_name = ? and vercode = ?",
                        strings);
            }

            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    public ArrayList<String> getExcludedAds() {

        ArrayList<String> excludedAds = new ArrayList<>();

        Cursor cursor = database.rawQuery("select * from excludedads", null);

        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
            excludedAds.add(cursor.getString(cursor.getColumnIndex(Schema.ExcludedAds.COLUMN_PACKAGE)));
        }

        return excludedAds;
    }

    public Cursor getUpdates() {

        //select  apk.package_name, (installed.version_code < apk.version_code) as is_update, apk.version_code as repoVC from apk join installed on  apk.package_name = installed.package_name group by apk.package_name, is_update order by is_update desc
        Cursor c = database.rawQuery("select * from updates where url not null and updates.package_name not in (select package_name from excluded) ", null);
        return c;
    }

    private ContentValues buildContentValuesFromStore(Store store){
        ContentValues values = new ContentValues();

        values.put(Schema.Repo.COLUMN_AVATAR, store.getAvatar());

        if(store.getId() > 0) {
            values.put(Schema.Repo.COLUMN_ID, store.getId());
        }
        values.put(Schema.Repo.COLUMN_DOWNLOADS, store.getDownloads());
        values.put(Schema.Repo.COLUMN_THEME, store.getTheme());
        values.put(Schema.Repo.COLUMN_DESCRIPTION, store.getDescription());
        values.put(Schema.Repo.COLUMN_ITEMS, store.getItems());
        values.put(Schema.Repo.COLUMN_VIEW, store.getView());

        if (store.getLogin() != null) {
            values.put(Schema.Repo.COLUMN_USERNAME, store.getLogin().getUsername());
            values.put(Schema.Repo.COLUMN_PASSWORD, store.getLogin().getPassword());
        }
        values.put(Schema.Repo.COLUMN_IS_USER, true);
        return values;
    }

    public boolean hasInstalled(){

        Cursor cursor = database.rawQuery("select 1 from updates", null);

        try{
            return cursor.moveToFirst();
        }finally {
            cursor.close();
        }


    }

    public List<String> getSubscribedStoreNames() {
        ArrayList<String> storeNames = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = getStoresCursor();
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                storeNames.add(cursor.getString(cursor.getColumnIndex(Schema.Repo.COLUMN_NAME)));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return storeNames;
    }

    public Cursor getStoresCursor() {
        Cursor c = database.rawQuery("select * from repo where is_user = 1", null);
        c.getCount();
        return c;
    }

    public long updateStore(Store store, long l) {
        ContentValues values = BuildContentValuesFromStore(store);
        return database.update(Schema.Repo.getName(), values, "id_repo = ?", new String[]{l + ""});
    }

    public long insertStore(Store store) {

        Cursor servers = getStoresCursor();

        for(servers.moveToFirst(); !servers.isAfterLast();servers.moveToNext()){
            if(servers.getString(servers.getColumnIndex("name")).equals(store.getName())){
                return servers.getLong(servers.getColumnIndex("id_repo"));
            }
        }

        ContentValues values = buildContentValuesFromStore(store);


        values.put(Schema.Repo.COLUMN_URL, store.getBaseUrl());
        values.put(Schema.Repo.COLUMN_NAME, store.getName());

        invalidateUpdates();

        return database.insert(Schema.Repo.getName(), null, values);
    }

    @Nullable
    public Login getStoreLogin(@Nullable final String name, @Nullable final Long id) {
        if (name == null && id == null) {
            return null;
        }
        final Cursor stores = getStoresCursor();
        final int columnName = stores.getColumnIndex(Schema.Repo.COLUMN_NAME);
        final int columnId = stores.getColumnIndex(Schema.Repo.COLUMN_ID);
        final int columnUserName = stores.getColumnIndex(Schema.Repo.COLUMN_USERNAME);
        final int columnPassword = stores.getColumnIndex(Schema.Repo.COLUMN_PASSWORD);
        try {
            for (stores.moveToFirst(); !stores.isAfterLast(); stores.moveToNext()) {
                final Long storeId = stores.getLong(columnId);
                final String storeName = stores.getString(columnName);
                if (!storeId.equals(id) && !TextUtils.equals(name, storeName)) {
                    continue;
                }
                final String userName = stores.getString(columnUserName);
                final String password = stores.getString(columnPassword);
                if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(password)) {
                    return null;
                }
                final Login login = new Login();
                login.setUsername(userName);
                login.setPasswordSha1(password);
                return login;
            }
            return null;
        } finally {
            stores.close();
        }
    }

    @Nullable
    public Login getStoreLogin(@NonNull final Long id) {
        return getStoreLogin(null, id);
    }

    @Nullable
    public Login getStoreLogin(@NonNull final String name) {
        return getStoreLogin(name, null);
    }

    public void updateStoreLogin(final long storeId, @NonNull final Login login) {
        final ContentValues values = new ContentValues();
        values.put(Schema.Repo.COLUMN_USERNAME, login.getUsername());
        values.put(Schema.Repo.COLUMN_PASSWORD, login.getPasswordSha1());
        database.update(Schema.Repo.getName(), values, "id_repo = ?", new String[]{storeId + ""});
    }

    public long insertStore(Store store, Context mContext) {

        // Defines a new Uri object that receives the result of the insertion
        Uri mNewUri;

        // Defines an object to contain the new values to insert
        ContentValues values = buildContentValuesFromStore(store);

        /*
         * Sets the values of each column and inserts the word. The arguments to the "put"
         * method are "column name" and "value"
         */
        values.put(Schema.Repo.COLUMN_URL, store.getBaseUrl());
        values.put(Schema.Repo.COLUMN_NAME, store.getName());


        mNewUri = mContext.getContentResolver().insert(
                DatabaseProvider.ProviderConstants.CONTENT_URI,
                values
        );

        invalidateUpdates();

        return ContentUris.parseId(mNewUri);

//        return database.insert(Schema.Repo.getName(), null, values);
    }

    public void updatePackage(UpdatesResponse.UpdateApk aPackage) {

        ContentValues values = new ContentValues();

        values.put(Schema.Updates.COLUMN_TIMESTAMP, System.currentTimeMillis());
        values.put(Schema.Updates.COLUMN_ALT_URL, aPackage.apk.path_alt);
        values.put(Schema.Updates.COLUMN_URL, aPackage.apk.path);
        values.put(Schema.Updates.COLUMN_FILESIZE, aPackage.apk.filesize.intValue());
        values.put(Schema.Updates.COLUMN_UPDATE_VERNAME, aPackage.versionName);
        values.put(Schema.Updates.COLUMN_REPO, aPackage.storeName);
        values.put(Schema.Updates.COLUMN_MD5, aPackage.md5sum);
        values.put(Schema.Updates.COLUMN_ICON, aPackage.icon);
        values.put(Schema.Updates.COLUMN_UPDATE_VERCODE, aPackage.vercode);

        database.update(Schema.Updates.getName(), values, "package_name = ?", new String[]{aPackage.packageName});

    }



    public void insertInstalled(UpdatesApi.Package apk) {

        ContentValues values = new ContentValues();

        values.put(Schema.Updates.COLUMN_PACKAGE, apk.packageName);
        values.put(Schema.Updates.COLUMN_VERCODE, apk.vercode.intValue());
        values.put(Schema.Updates.COLUMN_SIGNATURE, apk.signature);
        values.put(Schema.Updates.COLUMN_TIMESTAMP, 0);

        Cursor cursor = database.rawQuery("select 1 from updates where package_name = ?", new String[]{apk.packageName});

        if(cursor.getCount()==0){
            Logger.d("databaseUpdate ",""+database.insert(Schema.Updates.getName(), null, values));
        }

        cursor.close();

    }

    public void invalidateUpdates(){

        ContentValues values = new ContentValues();
        values.put(Schema.Updates.COLUMN_TIMESTAMP, 0);
        database.update(Schema.Updates.getName(), values, null, null);
    }

    public void addToExcludeUpdate(UpdateRow row) {
        ContentValues values = new ContentValues();

        String apkid = row.packageName;
        int vercode = row.versionCode;
        String verName = row.versionName;
        String name = row.appName;
        String iconpath = row.icon;

        values.put(Schema.Excluded.COLUMN_PACKAGE_NAME, apkid);
        values.put(Schema.Excluded.COLUMN_VERCODE, vercode);
        values.put(Schema.Excluded.COLUMN_VERNAME, verName);
        values.put(Schema.Excluded.COLUMN_NAME, name);
        values.put(Schema.Excluded.COLUMN_ICONPATH, iconpath);
        database.insert(Schema.Excluded.getName(), null, values);
    }

    public void resetPackage(String packageName) {
        ContentValues values = new ContentValues();

        values.put(Schema.Updates.COLUMN_TIMESTAMP, System.currentTimeMillis());

        values.putNull(Schema.Updates.COLUMN_ALT_URL);
        values.putNull(Schema.Updates.COLUMN_URL);
        values.putNull(Schema.Updates.COLUMN_FILESIZE);
        values.putNull(Schema.Updates.COLUMN_UPDATE_VERNAME);
        values.putNull(Schema.Updates.COLUMN_REPO);
        values.putNull(Schema.Updates.COLUMN_ICON);
        values.putNull(Schema.Updates.COLUMN_MD5);

        database.update(Schema.Updates.getName(), values, "package_name = ?", new String[]{packageName});
    }

    private ContentValues BuildContentValuesFromStore(Store store){
        ContentValues values = new ContentValues();

        values.put(Schema.Repo.COLUMN_AVATAR, store.getAvatar());

        if(store.getId() > 0) {
            values.put(Schema.Repo.COLUMN_ID, store.getId());
        }
        values.put(Schema.Repo.COLUMN_DOWNLOADS, store.getDownloads());
        values.put(Schema.Repo.COLUMN_THEME, store.getTheme());
        values.put(Schema.Repo.COLUMN_DESCRIPTION, store.getDescription());
        values.put(Schema.Repo.COLUMN_ITEMS, store.getItems());
        values.put(Schema.Repo.COLUMN_VIEW, store.getView());

        if (store.getLogin() != null) {
            values.put(Schema.Repo.COLUMN_USERNAME, store.getLogin().getUsername());
            values.put(Schema.Repo.COLUMN_PASSWORD, store.getLogin().getPassword());
        }
        values.put(Schema.Repo.COLUMN_IS_USER, true);
        return values;
    }

    public long insertRollbackAction(RollBackItem rollBackItem) {
        ContentValues values = new ContentValues();

        values.put(Schema.RollbackTbl.COLUMN_NAME, rollBackItem.getName());
        values.put(Schema.RollbackTbl.COLUMN_APKID, rollBackItem.getPackageName());
        values.put(Schema.RollbackTbl.COLUMN_VERSION, rollBackItem.getVersion());
        values.put(Schema.RollbackTbl.COLUMN_PREVIOUS_VERSION, rollBackItem.getPreviousVersion());
        values.put(Schema.RollbackTbl.COLUMN_ICONPATH, rollBackItem.getIconPath());
        values.put(Schema.RollbackTbl.COLUMN_MD5, rollBackItem.getMd5());
        values.put(Schema.RollbackTbl.COLUMN_IS_TRUSTED, rollBackItem.getTrusted());

        String action = "";

        if(rollBackItem.getAction()!=null) {

            if (!TextUtils.isEmpty(rollBackItem.getAction().getReferrer())) {
                action = rollBackItem.getAction().toString() + "|" + rollBackItem.getAction().getReferrer();
            } else {
                action = rollBackItem.getAction().toString();
            }

        }

        values.put(Schema.RollbackTbl.COLUMN_ACTION, action);
        values.put(Schema.RollbackTbl.COLUMN_CONFIRMED, 0);
        values.put(Schema.RollbackTbl.COLUMN_REPO, rollBackItem.getRepoName());

        Cursor cursor = database.rawQuery("select 1 from rollbacktbl  where package_name = ? and confirmed = 0", new String[]{rollBackItem.getPackageName()});
        long id;
        if (cursor.getCount() == 0) {
            id = database.insert(Schema.RollbackTbl.getName(), null, values);
        } else {
            id = database.update(Schema.RollbackTbl.getName(), values, "package_name = ? and confirmed = 0", new String[]{rollBackItem.getPackageName()});
        }
        Logger.d("AptoideDatabase","RowID = "+id);
        cursor.close();
        return id;
    }

    public boolean updateDowngradingAction(String packageName) {

        ContentValues values = new ContentValues();
        values.put(Schema.RollbackTbl.COLUMN_ACTION, RollBackItem.Action.DOWNGRADING.toString());
        int updatedRows = database.update(Schema.RollbackTbl.getName(), values, "package_name = ? and action = ?", new String[]{packageName, ""});

        return updatedRows > 0;
    }

    public Cursor getApkInfo(long id) {
        Cursor c = database.rawQuery("select apk.price as price, repo.apk_path as apk_path, apk.path as path, apk.md5, apk.version_code as version_code, apk.package_name as package_name, apk.name as name, apk.version_name as version_name, apk.rating as rating, apk.downloads as downloads, apk.sdk as sdk, apk.screen as screen, apk.icon as icon, repo.icons_path as iconpath, repo.name as reponame from apk join repo on apk.id_repo = repo.id_repo where apk.id_apk = ?", new String[]{String.valueOf(id)});
        c.moveToFirst(); //probably safe to delete this line, it should be up to the callers responsibility
        return c;
    }


    public void deleteRollbackItems(){
        database.delete(Schema.RollbackTbl.getName(), null, null);
    }


    public Cursor getRollbackActions() {

        Cursor c = database.rawQuery("select rowid as _id, icon_path, version, previous_version, name, strftime('%d-%m-%Y', datetime(timestamp, 'unixepoch')) as cat_timestamp, action, package_name, md5, rollbacktbl.timestamp as real_timestamp from rollbacktbl  where rollbacktbl.confirmed = 1 order by rollbacktbl.timestamp desc", null);
        c.getCount();

        return c;
    }


    public void confirmRollBackAction(String packageName, String oldAction, String newAction) {
        ContentValues values = new ContentValues();
        values.put(Schema.RollbackTbl.COLUMN_TIMESTAMP, Long.toString(System.currentTimeMillis() / 1000));
        values.put(Schema.RollbackTbl.COLUMN_ACTION, newAction);
        values.put(Schema.RollbackTbl.COLUMN_CONFIRMED, 1);

        int result = database.update(Schema.RollbackTbl.getName(), values, "package_name = ? and action = ?", new String[]{packageName, oldAction});
        Logger.d("AptoideDatabase", "Trying to update " + packageName + " with action completed " + newAction + " RESULT: " + ((result == 1) ? "Success" : "Fail"));
    }


    public void deleteScheduledDownloadByPackageName(String id) {
        database.delete(Schema.Scheduled.getName(), "package_name = ?", new String[]{id});
    }


    public String getNotConfirmedRollbackAction(String packageName) {
        Cursor cursor = database.rawQuery("select action from rollbacktbl where package_name = ? and confirmed = ?", new String[]{packageName, Integer.toString(0)});
        int resultsCount = cursor.getCount();

        String action = null;
        if(resultsCount != 0) {
            cursor.moveToFirst();
            action = cursor.getString(0);
        }
        cursor.close();
        return action;
    }

    public String getIsTrustedAppRollbackAction(String packageName) {
        Cursor cursor = database.rawQuery("select isTrusted from rollbacktbl where package_name = ? and confirmed = ?", new String[]{packageName, Integer.toString(0)});
        int resultsCount = cursor.getCount();

        String action = GetAppMeta.File.Malware.UNKNOWN;
        if(resultsCount != 0) {
            cursor.moveToFirst();
            action = cursor.getString(0);
        }
        cursor.close();
        return action;
    }

    /**
     * Delete installed Apk from packageName
     * @param packageName
     */
    public void deleteInstalledApk(String packageName) {
        database.delete(Schema.Updates.getName(), "package_name = ?", new String[]{packageName});
    }


    public long getApkFromPackage(String param) {
        return getApkFromPackage(
                "select id_apk from apk where package_name = ? and is_compatible = 1 order by version_code ",
                new String[]{param});
    }

    public long getApkFromPackage(String param, String repo) {
        return getApkFromPackage(
                "select id_apk from apk join repo where package_name = ? and is_compatible = 1 and repo.name = ? order by version_code ",
                new String[]{param, repo});
    }

    /**
     * Table <b>apk</b> wasn't migrated from v6.
     * When receiving intents from <i>market</i>, it should search the db and open appview if found.
     *

     Caused by: android.database.sqlite.SQLiteException: no such table: apk (code 1): , while compiling: select id_apk from apk where package_name = ? and is_compatible = 1 order by version_code
     at android.database.sqlite.SQLiteConnection.nativePrepareStatement(Native Method)
     at android.database.sqlite.SQLiteConnection.acquirePreparedStatement(SQLiteConnection.java:889)
     at android.database.sqlite.SQLiteConnection.prepare(SQLiteConnection.java:500)
     at android.database.sqlite.SQLiteSession.prepare(SQLiteSession.java:588)
     at android.database.sqlite.SQLiteProgram.<init>(SQLiteProgram.java:58)
     at android.database.sqlite.SQLiteQuery.<init>(SQLiteQuery.java:37)
     at android.database.sqlite.SQLiteDirectCursorDriver.query(SQLiteDirectCursorDriver.java:44)
     at android.database.sqlite.SQLiteDatabase.rawQueryWithFactory(SQLiteDatabase.java:1316)
     at android.database.sqlite.SQLiteDatabase.rawQuery(SQLiteDatabase.java:1255)
     at com.aptoide.amethyst.database.AptoideDatabase.getApkFromPackage(AptoideDatabase.java:458)
     at com.aptoide.amethyst.database.AptoideDatabase.getApkFromPackage(AptoideDatabase.java:444)
     at com.aptoide.amethyst.receivers.IntentReceiver.startMarketIntent(IntentReceiver.java:424)
     at com.aptoide.amethyst.receivers.IntentReceiver.continueLoading(IntentReceiver.java:159)
     at com.aptoide.amethyst.receivers.IntentReceiver.onCreate(IntentReceiver.java:96)

     *
     * @param sql
     * @param selectionArgs
     * @return
     */
    private long getApkFromPackage(String sql, String[] selectionArgs) {
        Cursor c = null;
        try {
            c = database.rawQuery(sql, selectionArgs);
            if (c.moveToFirst()) {
                return c.getInt(0);
            } else {
                return 0;
            }
        }
        catch (Exception e){
            Logger.printException(e);
            return 0;
        } finally {
            if (c != null) c.close();
        }
    }

    public boolean existsRepo(String repoUrl) {
        Cursor c = null;
        try {
            c = database.rawQuery("select 1 from repo where url = ?", new String[]{repoUrl});
            return c.moveToFirst();
        } finally {
            if (c != null) c.close();
        }
    }

    public boolean existsStore(long storeId) {
        Cursor c = database.rawQuery("select * from repo where id_repo = ?", new String[]{String.valueOf(storeId)});
        return c.getCount() == 1;
    }

    public boolean existsStore(String storeName) {
        boolean exists = false;
        Cursor c = database.rawQuery("select * from repo where name = ?", new String[]{String.valueOf(storeName)});
        exists = c.getCount() == 1;
        return exists;
    }

    public Cursor getStore(long storeId) {
        Cursor c = database.rawQuery("select * from repo where id_repo = ?", new String[]{String.valueOf(storeId)});
        c.getCount();
        return c;
    }

    /**
     * Removes stores from a list of stores. Method was refactored from v6, where it also
     * removed data from two now deprecated tables: category && apk.
     * @param stores
     */
    public void removeStores(List<Store> stores) {
        Logger.d("AptoideDatabase", "Deleting stores " + stores);
        if (stores != null && stores.size() > 0) {

            database.beginTransaction();
            for (Store store: stores) {
                database.delete("repo", "id_repo = ? ", new String[]{String.valueOf(store.getId())});
            }

            database.setTransactionSuccessful();
            database.endTransaction();

            invalidateUpdates();
        }
    }

    public List<InstalledPackage> getStartupInstalled() {

        ArrayList<InstalledPackage> installedPackages = new ArrayList<>();

        Cursor c = database.rawQuery("select package_name, version_code from updates", null);

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            installedPackages.add(new InstalledPackage(null, c.getString(0), c.getInt(1), null, null));
        }

        c.close();

        return installedPackages;
    }

    public void addToAdsExcluded(String packageName){
        ContentValues values = new ContentValues();
        values.put(Schema.ExcludedAds.COLUMN_PACKAGE, packageName);
        database.insert(Schema.ExcludedAds.getName(), null, values);

    }

    public String getUnistallingActionMd5(String packageName) {
        Cursor cursor = database.rawQuery("select md5 from rollbacktbl  where package_name = ? and action = ?", new String[]{packageName, RollBackItem.Action.UNINSTALLING.toString()});
        int resultsCount = cursor.getCount();

        String md5 = null;
        if (resultsCount != 0) {
            cursor.moveToFirst();
            md5 = cursor.getString(0);
        }
        cursor.close();
        return md5;
    }

    public Cursor getScheduledDownloads() {

        Cursor c = null;

        try {
            c = database.rawQuery("select rowid as _id, * from scheduled", null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return c;
    }

    public boolean hasScheduledDownloads() {
        final Cursor c = getScheduledDownloads();
        final int count = c.getCount();
        c.close();
        return count != 0;
    }

    @NonNull
    public List<ScheduledDownloadItem> getScheduledDownloadsList() {
        final Cursor c = getScheduledDownloads();
        if (c == null) {
            return Collections.emptyList();
        }

        final List<ScheduledDownloadItem> scheduledDownloads = new ArrayList<>();
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            final ScheduledDownloadItem scheduledDownload = new ScheduledDownloadItem();
            scheduledDownload.setPackage_name(c.getString(c.getColumnIndex(Schema.Scheduled.COLUMN_PACKAGE_NAME)));
            scheduledDownload.setMd5(c.getString(c.getColumnIndex(Schema.Scheduled.COLUMN_MD5)));
            scheduledDownload.setName(c.getString(c.getColumnIndex(Schema.Scheduled.COLUMN_NAME)));
            scheduledDownload.setVersion_name(c.getString(c.getColumnIndex(Schema.Scheduled.COLUMN_VERSION_NAME)));
            scheduledDownload.setRepo_name(c.getString(c.getColumnIndex(Schema.Scheduled.COLUMN_REPO)));
            scheduledDownload.setIcon(c.getString(c.getColumnIndex(Schema.Scheduled.COLUMN_ICON)));

            scheduledDownloads.add(scheduledDownload);
        }
        c.close();

        return scheduledDownloads;
    }

    public void deleteScheduledDownload(String id) {
        database.delete(Schema.Scheduled.getName(), "md5 = ?", new String[]{id});
    }

    public void addToAmazonABTesting(String packageName) {
//        System.out.println("Debug: Amazon: Insert Amazon: " + packageName);

        ContentValues values = new ContentValues();
        values.put(Schema.AmazonABTesting.COLUMN_PACKAGE_NAME, packageName);

//        System.out.println("Debug: Amazon: Values: " + values.getAsString(Schema.AmazonABTesting.COLUMN_PACKAGE_NAME));
        database.insert(Schema.AmazonABTesting.getName(), null, values);
    }

    public void setReferrerToRollbackAction(String packageName, String referrer) {
        ContentValues values = new ContentValues();

        RollBackItem.Action action = RollBackItem.Action.INSTALLING.setReferrer(referrer);

        String actionString = action.toString() + "|" + action.getReferrer();

        values.put(Schema.RollbackTbl.COLUMN_ACTION, actionString);

        Cursor cursor = database.rawQuery("select 1 from rollbacktbl  where package_name = ? and confirmed = 0", new String[]{packageName});
        if (cursor.getCount() != 0) {
            database.update(Schema.RollbackTbl.getName(), values, "package_name = ? and confirmed = 0", new String[]{packageName});
        }
        cursor.close();
    }

    public void deleteAmazomABTesting(String packageName) {
        database.delete(Schema.AmazonABTesting.getName(), "package_name = ?", new String[]{packageName});
    }

    public boolean isAmazonABTesting(String packageName) {
        Cursor cursor = database.rawQuery("select * from amazonABTesting", null);

        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            if (packageName.equals(cursor.getString(0))) {
                return true;
            }
        }

        return false;
    }

    public void scheduledDownloadIfMd5(String apkid, String md5, String vername, String repoName, String name, String icon) {
        if (md5 != null) {
            insertScheduledDownload(apkid, md5, vername, repoName, name, icon);
            Toast.makeText(Aptoide.getContext(), R.string.added_to_scheduled, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(Aptoide.getContext(), R.string.please_wait, Toast.LENGTH_SHORT).show();
        }
    }

    private void insertScheduledDownload(String apkid, String md5, String vername, String repoName, String name, String icon) {

        Cursor c = database.query(Schema.Scheduled.getName(), null,
                "repo_name = ? and md5 = ?",
                new String[] { repoName, md5 + "" }, null, null, null);

        if (c.moveToFirst()) {
            c.close();
            return;
        }

        c.close();

        ContentValues values = new ContentValues();
        values.put(Schema.Scheduled.COLUMN_NAME, name);
        values.put(Schema.Scheduled.COLUMN_PACKAGE_NAME, apkid);
        values.put(Schema.Scheduled.COLUMN_VERSION_NAME, vername);
        values.put(Schema.Scheduled.COLUMN_REPO, repoName);
        values.put(Schema.Scheduled.COLUMN_ICON, icon);
        values.put(Schema.Scheduled.COLUMN_MD5, md5);
        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(Aptoide.getContext());
        sPref.edit().putBoolean("schTrigger", true).commit();

        database.insert(Schema.Scheduled.getName(), null, values);
    }


    public SQLiteDatabase getDatabase() {
        return database;
    }

}