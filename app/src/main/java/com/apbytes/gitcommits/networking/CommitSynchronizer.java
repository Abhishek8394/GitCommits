package com.apbytes.gitcommits.networking;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;

import com.apbytes.gitcommits.Utility;
import com.apbytes.gitcommits.dbHelpers.DBContract;
import com.apbytes.gitcommits.githubHelpers.GitCommit;
import com.apbytes.gitcommits.githubHelpers.GitCommitList;
import com.apbytes.gitcommits.githubHelpers.GitRepo;
import com.apbytes.gitcommits.githubHelpers.GithubClient;

import java.util.ArrayList;

/**
 * Created by Abhishek on 6/10/2018.
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
        return cv;
    }

    public void syncCommits(GithubClient githubClient, String userName, String repoName){
        GitRepo repo = new GitRepo(githubClient, userName, repoName);
        GitCommitList commitList = repo.getCommits(null);
        ArrayList<GitCommit> commits = commitList.getCommitList();
        // create array instead of arraylist since we need that for bulk insert.
        ContentValues[]commitContentValues = new ContentValues[commits.size()];
        for(int i=0; i<commits.size(); i++){
            GitCommit commit = commits.get(i);
            commitContentValues[i] = commitToContentValues(commit);
        }
        contentResolver.bulkInsert(DBContract.CommitEntry.buildCommitTableUri(), commitContentValues);
    }
}
