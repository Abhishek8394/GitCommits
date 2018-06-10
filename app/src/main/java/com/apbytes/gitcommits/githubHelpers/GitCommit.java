package com.apbytes.gitcommits.githubHelpers;

import java.util.Date;

/**
 * Created by Abhishek on 6/8/2018.
 */

public class GitCommit {
    String userName;
    String commitMessage;
    Date commitTime;

    public GitCommit(){
        commitTime = new Date();
    }

    public GitCommit(String userName, String commitMessage, Date commitTime) {
        this.userName = userName;
        this.commitMessage = commitMessage;
        this.commitTime = commitTime;
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
}
