package com.apbytes.gitcommits;

import android.net.Uri;
import android.support.test.runner.AndroidJUnit4;

import com.apbytes.gitcommits.githubHelpers.GitCommit;
import com.apbytes.gitcommits.githubHelpers.GitCommitList;
import com.apbytes.gitcommits.githubHelpers.GitRepo;
import com.apbytes.gitcommits.githubHelpers.GithubClient;
import com.apbytes.gitcommits.networking.NetworkHelper;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.InvalidObjectException;
import java.util.ArrayList;

/**
 * Created by Abhishek on 6/10/2018.
 */
@RunWith(AndroidJUnit4.class)
public class NetTester {
    @Test
    public void test_getRequest(){
        String url = "https://api.github.com/repos/tensorflow/tensorflow/commits";
        Uri uri = Uri.parse(url);
        Assert.assertTrue(uri != null);
        String response = NetworkHelper.makeRequest(url, "GET", null, null);
        Assert.assertTrue(response.trim().length() > 0);
    }

    @Test
    public void test_fetchCommits() throws InvalidObjectException {
        GithubClient ghc = new GithubClient();
        GitRepo repo = ghc.getRepository("tensorflow", "tensorflow");
        GitCommitList gitCommitList = repo.getCommits(null);
        ArrayList<Long> dates = new ArrayList<>();
        for(GitCommit commit : gitCommitList.getCommitList()){
            dates.add(commit.getCommitTime().getTime());
            if(dates.size() > 0){
                Assert.assertTrue(dates.get(dates.size() - 1) > dates.get(dates.size() - 2));
            }
        }
    }
}
