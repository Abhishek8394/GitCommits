package com.apbytes.gitcommits;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.apbytes.gitcommits.Utils.Utility;
import com.apbytes.gitcommits.dbHelpers.DBContract;
import com.apbytes.gitcommits.dbHelpers.DBHelper;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

/**
 * Created by Abhishek on 6/8/2018.
 * Just basic tests. Should include more testcases,
 * But for now just testing that it does what is expected.
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class DBTest {
    Context context;

    public void clearDatabase(){
        Context mContext = InstrumentationRegistry.getTargetContext();
        mContext.getContentResolver().delete(DBContract.CommitEntry.buildCommitTableUri(),null,null);
        Cursor c = mContext.getContentResolver().query(DBContract.CommitEntry.buildCommitTableUri(),null,null,null,null);
        Assert.assertEquals("Tables not empty!!",0,c.getCount());
        c.close();
    }

    @Before
    public void setup(){
        System.out.println("setup");
        clearDatabase();
    }

    @Test
    public void  test_createDB(){
        // InstrumentationRegistry.getTargetContext().deleteDatabase(DBContract.CommitEntry.TABLE_NAME);
        DBHelper dbHelper = new DBHelper(InstrumentationRegistry.getTargetContext());
        // list of tables that should exist.
        final HashSet<String> tables = new HashSet<>();
        tables.add(DBContract.CommitEntry.TABLE_NAME);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Assert.assertTrue(db.isOpen());
        Cursor c = null;
        try{
            c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
            Assert.assertTrue(c.moveToFirst());
            do{
                tables.remove(c.getString((0)));
            }while(c.moveToNext());
            Assert.assertTrue("All tables were not created", tables.isEmpty());
        }
        catch (SQLiteException se){
            se.printStackTrace();
            Assert.fail("DB Error");
        }
        assert c!=null;
        c.close();
        // should also test for columns but at this point we are done.
    }

    public ContentValues[] getDummyCommitRows(int num){
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

    @Test
    public void test_insert(){
        // TODO test for contentObserver notifications too.
        ContentValues insertRow = getDummyCommitRows(1)[0];
        ContentResolver cr = InstrumentationRegistry.getTargetContext().getContentResolver();
        Uri newUri = cr.insert(DBContract.CommitEntry.buildCommitTableUri(), insertRow);
        Cursor c = cr.query(DBContract.CommitEntry.buildCommitTableUri(), null, null, null, null);
        c.moveToLast();
        // must be last row.
        Assert.assertEquals(DBContract.CommitEntry.getIdFromUri(newUri).toString(), c.getString(0));
        c.close();
    }

    @Test
    public void test_bulkInsertAndQuery(){
        // TODO test for contentObserver notifications too.
        ContentValues []cvs = getDummyCommitRows(10);
        ContentResolver cr = InstrumentationRegistry.getTargetContext().getContentResolver();
        int numInserted = cr.bulkInsert(DBContract.CommitEntry.buildCommitTableUri(), cvs);
        Assert.assertEquals(cvs.length, numInserted);

        // test bulk query
        Cursor c = null;
        c = cr.query(DBContract.CommitEntry.buildCommitTableUri(), null, null, null, null);
        Assert.assertEquals(c.getCount(), cvs.length);
        c.moveToFirst();
        // get first record primary key
        int startId = c.getInt(0);
        // set up mapping for col names to int indexes
        HashMap<String, Integer> col2ind = new HashMap<>();
        String []cols = new String[]{
                DBContract.CommitEntry.COLUMN_MESSAGE,
                DBContract.CommitEntry.COLUMN_AUTHOR_NAME,
                DBContract.CommitEntry.COLUMN_COMMIT_TIME,
                DBContract.CommitEntry.COLUMN_COMMIT_HASH
        };
        for(int i=0; i<cols.length; i++){
            col2ind.put(cols[i], c.getColumnIndex(cols[i]));
        }
        c.close();
        // test individual query
        for(int i=0; i<cvs.length; i++){
            c = cr.query(DBContract.CommitEntry.buildCommitUriWithId(startId + i), null, null, null, null);
            c.moveToFirst();
            // compare columns
            for(int j=0; j<cols.length; j++){
                // col name
                String col = cols[j];
                // col ind
                int ind = col2ind.get(col);
                // compare with i-th mock data row
                //Assert.assertEquals(cvs[i].getAsString(col) + " : " + col, false, true);
                Assert.assertEquals(c.getString(ind), cvs[i].getAsString(col));
            }
            c.close();
        }
    }
}
