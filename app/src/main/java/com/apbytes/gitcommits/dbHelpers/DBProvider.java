package com.apbytes.gitcommits.dbHelpers;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by Abhishek on 6/8/2018.
 * This is the ContentProvider. Provides interface to the SQLite DB.
 * Currently supports CRD out of CRUD.
 */

public class DBProvider extends ContentProvider {

    private DBHelper dbHelper;
    private UriMatcher uriMatcher = buildUriMatcher();
    // We need this if we have more tables and need join.
    private static final SQLiteQueryBuilder sqliteQueryBuilder;
    static {
        sqliteQueryBuilder = new SQLiteQueryBuilder();
        sqliteQueryBuilder.setTables(DBContract.CommitEntry.TABLE_NAME);
    }

    // Int codes for URI matches.
    private static final int COMMITS = 1;
    private static final int COMMIT_BY_ID = 2;
    private static final int COMMIT_BY_HASH = 3;

    static UriMatcher buildUriMatcher(){
        // The URIs mentioned here must be in sync with buildxUri method of `DBContract.Commit` or any member classes.
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        String authority = DBContract.CONTENT_AUTHORITY;
        // generic queries, inserts, delete against commits.
        uriMatcher.addURI(authority, DBContract.PATH_COMMIT, COMMITS);
        // query to operate on specific commit.
        uriMatcher.addURI(authority, DBContract.PATH_COMMIT + "/id/#", COMMIT_BY_ID);
        // query to operate on specific commit based on hash
        uriMatcher.addURI(authority, DBContract.PATH_COMMIT + "/hash/*", COMMIT_BY_HASH);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DBHelper(getContext());
        return true;
    }

    /**
     * Fetch a commit by id. Assumes Id is in uri and is a valid URI.
     * @param uri
     * @param projection
     * @return
     */
    private Cursor getCommitById(Uri uri, String[] projection){
        String id = DBContract.CommitEntry.getIdFromUri(uri);
        String query = DBContract.CommitEntry.TABLE_NAME + "." + DBContract.CommitEntry._ID + "=?";
        Cursor result = sqliteQueryBuilder.query(dbHelper.getReadableDatabase(), projection,
                query , new String[]{id}, null, null, null);
        return result;
    }

    /**
     * Fetch a commit by hash. Assumes hash is in uri and is a valid URI.
     * @param uri
     * @param projection
     * @return
     */
    private Cursor getCommitByHash(Uri uri, String[] projection){
        String hash = DBContract.CommitEntry.getHashFromUri(uri);
        String query = DBContract.CommitEntry.TABLE_NAME + "." +
                DBContract.CommitEntry.COLUMN_COMMIT_HASH + "=?";
        Cursor result = sqliteQueryBuilder.query(dbHelper.getReadableDatabase(), projection,
                query , new String[]{hash}, null, null, null);
        return result;
    }

    /**
     * Delete a specfic commit.
     * @param uri
     * @return
     */
    private int deleteCommitById(Uri uri){
        String id = DBContract.CommitEntry.getIdFromUri(uri);
        String query = DBContract.CommitEntry.TABLE_NAME + "." + DBContract.CommitEntry._ID + "=?";
        return dbHelper.getWritableDatabase().delete(DBContract.CommitEntry.TABLE_NAME, query, new String[]{id});
    }

    /**
     * Delete a specfic commit based on hash.
     * @param uri
     * @return
     */
    private int deleteCommitByHash(Uri uri){
        String hash = DBContract.CommitEntry.getHashFromUri(uri);
        String query = DBContract.CommitEntry.TABLE_NAME + "."
                + DBContract.CommitEntry.COLUMN_COMMIT_HASH + "=?";
        return dbHelper.getWritableDatabase().delete(DBContract.CommitEntry.TABLE_NAME, query, new String[]{hash});
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor result = null;
        switch (uriMatcher.match(uri)){
            case COMMITS:
                // generic query against commits table
                result = dbHelper.getReadableDatabase().query(
                        DBContract.CommitEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder
                );
                break;
            case COMMIT_BY_ID:
                // get a specific commit.
                result = getCommitById(uri, projection);
                break;
            case COMMIT_BY_HASH:
                //get commit by hash
                result = getCommitByHash(uri, projection);
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri.toString());
        }
        // Register so we can listen for changes
        result.setNotificationUri(getContext().getContentResolver(), uri);
        return result;

    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch(uriMatcher.match(uri)){
            case COMMITS:
                return DBContract.CommitEntry.CONTENT_TYPE;
            case COMMIT_BY_ID:
            case COMMIT_BY_HASH:
                return DBContract.CommitEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri.toString());
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Uri resultUri;
        switch (uriMatcher.match(uri)){
            case COMMITS:
                // insert a row
                long _id = db.insert(DBContract.CommitEntry.TABLE_NAME, null, values);
                // verify insert was successful
                if(_id > 0){
                    resultUri = DBContract.CommitEntry.buildCommitUriWithId(_id);
                }
                else{
                    throw new SQLException("Cannot insert row");
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown insert URI: " + uri);
        }
        // notify listeners.
        getContext().getContentResolver().notifyChange(uri, null);
        return resultUri;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int retCount = 0;
        switch (uriMatcher.match(uri)){
            case COMMITS:
                db.beginTransaction();
                try{
                    for(int i=0; i<values.length; i++){
                        ContentValues cv = values[i];
                        long id = db.insertOrThrow(DBContract.CommitEntry.TABLE_NAME, null, cv);
                        if(id > 0){
                            retCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                }
                finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                break;
            default:
                throw new UnsupportedOperationException("Unknown bulk URI: " + uri);
        }
        return retCount;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsDeleted = 0;
        switch (uriMatcher.match(uri)){
            case COMMITS:
                // want to perform delete query on table
                rowsDeleted = db.delete(DBContract.CommitEntry.TABLE_NAME, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(DBContract.CommitEntry.buildCommitTableUri(), null);
                break;
            case COMMIT_BY_ID:
                // delete a specific commit.
                rowsDeleted = deleteCommitById(uri);
                getContext().getContentResolver().notifyChange(DBContract.CommitEntry.buildCommitTableUri(), null);
                break;
            case COMMIT_BY_HASH:
                // delete a specific commit.
                rowsDeleted = deleteCommitByHash(uri);
                getContext().getContentResolver().notifyChange(DBContract.CommitEntry.buildCommitTableUri(), null);
                break;
            default:
                throw new UnsupportedOperationException("Unknown delete URI: " + uri);

        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
