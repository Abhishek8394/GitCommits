package com.apbytes.gitcommits.githubHelpers;

import org.json.JSONException;

import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Abhishek on 6/8/2018.
 */

public class GitRepo {
    private String userName;
    private String repoName;
    private GithubClient githubClient;


    public GitRepo(GithubClient gClient, String userName, String repoName) {
        this.userName = userName;
        this.repoName = repoName;
        this.githubClient = gClient;
    }

    public GitCommitList getCommits(HashMap<String, String> params) throws InvalidObjectException {
        GitCommitList gitCommitList = new GitCommitList(this);
        try {
            gitCommitList.fetchCommits(null);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return gitCommitList;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRepoName() {
        return repoName;
    }

    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }

    public GithubClient getGithubClient() {
        return githubClient;
    }

    public void setGithubClient(GithubClient githubClient) {
        this.githubClient = githubClient;
    }
}
