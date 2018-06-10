package com.apbytes.gitcommits;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.apbytes.gitcommits.UIAdapters.CommitAdapter;
import com.apbytes.gitcommits.dbHelpers.DBContract;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "MainActivityFragment";
    Button addbtn, removebtn;
    ContentResolver cr;
    ContentValues []cvpool;
    int cvptr;
    CommitAdapter cursorAdapter;
    ListView lv;
    public MainActivityFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
    }

    public ContentValues[] getDummyCommitRows(int num){
        ContentValues []cvs = new ContentValues[num];
        DateFormat dateFormat = new SimpleDateFormat(Utility.ISO_DATE_FORMAT);
        for(int i=0; i<num; i++){
            ContentValues cv = new ContentValues();
            cv.put(DBContract.CommitEntry.COLUMN_AUTHOR_NAME, "author-" + i);
            cv.put(DBContract.CommitEntry.COLUMN_MESSAGE, "message - " + i);
            cv.put(DBContract.CommitEntry.COLUMN_COMMIT_TIME, dateFormat.format(new Date()));
            cvs[i] = cv;
        }
        return cvs;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        addbtn = (Button) v.findViewById(R.id.addBtn);
        removebtn = (Button) v.findViewById(R.id.removeBtn);
        lv = (ListView) v.findViewById(R.id.commitListView);
        cr = getActivity().getContentResolver();
        cursorAdapter = new CommitAdapter(getActivity(), null,  CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        lv.setAdapter(cursorAdapter);
        cvpool = getDummyCommitRows(10);
        cvptr = 0;
        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cr.insert(DBContract.CommitEntry.buildCommitTableUri(), cvpool[cvptr]);
                cvptr = (cvptr + 1) % cvpool.length;
            }
        });
        removebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor cursor = (Cursor) cursorAdapter.getItem(cursorAdapter.getCount() - 1);
                if(cursor != null && cursor.moveToLast()){
                    Log.d(TAG, "removed");
                    cr.delete(DBContract.CommitEntry.buildCommitUriWithId(cursor.getInt(0)), null, null);
                }

            }
        });
        return v;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), DBContract.CommitEntry.buildCommitTableUri(), null, null, null, DBContract.CommitEntry.COLUMN_COMMIT_TIME + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }
}
