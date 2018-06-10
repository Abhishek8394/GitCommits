package com.apbytes.gitcommits.dbHelpers;

import android.content.ContentResolver;
import android.database.Cursor;

import java.util.ArrayList;

/**
 * Created by Abhishek on 6/10/2018.
 */

public class CommonQueriesHelper {
    public static Cursor getCommitsByHashes(ContentResolver contentResolver, ArrayList<String> hashList){
        // generate list of question marks.
        String qmarks = (new String(new char[hashList.size()])).replace("\0", "?, ");
        // remove the trailing comma and space.
        qmarks = qmarks.substring(0, qmarks.length() - 2);
        // construct query.
        String query = DBContract.CommitEntry.TABLE_NAME + "."
                + DBContract.CommitEntry.COLUMN_COMMIT_HASH
                + " IN (" + qmarks + ")";
        Cursor cursor = contentResolver.query(DBContract.CommitEntry.buildCommitTableUri(), null,
                query, hashList.toArray(new String[]{}), null);
        return cursor;
    }
}
