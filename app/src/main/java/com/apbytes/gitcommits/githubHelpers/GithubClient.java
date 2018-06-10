package com.apbytes.gitcommits.githubHelpers;

/**
 * Created by Abhishek on 6/8/2018.
 */

public class GithubClient {
    private String token;
    private static String baseUri = "";

    public GithubClient() {

    }

    public GithubClient(String token){
        this.token = token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public GitRepo getRepository(String userName, String repoName){
        return new GitRepo(this, userName, repoName);
    }
}
