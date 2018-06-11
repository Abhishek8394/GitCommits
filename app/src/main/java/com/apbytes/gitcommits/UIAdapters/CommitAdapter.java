package com.apbytes.gitcommits.UIAdapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.apbytes.gitcommits.R;
import com.apbytes.gitcommits.Utils.Utility;
import com.apbytes.gitcommits.dbHelpers.DBContract;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Abhishek on 6/9/2018.
 * Adapter for rendering commit objects on a view.
 */

public class CommitAdapter extends CursorAdapter {
    public static final String  TAG = "CommitAdapter";
    SimpleDateFormat localDisplayFormat;

    public CommitAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        localDisplayFormat = new SimpleDateFormat("dd MMMM, yyyy hh:mm a");
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.commit_listview_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView titleTV = (TextView) view.findViewById(R.id.commitTitle);
        TextView authorTV = (TextView) view.findViewById(R.id.commitAuthor);
        TextView timeTV = (TextView) view.findViewById(R.id.commitTime);
        TextView messageTV = (TextView) view.findViewById(R.id.commitMessage);
        // extract author name
        String author = cursor.getString(cursor.getColumnIndex(DBContract.CommitEntry.COLUMN_AUTHOR_NAME));
        // time_string is ISO format.
        String timestring = cursor.getString(cursor.getColumnIndex(DBContract.CommitEntry.COLUMN_COMMIT_TIME));
        // extract commit message
        String message = cursor.getString(cursor.getColumnIndex(DBContract.CommitEntry.COLUMN_MESSAGE));
        // Convert time to local format.
        try {
            Date d = Utility.convertISOtoLocale(timestring);
            timestring = localDisplayFormat.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // bind values
        String []messageSplit = message.split("\n");
        // get only title of message and trim if too long.
        String titleText = messageSplit[0].substring(0, Math.min(23, messageSplit[0].length())) + "..";
        titleTV.setText(titleText);
        authorTV.setText("@" + author);
        timeTV.setText(timestring);
        messageTV.setText(message);
    }
}
