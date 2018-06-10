package com.apbytes.gitcommits.Utils;

import android.content.ContentValues;

import com.apbytes.gitcommits.dbHelpers.DBContract;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Abhishek on 6/10/2018.
 */

public class DummyDataGenerators {
    public static ContentValues[] getDummyCommitRows(int num){
        ContentValues []cvs = new ContentValues[num];
        DateFormat dateFormat = new SimpleDateFormat(Utility.ISO_DATE_FORMAT);
        for(int i=0; i<num; i++){
            ContentValues cv = new ContentValues();
            cv.put(DBContract.CommitEntry.COLUMN_AUTHOR_NAME, "author-" + i);
            cv.put(DBContract.CommitEntry.COLUMN_MESSAGE, "message - " + i);
            cv.put(DBContract.CommitEntry.COLUMN_COMMIT_TIME, dateFormat.format(new Date()));
            cv.put(DBContract.CommitEntry.COLUMN_COMMIT_HASH, UUID.randomUUID().toString());
            cvs[i] = cv;
        }
        return cvs;
    }
}
