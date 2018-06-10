package com.apbytes.gitcommits;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
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
import com.apbytes.gitcommits.dbHelpers.DBProvider;
import com.apbytes.gitcommits.githubHelpers.GithubClient;
import com.apbytes.gitcommits.networking.CommitSynchronizer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

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
    CommitSynchronizer commitSynchronizer;
    int maxRecords = 10;

    public MainActivityFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
        commitSynchronizer = new CommitSynchronizer(getActivity());
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
//                cr.insert(DBContract.CommitEntry.buildCommitTableUri(), cvpool[cvptr]);
//                cvptr = (cvptr + 1) % cvpool.length;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        commitSynchronizer.syncCommits(new GithubClient(), "poynt", "PoyntSamples");
                    }
                }).start();
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
        Uri commitTableUri = DBContract.CommitEntry.buildCommitTableUri();
        commitTableUri = commitTableUri.buildUpon()
                .appendQueryParameter(DBProvider.QUERY_LIMIT_PARAM, maxRecords + "")
                .build();
        return new CursorLoader(getActivity(), commitTableUri, null, null, null, DBContract.CommitEntry.COLUMN_COMMIT_TIME + " DESC");
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
