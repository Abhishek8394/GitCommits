package com.apbytes.gitcommits.githubHelpers;

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

    public ArrayList<GitCommit> getCommits(HashMap<String, String> params){
        return null;
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
