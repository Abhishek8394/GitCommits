package com.apbytes.gitcommits.githubHelpers;

import java.util.Date;

/**
 * Created by Abhishek on 6/8/2018.
 */

public class GitCommit {
    private String userName;
    private String commitMessage;
    private Date commitTime;
    private String commitHash;

    public GitCommit(){
        commitTime = new Date();
    }

    public GitCommit(String userName, String commitMessage, Date commitTime, String commitHash) {
        this.userName = userName;
        this.commitMessage = commitMessage;
        this.commitTime = commitTime;
        this.commitHash = commitHash;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCommitMessage() {
        return commitMessage;
    }

    public void setCommitMessage(String commitMessage) {
        this.commitMessage = commitMessage;
    }

    public Date getCommitTime() {
        return commitTime;
    }

    public void setCommitTime(Date commitTime) {
        this.commitTime = commitTime;
    }

    public String getCommitHash() {
        return commitHash;
    }

    public void setCommitHash(String commitHash) {
        this.commitHash = commitHash;
    }
}
