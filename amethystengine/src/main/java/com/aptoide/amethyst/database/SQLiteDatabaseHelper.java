package com.aptoide.amethyst.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;

import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.database.schema.annotations.OnConflict;
import com.aptoide.amethyst.database.schema.annotations.SQLType;
import com.aptoide.amethyst.database.schema.Schema;
import com.aptoide.amethyst.database.schema.annotations.ColumnDefinition;
import com.aptoide.amethyst.database.schema.annotations.TableDefinition;
import com.aptoide.models.ScheduledDownloadItem;
import com.aptoide.models.StoreItemDB;
import com.aptoide.models.stores.Login;
import com.aptoide.models.displayables.ExcludedUpdate;

import java.lang.reflect.Field;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: brutus
 * Date: 04-10-2013
 * Time: 14:41
 * To change this template use File | Settings | File Templates.
 */
public class SQLiteDatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "SQLiteDatabaseHelper";
    private static SQLiteDatabaseHelper sInstance;
    private boolean primaryKeyDefined;

    private static final int DATABASE_VERSION = 31;

    public static SQLiteDatabaseHelper getInstance(Context context) {

        synchronized (SQLiteDatabaseHelper.class){
            if (sInstance == null) {

                sInstance = new SQLiteDatabaseHelper(context.getApplicationContext());
            }
        }
        return sInstance;
    }

    /**
     * Constructor should be private to prevent direct instantiation.
     * make call to static factory method "getInstance()" instead.
     */
    private SQLiteDatabaseHelper(Context context) {
        super(context, "aptoide.db", null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            createDb(db);
        } catch (IllegalAccessException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {


        dropTables(db, 0);
        dropIndexes(db, 0);
        removeSharedPreferences();


        try {
            createDb(db);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }


    }

    private void createDb(SQLiteDatabase db) throws IllegalAccessException {
        Class[] db_tables = Schema.class.getDeclaredClasses();

        String sql_stmt;

        // Table
        TableDefinition table_definition;
        ColumnDefinition column_definition;
        Field[] table_columns;
        for (Class table : db_tables) {
            primaryKeyDefined = false;
            table_columns = table.getDeclaredFields();
            table_definition = ((TableDefinition) table.getAnnotation(TableDefinition.class));

            sql_stmt = "CREATE TABLE IF NOT EXISTS " + table.getSimpleName().toLowerCase(Locale.ENGLISH) + " (";

            // Table_collumns
            Field column;

            Iterator<Field> it = Arrays.asList(table_columns).iterator();

            while (it.hasNext()) {
                column = it.next();
                column_definition = column.getAnnotation(ColumnDefinition.class);
                column.setAccessible(true);

                sql_stmt += column.get(null) + " " + column.getAnnotation(ColumnDefinition.class).type();

                if (!column_definition.defaultValue().equals("")) {
                    sql_stmt += " DEFAULT \"" + column_definition.defaultValue() + "\"";
                }

                sql_stmt += getColumnConstraints(column_definition);

                if (it.hasNext()) {
                    sql_stmt += ", ";
                }
            }

            // ------------------------- Table primary key -------------------------------


            if (!primaryKeyDefined) {
                if (table_definition != null && table_definition.primaryKey().length != 0) {
                    sql_stmt += ", ";
                    sql_stmt += getPrimaryKey(table_definition);
                }
            } else {
                if (table_definition != null && table_definition.primaryKey().length != 0) {
                    throw new IllegalArgumentException("PRIMARY KEY defined twice, at column and table level!");

                }
            }


            // --------------------------------- Table Unique Composite Fields --------------------------------------------
            if (table_definition != null && table_definition.uniques().length != 0) {
                sql_stmt += ", ";
                sql_stmt += getCompositeUniques(table_definition);
            }
            sql_stmt += ")";

            db.execSQL(sql_stmt);

            // --------------------------------------------------- Indexes Creation ----------------------------------------------

            if (table_definition != null) {
                createTableIndexes(table_definition, table.getSimpleName(), db);
            }


        }
    }

    private void createTableIndexes(TableDefinition table_definition, String table_name, SQLiteDatabase db) {
        TableDefinition.Index[] indexes = table_definition.indexes();
        String indexes_stmt;

        Iterator<TableDefinition.Index> iterator = Arrays.asList(indexes).iterator();

        TableDefinition.Index index;
        while (iterator.hasNext()) {
            indexes_stmt = "CREATE ";
            index = iterator.next();

            if (index.unique()) {
                indexes_stmt += "UNIQUE ";
            }

            indexes_stmt += "INDEX IF NOT EXISTS " + index.index_name() + " ON " + table_name + " (";

            TableDefinition.Key[] keys = index.keys();
            Iterator<TableDefinition.Key> keys_iterator = Arrays.asList(keys).iterator();

            TableDefinition.Key key;
            while (keys_iterator.hasNext()) {
                key = keys_iterator.next();
                indexes_stmt += key.field();
                if (key.descending()) {
                    indexes_stmt += " DESC";
                }
                if (keys_iterator.hasNext()) {
                    indexes_stmt += ", ";
                }
            }
            indexes_stmt += ");";

            db.execSQL(indexes_stmt);
        }
    }

    private String getCompositeUniques(TableDefinition table_definition) {
        TableDefinition.Composite_Unique[] uniques = table_definition.uniques();

        String uniques_stmt = "";
        String[] unique_fields;
        Iterator<TableDefinition.Composite_Unique> iterator = Arrays.asList(uniques).iterator();
        while (iterator.hasNext()) {
            uniques_stmt = "UNIQUE (";
            unique_fields = iterator.next().fields();

            Iterator<String> iterator1 = Arrays.asList(unique_fields).iterator();
            while (iterator1.hasNext()) {
                uniques_stmt += iterator1.next();
                if (iterator1.hasNext()) {
                    uniques_stmt += ", ";
                }
            }
            uniques_stmt += ")";

            if (iterator.hasNext()) {
                uniques_stmt += ", ";
            }
        }
        return uniques_stmt;
    }

    private String getPrimaryKey(TableDefinition table_definition) {
        String[] primary_key = table_definition.primaryKey();
        String pk = "PRIMARY KEY (";

        Iterator<String> iterator = Arrays.asList(primary_key).iterator();
        while (iterator.hasNext()) {

            pk += iterator.next();
            if (iterator.hasNext()) {
                pk += ", ";
            }
        }
        pk += ")";
        return pk;
    }

    private String getColumnConstraints(ColumnDefinition column_definition) {
        String column_constraints = "";
        if (column_definition.primaryKey()) {
            if (primaryKeyDefined) {
                throw new IllegalArgumentException("Can only define one PRIMARY KEY, to define a composite PRIMARY KEY, use @TableDefinition annotation");
            }
            primaryKeyDefined = true;
            column_constraints += " PRIMARY KEY";
        }
        if (column_definition.autoIncrement()) {
            if (!column_definition.primaryKey() || column_definition.type() != SQLType.INTEGER) {
                throw new IllegalArgumentException("AUTOINCREMENT only allowed to PRIMARY KEYs with type INTEGER");
            }
            column_constraints += " AUTOINCREMENT";
        }
        if (column_definition.unique()) {
            column_constraints += " UNIQUE";
        }
        if (column_definition.notNull()) {
            column_constraints += " NOT NULL";
        }
        if(!column_definition.onConflict().equals(OnConflict.NONE)){
            column_constraints += " ON CONFLICT " + column_definition.onConflict().name();
        }
        return column_constraints;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "SQLiteDatabaseHelper onUpgrade()");

        ArrayList<StoreItemDB> oldStores = new ArrayList<>();
        List<ScheduledDownloadItem> oldScheduledDownloads = new ArrayList<>();
        List<ExcludedUpdate> oldExcludedUpdates = new ArrayList<>();

        if (oldVersion >= 13 && oldVersion <= 20) {

            try {
                Cursor c = db.query("repo", new String[]{"url", "name", "username", "password"}, null, null, null, null, null);
                for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

                    StoreItemDB server = new StoreItemDB();
                    server.setUrl(c.getString(0));
                    server.setName(c.getString(1));

                    if(c.getString(2)!=null){
                        server.login = new Login();
                        server.login.setUsername(c.getString(2));
                        server.login.setPassword(c.getString(3));
                    }

                    oldStores.add(server);
                }
                c.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if (oldVersion >= 21 && oldVersion < 28 ){
            try {
                Cursor c = db.query("repo", null, Schema.Repo.COLUMN_IS_USER +"=?", new String[]{"1"}, null, null, null);

                for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

                    StoreItemDB store = new StoreItemDB();
                    store.setId(c.getInt(c.getColumnIndex(Schema.Repo.COLUMN_ID)));
                    store.setUrl(c.getString(c.getColumnIndex(Schema.Repo.COLUMN_URL)));
                    store.setName(c.getString(c.getColumnIndex(Schema.Repo.COLUMN_NAME)));
                    store.setTheme(c.getString(c.getColumnIndex(Schema.Repo.COLUMN_THEME)));
                    store.setDownloads(c.getLong(c.getColumnIndex(Schema.Repo.COLUMN_DOWNLOADS)));

                    if(c.getString(c.getColumnIndex(Schema.Repo.COLUMN_USERNAME))!=null){
                        store.login = new Login();
                        store.login.setUsername(c.getString(c.getColumnIndex(Schema.Repo.COLUMN_USERNAME)));
                        store.login.setPassword(c.getString(c.getColumnIndex(Schema.Repo.COLUMN_PASSWORD)));
                    }

                    store.setAvatarUrl(c.getString(c.getColumnIndex(Schema.Repo.COLUMN_AVATAR)));

                    oldStores.add(store);
                }
                c.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if (oldVersion >= 28 && oldVersion < 31 ){
            try {
                Cursor c = db.query("repo", null, Schema.Repo.COLUMN_IS_USER +"=?", new String[]{"1"}, null, null, null);

                for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

                    StoreItemDB server = new StoreItemDB();
                    server.setId(c.getInt(c.getColumnIndex(Schema.Repo.COLUMN_ID)));
                    server.setUrl(c.getString(c.getColumnIndex(Schema.Repo.COLUMN_URL)));
                    server.setName(c.getString(c.getColumnIndex(Schema.Repo.COLUMN_NAME)));
                    server.setTheme(c.getString(c.getColumnIndex(Schema.Repo.COLUMN_THEME)));
                    server.setDownloads(c.getLong(c.getColumnIndex(Schema.Repo.COLUMN_DOWNLOADS)));

                    if(c.getString(c.getColumnIndex(Schema.Repo.COLUMN_USERNAME))!=null){
                        server.login = new Login();
                        server.login.setUsername(c.getString(c.getColumnIndex(Schema.Repo.COLUMN_USERNAME)));
                        server.login.setPassword(c.getString(c.getColumnIndex(Schema.Repo.COLUMN_PASSWORD)));
                    }

                    server.setAvatarUrl(c.getString(c.getColumnIndex(Schema.Repo.COLUMN_AVATAR)));

                    //New
                    server.setDescription(c.getString(c.getColumnIndex(Schema.Repo.COLUMN_DESCRIPTION)));
                    server.setView(c.getString(c.getColumnIndex(Schema.Repo.COLUMN_VIEW)));

                    oldStores.add(server);
                }
                c.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if( oldVersion == 21 ){
            db.execSQL("ALTER TABLE " +Schema.RollbackTbl.getName()+ " ADD COLUMN reponame TEXT");
            db.delete(Schema.RollbackTbl.getName(), "confirmed = ?", new String[]{"0"});
        }

        if (oldVersion >= 13 && oldVersion < 31) {

            Cursor c = db.rawQuery("select * from " + Schema.Scheduled.getName(), null);
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                ScheduledDownloadItem scheduledDownload = new ScheduledDownloadItem();
                scheduledDownload.setPackage_name(c.getString(c.getColumnIndex(Schema.Scheduled.COLUMN_PACKAGE_NAME)));
                scheduledDownload.setMd5(c.getString(c.getColumnIndex(Schema.Scheduled.COLUMN_MD5)));
                scheduledDownload.setName(c.getString(c.getColumnIndex(Schema.Scheduled.COLUMN_NAME)));
                scheduledDownload.setVersion_name(c.getString(c.getColumnIndex(Schema.Scheduled.COLUMN_VERSION_NAME)));
                scheduledDownload.setRepo_name(c.getString(c.getColumnIndex(Schema.Scheduled.COLUMN_REPO)));
                scheduledDownload.setIcon(c.getString(c.getColumnIndex(Schema.Scheduled.COLUMN_ICON)));

                oldScheduledDownloads.add(scheduledDownload);
            }

            Cursor cursor = db.rawQuery("select * from " + Schema.Excluded.getName(), null);
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                ExcludedUpdate excludedUpdate = new ExcludedUpdate(0);
                excludedUpdate.setVersionName(cursor.getString(cursor.getColumnIndex(Schema.Excluded.COLUMN_PACKAGE_NAME)));
                excludedUpdate.setName(cursor.getString(cursor.getColumnIndex(Schema.Excluded.COLUMN_NAME)));
                excludedUpdate.setVercode(cursor.getInt(cursor.getColumnIndex(Schema.Excluded.COLUMN_VERCODE)));
                excludedUpdate.setIcon(cursor.getString(cursor.getColumnIndex(Schema.Excluded.COLUMN_ICONPATH)));

                oldExcludedUpdates.add(excludedUpdate);
            }

        }

        dropIndexes(db, oldVersion);
        dropTables(db, oldVersion);

        try {
            createDb(db);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        if (oldVersion >= 13 && oldVersion < 31) {

            for (StoreItemDB server : oldStores) {
                ContentValues values = new ContentValues();

                values.put(Schema.Repo.COLUMN_ID, server.getId());

                values.put(Schema.Repo.COLUMN_NAME, server.getName());
                values.put(Schema.Repo.COLUMN_IS_USER, true);
                values.put(Schema.Repo.COLUMN_URL, server.getUrl());

                values.put(Schema.Repo.COLUMN_THEME, server.getTheme());
                values.put(Schema.Repo.COLUMN_DOWNLOADS, server.getDownloads());

                //New
                values.put(Schema.Repo.COLUMN_DESCRIPTION, server.getDescription());
                values.put(Schema.Repo.COLUMN_VIEW, server.getView());

                if(server.login!=null){
                    values.put(Schema.Repo.COLUMN_USERNAME, server.login.getUsername());
                    values.put(Schema.Repo.COLUMN_PASSWORD, server.login.getPassword());
                }

                values.put(Schema.Repo.COLUMN_AVATAR, server.getAvatarUrl());

                db.insert(Schema.Repo.getName(), null, values);
            }

            for (ScheduledDownloadItem scheduledDownload : oldScheduledDownloads) {
                ContentValues values = new ContentValues();

                values.put(Schema.Scheduled.COLUMN_PACKAGE_NAME, scheduledDownload.getPackage_name());
                values.put(Schema.Scheduled.COLUMN_MD5, scheduledDownload.getMd5());
                values.put(Schema.Scheduled.COLUMN_NAME, scheduledDownload.getName());
                values.put(Schema.Scheduled.COLUMN_VERSION_NAME, scheduledDownload.getVersion_name());
                values.put(Schema.Scheduled.COLUMN_REPO, scheduledDownload.getRepo_name());
                values.put(Schema.Scheduled.COLUMN_ICON, scheduledDownload.getIcon());

                db.insert(Schema.Scheduled.getName(), null, values);
            }

            for (ExcludedUpdate excludedUpdate : oldExcludedUpdates) {
                ContentValues values = new ContentValues();

                values.put(Schema.Excluded.COLUMN_PACKAGE_NAME, excludedUpdate.getVersionName());
                values.put(Schema.Excluded.COLUMN_NAME, excludedUpdate.getName());
                values.put(Schema.Excluded.COLUMN_VERCODE, excludedUpdate.getVercode());
                values.put(Schema.Excluded.COLUMN_ICONPATH, excludedUpdate.getIcon());

                db.insert(Schema.Excluded.getName(), null, values);
            }
        }


        removeSharedPreferences();

    }

    private void removeSharedPreferences() {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Aptoide.getContext());
        preferences.edit().remove("editorschoiceTimestamp").remove("topappsTimestamp").remove("updates").apply();

    }

    private void dropIndexes(SQLiteDatabase db, int oldVersion) {

        if(oldVersion == 22) return;
        Class[] db_tables = Schema.class.getDeclaredClasses();

        String drop_stmt;
        for (Class table : db_tables) {
            TableDefinition td = ((TableDefinition) table.getAnnotation(TableDefinition.class));
            if (td != null) {
                for (TableDefinition.Index index : td.indexes()) {
                    drop_stmt = "DROP INDEX IF EXISTS " + index.index_name();
                    db.execSQL(drop_stmt);
                }
            }
        }
    }

    private void dropTables(SQLiteDatabase db, int oldVersion) {
        if( oldVersion == 22) return;

        Class[] db_tables = Schema.class.getDeclaredClasses();


        String drop_stmt;

        boolean dropRollback = oldVersion < 21;

        for (Class table : db_tables) {
            String tableName = table.getSimpleName().toLowerCase(Locale.ENGLISH);

            if (dropRollback) {
                drop_stmt = "DROP TABLE IF EXISTS " + tableName;
            } else if (!tableName.equals(Schema.RollbackTbl.getName())) {
                drop_stmt = "DROP TABLE IF EXISTS " + tableName;
            }else{
                continue;
            }

            Log.d("Aptoide-AptoideDatabase", "executing " + drop_stmt);

            db.execSQL(drop_stmt);
        }
    }

}