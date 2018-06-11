package com.apbytes.gitcommits.networking;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.apbytes.gitcommits.Utils.Utility;
import com.apbytes.gitcommits.dbHelpers.CommonQueriesHelper;
import com.apbytes.gitcommits.dbHelpers.DBContract;
import com.apbytes.gitcommits.githubHelpers.GitCommit;
import com.apbytes.gitcommits.githubHelpers.GitCommitList;
import com.apbytes.gitcommits.githubHelpers.GitRepo;
import com.apbytes.gitcommits.githubHelpers.GithubClient;

import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by Abhishek on 6/10/2018.
 * Fetched commits and inserts them in database.
 * Skips duplicates.
 */

public class CommitSynchronizer {
    ContentResolver contentResolver;

    public CommitSynchronizer(Context context){
        contentResolver = context.getContentResolver();
    }

    public static ContentValues commitToContentValues(GitCommit commit){
        ContentValues cv = new ContentValues();
        cv.put(DBContract.CommitEntry.COLUMN_MESSAGE, commit.getCommitMessage());
        cv.put(DBContract.CommitEntry.COLUMN_AUTHOR_NAME, commit.getUserName());
        cv.put(DBContract.CommitEntry.COLUMN_COMMIT_TIME, Utility.ISODatetoString(commit.getCommitTime()));
        cv.put(DBContract.CommitEntry.COLUMN_COMMIT_HASH, commit.getCommitHash());
        return cv;
    }

    /**
     * Fetch and store commits.
     * NOTE: Call this from a separate thread, since it involves a network call and some
     * long running tasks.
     * @param githubClient
     * @param userName
     * @param repoName
     * @throws InvalidObjectException
     */
    public void syncCommits(GithubClient githubClient, String userName, String repoName) throws InvalidObjectException {
        GitRepo repo = new GitRepo(githubClient, userName, repoName);
        GitCommitList commitList = repo.getCommits(null);
        ArrayList<GitCommit> commits = commitList.getCommitList();
        /**
         * Approach: Eliminate duplicates.
         * Rather than querying for each commit, we collect hashes in do it in one query.
         * Then filter out those results from our fetched commits since we do not overwrite
         * commits. Then whatever is left, is bulk inserted.
         */
        // Collect Hashes in a list
        ArrayList<String> hashList = new ArrayList<>();
        for(GitCommit commit: commits){
            hashList.add(commit.getCommitHash());
        }
        // Get existing commits.
        Cursor cursor = CommonQueriesHelper.getCommitsByHashes(contentResolver, hashList);
        // Create a hashset of existing commit hashes.
        HashSet<String> existingHashes = new HashSet<>();
        if(cursor.moveToFirst()){
            do{
                existingHashes.add(cursor.getString(cursor.getColumnIndex(DBContract.CommitEntry.COLUMN_COMMIT_HASH)));
            }while(cursor.moveToNext());
        }
        cursor.close();
        // collect new ones.
        ArrayList<ContentValues> commitContentValues = new ArrayList<>();
        for(int i=0; i<commits.size(); i++){
            GitCommit commit = commits.get(i);
            // dont overwrite duplicates.
            if(! existingHashes.contains(commit.getCommitHash())){
                commitContentValues.add(commitToContentValues(commit));
            }
        }
        // bulk insert
        contentResolver.bulkInsert(DBContract.CommitEntry.buildCommitTableUri(), commitContentValues.toArray(new ContentValues[]{}));
    }
}
