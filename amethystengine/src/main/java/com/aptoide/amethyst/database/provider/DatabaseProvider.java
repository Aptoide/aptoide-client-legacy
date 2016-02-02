package com.aptoide.amethyst.database.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.aptoide.amethyst.BuildConfig;
import com.aptoide.amethyst.database.SQLiteDatabaseHelper;
import com.aptoide.amethyst.database.schema.Schema;

import java.util.HashMap;

/**
 * Created by hsousa on 17-06-2015.
 */
public class DatabaseProvider extends ContentProvider {

    /**
     * Aptoide database helper
     */
    private SQLiteDatabaseHelper database;

    /**
     * A projection map used to select columns from the database
     */
    private static HashMap<String, String> sReposProjectionMap;

    /*
     * Constants used by the Uri matcher to choose an action based on the pattern
     * of the incoming URI
     */
    private static final int REPOS = 1;
    private static final int REPO_ID = 2;
//    private static final int ROLLBACKS = 2;
//    private static final int UPDATES = 3;

    /**
     * Build and return a {@link UriMatcher} that catches all {@link Uri}variations supported by this {@link ContentProvider}.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {

        sUriMatcher.addURI(ProviderConstants.AUTHORITY, ProviderConstants.PATH_REPO, REPOS);
        sUriMatcher.addURI(ProviderConstants.AUTHORITY, ProviderConstants.PATH_REPO_ID + "/#", REPO_ID);

        /*
         * Creates and initializes a projection map that returns all columns
         */
        // Creates a new projection map instance. The map returns a column name
        // given a string. The two are usually equal.
        sReposProjectionMap = new HashMap<>();

        // Maps the string to the column names
        sReposProjectionMap.put(Schema.Repo.COLUMN_ID, Schema.Repo.COLUMN_ID);
        sReposProjectionMap.put(Schema.Repo.COLUMN_URL, Schema.Repo.COLUMN_URL);
        sReposProjectionMap.put(Schema.Repo.COLUMN_APK_PATH, Schema.Repo.COLUMN_APK_PATH);
        sReposProjectionMap.put(Schema.Repo.COLUMN_ICONS_PATH, Schema.Repo.COLUMN_ICONS_PATH);
        sReposProjectionMap.put(Schema.Repo.COLUMN_WEBSERVICES_PATH, Schema.Repo.COLUMN_WEBSERVICES_PATH);
        sReposProjectionMap.put(Schema.Repo.COLUMN_HASH, Schema.Repo.COLUMN_HASH);
        sReposProjectionMap.put(Schema.Repo.COLUMN_THEME, Schema.Repo.COLUMN_THEME);
        sReposProjectionMap.put(Schema.Repo.COLUMN_AVATAR, Schema.Repo.COLUMN_AVATAR);
        sReposProjectionMap.put(Schema.Repo.COLUMN_DOWNLOADS, Schema.Repo.COLUMN_DOWNLOADS);
        sReposProjectionMap.put(Schema.Repo.COLUMN_DESCRIPTION, Schema.Repo.COLUMN_DESCRIPTION);
        sReposProjectionMap.put(Schema.Repo.COLUMN_VIEW, Schema.Repo.COLUMN_VIEW);
        sReposProjectionMap.put(Schema.Repo.COLUMN_ITEMS, Schema.Repo.COLUMN_ITEMS);
        sReposProjectionMap.put(Schema.Repo.COLUMN_LATEST_TIMESTAMP, Schema.Repo.COLUMN_LATEST_TIMESTAMP);
        sReposProjectionMap.put(Schema.Repo.COLUMN_TOP_TIMESTAMP, Schema.Repo.COLUMN_TOP_TIMESTAMP);
        sReposProjectionMap.put(Schema.Repo.COLUMN_IS_USER, Schema.Repo.COLUMN_IS_USER);
        sReposProjectionMap.put(Schema.Repo.COLUMN_FAILED, Schema.Repo.COLUMN_FAILED);
        sReposProjectionMap.put(Schema.Repo.COLUMN_NAME, Schema.Repo.COLUMN_NAME);
        sReposProjectionMap.put(Schema.Repo.COLUMN_USERNAME, Schema.Repo.COLUMN_USERNAME);
        sReposProjectionMap.put(Schema.Repo.COLUMN_PASSWORD, Schema.Repo.COLUMN_PASSWORD);
    }

    @Override
    public boolean onCreate() {
        database = SQLiteDatabaseHelper.getInstance(getContext());

        // Assumes that any failures will be reported by a thrown exception.
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        // Constructs a new query builder and sets its table name
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(ProviderConstants.REPO_TABLE_NAME);

        /**
         * Choose the projection and adjust the "where" clause based on URI pattern-matching.
         */
        switch (sUriMatcher.match(uri)) {
            // If the incoming URI is for repos, chooses the Notes projection
            case REPOS:
                qb.setProjectionMap(sReposProjectionMap);
                break;

           /* If the incoming URI is for a single note identified by its ID, chooses the
            * note ID projection, and appends "_ID = <noteID>" to the where clause, so that
            * it selects that single note
            */
            case REPO_ID:
                qb.setProjectionMap(sReposProjectionMap);
                qb.appendWhere(
                        Schema.Repo.COLUMN_ID +    // the name of the ID column
                                "=" +
                                // the position of the note ID itself in the incoming URI
                                uri.getPathSegments().get(ProviderConstants.REPO_ID_PATH_POSITION));
                break;

            default:
                // If the URI doesn't match any of the known patterns, throw an exception.
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        String orderBy;
        // If no sort order is specified, uses the default
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = ProviderConstants.DEFAULT_SORT_ORDER;
        } else {
            // otherwise, uses the incoming sort order
            orderBy = sortOrder;
        }

        // Opens the database object in "read" mode, since no writes need to be done.
        SQLiteDatabase db = database.getReadableDatabase();

       /*
        * Performs the query. If no problems occur trying to read the database, then a Cursor
        * object is returned; otherwise, the cursor variable contains null. If no records were
        * selected, then the Cursor object is empty, and Cursor.getCount() returns 0.
        */
        Cursor c = qb.query(
                db,            // The database to query
                projection,    // The columns to return from the query
                selection,     // The columns for the where clause
                selectionArgs, // The values for the where clause
                null,          // don't group the rows
                null,          // don't filter by row groups
                orderBy        // The sort order
        );

        // Tells the Cursor what URI to watch, so it knows when its source data changes
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }
    /*
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

            // Using SQLiteQueryBuilder instead of query() method
            SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

            // check if the caller has requested a column which does not exists
//            checkColumns(projection);

            // Set the table
            queryBuilder.setTables(Schema.Repo.REPO_TABLE_NAME);

            switch (sUriMatcher.match(uri)) {
                case REPOS:
                    break;
                case REPO_ID:
                    // adding the ID to the original query
                    queryBuilder.appendWhere(Schema.Repo.COLUMN_ID + "=" + uri.getLastPathSegment());
                    break;
                default:
                    throw new IllegalArgumentException("Unknown URI: " + uri);
            }

            Cursor cursor = queryBuilder.query(database.getReadableDatabase(), projection, selection,
                    selectionArgs, null, null, sortOrder);

            // make sure that potential listeners are getting notified
            cursor.setNotificationUri(getContext().getContentResolver(), uri);

            return cursor;
        }
*/
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sUriMatcher.match(uri);
        long id;
        switch (uriType) {
            case REPOS:
                id = database.getWritableDatabase().insert(ProviderConstants.REPO_TABLE_NAME, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(ProviderConstants.PATH_REPO_ID + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        // Opens the database object in "write" mode.
        SQLiteDatabase db = database.getWritableDatabase();

        int uriType = sUriMatcher.match(uri);
        int rowsDeleted;
        switch (uriType) {
            case REPOS:
                rowsDeleted = db.delete(ProviderConstants.REPO_TABLE_NAME, selection,
                        selectionArgs);
                break;
            case REPO_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = db.delete(
                            ProviderConstants.REPO_TABLE_NAME,
                            Schema.Repo.COLUMN_ID + "=" + id,
                            null);
                } else {
                    rowsDeleted = db.delete(
                            ProviderConstants.REPO_TABLE_NAME,
                            Schema.Repo.COLUMN_ID + "=" + id + " and " + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // Opens the database object in "write" mode.
        SQLiteDatabase db = database.getWritableDatabase();

        int uriType = sUriMatcher.match(uri);
        int rowsUpdated;
        switch (uriType) {
            case REPOS:
                rowsUpdated = db.update(ProviderConstants.REPO_TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case REPO_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = db.update(ProviderConstants.REPO_TABLE_NAME,
                            values,
                            Schema.Repo.COLUMN_ID + "=" + id,
                            null);
                } else {
                    rowsUpdated = db.update(ProviderConstants.REPO_TABLE_NAME,
                            values,
                            Schema.Repo.COLUMN_ID + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }
    
    
    public static class ProviderConstants {
        /**
         * Repo database name
         */
        public static final String REPO_TABLE_NAME = "repo";

        public static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".DatabaseProvider";

        /*
         * URI definitions
         */

        /**
         * The scheme part for this provider's URI
         */
        private static final String SCHEME = "content://";

        /**
         * Path part for the Repo URI
         */
        public static final String PATH_REPO = "/repos";

        /**
         * Path part for the Repo ID URI
         */
        public static final String PATH_REPO_ID = "/repos/";

        /**
         * 0-relative position of a note ID segment in the path part of a note ID URI
         */
        public static final int REPO_ID_PATH_POSITION = 1;

        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.parse(SCHEME + AUTHORITY + PATH_REPO);

        /**
         * The content URI base for a single note. Callers must
         * append a numeric note id to this Uri to retrieve a note
         */
        public static final Uri CONTENT_ID_URI_BASE
                = Uri.parse(SCHEME + AUTHORITY + PATH_REPO_ID);

        /**
         * The content URI match pattern for a single note, specified by its ID. Use this to match
         * incoming URIs or to construct an Intent.
         */
        public static final Uri CONTENT_ID_URI_PATTERN
                = Uri.parse(SCHEME + AUTHORITY + PATH_REPO_ID + "/#");

        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = "modified DESC";

    }
}
