package com.apbytes.gitcommits.githubHelpers;

import java.util.Date;

/**
 * Created by Abhishek on 6/8/2018.
 */

public class GitCommit {
    String userName;
    String commiterName;
    Date commitTime;

    GitCommit(){

    }

    public GitCommit(String userName, String commiterName, Date commitTime) {
        this.userName = userName;
        this.commiterName = commiterName;
        this.commitTime = commitTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCommiterName() {
        return commiterName;
    }

    public void setCommiterName(String commiterName) {
        this.commiterName = commiterName;
    }

    public Date getCommitTime() {
        return commitTime;
    }

    public void setCommitTime(Date commitTime) {
        this.commitTime = commitTime;
    }
}
