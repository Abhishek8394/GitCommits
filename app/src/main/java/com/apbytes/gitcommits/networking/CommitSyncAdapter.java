package com.apbytes.gitcommits.networking;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

import com.apbytes.gitcommits.githubHelpers.GithubClient;

import java.io.InvalidObjectException;

/**
 * Created by Abhishek on 6/10/2018.
 * Sync adapter for syncing commits when app isn't active.
 * In the requesting bundle provide `REPO_USER_NAME_KEY` and `REPO_NAME_KEY`
 * containing the repo user name and the repo name respectively.
 */

public class CommitSyncAdapter extends AbstractThreadedSyncAdapter {
    public static final String REPO_NAME_KEY = "com.apbytes.gitcommits.syncadapter.repo.name.key";
    public static final String REPO_USER_NAME_KEY = "com.apbytes.gitcommits.syncadapter.repo.username.key";
    ContentResolver contentResolver;

    public CommitSyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        contentResolver = context.getContentResolver();
    }

    public CommitSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        contentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        // ideally a value in extras would tell us what task
        // to perform if we have multiple. But we have just one task now
        // so that's what we do.
        GithubClient githubClient = new GithubClient();
        String userName = extras.getString(REPO_USER_NAME_KEY);
        String repoName = extras.getString(REPO_NAME_KEY);
        CommitSynchronizer commitSynchronizer = new CommitSynchronizer(getContext());
        try {
            commitSynchronizer.syncCommits(githubClient, userName, repoName);
        } catch (InvalidObjectException e) {
            e.printStackTrace();
        }
    }
}
