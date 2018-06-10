package com.apbytes.gitcommits.dbHelpers;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

import com.jcabi.github.Content;

/**
 * Created by Abhishek on 6/8/2018.
 */

public class DBContract {

    public static final String CONTENT_AUTHORITY = "com.apbytes.gitcommits.app";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_COMMIT = "commits";

    public DBContract() {
    }

    public static final class CommitEntry implements BaseColumns{
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                                              .appendPath(PATH_COMMIT).build();

        public static final String CONTENT_TYPE =
                        ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
                        + CONTENT_AUTHORITY + "/"
                        + PATH_COMMIT;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"
                        + CONTENT_AUTHORITY + "/"
                        + PATH_COMMIT;
        // Ideally should point to following tables depending on how much we persist
        // 1. User
        // 2. Repo
        // 3. Branch
        public static final String TABLE_NAME = "commits";
        public static final String COLUMN_AUTHOR_NAME = "author_name";
        public static final String COLUMN_MESSAGE = "message";
        public static final String COLUMN_COMMIT_TIME = "commit_time";
        public static final String COLUMN_COMMIT_HASH = "commit_hash";

        /**
         * Returns the command to execute for creating a commits table.
         * @return String
         */
        public static String getCreateCommitsTableQuery(){
            String query = "CREATE TABLE " + TABLE_NAME + " ("
                           + CommitEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + ", "
                           + COLUMN_AUTHOR_NAME + " TEXT" + ", "
                           + COLUMN_MESSAGE + " TEXT" + ", "
                           + COLUMN_COMMIT_HASH + " TEXT UNIQUE" + ", "
                           + COLUMN_COMMIT_TIME + " TEXT" + " );";
            return query;
        }

        /**
         * Returns the command to execute for deleting commits table.
         * @return String
         */
        public static String getDeleteCommitsTableQuery(){
            return "DROP TABLE IF EXISTS " + TABLE_NAME;
        }

        /**
         * URIs for querying against base commits table.
         * @return
         */
        public static Uri buildCommitTableUri(){
            return CONTENT_URI;
        }

        /**
         * Build a uri for a single commit. We do not support these but can be used
         * for showing details of individual commit.
         * Still making these to satisfy ContentProvider's insert.
         * @param _id
         * @return
         */
        public static Uri buildCommitUriWithId(long _id){
            return CONTENT_URI.buildUpon().appendPath("id").appendPath(_id + "").build();
        }

        /**
         * Build a uri for a single commit based on commit hash
         * @param hash Commit hash
         * @return
         */
        public static Uri buildCommitUriWithHash(String hash){
            return CONTENT_URI.buildUpon().appendPath("hash").appendPath(hash).build();
        }

        /**
         * Extracts ID from uri created by buildCommitUriWithId
         * @param uri
         * @return
         */
        public static String getIdFromUri(Uri uri){
            // since uri is /id/<id> we get index 2
            return uri.getPathSegments().get(2);
        }

        /**
         * Extracts Commit hash from uri created by buildCommitUriWithId
         * @param uri
         * @return
         */
        public static String getHashFromUri(Uri uri){
            // since uri is /hash/<hash> we get index 2
            return uri.getPathSegments().get(2);
        }
    }
}
