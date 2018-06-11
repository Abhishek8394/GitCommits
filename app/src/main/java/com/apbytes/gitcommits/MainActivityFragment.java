package com.apbytes.gitcommits;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.apbytes.gitcommits.UIAdapters.CommitAdapter;
import com.apbytes.gitcommits.dbHelpers.DBContract;
import com.apbytes.gitcommits.dbHelpers.DBProvider;
import com.apbytes.gitcommits.networking.CommitSynchronizer;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "MainActivityFragment";
    Button removebtn;
    ContentResolver contentResolver;
    CommitAdapter cursorAdapter;
    ListView commitListView;
    CommitSynchronizer commitSynchronizer;
    // this determines how many commits are seen.
    int maxRecords = 10;

    public MainActivityFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
        commitSynchronizer = new CommitSynchronizer(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        commitListView = (ListView) v.findViewById(R.id.commitListView);
        contentResolver = getActivity().getContentResolver();
        cursorAdapter = new CommitAdapter(getActivity(), null,  CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        commitListView.setAdapter(cursorAdapter);
//      // Kept code for debugging purposes.
//        removebtn = (Button) v.findViewById(R.id.removeBtn);
//        removebtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Cursor cursor = (Cursor) cursorAdapter.getItem(cursorAdapter.getCount() - 1);
//                if(cursor != null && cursor.moveToLast()){
//                    Log.d(TAG, "removed");
//                    contentResolver.delete(DBContract.CommitEntry.buildCommitUriWithId(cursor.getInt(0)), null, null);
//                }
//
//            }
//        });
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
