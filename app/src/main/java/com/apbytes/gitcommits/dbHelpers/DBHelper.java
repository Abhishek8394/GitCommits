package com.apbytes.gitcommits.dbHelpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.apbytes.gitcommits.Utility;

/**
 * Created by Abhishek on 6/8/2018.
 */

public class DBHelper extends SQLiteOpenHelper {
    public static final String TAG = "DBHelper";
    public static final String DB_NAME = "gitcommits";
    public static final int DB_VER = 2;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createCommitTable = DBContract.CommitEntry.getCreateCommitsTableQuery();
        db.execSQL(createCommitTable);
        Utility.log(TAG, "Created DB");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DBContract.CommitEntry.getDeleteCommitsTableQuery());
        Utility.log(TAG, "Dropped table");
        onCreate(db);
    }

}
